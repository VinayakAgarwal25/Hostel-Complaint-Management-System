-- ====================================================================
-- HOSTEL COMPLAINT & MAINTENANCE MANAGEMENT SYSTEM
-- Database: MySQL (5.7 / 8.0+ / H2 MySQL-Mode Compatible)
-- Converted from Oracle SQL UCS310 Database Project
-- ====================================================================

DROP DATABASE IF EXISTS hostel_cms;
CREATE DATABASE hostel_cms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hostel_cms;

-- Disable foreign key checks for clean table drops
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS complaint_escalation;
DROP TABLE IF EXISTS complaint_attachment;
DROP TABLE IF EXISTS complaint_feedback;
DROP TABLE IF EXISTS complaint_assignment;
DROP TABLE IF EXISTS complaint_status_history;
DROP TABLE IF EXISTS complaint;
DROP TABLE IF EXISTS complaint_category;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- --------------------------------------------------------------------
-- 1. USERS TABLE (Students, Wardens, Admins)
-- --------------------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    room_number VARCHAR(20) DEFAULT 'A-101',
    hostel_block VARCHAR(20) DEFAULT 'Block A',
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------------------
-- 2. DEPARTMENT TABLE (Maintenance, Electrical, Plumbing, etc.)
-- --------------------------------------------------------------------
CREATE TABLE department (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(80) NOT NULL UNIQUE
);

-- --------------------------------------------------------------------
-- 3. STAFF TABLE (Technicians / Maintenance Staff)
-- --------------------------------------------------------------------
CREATE TABLE staff (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department_id INT NOT NULL,
    phone VARCHAR(20) DEFAULT '9876543210',
    CONSTRAINT fk_staff_department FOREIGN KEY (department_id) 
        REFERENCES department(department_id) ON DELETE CASCADE
);

-- --------------------------------------------------------------------
-- 4. COMPLAINT CATEGORY TABLE
-- --------------------------------------------------------------------
CREATE TABLE complaint_category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(80) NOT NULL UNIQUE
);

-- --------------------------------------------------------------------
-- 5. COMPLAINT TABLE
-- --------------------------------------------------------------------
CREATE TABLE complaint (
    complaint_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    department_id INT NOT NULL,
    description TEXT NOT NULL,
    status ENUM('Pending', 'In Progress', 'Resolved') DEFAULT 'Pending' NOT NULL,
    priority ENUM('Low', 'Medium', 'High') DEFAULT 'Medium' NOT NULL,
    date_registered TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    date_resolved TIMESTAMP NULL DEFAULT NULL,
    CONSTRAINT fk_complaint_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_complaint_category FOREIGN KEY (category_id) 
        REFERENCES complaint_category(category_id),
    CONSTRAINT fk_complaint_department FOREIGN KEY (department_id) 
        REFERENCES department(department_id)
);

-- --------------------------------------------------------------------
-- 6. COMPLAINT STATUS HISTORY TABLE
-- --------------------------------------------------------------------
CREATE TABLE complaint_status_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_history_complaint FOREIGN KEY (complaint_id) 
        REFERENCES complaint(complaint_id) ON DELETE CASCADE
);

-- --------------------------------------------------------------------
-- 7. COMPLAINT ASSIGNMENT TABLE
-- --------------------------------------------------------------------
CREATE TABLE complaint_assignment (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL UNIQUE,
    staff_id INT NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_assignment_complaint FOREIGN KEY (complaint_id) 
        REFERENCES complaint(complaint_id) ON DELETE CASCADE,
    CONSTRAINT fk_assignment_staff FOREIGN KEY (staff_id) 
        REFERENCES staff(staff_id) ON DELETE CASCADE
);

-- --------------------------------------------------------------------
-- 8. COMPLAINT FEEDBACK TABLE
-- --------------------------------------------------------------------
CREATE TABLE complaint_feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL UNIQUE,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comments VARCHAR(400),
    feedback_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_feedback_complaint FOREIGN KEY (complaint_id) 
        REFERENCES complaint(complaint_id) ON DELETE CASCADE
);

-- --------------------------------------------------------------------
-- 9. COMPLAINT ATTACHMENT TABLE
-- --------------------------------------------------------------------
CREATE TABLE complaint_attachment (
    attachment_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    uploaded_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_attachment_complaint FOREIGN KEY (complaint_id) 
        REFERENCES complaint(complaint_id) ON DELETE CASCADE
);

-- --------------------------------------------------------------------
-- 10. COMPLAINT ESCALATION TABLE
-- --------------------------------------------------------------------
CREATE TABLE complaint_escalation (
    escalation_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    escalation_level INT NOT NULL CHECK (escalation_level BETWEEN 1 AND 5),
    escalation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_escalation_complaint FOREIGN KEY (complaint_id) 
        REFERENCES complaint(complaint_id) ON DELETE CASCADE
);

-- ====================================================================
-- SEED DATA
-- ====================================================================

-- 1. Insert Departments
INSERT INTO department (department_id, department_name) VALUES
(1, 'Maintenance'),
(2, 'IT & Wi-Fi'),
(3, 'Housekeeping'),
(4, 'Security'),
(5, 'Mess & Cafeteria'),
(6, 'Hostel Administration'),
(7, 'Electrical'),
(8, 'Plumbing');

-- 2. Insert Staff
INSERT INTO staff (staff_id, name, department_id, phone) VALUES
(1, 'Ramesh Yadav', 1, '9876543201'),
(2, 'Suresh Patil', 1, '9876543202'),
(3, 'Kunal Bedi', 2, '9876543203'),
(4, 'Farah Khan', 2, '9876543204'),
(5, 'Manoj Tiwari', 2, '9876543205'),
(6, 'Lata More', 3, '9876543206'),
(7, 'Sunita Yadav', 3, '9876543207'),
(8, 'Prakash Rawat', 4, '9876543208'),
(9, 'Imran Sheikh', 4, '9876543209'),
(10, 'Joseph Dsouza', 5, '9876543210'),
(11, 'Geeta Arora', 5, '9876543211'),
(12, 'Mahesh Pillai', 6, '9876543212'),
(13, 'Rekha Singh', 6, '9876543213'),
(14, 'Vivek Pandey', 7, '9876543214'),
(15, 'Anil Chauhan', 7, '9876543215'),
(16, 'Bharat Lal', 8, '9876543216'),
(17, 'Nirmal Das', 8, '9876543217'),
(18, 'Naveen Kumar', 1, '9876543218'),
(19, 'Pallavi Desai', 6, '9876543219'),
(20, 'Ajay Soni', 7, '9876543220');

-- 3. Insert Categories
INSERT INTO complaint_category (category_id, category_name) VALUES
(1, 'Noise & Disturbance'),
(2, 'Water Leakage & Supply'),
(3, 'Power Outage & Geyser'),
(4, 'Wi-Fi & LAN Network'),
(5, 'Cleanliness & Garbage'),
(6, 'Pest Control & Insects'),
(7, 'Furniture Damage & Carpentry'),
(8, 'Mess Food Quality'),
(9, 'Security & Discipline'),
(10, 'Other Issues');

-- 4. Insert Users (Hostel Residents & Warden)
INSERT INTO users (user_id, name, email, room_number, hostel_block, role) VALUES
(1, 'Aarav Sharma', 'aarav.sharma@example.com', 'A-101', 'Block A', 'STUDENT'),
(2, 'Ananya Iyer', 'ananya.iyer@example.com', 'A-102', 'Block A', 'STUDENT'),
(3, 'Rohan Verma', 'rohan.verma@example.com', 'A-201', 'Block A', 'STUDENT'),
(4, 'Priya Nair', 'priya.nair@example.com', 'B-105', 'Block B', 'STUDENT'),
(5, 'Karan Mehta', 'karan.mehta@example.com', 'B-210', 'Block B', 'STUDENT'),
(6, 'Sneha Kulkarni', 'sneha.kulkarni@example.com', 'B-304', 'Block B', 'STUDENT'),
(7, 'Aditya Rao', 'aditya.rao@example.com', 'C-102', 'Block C', 'STUDENT'),
(8, 'Meera Menon', 'meera.menon@example.com', 'C-205', 'Block C', 'STUDENT'),
(9, 'Vikram Singh', 'vikram.singh@example.com', 'C-311', 'Block C', 'STUDENT'),
(10, 'Isha Gupta', 'isha.gupta@example.com', 'D-108', 'Block D', 'STUDENT'),
(11, 'Arjun Reddy', 'arjun.reddy@example.com', 'D-202', 'Block D', 'STUDENT'),
(12, 'Neha Joshi', 'neha.joshi@example.com', 'D-305', 'Block D', 'STUDENT'),
(13, 'Siddharth Jain', 'siddharth.jain@example.com', 'A-302', 'Block A', 'STUDENT'),
(14, 'Pooja Patel', 'pooja.patel@example.com', 'B-112', 'Block B', 'STUDENT'),
(15, 'Rahul Das', 'rahul.das@example.com', 'C-109', 'Block C', 'STUDENT'),
(16, 'Hostel Warden (Admin)', 'warden@hostel.edu', 'Admin-01', 'Admin Block', 'WARDEN');

-- 5. Insert Complaints
INSERT INTO complaint (complaint_id, user_id, category_id, department_id, description, status, priority, date_registered, date_resolved) VALUES
(1, 1, 4, 2, 'Wi-Fi disconnecting repeatedly in hostel block A.', 'Resolved', 'High', '2024-01-05 10:00:00', '2024-01-08 14:30:00'),
(2, 2, 2, 8, 'Water leakage near washroom pipeline on second floor.', 'Resolved', 'Medium', '2024-01-18 09:15:00', '2024-01-24 16:00:00'),
(3, 3, 3, 7, 'Power outage in study hall during evening hours.', 'In Progress', 'High', '2024-02-02 18:30:00', NULL),
(4, 4, 5, 3, 'Corridor cleaning not completed for three days.', 'Resolved', 'Low', '2024-02-12 11:20:00', '2024-02-13 17:00:00'),
(5, 5, 8, 5, 'Dinner quality was poor and food was served cold.', 'Pending', 'Medium', '2024-02-25 21:00:00', NULL),
(6, 6, 9, 4, 'Unknown person entered hostel corridor without entry check.', 'In Progress', 'High', '2024-03-03 22:45:00', NULL),
(7, 7, 7, 1, 'Study table leg broken in room B-214.', 'Resolved', 'Low', '2024-03-15 14:10:00', '2024-03-18 11:00:00'),
(8, 8, 1, 6, 'Loud music from common room during examination week.', 'Resolved', 'Medium', '2024-03-28 23:00:00', '2024-04-03 10:00:00'),
(9, 9, 6, 3, 'Cockroaches found in common pantry area.', 'Pending', 'High', '2024-04-07 16:40:00', NULL),
(10, 10, 10, 6, 'Request for updated hostel mess notice board timetable.', 'Resolved', 'Low', '2024-04-20 12:00:00', '2024-04-22 15:30:00'),
(11, 11, 4, 2, 'LAN port not functioning in room C-109.', 'Resolved', 'Medium', '2024-05-04 15:00:00', '2024-05-10 18:00:00'),
(12, 12, 3, 7, 'Frequent voltage fluctuation causing geyser tripping.', 'Resolved', 'High', '2024-05-17 07:30:00', '2024-06-01 12:00:00'),
(13, 13, 2, 8, 'Tap continuously running in ground floor washroom.', 'In Progress', 'Medium', '2024-06-02 08:20:00', NULL),
(14, 14, 5, 3, 'Overflowing dustbins near hostel entrance gate.', 'Resolved', 'Low', '2024-06-16 10:15:00', '2024-06-17 11:45:00'),
(15, 15, 8, 5, 'Breakfast items not available by serving time.', 'Pending', 'Medium', '2024-06-30 08:45:00', NULL);

-- 6. Insert Assignments
INSERT INTO complaint_assignment (assignment_id, complaint_id, staff_id, assigned_date) VALUES
(1, 1, 3, '2024-01-06 10:00:00'),
(2, 2, 16, '2024-01-19 11:00:00'),
(3, 3, 14, '2024-02-04 09:30:00'),
(4, 4, 6, '2024-02-12 12:00:00'),
(5, 6, 8, '2024-03-04 10:15:00'),
(6, 7, 1, '2024-03-16 11:30:00'),
(7, 8, 12, '2024-03-29 09:00:00'),
(8, 10, 13, '2024-04-21 14:00:00'),
(9, 11, 4, '2024-05-05 16:00:00'),
(10, 12, 15, '2024-05-18 10:00:00'),
(11, 13, 17, '2024-06-03 11:00:00'),
(12, 14, 7, '2024-06-16 10:30:00');

-- 7. Insert Status History
INSERT INTO complaint_status_history (history_id, complaint_id, status, updated_on) VALUES
(1, 1, 'Pending', '2024-01-05 10:00:00'),
(2, 1, 'In Progress', '2024-01-06 10:00:00'),
(3, 1, 'Resolved', '2024-01-08 14:30:00'),
(4, 2, 'Pending', '2024-01-18 09:15:00'),
(5, 2, 'Resolved', '2024-01-24 16:00:00'),
(6, 3, 'Pending', '2024-02-02 18:30:00'),
(7, 3, 'In Progress', '2024-02-04 09:30:00'),
(8, 4, 'Pending', '2024-02-12 11:20:00'),
(9, 4, 'Resolved', '2024-02-13 17:00:00'),
(10, 5, 'Pending', '2024-02-25 21:00:00'),
(11, 6, 'Pending', '2024-03-03 22:45:00'),
(12, 6, 'In Progress', '2024-03-04 10:15:00');

-- 8. Insert Feedback
INSERT INTO complaint_feedback (feedback_id, complaint_id, rating, comments, feedback_date) VALUES
(1, 1, 5, 'Wi-Fi router issue resolved quickly and internet is very stable now.', '2024-01-09 11:00:00'),
(2, 2, 4, 'Leakage stopped completely after plumbing pipe repair.', '2024-01-25 09:30:00'),
(3, 4, 5, 'Corridor was thoroughly cleaned within an hour of assignment.', '2024-02-14 10:00:00'),
(4, 7, 3, 'Table repaired satisfactorily, thanks for prompt action.', '2024-03-19 15:00:00'),
(5, 8, 4, 'Warden intervened immediately and noise was stopped.', '2024-04-04 12:00:00');

-- 9. Insert Escalations
INSERT INTO complaint_escalation (escalation_id, complaint_id, escalation_level, escalation_date) VALUES
(1, 5, 1, '2024-02-28 10:00:00'),
(2, 9, 2, '2024-04-10 14:00:00');

-- Update auto increment counters
ALTER TABLE users AUTO_INCREMENT = 100;
ALTER TABLE department AUTO_INCREMENT = 10;
ALTER TABLE staff AUTO_INCREMENT = 30;
ALTER TABLE complaint_category AUTO_INCREMENT = 20;
ALTER TABLE complaint AUTO_INCREMENT = 100;
ALTER TABLE complaint_status_history AUTO_INCREMENT = 100;
ALTER TABLE complaint_assignment AUTO_INCREMENT = 100;
ALTER TABLE complaint_feedback AUTO_INCREMENT = 100;
ALTER TABLE complaint_attachment AUTO_INCREMENT = 100;
ALTER TABLE complaint_escalation AUTO_INCREMENT = 100;
