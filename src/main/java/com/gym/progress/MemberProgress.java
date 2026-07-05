package com.gym.progress;

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
import java.util.UUID;

@Entity
@Table(name = "member_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MemberProgress extends BaseEntity {

    @Column(nullable = false)
    private UUID memberUserId;

    @Column(nullable = false)
    private LocalDate recordedDate;

    private Double weightKg;
    private Double heightCm;
    private Double chestCm;
    private Double waistCm;
    private Double armsCm;
    private Double bodyFatPercent;

    private Double bmi; // computed at save-time from weight+height, stored for fast chart queries

    private String photoUrl;

    @Column(name = "recorded_by_user_id")
    private UUID recordedByUserId; // trainer or the member themself
}
