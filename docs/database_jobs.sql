-- StudentBridge jobs table
-- ------------------------
-- Run this SQL in the `studentbridge` MySQL database before using JobServlet.
-- The frontend calls ../JobServlet, and JobServlet reads from this table.

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
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_jobs_employer_email (employer_email),
  INDEX idx_jobs_location (location),
  INDEX idx_jobs_category (category),
  INDEX idx_jobs_type (type)
);

-- If you already created the jobs table before these optional employer fields
-- were added, run these ALTER TABLE lines one time.
--
-- ALTER TABLE jobs ADD COLUMN employer_email VARCHAR(255);
-- ALTER TABLE jobs ADD COLUMN working_hours VARCHAR(150);
-- ALTER TABLE jobs ADD COLUMN requirements TEXT;
-- ALTER TABLE jobs ADD COLUMN contact_email VARCHAR(255);
-- CREATE INDEX idx_jobs_employer_email ON jobs (employer_email);

-- Sample data for local testing/demo.
-- If you run this more than once, it will add duplicate demo rows.

INSERT INTO jobs (title, company, location, category, type, salary, description) VALUES
('Cafe Assistant', 'CoffeeDay', 'Ulsan', 'Student', 'Part-time', '₩10,000/hr', 'Serve drinks, prepare simple orders, and assist customers during busy hours.'),
('Restaurant Server', 'Seoul Kitchen', 'Seoul', 'Student', 'Part-time', '₩11,000/hr', 'Take orders, serve food, and help keep the restaurant clean and welcoming.'),
('English Tutor', 'Bright Academy', 'Busan', 'Student', 'Part-time', '₩20,000/hr', 'Teach basic English conversation to elementary and middle school students.'),
('Barista', 'Cafe Aroma', 'Daegu', 'Student', 'Part-time', '₩10,500/hr', 'Prepare coffee, manage orders, and support daily cafe operations.'),
('Store Staff', 'GS25', 'Ulsan', 'Student', 'Part-time', '₩9,860/hr', 'Handle cashier work, stock shelves, and support night-shift store operations.'),
('Frontend Developer', 'TechCorp', 'Seoul', 'IT', 'Internship', '₩25,000/hr', 'Help build responsive UI pages and support frontend bug fixes.'),
('Backend Developer', 'DevWorks', 'Busan', 'IT', 'Internship', '₩30,000/hr', 'Assist with API development, database queries, and backend testing.'),
('UI Designer', 'Creative Studio', 'Seoul', 'Design', 'Internship', '₩22,000/hr', 'Create wireframes, improve interface layouts, and support design reviews.'),
('Marketing Intern', 'MarketPro', 'Seoul', 'Marketing', 'Internship', '₩12,000/hr', 'Assist with social content, campaign research, and simple analytics reports.');
