-- V6__notifications.sql

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    receiver_user_id UUID NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_notifications_receiver ON notifications(receiver_user_id, read);
