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
@Table(name = "testimonials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Testimonial extends BaseEntity {

    @Column(nullable = false)
    private String memberName;

    @Column(length = 1000, nullable = false)
    private String message;

    private String photoUrl;
    private Integer rating; // 1-5

    @lombok.Builder.Default
    private boolean approved = false; // manager approves before it shows publicly

    @lombok.Builder.Default
    private boolean hidden = false;
}
