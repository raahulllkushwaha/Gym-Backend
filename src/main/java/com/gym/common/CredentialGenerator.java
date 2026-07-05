package com.gym.common;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CredentialGenerator {

    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ"; // no I/O confusion
    private static final String LOWER = "abcdefghijkmnpqrstuvwxyz";
    private static final String DIGITS = "23456789";
    private static final String SPECIAL = "@#$%&*!";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;

    private final SecureRandom random = new SecureRandom();

    /** e.g. RAHUL482 from "Rahul Kushwaha" + random 3-digit suffix, uniqueness checked by caller. */
    public String generateUsername(String fullName) {
        String base = fullName.trim().split("\\s+")[0].toUpperCase();
        if (base.length() > 10) base = base.substring(0, 10);
        int suffix = 100 + random.nextInt(900);
        return base + suffix;
    }

    /** 10-char strong password guaranteeing at least one of each char class. */
    public String generatePassword() {
        StringBuilder sb = new StringBuilder();
        sb.append(UPPER.charAt(random.nextInt(UPPER.length())));
        sb.append(LOWER.charAt(random.nextInt(LOWER.length())));
        sb.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        sb.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        for (int i = 0; i < 6; i++) {
            sb.append(ALL.charAt(random.nextInt(ALL.length())));
        }
        // shuffle
        for (int i = sb.length() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(j));
            sb.setCharAt(j, tmp);
        }
        return sb.toString();
    }
}
