-- StudentBridge notification system update
-- Run this on the same MySQL database used by Tomcat/Render/Railway.

USE studentbridge;

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
