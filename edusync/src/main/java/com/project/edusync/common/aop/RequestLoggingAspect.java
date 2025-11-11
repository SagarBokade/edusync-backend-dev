package com.project.edusync.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class RequestLoggingAspect {

    /**
     * Defines a pointcut that targets all public methods in any class
     * within the 'com.project.edusync' package (and its sub-packages)
     * that is annotated with @RestController.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) && " +
            "execution(public * com.project.edusync..*.*(..))")
    public void restControllerEndpoints() {
        // This method is a marker for the pointcut
    }

    /**
     * Advice that runs *before* any method matched by the restControllerEndpoints() pointcut.
     * It logs the incoming request details.
     *
     * @param joinPoint provides access to the method signature.
     */
    @Before("restControllerEndpoints()")
    public void logIncomingRequest(JoinPoint joinPoint) {
        // 1. Get the HttpServletRequest
        HttpServletRequest request = getRequest();

        // 2. Get Client IP Address
        String clientIp = "N/A";
        if (request != null) {
            clientIp = getClientIp(request);
        }

        // 3. Get Endpoint Information
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        // 4. Log the information
        log.info("#########################################################");
        log.info("############       Incoming Request      ################");
        log.info("#########################################################");
        log.info("\tClientIp: {}", clientIp);
        log.info("\tClass: {}", className);
        log.info("\tMethod: {}", methodName);
    }

    /**
     * Safely retrieves the current HttpServletRequest from the RequestContextHolder.
     *
     * @return The HttpServletRequest, or null if not in a web request context.
     */
    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        log.warn("Not in a web request context. Cannot retrieve HttpServletRequest.");
        return null;
    }

    /**
     * Extracts the client's real IP address from the request,
     * accounting for proxies and load balancers.
     *
     * @param request The HttpServletRequest.
     * @return The client's IP address.
     */
    private String getClientIp(HttpServletRequest request) {
        // Check for X-Forwarded-For header (set by proxies/load balancers)
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            // The header can be a comma-separated list. The client's IP is the first one.
            return xForwardedForHeader.split(",")[0].trim();
        }

        // Fallback to the direct remote address
        return request.getRemoteAddr();
    }
}