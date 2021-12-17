package com.goganesh.packages.repository;

import com.goganesh.packages.domain.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SiteRepository extends JpaRepository<Site, UUID> {

    Optional<Site> findByUrl(String url);

    @Query("SELECT s FROM Site s WHERE s.url LIKE %:host%")
    Optional<Site> findByHost(@Param("host")String host);
}
