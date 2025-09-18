package com.project.ridex_backend.repository;

import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String userName);

    Optional<User> findFirstByRole(UserRole role);
}
