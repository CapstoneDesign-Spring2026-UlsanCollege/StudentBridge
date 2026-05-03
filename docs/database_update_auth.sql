-- StudentBridge authentication/profile database update notes
-- ---------------------------------------------------------
-- Review your current schema before running these statements.
-- If a column already exists, do not run that ALTER TABLE line again.
-- These statements are written for MySQL.

-- 1) Existing users table updates
-- The original app used: users(name, email, phone, password)
-- The new registration flow adds account type support.

ALTER TABLE users
  ADD COLUMN account_type VARCHAR(20) NOT NULL DEFAULT 'Student';

-- Recommended constraints/indexes for login and duplicate prevention.
-- Run only if these indexes do not already exist.

ALTER TABLE users
  ADD UNIQUE KEY unique_users_email (email);

-- Future security improvement:
-- Rename password to password_hash after implementing password hashing.
-- Do NOT run this until Java code is updated to use hashed passwords.
--
-- ALTER TABLE users
--   CHANGE COLUMN password password_hash VARCHAR(255) NOT NULL;

-- 2) Optional student profile table
-- This stores student-only fields without making the users table too wide.

CREATE TABLE IF NOT EXISTS student_profiles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_email VARCHAR(255) NOT NULL,
  university_name VARCHAR(255),
  major VARCHAR(255),
  student_id VARCHAR(100),
  preferred_job_category VARCHAR(100),
  available_working_time VARCHAR(100),
  korean_language_level VARCHAR(100),
  address VARCHAR(255),
  latitude DECIMAL(10, 7),
  longitude DECIMAL(10, 7),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_student_profiles_coordinates (latitude, longitude),
  INDEX idx_student_profiles_user_email (user_email)
);

-- If student_profiles already exists, run these one time:
--
-- ALTER TABLE student_profiles ADD COLUMN address VARCHAR(255);
-- ALTER TABLE student_profiles ADD COLUMN latitude DECIMAL(10, 7);
-- ALTER TABLE student_profiles ADD COLUMN longitude DECIMAL(10, 7);
-- CREATE INDEX idx_student_profiles_coordinates ON student_profiles (latitude, longitude);

-- 3) Optional employer profile table
-- This stores employer-only business details.

CREATE TABLE IF NOT EXISTS employer_profiles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_email VARCHAR(255) NOT NULL,
  business_name VARCHAR(255),
  manager_name VARCHAR(255),
  business_location VARCHAR(255),
  business_type VARCHAR(100),
  job_posting_category VARCHAR(100),
  company_registration_number VARCHAR(100),
  company_description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_employer_profiles_user_email (user_email)
);

-- Optional foreign keys
-- Add these only after users.email is unique and all existing profile rows
-- match a valid users.email value.
--
-- ALTER TABLE student_profiles
--   ADD CONSTRAINT fk_student_profiles_user_email
--   FOREIGN KEY (user_email) REFERENCES users(email)
--   ON DELETE CASCADE;
--
-- ALTER TABLE employer_profiles
--   ADD CONSTRAINT fk_employer_profiles_user_email
--   FOREIGN KEY (user_email) REFERENCES users(email)
--   ON DELETE CASCADE;
