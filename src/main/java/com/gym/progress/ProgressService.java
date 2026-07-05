package com.gym.progress;

import com.gym.membership.MembershipRepository;
import com.gym.progress.dto.AddProgressRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final MemberProgressRepository progressRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public MemberProgress addSelf(UUID memberUserId, AddProgressRequest req) {
        Double height = req.heightCm() != null ? req.heightCm() : latestHeight(memberUserId);
        return save(memberUserId, req, height, memberUserId);
    }

    @Transactional
    public MemberProgress addByTrainer(UUID trainerUserId, AddProgressRequest req) {
        if (req.memberUserId() == null) {
            throw new IllegalArgumentException("memberUserId is required");
        }
        assertAssigned(trainerUserId, req.memberUserId());
        Double height = req.heightCm() != null ? req.heightCm() : latestHeight(req.memberUserId());
        return save(req.memberUserId(), req, height, trainerUserId);
    }

    public List<MemberProgress> history(UUID memberUserId) {
        return progressRepository.findByMemberUserIdAndDeletedFalseOrderByRecordedDateAsc(memberUserId);
    }

    /** Simple standalone BMI calculator - doesn't require a saved record. */
    public java.util.Map<String, Object> calculateBmi(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        double bmi = weightKg / (heightM * heightM);
        String category;
        if (bmi < 18.5) category = "Underweight";
        else if (bmi < 25) category = "Normal";
        else if (bmi < 30) category = "Overweight";
        else category = "Obese";

        return java.util.Map.of(
                "bmi", Math.round(bmi * 10.0) / 10.0,
                "category", category
        );
    }

    private MemberProgress save(UUID memberUserId, AddProgressRequest req, Double height, UUID recordedBy) {
        Double bmi = height != null ? calcBmi(req.weightKg(), height) : null;

        MemberProgress progress = MemberProgress.builder()
                .memberUserId(memberUserId)
                .recordedDate(LocalDate.now())
                .weightKg(req.weightKg())
                .heightCm(height)
                .chestCm(req.chestCm())
                .waistCm(req.waistCm())
                .armsCm(req.armsCm())
                .bodyFatPercent(req.bodyFatPercent())
                .bmi(bmi)
                .photoUrl(req.photoUrl())
                .recordedByUserId(recordedBy)
                .build();

        return progressRepository.save(progress);
    }

    private double calcBmi(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return Math.round((weightKg / (heightM * heightM)) * 10.0) / 10.0;
    }

    private Double latestHeight(UUID memberUserId) {
        List<MemberProgress> history = progressRepository.findByMemberUserIdAndDeletedFalseOrderByRecordedDateAsc(memberUserId);
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).getHeightCm() != null) return history.get(i).getHeightCm();
        }
        return null;
    }

    private void assertAssigned(UUID trainerUserId, UUID memberUserId) {
        var membership = membershipRepository.findByMemberUserIdAndDeletedFalse(memberUserId)
                .orElseThrow(() -> new IllegalArgumentException("Member has no active membership"));
        if (membership.getAssignedTrainerUserId() == null || !membership.getAssignedTrainerUserId().equals(trainerUserId)) {
            throw new AccessDeniedException("This member is not assigned to you");
        }
    }
}
