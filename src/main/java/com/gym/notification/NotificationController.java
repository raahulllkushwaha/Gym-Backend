package com.gym.notification;

import com.gym.common.ApiResponse;
import com.gym.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/me")
    public ApiResponse<Page<Notification>> myNotifications(@AuthenticationPrincipal CustomUserDetails principal,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Notifications", notificationService.forUser(principal.getId(), PageRequest.of(page, size)));
    }

    @GetMapping("/me/unread-count")
    public ApiResponse<Long> unreadCount(@AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.ok("Unread count", notificationService.unreadCount(principal.getId()));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<?> markRead(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails principal) {
        notificationService.markRead(id, principal.getId());
        return ApiResponse.ok("Marked as read");
    }
}
