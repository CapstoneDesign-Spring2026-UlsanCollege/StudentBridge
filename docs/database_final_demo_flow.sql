-- StudentBridge final demo flow migration
-- Run this on the Railway/Render MySQL database before the final demo.
-- It is written to be safe if the columns or indexes already exist.

USE studentbridge;

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

SET @schema_name = DATABASE();

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE jobs ADD COLUMN employer_id INT NULL',
    'SELECT ''jobs.employer_id already exists'''
  )
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = @schema_name
    AND TABLE_NAME = 'jobs'
    AND COLUMN_NAME = 'employer_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'CREATE INDEX idx_jobs_employer_id ON jobs (employer_id)',
    'SELECT ''idx_jobs_employer_id already exists'''
  )
  FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = @schema_name
    AND TABLE_NAME = 'jobs'
    AND INDEX_NAME = 'idx_jobs_employer_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE applications ADD COLUMN student_id INT NULL',
    'SELECT ''applications.student_id already exists'''
  )
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = @schema_name
    AND TABLE_NAME = 'applications'
    AND COLUMN_NAME = 'student_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'CREATE INDEX idx_applications_student_id ON applications (student_id)',
    'SELECT ''idx_applications_student_id already exists'''
  )
  FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = @schema_name
    AND TABLE_NAME = 'applications'
    AND INDEX_NAME = 'idx_applications_student_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
