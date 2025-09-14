package com.project.ridex_backend.repository;

import com.project.ridex_backend.entity.UserRegisterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserRegisterRequest, Long> {

    boolean existsByEmail(String email);
}
