# Sprint 2 - Midterm Preparation

Last updated: 2026-05-01

## 0) Team + Sprint

| Item | Details |
|---|---|
| Team | StudentBridge |
| Sprint | Sprint 2 |
| Sprint phase | Midterm preparation and MVP hardening |
| Sprint focus | Make the project explainable, demoable, and supported by evidence |
| Sprint packet rule | Weekly packet links must be added for Week 6, Week 7, and Week 8 |

## 1) Sprint Goal

Deliver a believable midterm MVP flow for StudentBridge:

1. Student opens the homepage.
2. Student searches student-friendly jobs.
3. Student registers or logs in.
4. Team explains the planned application flow and shows progress toward database-backed storage.

The goal is not to claim the final product is complete. The goal is to prove that the team has a real product direction, working pieces, honest blockers, and a realistic path to the final presentation.

## 2) Midterm-Critical Feature

| Feature | Why it matters | Current status |
|---|---|---|
| Job search + account flow | It is the clearest student value proposition and easiest investor-style demo flow. | Job search works in frontend; registration/login partially work through Tomcat/MySQL. |

## 3) Definition of Done

Sprint 2 is done when:

- The homepage, job search page, register page, and login page open from the same Tomcat deployment.
- Job search filtering works without console errors.
- Register/login are tested against a local MySQL database.
- Current limitations are documented in `docs/TESTING_NOTES.md`.
- Demo plan and backup demo are ready.
- Every team member has a visible responsibility and evidence receipt.
- Sprint evidence links are added to this file and to weekly packet issues.

## 4) Weekly Focus

| Week | Focus | Required output | Evidence placeholder |
|---|---|---|---|
| Week 6 | Clean up scope, choose midterm flow, define tests | Sprint 2 plan, testing plan, known blockers | `[TODO: Week 6 packet link]` |
| Week 7 | Build, test, rehearse | Improved demo, draft slides/brochure, backup demo draft | `[TODO: Week 7 packet link]` |
| Week 8 | Deliver pitch | Pitch, demo, brochure/QR, evidence links | `[TODO: Week 8 packet link]` |

## 5) What Shipped / Exists Now

| Item | Owner | Evidence | Notes |
|---|---|---|---|
| Homepage UI | Mezbah / Team | `index.html` | Working as frontend entry point. |
| Job search UI and filters | Sami / Ali / Team | `frontend/jobsearch.html` | Working with seeded JavaScript data. |
| Register servlet | Mezbah | `Backend/RegisterServlet.java` | Needs Tomcat/MySQL test evidence. |
| Login servlet | Ali / Mezbah | `Backend/LoginServlet.java` | Needs session and error handling verification. |
| MySQL connection helper | Mezbah | `Backend/DBConnection.java` | Works only when local credentials and schema match. |
| Documentation cleanup | Guramg / Team | `docs/` | Evidence links still need real issue/PR URLs. |

## 6) Bugs / Broken Things

| Bug / problem | Severity | Owner | Evidence / issue link | Next step |
|---|---|---|---|---|
| Passwords are stored as plain text | P1 | Backend owner | `[TODO: issue link]` | Add password hashing before real users. |
| Database credentials are hard-coded | P1 | Backend owner | `[TODO: issue link]` | Move to local config or document per-machine setup. |
| Register/login form action paths may miss the `/StudentBridge` Tomcat context | P1 | Frontend/backend owner | `[TODO: issue link]` | Test deployed forms and update paths if needed. |
| Job application is not connected | P1 | Backend + frontend owners | `[TODO: issue link]` | Add application form, servlet, and MySQL table. |
| Job listings are seeded in frontend code | P2 | Backend owner | `[TODO: issue link]` | Move jobs into MySQL after midterm-critical flow is stable. |

## 7) Sprint Evidence

Do not invent links. Replace placeholders only with real evidence.

| Evidence type | Link |
|---|---|
| GitHub project board snapshot | `[TODO: board screenshot/link]` |
| Week 6 sprint packet | `[TODO: link]` |
| Week 7 sprint packet | `[TODO: link]` |
| Week 8 sprint packet | `[TODO: link]` |
| Demo video | `[TODO: link]` |
| Screenshot evidence | `[TODO: link]` |
| Pull requests merged | `[TODO: links]` |
| Issues closed | `[TODO: links]` |

Existing local supporting notes:

- `docs/Sprint/SPRINT_2.md`
- `docs/Sprint-packets/Weekly Sprint Packet-Week6.md`
- `docs/Sprint/Weekly Sprint Packet-Week7.md`
- `docs/Sprint/Weekly Sprint Packet-Week9.md`

## 8) Backup Demo Plan

If Tomcat or MySQL fails during the pitch:

1. Switch within 15 seconds.
2. Show the static homepage and job search page.
3. Play the backup demo video.
4. Show screenshots of register/login setup and database test.
5. Explain the blocker honestly and point to the issue/roadmap.

Backup evidence links:

- Demo video: `[TODO: backup video link]`
- Screenshots: `[TODO: screenshot link]`
- Issue tracking blocker: `[TODO: issue link]`

## 9) Plan for Next Week

1. Stabilize local Tomcat deployment and document exact setup.
2. Test register/login with a clean MySQL schema.
3. Add application table and simplest possible application submission flow.
4. Prepare brochure QR code destination.
5. Add real issue, PR, screenshot, and demo links.

## 10) Instructor Notes

The team should verify all evidence links before submission. This document is written to support the midterm pitch, but the grading evidence must come from real GitHub issues, PRs, screenshots, and sprint packets.
