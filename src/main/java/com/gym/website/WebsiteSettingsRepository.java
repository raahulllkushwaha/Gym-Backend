package com.gym.website;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebsiteSettingsRepository extends JpaRepository<WebsiteSettings, UUID> {
    // singleton - always just fetch findAll().get(0), created if absent by service
}
