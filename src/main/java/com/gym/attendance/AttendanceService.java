package com.gym.attendance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Transactional
    public Attendance checkIn(UUID userId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserIdAndAttendanceDate(userId, today)
                .orElseGet(() -> Attendance.builder()
                        .userId(userId)
                        .attendanceDate(today)
                        .build());

        if (attendance.getCheckInTime() != null) {
            throw new IllegalStateException("Already checked in today at " + attendance.getCheckInTime());
        }
        attendance.setCheckInTime(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    @Transactional
    public Attendance checkOut(UUID userId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserIdAndAttendanceDate(userId, today)
                .orElseThrow(() -> new IllegalStateException("You have not checked in today"));

        if (attendance.getCheckOutTime() != null) {
            throw new IllegalStateException("Already checked out today at " + attendance.getCheckOutTime());
        }
        attendance.setCheckOutTime(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> history(UUID userId) {
        return attendanceRepository.findByUserIdOrderByAttendanceDateDesc(userId);
    }

    public List<Attendance> todayReport() {
        return attendanceRepository.findByAttendanceDate(LocalDate.now());
    }

    public long todayCount() {
        return attendanceRepository.countByAttendanceDate(LocalDate.now());
    }
}
