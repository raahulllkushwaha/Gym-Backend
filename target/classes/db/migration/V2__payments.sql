-- V2__payments.sql

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    member_user_id UUID NOT NULL REFERENCES users(id),
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    amount NUMERIC(10,2) NOT NULL,
    discount NUMERIC(10,2) NOT NULL DEFAULT 0,
    gst NUMERIC(10,2) NOT NULL DEFAULT 0,
    remaining_balance NUMERIC(10,2) NOT NULL DEFAULT 0,
    payment_mode VARCHAR(20) NOT NULL,
    transaction_id VARCHAR(255),
    payment_date DATE NOT NULL,
    plan_name_snapshot VARCHAR(255),
    recorded_by_user_id VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_payments_member ON payments(member_user_id);
CREATE INDEX idx_payments_date ON payments(payment_date);
