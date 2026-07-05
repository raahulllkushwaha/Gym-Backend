package com.gym.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    Optional<Attendance> findByUserIdAndAttendanceDate(UUID userId, LocalDate date);
    List<Attendance> findByUserIdOrderByAttendanceDateDesc(UUID userId);
    List<Attendance> findByAttendanceDate(LocalDate date);
    long countByAttendanceDate(LocalDate date);
}
