package com.gym.user;

public enum UserStatus {
    ACTIVE,
    SUSPENDED,
    LOCKED,
    PENDING_APPROVAL   // used by signup-request flow before manager approves
}
