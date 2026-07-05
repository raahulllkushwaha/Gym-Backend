package com.gym.diet;

import com.gym.common.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "diet_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DietPlan extends BaseEntity {

    @Column(nullable = false)
    private UUID memberUserId;

    @Column(nullable = false)
    private UUID trainerUserId;

    @Column(nullable = false)
    private String title; // e.g. "Weight Loss - Week 1"

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "diet_plan_meals", joinColumns = @JoinColumn(name = "diet_plan_id"))
    @Column(name = "meal_line", length = 255)
    @lombok.Builder.Default
    private List<String> meals = new ArrayList<>(); // e.g. "Breakfast: 4 egg whites + oats"

    @Column(length = 500)
    private String notes;
}
