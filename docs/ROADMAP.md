# StudentBridge Roadmap

Last updated: 2026-05-01

## Roadmap Principle

StudentBridge should grow from one reliable student flow before adding larger employer, notification, or AI features. A small working system is better for the capstone than a large unfinished feature list.

## Current MVP Status

| Area | Status |
|---|---|
| Homepage | Working |
| Job search filters | Working as frontend demo |
| Register/login UI | Working |
| Register/login backend | Partially working; needs setup verification |
| MySQL schema | Basic `users` table documented |
| Job applications | Not connected |
| Employer posting | Not connected |
| Testing evidence | Needs real linked screenshots/packets |

## Roadmap To Final Presentation

| Phase | Goal | Deliverables | Evidence required |
|---|---|---|---|
| Midterm cleanup | Make pitch and demo credible | Complete docs, demo script, backup video, QR destination | Sprint packet, screenshots, demo video |
| Sprint after midterm | Stabilize account flow | Reliable Tomcat/MySQL register/login, documented setup | PR, test notes, DB screenshot |
| Application sprint | Complete student application flow | Application form, servlet, `applications` table, confirmation page/message | PR, issue, demo clip |
| Job data sprint | Move jobs toward database | `jobs` table, seed data, basic list loading strategy | PR, schema notes, screenshots |
| Employer sprint | Add smallest employer posting flow | Employer can create/view a job post | PR, test notes, demo |
| Final hardening | Prepare reliable final demo | Bug fixes, password hashing or documented demo-only limitation, backup plan | QA checklist, final demo video |
| Final handoff | Make repo maintainable | Updated README, setup, architecture, testing notes, AI audit | Final docs PR and release notes |

## Final MVP Target

By the final presentation, StudentBridge should demonstrate:

1. Student can search jobs.
2. Student can register and log in.
3. Student can apply to a job.
4. Application is stored in MySQL.
5. Team can show evidence, tests, and known limitations.

## Stretch Goals

Only attempt these after the final MVP target works:

- Employer dashboard for reviewing applications.
- Korean/English language toggle.
- Resume upload.
- Email notification.
- Location/map support.
- AI job recommendation.
- Public cloud deployment.

## Risk-Based Priorities

| Priority | Item | Reason |
|---|---|---|
| 1 | Reliable local setup | Demo fails if Tomcat/MySQL setup is unclear. |
| 2 | Application storage | This completes the main student value flow. |
| 3 | Security cleanup | Password handling and credentials must be improved before real users. |
| 4 | Evidence links | Class grading depends on linked proof. |
| 5 | Employer features | Important, but can be smaller for final MVP if time is limited. |

## Decision Log Placeholders

| Decision | Status | Link |
|---|---|---|
| Keep Java Servlet + Tomcat + MySQL stack | Confirmed | `[TODO: decision issue link if available]` |
| Use local Tomcat as primary midterm demo | Proposed | `[TODO: issue/link]` |
| Use backup video if live demo fails | Proposed | `[TODO: demo video link]` |
| Add application flow before employer dashboard | Proposed | `[TODO: roadmap issue link]` |

## Dates To Fill In

| Milestone | Date |
|---|---|
| Midterm pitch | `[TODO: exact date]` |
| Final presentation | `[TODO: exact date]` |
| Final documentation freeze | `[TODO: exact date]` |
