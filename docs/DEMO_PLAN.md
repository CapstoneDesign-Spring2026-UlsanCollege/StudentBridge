# StudentBridge Demo Plan

Last updated: 2026-05-01

## Demo Goal

Show that StudentBridge is a real, working capstone product in progress: international students can discover student-friendly jobs and the team has started account/database integration using Java Servlets, Tomcat, and MySQL.

## Demo Environment

| Item | Value |
|---|---|
| Primary demo | Local Tomcat |
| URL | <http://localhost:8080/StudentBridge/> |
| Backend | Java Servlets deployed under Tomcat |
| Database | Local MySQL database `studentbridge` |
| Browser | Chrome or Edge recommended |
| Backup | Demo video + screenshots + static frontend |

## 3-Bullet Demo Script

1. Show the problem and product entry point on the homepage.
2. Search and filter jobs on the Jobs page.
3. Show register/login flow and explain the next step: database-backed job applications.

## Detailed Live Demo Flow

| Step | Action | What to say | Expected result |
|---|---|---|---|
| 1 | Open local URL | StudentBridge focuses on international students looking for part-time jobs in Korea. | Homepage loads. |
| 2 | Click Jobs | The platform makes job discovery easier by filtering jobs by student needs. | Job search page opens. |
| 3 | Filter by location/category/type | A student can narrow opportunities quickly. | Job cards update. |
| 4 | Click Register | Students and employers can create accounts. | Register page opens. |
| 5 | Submit test account | Java Servlet receives form and stores user in MySQL. | Redirects to login if database is ready. |
| 6 | Log in | Login servlet checks MySQL and starts a session. | Redirects to homepage. |
| 7 | Click Apply on job card | Application storage is the next planned backend feature. | Placeholder alert appears. |

## Demo Data

Use only test data.

| Field | Example |
|---|---|
| Name | Demo Student |
| Email | demo.student@example.com |
| Phone | 010-0000-0000 |
| Password | demo1234 |
| Account type | Student |

## Backup Demo Plan

If the live demo fails or stalls, switch within 15 seconds.

| Failure | Backup action |
|---|---|
| Tomcat does not start | Show demo video from local file or uploaded link. |
| MySQL connection fails | Show static homepage/job search and explain DB issue using testing notes. |
| Register/login fails | Show screenshots of successful setup and point to blocker issue. |
| Internet fails | Use local screenshots/video and local repository files. |

Backup evidence placeholders:

- Backup video: `[TODO: add link]`
- Screenshot folder: `[TODO: add link]`
- Tomcat terminal screenshot: `[TODO: add link]`
- MySQL table screenshot: `[TODO: add link]`
- Blocker issue: `[TODO: add link]`

## Brochure / QR Code Support

The QR code should point to one stable destination:

| QR destination option | Recommendation |
|---|---|
| Live demo URL | Use only if verified before presentation. |
| GitHub repo | Safe for evidence, but less friendly for investors. |
| Demo landing page or video | Best backup if live deployment is not stable. |
| Brochure PDF | Useful for audience handout. |

Final QR link: `[TODO: add final QR destination]`

## Roles During Demo

| Role | Responsibility | Owner |
|---|---|---|
| Demo Driver | Controls browser and switches to backup if needed. | `[TODO: assign]` |
| Narrator | Explains problem, user, and solution. | `[TODO: assign]` |
| Technical Support | Starts Tomcat/MySQL and handles errors. | `[TODO: assign]` |
| Evidence Lead | Shows repo links, sprint packet, and screenshots. | `[TODO: assign]` |

## Pre-Demo Checklist

- [ ] Tomcat starts cleanly.
- [ ] MySQL service is running.
- [ ] `studentbridge` database exists.
- [ ] `users` table exists.
- [ ] `./deploy.sh` completes.
- [ ] Homepage opens from local Tomcat URL.
- [ ] Job search filters work.
- [ ] Register/login tested with demo account.
- [ ] Backup video opens without internet.
- [ ] QR code destination tested on a phone.
