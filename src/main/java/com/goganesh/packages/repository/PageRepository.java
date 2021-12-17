package com.goganesh.packages.repository;

import com.goganesh.packages.domain.Page;
import com.goganesh.packages.domain.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PageRepository extends JpaRepository<Page, UUID> {

    List<Page> findBySite(Site site);
    Optional<Page> findByPath(String path);
}
