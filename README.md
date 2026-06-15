# StudentBridge

StudentBridge is a university capstone web platform that helps international students in South Korea find student-friendly part-time jobs and helps local employers reach student workers.

The current implementation uses a static HTML/CSS/JavaScript frontend, Java Servlets running on Apache Tomcat, and a MySQL database accessed through JDBC.

## Final Portfolio

The Spring 2026 final portfolio is available at [`portfolio/README.md`](portfolio/README.md). It includes the final MVP scope, semester journey, QA report, AI/code ownership notes, presentation package, and individual portfolio pages.

## Current Status

| Area | Status | Notes |
|---|---|---|
| Homepage | Working | `index.html` presents the product, navigation, and calls to action. |
| Job search UI | Working as frontend demo | `frontend/jobsearch.html` filters seeded in-browser job data. |
| Registration | Partially working | `RegisterServlet` inserts users into MySQL when Tomcat and the database are configured. |
| Login | Partially working | `LoginServlet` checks MySQL credentials and creates a servlet session. |
| Job application | Not connected yet | The current job cards show an alert instead of storing applications. |
| Live deployment | Needs verification | Local Tomcat is the reliable demo path for now. |

## Demo Links

| Demo type | Link | Status |
|---|---|---|
| Local Tomcat demo | <http://localhost:8080/StudentBridge/> | Use for the midterm live demo after running setup. |
| Static GitHub Pages demo | <https://capstonedesign-spring2026-ulsancollege.github.io/StudentBridge/> | Verify before using in a presentation. |
real page link  | https://studentbridge-6jn2.onrender.com
| Demo video | Not available in this repo | Use screenshots and local/Render demo as backup evidence. |
| Screenshots | [demo-screenshots](demo-screenshots) | Final backup screenshots for homepage, login, register, jobs, employer dashboard, post job, and student profile. |

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | HTML, CSS, JavaScript |
| Backend | Java Servlets with Jakarta Servlet API |
| Server | Apache Tomcat 10.1.x |
| Database | MySQL with JDBC |
| Driver/dependencies | `server/mysql-connector-j-9.3.0.jar`, `server/servlet-api.jar` |

## Core Demo Flow

1. Open the StudentBridge homepage.
2. Navigate to the job search page.
3. Search/filter part-time jobs by title, location, category, or type.
4. Open registration and create a test user after Tomcat/MySQL setup.
5. Log in with the test user and return to the homepage.
6. Explain that application storage is the next backend milestone.

## Documentation Index

| Document | Purpose |
|---|---|
| [Project Overview](docs/PROJECT_OVERVIEW.md) | Problem, users, solution, MVP scope, and evidence links. |
| [Architecture](docs/ARCHITECTURE.md) | Frontend/backend/database structure and key risks. |
| [Setup Guide](docs/SETUP_GUIDE.md) | Java Servlet, Tomcat, and MySQL setup steps. |
| [User Stories](docs/USER_STORIES.md) | MVP stories, acceptance criteria, and status. |
| [Sprint 2](docs/SPRINT_2.md) | Midterm sprint goal, evidence needs, and blockers. |
| [Testing Notes](docs/TESTING_NOTES.md) | Manual test plan and current test status. |
| [Demo Plan](docs/DEMO_PLAN.md) | Live demo script, backup plan, and QR support. |
| [Midterm Pitch Notes](docs/MIDTERM_PITCH_NOTES.md) | Investor-style pitch outline and ask. |
| [Roadmap](docs/ROADMAP.md) | Path from current MVP to final presentation. |
| [Team Roles](docs/TEAM_ROLES.md) | Team ownership, weekly role rotation, and responsibilities. |
| [AI Code Ownership Audit](docs/AI_CODE_OWNERSHIP_AUDIT.md) | AI usage, code ownership, risks, and stabilization goals. |
| [Design Package](docs/DESIGN_PACKAGE.md) | Paper.design UI plan, Canva visual package, and brochure content. |
| [Docs Index](docs/README.md) | Full documentation map for the repository. |

## Known Issues

- Database credentials are currently hard-coded in `Backend/DBConnection.java`; replace with safer local configuration before public deployment.
- Passwords are stored as plain text; hashing is required before real users.
- `frontend/register.html` and `frontend/login.html` use absolute form actions that may need adjustment under the Tomcat `/StudentBridge` context.
- Job listings are currently seeded in frontend JavaScript, not loaded from MySQL.
- Application submission is not implemented yet.
- Evidence links in docs must be replaced with real GitHub issue, PR, screenshot, and demo links before submission.

## Quick Start

Use the full [Setup Guide](docs/SETUP_GUIDE.md). The short version is:

```bash
./deploy.sh
```

Then start Tomcat and open:

```text
http://localhost:8080/StudentBridge/
```

## Evidence Policy

This project follows the class rule: if it is not linked, it did not happen. Add real links for GitHub issues, pull requests, board screenshots, demo videos, screenshots, and sprint packets before using the documentation for grading.
