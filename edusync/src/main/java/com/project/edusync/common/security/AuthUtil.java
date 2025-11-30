package com.project.edusync.common.security;

import com.project.edusync.common.exception.iam.InsufficientAuthenticationException;
import com.project.edusync.iam.model.entity.Role;
import com.project.edusync.iam.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthUtil {

    /**
     * Retrieves the full User entity of the currently authenticated user.
     *
     * @return The authenticated User entity.
     * @throws InsufficientAuthenticationException if no user is authenticated or is anonymous.
     * @throws IllegalStateException if the principal is not of the expected User type.
     */
    public User getCurrentUser() {
        log.debug("Attempting to retrieve current authenticated user.");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Authentication check failed: No authentication object found or user is not authenticated.");
            throw new InsufficientAuthenticationException("User is not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User user = (User) principal;
            log.debug("Successfully retrieved authenticated user: {}", user.getUsername());
            return user;
        }

        if (principal instanceof String && "anonymousUser".equals(principal)) {
            log.warn("Authentication check failed: User is anonymous.");
            throw new InsufficientAuthenticationException("User is anonymous.");
        }

        // If the principal is not what we expect, it's a critical configuration error.
        String principalType = (principal == null ? "null" : principal.getClass().getName());
        log.error("CRITICAL CONFIGURATION ERROR: Authenticated principal is not of the expected type 'User'. Found: {}. Please ensure your CustomUserDetailService returns the 'com.project.edusync.iam.model.entity.User' object.", principalType);
        throw new IllegalStateException("Authenticated principal is not of the expected type 'User'. " +
                "Found: " + principalType +
                ". Please ensure your CustomUserDetailService returns the 'com.project.edusync.iam.model.entity.User' object.");
    }

    /**
     * A convenience method to retrieve the user ID of the currently authenticated user.
     *
     * @return The Long user ID of the authenticated user.
     * @throws InsufficientAuthenticationException if no user is authenticated.
     * @throws IllegalStateException if the principal is not of the expected User type.
     */
    public Long getCurrentUserId() {
        log.debug("Retrieving user ID for current user.");
        return getCurrentUser().getId();
    }

    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Value("${app.jwt.expirationTime}")
    private long jwtExpirationTime;

    @Value("${app.jwt.refresh-expirationTime}")
    private long jwtRefreshExpirationTime;

    private SecretKey _signingKey;

    @PostConstruct
    public void init() {
        this._signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        log.info("JWT signing key initialized successfully.");
    }

    private SecretKey getSigningKey() {
        return this._signingKey;
    }

    /**
     * Generates a short-lived Access Token containing user authorities.
     *
     * @param username The user's username (subject).
     * @param roles    The user's roles.
     * @return A signed JWT Access Token.
     */
    public String generateAccessToken(String username, Set<Role> roles) {
        log.debug("Generating Access Token for user: {}", username);

        // 1. Convert roles to a simple List<String> for the claim
        List<String> authorityStrings = roles.stream()
                .map(Role::getName) // Assuming Role has a getName() or similar
                .collect(Collectors.toList());
        log.debug("Included {} authorities in access token for user: {}", authorityStrings.size(), username);

        // 2. Create a 'claims' map to store the authorities
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorityStrings);

        // 3. Build the token
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .claims(claims)
                .signWith(getSigningKey())
                .compact();

        log.debug("Access Token generated successfully for user: {}.", username);
        return token;
    }

    /**
     * Extracts all claims from the token using the secure key.
     */
    private Claims getAllClaimsFromToken(String token) {
        log.trace("Parsing all claims from token.");
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the username (subject) from the token.
     */
    public String getUsernameFromToken(String token) {
        log.debug("Extracting username from token.");
        String username = getAllClaimsFromToken(token).getSubject();
        log.debug("Extracted username '{}' from token.", username);
        return username;
    }

    /**
     * Extracts authorities from the token's "authorities" claim.
     */
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        log.debug("Extracting authorities from token.");
        Claims claims = getAllClaimsFromToken(token);

        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");

        if (authorities == null || authorities.isEmpty()) {
            log.debug("No 'authorities' claim found in token or claim is empty.");
            return Collections.emptyList();
        }

        log.debug("Found {} authorities in token.", authorities.size());
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * --- NEW ---
     * Validates a token by attempting to parse it.
     *
     * @param token The JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        log.debug("Validating token.");
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            log.debug("Token validated successfully.");
            return true;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        log.warn("Token validation failed.");
        return false;
    }
}