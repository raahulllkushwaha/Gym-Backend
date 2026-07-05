package com.gym.membership;

import com.gym.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "membership_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MembershipPlan extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name; // e.g. Monthly, Quarterly, Yearly, Student, Couple, Premium, VIP

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private int durationInDays; // 30, 90, 180, 365 etc - fully configurable

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @lombok.Builder.Default
    private boolean active = true;

    /** Yearly-type plans get the extra 30-days-before reminder in addition to 3-days-before. */
    @lombok.Builder.Default
    private boolean longTermPlan = false;
}
