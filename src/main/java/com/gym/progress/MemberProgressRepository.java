package com.gym.progress;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MemberProgressRepository extends JpaRepository<MemberProgress, UUID> {
    List<MemberProgress> findByMemberUserIdAndDeletedFalseOrderByRecordedDateAsc(UUID memberUserId);
}
