-- ====================================================================
-- SCHEMA CREATION (MySQL & H2 MySQL-Mode Compatible)
-- ====================================================================

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    room_number VARCHAR(20) DEFAULT 'A-101',
    hostel_block VARCHAR(20) DEFAULT 'Block A',
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS department (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS staff (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department_id INT NOT NULL,
    phone VARCHAR(20) DEFAULT '9876543210',
    CONSTRAINT fk_staff_dept FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS complaint_category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS complaint (
    complaint_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    department_id INT NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending' NOT NULL,
    priority VARCHAR(10) DEFAULT 'Medium' NOT NULL,
    date_registered TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    date_resolved TIMESTAMP NULL DEFAULT NULL,
    CONSTRAINT fk_complaint_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_complaint_cat FOREIGN KEY (category_id) REFERENCES complaint_category(category_id),
    CONSTRAINT fk_complaint_dept FOREIGN KEY (department_id) REFERENCES department(department_id)
);

CREATE TABLE IF NOT EXISTS complaint_status_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_history_comp FOREIGN KEY (complaint_id) REFERENCES complaint(complaint_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS complaint_assignment (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL UNIQUE,
    staff_id INT NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_assign_comp FOREIGN KEY (complaint_id) REFERENCES complaint(complaint_id) ON DELETE CASCADE,
    CONSTRAINT fk_assign_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS complaint_feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL UNIQUE,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comments VARCHAR(400),
    feedback_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_feed_comp FOREIGN KEY (complaint_id) REFERENCES complaint(complaint_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS complaint_attachment (
    attachment_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    uploaded_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_att_comp FOREIGN KEY (complaint_id) REFERENCES complaint(complaint_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS complaint_escalation (
    escalation_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    escalation_level INT NOT NULL CHECK (escalation_level BETWEEN 1 AND 5),
    escalation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_esc_comp FOREIGN KEY (complaint_id) REFERENCES complaint(complaint_id) ON DELETE CASCADE
);
