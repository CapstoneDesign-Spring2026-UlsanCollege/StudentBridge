# StudentBridge Project Overview

Last updated: 2026-05-01

## Team Information

| Item | Details |
|---|---|
| Team name | StudentBridge |
| Repository | <https://github.com/CapstoneDesign-Spring2026-UlsanCollege/StudentBridge> |
| Project type | Capstone Design web platform |
| Primary stack | HTML/CSS/JavaScript, Java Servlets, Apache Tomcat, MySQL |

## Problem Statement

International students in South Korea often need part-time jobs, but the search process is difficult. Job information is scattered across general job boards, social media, local signs, and word of mouth. Language barriers, unclear job requirements, visa concerns, and lack of local work experience make it harder for students to know which jobs are realistic and safe to apply for.

Local employers also lose time when they want student workers but do not have a simple way to reach international students near universities.

## Target Users

| User group | Main need |
|---|---|
| International students in South Korea | Find student-friendly part-time jobs by city, job type, and language expectations. |
| Local employers | Share job opportunities with motivated student workers. |
| University support staff | Recommend a clearer job-search path for international students. |

## Solution Overview

StudentBridge is a web platform focused on part-time job discovery for international students. The current MVP gives students a clear homepage, a searchable job board, and account registration/login through Java Servlets and MySQL. The final version should connect job listings and applications to the database so a student can complete the job-search flow from account creation to application submission.

## Core Features

| Feature | Current status | Notes |
|---|---|---|
| Homepage and navigation | Working | Product explanation, links to Jobs, Login, and Register. |
| Job search page | Working as frontend demo | Filters seeded job data by title, location, category, and job type. |
| Student/employer registration | Partially working | Servlet stores basic user data in MySQL when setup is complete. |
| Login | Partially working | Servlet validates email/password and creates a session. |
| Job application | Planned | Current Apply button is a placeholder alert. |
| Employer job posting | Planned | Needed for final MVP, not yet implemented. |

## Minimum Viable Product

The smallest believable MVP is:

1. A student opens StudentBridge.
2. The student searches available part-time jobs.
3. The student registers or logs in.
4. The student selects a job and submits an application.
5. The application is stored in MySQL and can be shown as confirmation.

The current project already demonstrates steps 1-3 partially and has a frontend-only job search. Steps 4-5 are the main remaining backend milestone.

## Current Working Demo Flow

1. Open `http://localhost:8080/StudentBridge/`.
2. Show the homepage and explain the problem.
3. Click Jobs and filter listings by Ulsan, Student, or Part-time.
4. Click Register and show the fields for student/employer account creation.
5. If MySQL and Tomcat are configured, submit a test account and show redirect to Login.
6. Log in with the test account.
7. Click Apply on a job and explain that application storage is the next sprint item.

## Technology Stack

| Layer | Technology | Current files |
|---|---|---|
| Frontend | HTML, CSS, JavaScript | `index.html`, `frontend/*.html`, `frontend/style.css`, `frontend/script.js` |
| Backend | Java Servlets | `Backend/RegisterServlet.java`, `Backend/LoginServlet.java` |
| Server | Apache Tomcat 10.1.x | `deploy.sh`, Tomcat webapps deployment |
| Database | MySQL | `studentbridge` database, `users` table |
| Database access | JDBC | `Backend/DBConnection.java`, MySQL Connector/J |

## Scope Rules

| In scope for final | Out of scope unless time remains |
|---|---|
| Reliable login/register flow | Mobile app |
| Searchable job list | AI job recommendations |
| Store jobs in MySQL | Payment system |
| Submit job applications | Full messaging system |
| Basic employer job posting | Advanced analytics |
| Clear demo, tests, and documentation | Production-scale deployment |

## Evidence Placeholders

Do not replace these with fake links. Add real links when they exist.

| Evidence needed | Link |
|---|---|
| GitHub project board | `[TODO: board link]` |
| Main MVP issue | `[TODO: issue link]` |
| Login/register PR | `[TODO: PR link]` |
| Job search PR | `[TODO: PR link]` |
| Demo video | `[TODO: demo video link]` |
| Screenshot album | `[TODO: screenshot link]` |
| Sprint 2 packet | `[TODO: Sprint 2 / Week 6-8 packet links]` |
| Live demo | `[TODO: verified live demo link]` |

## Assumptions To Verify

- The team member list and ownership map are based on existing repository documentation.
- The reliable demo target is local Tomcat unless the static GitHub Pages link is verified before presentation.
- The MySQL schema in the setup guide matches the current servlet code, but the final application flow will need additional tables.
