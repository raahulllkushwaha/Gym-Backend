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
@Table(name = "blogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Blog extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 10000, nullable = false)
    private String content;

    private String coverImageUrl;
    private String category; // "Fitness Tips", "Diet Tips", "Announcements"

    @lombok.Builder.Default
    private boolean published = true;
}
