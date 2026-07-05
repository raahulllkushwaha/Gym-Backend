package com.gym.reports;

import com.gym.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/manager/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public ApiResponse<?> dashboard() {
        return ApiResponse.ok("Dashboard summary", reportService.dashboardSummary());
    }

    @GetMapping("/revenue")
    public ApiResponse<?> revenue(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ApiResponse.ok("Revenue report", reportService.revenueBetween(start, end));
    }

    @GetMapping("/memberships")
    public ApiResponse<?> membershipStats() {
        return ApiResponse.ok("Membership statistics", reportService.membershipStats());
    }
}
