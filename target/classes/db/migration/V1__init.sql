-- V1__init.sql
-- Core auth, membership, trainer-change, gallery tables

CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    created_by_user_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

CREATE TABLE signup_requests (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    age INT,
    gender VARCHAR(10),
    preferred_plan_name VARCHAR(255),
    message VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_by_user_id UUID,
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_signup_requests_status ON signup_requests(status);
CREATE INDEX idx_signup_requests_email ON signup_requests(email);

CREATE TABLE membership_plans (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000),
    duration_in_days INT NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    long_term_plan BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE memberships (
    id UUID PRIMARY KEY,
    member_user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    plan_id UUID NOT NULL REFERENCES membership_plans(id),
    assigned_trainer_user_id UUID REFERENCES users(id),
    start_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    reminder_3day_sent BOOLEAN NOT NULL DEFAULT FALSE,
    reminder_30day_sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_memberships_status_expiry ON memberships(status, expiry_date);
CREATE INDEX idx_memberships_trainer ON memberships(assigned_trainer_user_id);

CREATE TABLE trainer_change_requests (
    id UUID PRIMARY KEY,
    member_user_id UUID NOT NULL REFERENCES users(id),
    current_trainer_user_id UUID REFERENCES users(id),
    requested_trainer_user_id UUID NOT NULL REFERENCES users(id),
    reason VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_by_user_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_trainer_change_status ON trainer_change_requests(status);

CREATE TABLE gallery_items (
    id UUID PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    type VARCHAR(10) NOT NULL,
    caption VARCHAR(500),
    category VARCHAR(255),
    visible BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_gallery_visible ON gallery_items(visible);
