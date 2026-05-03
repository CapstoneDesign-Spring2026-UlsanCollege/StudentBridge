# AI Code Ownership Audit

Last updated: 2026-05-01

## 1) Team + Project

- **Team:** StudentBridge
- **Project name:** StudentBridge
- **Current repo:** <https://github.com/CapstoneDesign-Spring2026-UlsanCollege/StudentBridge>
- **Current local demo:** <http://localhost:8080/StudentBridge/>
- **Static demo candidate:** <https://capstonedesign-spring2026-ulsancollege.github.io/StudentBridge/> `[TODO: verify before pitch]`
- **Date updated:** 2026-05-01

## 2) What Our App Currently Does

- Feature / flow 1: Shows a StudentBridge homepage with navigation and product explanation.
- Feature / flow 2: Lets users search and filter seeded job listings in the browser.
- Feature / flow 3: Provides register/login forms connected to Java Servlets when deployed through Tomcat.

### Current MVP flow

Our main user can:

1. Open StudentBridge from local Tomcat.
2. Browse and filter jobs.
3. Create an account if MySQL is configured.
4. Log in and return to the homepage.

The job application flow is not fully connected yet.

## 3) What Works Right Now

| Working item | Evidence link | Owner who can explain it |
|---|---|---|
| Homepage UI | `index.html` and `[TODO: screenshot link]` | Mezbah / Team |
| Job search filters | `frontend/jobsearch.html` and `[TODO: screenshot link]` | Sami / Ali |
| Register form UI | `frontend/register.html` and `[TODO: test evidence]` | Mezbah |
| Register servlet code | `Backend/RegisterServlet.java` and `[TODO: PR link]` | Mezbah |
| Login servlet code | `Backend/LoginServlet.java` and `[TODO: PR link]` | Ali / Mezbah |
| MySQL connection helper | `Backend/DBConnection.java` and `[TODO: DB test screenshot]` | Mezbah |

## 4) Code We Understand

| Code area | File / folder | What it does | Who can explain it? | Evidence |
|---|---|---|---|---|
| Homepage | `index.html` | Provides product landing page and navigation. | Mezbah / Team | `[TODO: screenshot or PR]` |
| Job search page | `frontend/jobsearch.html` | Filters seeded job cards by keyword, location, category, and type. | Sami / Ali Ashraf | `[TODO: screenshot or PR]` |
| Registration form | `frontend/register.html` | Collects name, email, phone, password, confirmation, and account type. | Mezbah | `[TODO: test evidence]` |
| Login form | `frontend/login.html` | Collects email/password and posts to servlet. | Mezbah / Ali Ashraf | `[TODO: test evidence]` |
| Register servlet | `Backend/RegisterServlet.java` | Checks password confirmation and inserts user into MySQL. | Mezbah | `[TODO: PR or walkthrough]` |
| Login servlet | `Backend/LoginServlet.java` | Validates credentials, creates session, redirects. | Ali Ashraf / Mezbah | `[TODO: PR or walkthrough]` |
| Database connection | `Backend/DBConnection.java` | Opens local MySQL connection using JDBC. | Mezbah | `[TODO: DB test screenshot]` |
| Deployment script | `deploy.sh` | Compiles servlets and copies files into Tomcat. | Team | `[TODO: terminal output screenshot]` |

## 5) Code We Do NOT Fully Understand Yet

| Code area | What is confusing? | Risk level | Owner | Next step |
|---|---|---|---|---|
| Tomcat servlet URL mapping | Form action paths may not include the `/StudentBridge` context. | High | Backend owner | Test deployed forms and document correct URLs. |
| Session handling | Login creates a session, but pages do not yet use session state. | High | Backend owner | Add simple logged-in display or protected page later. |
| Password security | Passwords are stored as plain text. | High | Backend owner | Add hashing and update tests. |
| Job application storage | Apply buttons are not connected to backend/database. | High | Team | Build simplest application servlet and table. |
| Role storage | `accountType` is collected but not stored by the current SQL insert. | Medium | Backend owner | Add role column or separate profile table. |

## 6) AI-Assisted Work

| Area | AI tool used | What AI helped with | What humans checked/changed | Evidence |
|---|---|---|---|---|
| UI structure and wording | ChatGPT / AI assistant | Suggested page structure, copy, and styling ideas. | Team adjusted pages and tested navigation. | `[TODO: PR/review link]` |
| Servlet/JDBC examples | ChatGPT / AI assistant | Helped draft Java Servlet and JDBC patterns. | Team compiled and tested locally. | `[TODO: compile/test evidence]` |
| Documentation organization | AI assistant | Reorganized capstone docs to match class requirements. | Team must review and add real evidence links. | `[TODO: documentation PR link]` |

## 7) Bugs / Unreliable Features

| Bug / problem | Severity | Evidence link | Owner | Next action |
|---|---|---|---|---|
| Hard-coded DB credentials | P1 | `[TODO: issue link]` | Backend owner | Move to local config or document setup safely. |
| Plain-text passwords | P1 | `[TODO: issue link]` | Backend owner | Add hashing before real users. |
| Registration/login may fail under context path | P1 | `[TODO: issue link]` | Frontend/backend owner | Test form action URLs under Tomcat. |
| Application flow not connected | P1 | `[TODO: issue link]` | Team | Add application servlet/table. |
| Job data not from database | P2 | `[TODO: issue link]` | Backend owner | Create jobs table and query flow. |

## 8) Risk List

| Risk | Why it matters | Mitigation | Owner |
|---|---|---|---|
| Live demo depends on local Tomcat/MySQL | Setup failure can stop pitch demo. | Prepare backup video and screenshots. | Demo Driver / QA Lead |
| Security basics are incomplete | Real users cannot safely use plain-text passwords. | Treat current system as class demo only; add hashing. | Backend owner |
| Evidence links are missing | Class rule says unlinked work does not count. | Add real issue, PR, screenshot, and demo links. | PM / Scribe |
| Too many planned features | Scope could become unrealistic. | Focus final MVP on search, account, and application flow. | PM |

## 9) Team Ownership Map

This ownership map is based on existing repository docs and should be verified by the team.

| Student | Owned area | Can explain? | Evidence link | Needs help with |
|---|---|---|---|---|
| Mezbah Uddin | Backend, DB connection, homepage | Clear | `[TODO: evidence links]` | Credential/security cleanup |
| Sami Ul Alim | Job search UI / frontend pages | Needs verification | `[TODO: evidence links]` | Backend integration |
| Ali Mohamad Ashraf | Login/job page support | Needs verification | `[TODO: evidence links]` | Servlet testing |
| Guramg Roman | Documentation / sprint evidence | Needs verification | `[TODO: evidence links]` | Link cleanup and evidence collection |
| Dipesh Chaulagain | Testing / QA support | Needs verification | `[TODO: evidence links]` | Repeatable test evidence |

## 10) Top 3 Stabilization Goals

1. Make Tomcat + MySQL register/login work reliably on the demo machine.
2. Connect the Apply flow to a basic application form and MySQL table.
3. Add real evidence links for issues, PRs, screenshots, sprint packets, and demo video.

## 11) Definition of Done for Sprint 3

- [ ] Core MVP flow works from the browser.
- [ ] Core MVP flow has linked evidence.
- [ ] P0/P1 bugs are fixed or clearly documented.
- [ ] Every member can explain one code/doc/test area.
- [ ] AI-assisted work has been reviewed by humans.
- [ ] Weekly Sprint Packet links this audit.
