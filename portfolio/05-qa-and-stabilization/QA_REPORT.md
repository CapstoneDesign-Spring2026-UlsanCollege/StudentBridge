# QA Report

## QA Goal

Prove that the final MVP can be demonstrated clearly, and document the exact limits that remain before StudentBridge could be used by real students and employers.

## Test Environment

| Item | Value |
|---|---|
| Browser | Chrome recommended |
| Backend | Java Servlets |
| Server | Apache Tomcat locally, Render for hosted demo |
| Database | MySQL |
| Local URL | <http://localhost:8080/StudentBridge/> |
| Hosted URL | <https://studentbridge-6jn2.onrender.com> |

## Core Flow Checklist

| ID | Flow | Expected result | Status | Evidence |
|---|---|---|---|---|
| QA-01 | Open homepage | Homepage loads with navigation and calls to action. | Ready for demo | [Screenshot](../../demo-screenshots/01-home.png) |
| QA-02 | Open login | Login page loads. | Ready for demo | [Screenshot](../../demo-screenshots/02-login.png) |
| QA-03 | Open register | Student registration page loads. | Ready for demo | [Screenshot](../../demo-screenshots/03-register-student.png) |
| QA-04 | Open jobs | Job search page loads. | Ready for demo | [Screenshot](../../demo-screenshots/04-jobsearch.png) |
| QA-05 | Open employer dashboard | Employer dashboard page is available. | Ready for demo with login/session setup | [Screenshot](../../demo-screenshots/05-employer-dashboard.png) |
| QA-06 | Open post job | Post-job form is available. | Ready for demo with employer login/session setup | [Screenshot](../../demo-screenshots/06-post-job.png) |
| QA-07 | Open student profile | Student profile page is available. | Ready for demo with student login/session setup | [Screenshot](../../demo-screenshots/07-student-profile.png) |
| QA-08 | Compile backend | Servlet files should compile with provided jars. | Needs final rerun before presentation | [Setup guide](../04-final-product/SETUP_AND_RUN_GUIDE.md) |
| QA-09 | Render deployment | Hosted app should open if environment variables are configured. | Needs final live check | [Deployment guide](../../docs/DEPLOY_RENDER.md) |
| QA-10 | Application submission | Student applies to job and app stores result. | Needs final live rerun | [Apply servlet](../../Backend/ApplyServlet.java) |

## Existing Validation Evidence

The repository includes a Sprint Bugfix Validation section in [../../docs/TESTING_NOTES.md](../../docs/TESTING_NOTES.md) dated 2026-05-21. It records:

- Servlet compile check passed.
- Shared frontend scripts syntax check passed.
- Static browser smoke tests passed without console errors.
- Docker build was not run because Docker was unavailable locally.

## Manual Tests To Run Before Presentation

| Test | Command or action |
|---|---|
| Backend compile | `javac -cp "server/mysql-connector-j-9.3.0.jar:server/servlet-api.jar" -d bin Backend/*.java` |
| Frontend scripts | `node --check frontend/script.js` |
| Homepage | Open hosted app and local Tomcat app. |
| Jobs | Search and filter jobs. |
| Register/login | Create a demo user and log in. |
| Employer post job | Log in as employer and submit one demo job. |
| MySQL verification | `SELECT id, title, company FROM jobs ORDER BY id DESC LIMIT 5;` |

## Browser Or Device Checks

- Desktop Chrome is the main presentation target.
- The frontend should remain usable on narrower screens because the shared nav and layout styles were polished during the final sprint.
- Mobile testing should be treated as a demo confidence check, not a full production certification.

## Accessibility And Security Basics

| Area | Status |
|---|---|
| Keyboard navigation | Basic browser behavior available, but full accessibility audit not complete. |
| Form labels | Main forms use visible labels/fields; needs final manual check. |
| Password security | Password hashing remains a known limitation before real users. |
| Secrets | `.env.example` exists; real credentials should stay out of screenshots and commits. |
| SQL safety | Servlet code uses prepared statements in key database flows. |

## Deployment Reliability

Render deployment exists, but local Tomcat should remain the backup. The demo team should test both paths on presentation morning.
