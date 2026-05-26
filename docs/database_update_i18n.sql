-- StudentBridge i18n database update
-- Run this on the same MySQL database used by Tomcat/Render/Railway.
-- It stores translated job text so Google Translation API is not called repeatedly.

CREATE TABLE IF NOT EXISTS translation_cache (
  id INT AUTO_INCREMENT PRIMARY KEY,
  source_text_hash CHAR(64) NOT NULL,
  source_text TEXT NOT NULL,
  source_lang VARCHAR(10) NOT NULL DEFAULT 'auto',
  target_lang VARCHAR(10) NOT NULL,
  translated_text MEDIUMTEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY ux_translation_cache (source_text_hash, source_lang, target_lang),
  INDEX idx_translation_cache_target_lang (target_lang)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
