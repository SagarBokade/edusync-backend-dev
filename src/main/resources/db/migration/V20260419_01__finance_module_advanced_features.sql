-- V20260419_01__finance_module_advanced_features.sql

-- Scholarship Types Table
CREATE TABLE scholarship_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    discount_type VARCHAR(50) NOT NULL,
    discount_value NUMERIC(10, 2) NOT NULL,
    eligibility_criteria TEXT,
    max_recipients INTEGER,
    active_count INTEGER DEFAULT 0,
    total_discount_issued NUMERIC(15, 2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Scholarship Assignments Table
CREATE TABLE scholarship_assignments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    student_name VARCHAR(255) NOT NULL,
    scholarship_type_id BIGINT NOT NULL REFERENCES scholarship_types(id),
    discount_type VARCHAR(50) NOT NULL,
    discount_value NUMERIC(10, 2) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    reason TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Installment Plans Table
CREATE TABLE installment_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    number_of_installments INTEGER NOT NULL,
    interval_days INTEGER NOT NULL,
    description TEXT,
    grace_period_days INTEGER NOT NULL,
    assigned_students INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Installment Assignments Table
CREATE TABLE installment_assignments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    student_name VARCHAR(255) NOT NULL,
    plan_id BIGINT NOT NULL REFERENCES installment_plans(id),
    total_amount NUMERIC(15, 2) NOT NULL,
    paid_installments INTEGER DEFAULT 0,
    total_installments INTEGER NOT NULL,
    next_due_date DATE NOT NULL,
    next_due_amount NUMERIC(15, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Refund Records Table
CREATE TABLE refund_records (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    student_name VARCHAR(255) NOT NULL,
    payment_id BIGINT,
    invoice_number VARCHAR(100),
    refund_amount NUMERIC(15, 2) NOT NULL,
    reason TEXT NOT NULL,
    refund_method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    remarks TEXT,
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Reminder Templates Table
CREATE TABLE reminder_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    channel VARCHAR(50) NOT NULL,
    trigger_type VARCHAR(50) NOT NULL,
    trigger_days INTEGER NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Reminder Logs Table
CREATE TABLE reminder_logs (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    student_name VARCHAR(255) NOT NULL,
    template_name VARCHAR(255) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    invoice_number VARCHAR(100),
    amount_due NUMERIC(15, 2),
    status VARCHAR(50) NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
