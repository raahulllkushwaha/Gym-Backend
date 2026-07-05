# Gym Management System - Backend

Spring Boot 3.3 / Java 21 backend for a full gym management system: Manager / Trainer / Member
roles, no public admin/trainer registration, auto-generated login credentials sent via email +
SMS, membership + payment tracking, workout/diet plans, attendance, progress/BMI, gallery, and a
full website CMS (announcements, offers, testimonials, blog, FAQs, contact form) - all editable
from the Manager panel with zero code changes.

## Tech stack

Java 21 - Spring Boot 3.3 - Spring Security (JWT) - Spring Data JPA - PostgreSQL - Flyway -
Quartz Scheduler - Spring Mail - Twilio (SMS) - OpenPDF (invoices) - Lombok - Maven - Docker

## Quick start (Docker - recommended)

```bash
cp .env.example .env
# edit .env: at minimum set JWT_SECRET, MAIL_USERNAME/MAIL_APP_PASSWORD,
# and BOOTSTRAP_MANAGER_* (this is the ONLY way to get your first login)

docker compose up --build
```

App comes up on `http://localhost:8080`. Flyway runs all migrations automatically on startup.

## Quick start (local, no Docker)

1. Start a local PostgreSQL, create database `gymdb`.
2. Set env vars (see `.env.example`) - at least `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`,
   `BOOTSTRAP_MANAGER_USERNAME`, `BOOTSTRAP_MANAGER_PASSWORD`, `BOOTSTRAP_MANAGER_EMAIL`,
   `BOOTSTRAP_MANAGER_PHONE`.
3. `mvn spring-boot:run`

> Note: this project was written in a sandboxed environment without access to Maven Central, so
> `mvn compile` has not been physically run here. Run it locally first - if anything doesn't
> compile it will most likely be a missing/renamed dependency version, quick fix.

## First login

There is **no public registration for Manager or Trainer, ever** - by design (see
`SecurityConfig`). The very first Manager account is created automatically on first startup by
`DataInitializer` from the `BOOTSTRAP_MANAGER_*` env vars. Log in with those, then:

- Create Trainers from `/api/manager/trainers` (credentials auto-generated, emailed + texted).
- Create Members directly from `/api/manager/members`, **or** let the public submit a signup
  request at `/api/auth/signup-request` and approve it from
  `/api/manager/signup-requests/{id}/approve`.

## Core design decisions

- **Single `users` table** with a `role` column (MANAGER/TRAINER/MEMBER) instead of three
  separate tables - one auth path, simpler joins.
- **`UserProvisioningService`** is the only place that ever generates a username/password. It is
  called from Manager-creates-Trainer, Manager-creates-Member, and Signup-request-approval - so
  credential generation + welcome email/SMS/in-app-notification never gets duplicated or
  forgotten in a new flow.
- **Membership is a separate entity from User** so plan history, freezes, and renewals can grow
  without touching auth.
- **File storage is local disk** (`FileStorageService`) behind a small interface-like class - swap
  its internals for AWS S3 / Cloudinary later without touching any controller/service that calls
  it.
- **Quartz** (not `@Scheduled`) for the expiry-reminder job, per your original spec.

## Environment variables

See `.env.example` for the full, documented list.

## Database migrations (Flyway)

| File | Contents |
|---|---|
| V1__init.sql | users, signup_requests, membership_plans, memberships, trainer_change_requests, gallery_items |
| V2__payments.sql | payments |
| V3__attendance.sql | attendance |
| V4__workout_diet_progress.sql | workout_plans (+exercises), workout_completions, diet_plans (+meals), member_progress |
| V5__website_cms.sql | website_settings, announcements, offers, testimonials, blogs, faqs, contact_messages |
| V6__notifications.sql | notifications (in-app) |

## API reference

All responses are wrapped: `{ "success": bool, "message": string, "data": ... }`.
Auth: `Authorization: Bearer <accessToken>` header, obtained from `/api/auth/login`.

### Public (no auth)
```
POST   /api/auth/login
POST   /api/auth/refresh
POST   /api/auth/signup-request
GET    /api/public/plans
GET    /api/public/gallery
GET    /api/public/settings
GET    /api/public/announcements
GET    /api/public/offers
GET    /api/public/testimonials
POST   /api/public/testimonials
GET    /api/public/blogs
GET    /api/public/faqs
POST   /api/public/contact
```

### Manager only
```
--- Trainers & Members ---
POST   /api/manager/trainers
DELETE /api/manager/trainers/{id}
PATCH  /api/manager/trainers/{id}/suspend
GET    /api/manager/trainers
POST   /api/manager/members
DELETE /api/manager/members/{id}

--- Signup requests ---
GET    /api/manager/signup-requests
POST   /api/manager/signup-requests/{id}/approve
POST   /api/manager/signup-requests/{id}/reject

--- Trainer-change requests ---
GET    /api/manager/trainer-change-requests
POST   /api/manager/trainer-change-requests/{id}/approve
POST   /api/manager/trainer-change-requests/{id}/reject

--- Membership plans ---
GET    /api/manager/plans
POST   /api/manager/plans
PUT    /api/manager/plans/{id}
DELETE /api/manager/plans/{id}

--- Payments ---
POST   /api/manager/payments
GET    /api/manager/payments
GET    /api/manager/payments/{id}/invoice   (PDF download)

--- Attendance ---
GET    /api/manager/attendance/today
GET    /api/manager/attendance/{userId}

--- Gallery ---
GET    /api/manager/gallery
POST   /api/manager/gallery                (multipart file upload)
PUT    /api/manager/gallery/{id}
PATCH  /api/manager/gallery/{id}/toggle-visibility
DELETE /api/manager/gallery/{id}

--- Reports ---
GET    /api/manager/reports/dashboard
GET    /api/manager/reports/revenue?start=YYYY-MM-DD&end=YYYY-MM-DD
GET    /api/manager/reports/memberships

--- Website CMS ---
PUT    /api/manager/website/settings
GET/POST/DELETE /api/manager/website/announcements[/{id}]
GET/POST/DELETE /api/manager/website/offers[/{id}]
GET    /api/manager/website/testimonials/pending
POST   /api/manager/website/testimonials/{id}/approve
DELETE /api/manager/website/testimonials/{id}
GET/POST/DELETE /api/manager/website/blogs[/{id}]
GET/POST/DELETE /api/manager/website/faqs[/{id}]
GET    /api/manager/website/contact-messages
```

### Trainer (+ Manager)
```
GET    /api/trainers/me/members
POST   /api/trainers/workout-plans
PUT    /api/trainers/workout-plans/{id}
DELETE /api/trainers/workout-plans/{id}
GET    /api/trainers/me/workout-plans
POST   /api/trainers/diet-plans
PUT    /api/trainers/diet-plans/{id}
DELETE /api/trainers/diet-plans/{id}
GET    /api/trainers/me/diet-plans
POST   /api/trainers/progress
POST   /api/trainers/me/attendance/check-in
POST   /api/trainers/me/attendance/check-out
```

### Member only
```
GET    /api/members/me/dashboard
POST   /api/members/me/request-trainer-change
GET    /api/members/me/workout-plans
POST   /api/members/me/workout-plans/{id}/complete
GET    /api/members/me/diet-plans
POST   /api/members/me/progress
GET    /api/members/me/progress
GET    /api/members/me/payments
GET    /api/members/me/invoices/{paymentId}    (PDF download)
POST   /api/members/me/attendance/check-in
POST   /api/members/me/attendance/check-out
GET    /api/members/me/attendance
```

### Any authenticated user
```
GET    /api/notifications/me
GET    /api/notifications/me/unread-count
PATCH  /api/notifications/{id}/read
GET    /api/utils/bmi?weightKg=&heightCm=
```

## What's intentionally simple / left for you to extend

- Local disk file storage instead of S3/Cloudinary (see `FileStorageService` docstring).
- Reports run in-memory over `findAll()` for now - fine at gym scale, swap for SQL aggregates if
  the member count grows into the tens of thousands.
- No multi-branch support, no QR/RFID/fingerprint hardware integration, no coupon/referral system,
  no equipment inventory - all called out as "nice to have" in your original spec and not core to
  a v1.
# Gym-Backend
