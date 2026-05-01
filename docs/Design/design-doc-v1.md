# Design Doc v1

## 1) Project purpose
**One-sentence summary:**
StudentBridge is a web platform that connects international students with local part-time job opportunities and allows business owners to hire and manage workers easily.

**Why this matters:**
Students in foreign countries struggle to find trusted part-time jobs, while business owners need reliable workers. This system bridges the gap between them.

---

## 2) Target users
- **Primary user:**
  International Students

- **Secondary user:**
  Business Owner / Employer

- **What they need:**

  **Students need:**
  - Search nearby part-time jobs
  - Apply easily
  - Track application status
  - View job details (salary, time, location)

  **Business Owners need:**
  - Post job openings
  - View student applications
  - Select and manage employees
  - Track worker performance

---

## 3) MVP scope

### In scope now
- User registration & login (Student / Owner)
- Job posting system (Owner)
- Job search & apply (Student)
- Dashboard for both users
- Application tracking system
- Basic notification system

### Out of scope for now
- Online payments
- AI job recommendations
- Mobile app
- Multi-language support

---

## 4) Core user flow

1. Student signs up and logs in
2. Student searches for part-time jobs
3. Student applies for a job
4. Business owner reviews applications
5. Owner selects student and assigns work

---

## 5) Architecture (C4-lite)

### Context view
- **users:**
  - Students
  - Business Owners

- **main system:**
  - StudentBridge Web Application

- **outside systems/services:**
  - Email notification system
  - Location/map service (future)

---

### Container view
- **frontend:**
  - HTML, CSS, JavaScript
  - User interface for students and owners

- **backend:**
  - Java Servlets on Apache Tomcat
  - Handles register/login now and future job/application logic

- **database:**
  - MySQL
  - Stores users, jobs, applications

- **other service:**
  - Notification service

---

### Diagram or image

[ Student ] --->

[ StudentBridge Web App ] ---> [ Backend ] ---> [ Database ]
/
[ Business Owner ]


---

## 6) Wireframes

### Screen 1 - Home Page
- **purpose:**
  Entry point for users

- **main action:**
  Login / Register / Browse Jobs

---

### Screen 2 - Job Dashboard
- **purpose:**
  Show job listings

- **main action:**
  Apply for jobs (Student) / Post jobs (Owner)

---

### Screen 3 - Application Detail
- **purpose:**
  Show job/application details

- **main action:**
  Accept / Reject application

---

## 7) Sprint 1 plan

### Top goals
1. Build login & register system
2. Create job listing page
3. Implement job apply feature

### Initial issues / work chunks
- Issue: Design homepage UI
- Issue: Create login system
- Issue: Build job posting feature
- Issue: Build job apply button logic

---

## 8) Risks / assumptions

### Risks
- Students may not trust platform initially
- Limited real job data

### Assumptions
- Students have internet access
- Business owners are willing to post jobs

---

## 9) Scope cut list
- Notification system
- Advanced filtering

---

## 10) Evidence links
- Board link:
- Sprint Packet link:
- Related issues:
- Related PRs (later):

---

## 11) Quality check
- [x] project purpose is clear
- [x] target users are specific
- [x] MVP scope is realistic
- [x] architecture is included
- [x] 3 wireframes are included
- [x] Sprint 1 goals are small enough to demo
- [x] risks are honest
- [ ] evidence links are included

🎨 💻 Simple UI Design (HTML + CSS)
✅ index.html

<!DOCTYPE html>
<html>
<head>
    <title>StudentBridge</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>

<header>
    <h1>StudentBridge</h1>
    <nav>
        <a href="#">Home</a>
        <a href="#">Jobs</a>
        <a href="#">Login</a>
    </nav>
</header>

<section class="hero">
    <h2>Find Part-Time Jobs Easily</h2>
    <p>Connecting Students with Local Businesses</p>
    <button>Browse Jobs</button>
</section>

<section class="jobs">
    <h2>Available Jobs</h2>

    <div class="job-card">
        <h3>Restaurant Helper</h3>
        <p>Location: Ulsan</p>
        <p>Salary: ₩10,000/hour</p>
        <button>Apply</button>
    </div>

    <div class="job-card">
        <h3>Convenience Store Staff</h3>
        <p>Location: Busan</p>
        <p>Salary: ₩9,800/hour</p>
        <button>Apply</button>
    </div>

</section>

<footer>
    <p>© 2026 StudentBridge</p>
</footer>

</body>
</html>

style.css
body {
    font-family: Arial;
    margin: 0;
    background: #f4f4f4;
}

header {
    background: #2c3e50;
    color: white;
    padding: 15px;
    display: flex;
    justify-content: space-between;
}

nav a {
    color: white;
    margin: 10px;
    text-decoration: none;
}

.hero {
    text-align: center;
    padding: 50px;
    background: #3498db;
    color: white;
}

.hero button {
    padding: 10px 20px;
    background: white;
    border: none;
    cursor: pointer;
}

.jobs {
    padding: 20px;
}

.job-card {
    background: white;
    padding: 15px;
    margin: 10px 0;
    border-radius: 8px;
}

.job-card button {
    background: #27ae60;
    color: white;
    border: none;
    padding: 8px;
    cursor: pointer;
}

footer {
    text-align: center;
    padding: 10px;
    background: #2c3e50;
    color: white;
}
