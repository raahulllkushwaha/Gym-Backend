package com.gym.trainer;

import com.gym.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "trainer_change_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TrainerChangeRequest extends BaseEntity {

    @Column(nullable = false)
    private UUID memberUserId;

    private UUID currentTrainerUserId; // may be null if member never had a trainer

    @Column(nullable = false)
    private UUID requestedTrainerUserId;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @lombok.Builder.Default
    private TrainerChangeRequestStatus status = TrainerChangeRequestStatus.PENDING;

    private UUID reviewedByUserId;
}
