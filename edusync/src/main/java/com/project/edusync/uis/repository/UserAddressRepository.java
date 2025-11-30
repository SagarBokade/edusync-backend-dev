package com.project.edusync.uis.repository;

import com.project.edusync.uis.model.entity.UserAddress;
import com.project.edusync.uis.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress,Long> {
    List<UserAddress> findByUserProfile_Id(Long ProfileId);

    List<UserAddress> findByUserProfile(UserProfile profile);

    void deleteByUserProfile(UserProfile userProfile);
}
