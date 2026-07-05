package com.gym.attendance;

import com.gym.common.ApiResponse;
import com.gym.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // ---- Member self check-in/out ----
    @PostMapping("/api/members/me/attendance/check-in")
    public ApiResponse<?> memberCheckIn(@AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.ok("Checked in", attendanceService.checkIn(principal.getId()));
    }

    @PostMapping("/api/members/me/attendance/check-out")
    public ApiResponse<?> memberCheckOut(@AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.ok("Checked out", attendanceService.checkOut(principal.getId()));
    }

    @GetMapping("/api/members/me/attendance")
    public ApiResponse<?> memberHistory(@AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.ok("Attendance history", attendanceService.history(principal.getId()));
    }

    // ---- Trainer self check-in/out ----
    @PostMapping("/api/trainers/me/attendance/check-in")
    public ApiResponse<?> trainerCheckIn(@AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.ok("Checked in", attendanceService.checkIn(principal.getId()));
    }

    @PostMapping("/api/trainers/me/attendance/check-out")
    public ApiResponse<?> trainerCheckOut(@AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.ok("Checked out", attendanceService.checkOut(principal.getId()));
    }

    // ---- Manager reports ----
    @GetMapping("/api/manager/attendance/today")
    public ApiResponse<?> todayReport() {
        return ApiResponse.ok("Today's attendance", attendanceService.todayReport());
    }

    @GetMapping("/api/manager/attendance/{userId}")
    public ApiResponse<?> userHistory(@PathVariable UUID userId) {
        return ApiResponse.ok("Attendance history", attendanceService.history(userId));
    }
}
