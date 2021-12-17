package com.goganesh.packages.repository;

import com.goganesh.packages.domain.Authority;
import com.goganesh.packages.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    List<Authority> findByUser(User user);
}
