package com.goganesh.packages.repository;

import com.goganesh.packages.domain.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SiteRepository extends JpaRepository<Site, UUID> {
}
