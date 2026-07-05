package com.gym.membership;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    Optional<Membership> findByMemberUserIdAndDeletedFalse(UUID memberUserId);

    List<Membership> findByStatusAndExpiryDateAndReminder3DaySentFalse(MembershipStatus status, LocalDate expiryDate);

    List<Membership> findByStatusAndExpiryDateAndReminder30DaySentFalse(MembershipStatus status, LocalDate expiryDate);

    List<Membership> findByStatusAndExpiryDateBefore(MembershipStatus status, LocalDate date);

    List<Membership> findByAssignedTrainerUserIdAndDeletedFalse(UUID trainerUserId);
}
