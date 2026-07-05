-- V4__workout_diet_progress.sql

CREATE TABLE workout_plans (
    id UUID PRIMARY KEY,
    member_user_id UUID NOT NULL REFERENCES users(id),
    trainer_user_id UUID NOT NULL REFERENCES users(id),
    day_of_week VARCHAR(10) NOT NULL,
    title VARCHAR(255) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_workout_plans_member ON workout_plans(member_user_id);
CREATE INDEX idx_workout_plans_trainer ON workout_plans(trainer_user_id);

CREATE TABLE workout_plan_exercises (
    workout_plan_id UUID NOT NULL REFERENCES workout_plans(id) ON DELETE CASCADE,
    exercise_line VARCHAR(255)
);

CREATE TABLE workout_completions (
    id UUID PRIMARY KEY,
    workout_plan_id UUID NOT NULL REFERENCES workout_plans(id),
    completed_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_workout_completion UNIQUE (workout_plan_id, completed_date)
);

CREATE TABLE diet_plans (
    id UUID PRIMARY KEY,
    member_user_id UUID NOT NULL REFERENCES users(id),
    trainer_user_id UUID NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_diet_plans_member ON diet_plans(member_user_id);

CREATE TABLE diet_plan_meals (
    diet_plan_id UUID NOT NULL REFERENCES diet_plans(id) ON DELETE CASCADE,
    meal_line VARCHAR(255)
);

CREATE TABLE member_progress (
    id UUID PRIMARY KEY,
    member_user_id UUID NOT NULL REFERENCES users(id),
    recorded_date DATE NOT NULL,
    weight_kg DOUBLE PRECISION,
    height_cm DOUBLE PRECISION,
    chest_cm DOUBLE PRECISION,
    waist_cm DOUBLE PRECISION,
    arms_cm DOUBLE PRECISION,
    body_fat_percent DOUBLE PRECISION,
    bmi DOUBLE PRECISION,
    photo_url VARCHAR(500),
    recorded_by_user_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_member_progress_member ON member_progress(member_user_id);
