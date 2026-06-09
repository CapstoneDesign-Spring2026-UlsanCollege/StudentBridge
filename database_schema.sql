CREATE DATABASE IF NOT EXISTS studentbridge
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE studentbridge;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  phone VARCHAR(40),
  password VARCHAR(255) NOT NULL,
  account_type VARCHAR(20) NOT NULL DEFAULT 'Student',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS student_profiles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_email VARCHAR(255) NOT NULL,
  university_name VARCHAR(255),
  major VARCHAR(255),
  student_id VARCHAR(100),
  preferred_job_category VARCHAR(100),
  available_working_time VARCHAR(100),
  korean_language_level VARCHAR(100),
  cv_link VARCHAR(500),
  address VARCHAR(255),
  latitude DECIMAL(10, 7),
  longitude DECIMAL(10, 7),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_student_profiles_coordinates (latitude, longitude),
  INDEX idx_student_profiles_user_email (user_email)
);

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

CREATE TABLE IF NOT EXISTS jobs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(150) NOT NULL,
  company VARCHAR(150) NOT NULL,
  location VARCHAR(100) NOT NULL,
  category VARCHAR(100) NOT NULL,
  type VARCHAR(50) NOT NULL,
  salary VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  employer_email VARCHAR(255),
  working_hours VARCHAR(150),
  requirements TEXT,
  contact_email VARCHAR(255),
  contact_phone VARCHAR(50),
  company_details TEXT,
  application_deadline DATE,
  logo_url VARCHAR(500),
  address VARCHAR(255),
  latitude DECIMAL(10, 7),
  longitude DECIMAL(10, 7),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_jobs_employer_email (employer_email),
  INDEX idx_jobs_coordinates (latitude, longitude),
  INDEX idx_jobs_location (location),
  INDEX idx_jobs_category (category),
  INDEX idx_jobs_type (type)
);

CREATE TABLE IF NOT EXISTS applications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  job_id INT NOT NULL,
  student_email VARCHAR(255) NOT NULL,
  cv_link VARCHAR(500) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_application (job_id, student_email),
  INDEX idx_applications_job_id (job_id),
  INDEX idx_applications_student_email (student_email)
);

CREATE TABLE IF NOT EXISTS notifications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  recipient_email VARCHAR(255) NOT NULL,
  recipient_account_type VARCHAR(20) NOT NULL,
  type VARCHAR(60) NOT NULL,
  title VARCHAR(160) NOT NULL,
  message VARCHAR(500) NOT NULL,
  target_url VARCHAR(500),
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  related_job_id INT,
  related_application_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  read_at TIMESTAMP NULL,
  INDEX idx_notifications_recipient (recipient_email, recipient_account_type, is_read, created_at),
  INDEX idx_notifications_related_job (related_job_id),
  INDEX idx_notifications_related_application (related_application_id)
);

CREATE TABLE IF NOT EXISTS conversations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  application_id INT NOT NULL,
  job_id INT NOT NULL,
  employer_email VARCHAR(255) NOT NULL,
  student_email VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_message_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_conversation_application (application_id),
  INDEX idx_conversations_employer (employer_email, last_message_at),
  INDEX idx_conversations_student (student_email, last_message_at),
  INDEX idx_conversations_job (job_id)
);

CREATE TABLE IF NOT EXISTS messages (
  id INT AUTO_INCREMENT PRIMARY KEY,
  conversation_id INT NOT NULL,
  sender_email VARCHAR(255) NOT NULL,
  sender_account_type VARCHAR(20) NOT NULL,
  body TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  read_at TIMESTAMP NULL,
  INDEX idx_messages_conversation (conversation_id, created_at),
  INDEX idx_messages_unread (conversation_id, sender_email, read_at)
);
