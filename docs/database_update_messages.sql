-- StudentBridge message system update
-- Run this on an existing MySQL database after applications are already enabled.
-- Select your database first if your SQL console is not already connected.
-- Local default is usually studentbridge. Railway may use railway.

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
