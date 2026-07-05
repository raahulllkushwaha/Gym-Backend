-- V3__attendance.sql

CREATE TABLE attendance (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    attendance_date DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_attendance_user_date UNIQUE (user_id, attendance_date)
);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);
