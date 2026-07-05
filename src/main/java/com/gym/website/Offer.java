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

import java.time.LocalDate;

@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Offer extends BaseEntity {

    @Column(nullable = false)
    private String title; // "20% Off New Year Offer"

    @Column(length = 1000)
    private String description;

    private String imageUrl;
    private LocalDate validFrom;
    private LocalDate validUntil;

    @lombok.Builder.Default
    private boolean active = true;
}
