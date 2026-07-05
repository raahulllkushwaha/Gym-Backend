# Gym Backend - Build Progress

## DONE
- [x] Project skeleton (pom.xml, application.yml, main class)
- [x] BaseEntity (UUID, audit timestamps, soft delete)
- [x] User entity + Role (MANAGER/TRAINER/MEMBER) + UserStatus
- [x] JWT security: JwtUtil, JwtAuthenticationFilter, CustomUserDetails(Service), SecurityConfig with role matrix
- [x] Global exception handler + ApiResponse wrapper
- [x] UserProvisioningService (auto username+password gen, sends welcome email+sms) - used by manager create & signup approval
- [x] Auth: login, refresh token, public signup-request (goes to PENDING_APPROVAL, no direct signup)
- [x] Notification: EmailService (HTML templates), SmsService (Twilio, dev-mode logs if no creds)
- [x] MembershipPlan (manager-defined, no code changes) + CRUD controller
- [x] Membership entity (per member: plan, trainer, dates, status, reminder flags)
- [x] Manager module: create/delete/suspend trainer, create/delete member, approve/reject signup requests
- [x] Trainer-change-request flow: member requests -> manager approves/rejects -> membership updated
- [x] Member self-service: dashboard (plan/status/days left/trainer), request trainer change
- [x] Quartz scheduler: daily job sends 3-day reminder (all plans) + 30-day reminder (yearly/long-term plans), auto-expires overdue memberships
- [x] File storage service (local disk, swappable for S3/Cloudinary later) + static resource serving
- [x] Gallery module: manager upload/edit/delete/toggle-visibility, public view endpoint

- [x] Flyway migrations V1 (core), V2 (payments), V3 (attendance)
- [x] Payment module: record payment/renewal, revenue queries, PDF invoice (OpenPDF), manager+member download endpoints
- [x] Attendance: member/trainer self check-in/out (one row per user per day, unique constraint), manager today-report + per-user history
- [x] Trainer panel: assigned-members list, workout plan CRUD (assignment-checked) + member mark-complete, diet plan CRUD, member progress logging (self + trainer), BMI calculator, Flyway V4
- [x] Reports: dashboard summary (today/month/year revenue, active/expired counts), revenue-between-dates, membership stats
- [x] Website CMS: settings (singleton), announcements (+broadcast as in-app notifications), offers, testimonials (submit+approve), blogs, FAQs, contact form - public read + manager write, Flyway V5
- [x] In-app Notification table wired into: welcome, expiry reminder, trainer-change-approved, announcement broadcast. User list/unread-count/mark-read endpoints. Flyway V6
- [x] Docker: multi-stage Dockerfile, docker-compose (app+postgres), .env.example
- [x] Seed data: DataInitializer creates ONE bootstrap MANAGER from env vars on first run (no-op once a manager exists)

## NEXT UP
- [x] Final review pass (fixed one real bug: public contact/testimonial endpoints now use DTOs instead of raw entities, so a client can no longer pass an `id` and overwrite another record)
- [x] README.md with full API list + setup steps
- [ ] Run `mvn clean install` locally once - sandbox had no Maven Central access so this was never physically compiled. Expect at most 1-2 small fixes (typo/import).
- [x] Final zip delivered

## KEY DESIGN DECISIONS (so future-you remembers why)
- Single `User` table with `role` enum column, instead of separate Manager/Trainer/Member tables - simpler joins, one auth path.
- No public registration endpoint for MANAGER/TRAINER at all. MEMBER can only get in via: (a) Manager directly creates, or (b) public signup-request -> PENDING -> Manager approves. Both paths funnel through UserProvisioningService so credential-gen + mail/sms is never duplicated.
- Membership is a separate entity from User (1:1 currently) so plan history / freezes / renewals can extend later without touching auth.
- Quartz over @Scheduled because doc explicitly asked for it and it's resume-friendlier.
