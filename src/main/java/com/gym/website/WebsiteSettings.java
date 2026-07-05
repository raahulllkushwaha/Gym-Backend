package com.gym.website;

import com.gym.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "website_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WebsiteSettings extends BaseEntity {

    @Column(nullable = false)
    private String gymName;

    private String phoneNumber;
    private String email;
    private String openingHours; // free text e.g. "Mon-Sat: 6 AM - 10 PM"
    private String logoUrl;
    private String heroBannerUrl;
    private String heroTagline;

    @Column(length = 2000)
    private String aboutUs;

    @Column(length = 1000)
    private String footerText;

    private String address;
    private String instagramUrl;
    private String facebookUrl;
    private String youtubeUrl;
    private String whatsappNumber;
}
