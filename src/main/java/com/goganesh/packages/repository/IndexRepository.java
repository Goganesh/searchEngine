package com.goganesh.packages.repository;

import com.goganesh.packages.domain.Index;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IndexRepository extends JpaRepository<Index, UUID> {
}
