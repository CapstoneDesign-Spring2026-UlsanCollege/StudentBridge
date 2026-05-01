# StudentBridge Testing Notes

Last updated: 2026-05-01

## Testing Goal

Prove the midterm-critical flow works well enough to demo and clearly document anything that does not work yet.

Primary flow:

```text
Homepage -> Jobs search -> Register -> Login -> Explain/prepare Apply flow
```

## Test Environment

| Item | Expected setup |
|---|---|
| Browser | Chrome or Edge |
| Java | JDK compatible with Tomcat 10.1.x |
| Servlet API | Jakarta Servlet API from `server/servlet-api.jar` |
| Server | Apache Tomcat 10.1.x |
| Database | MySQL running locally |
| Database name | `studentbridge` |
| URL | `http://localhost:8080/StudentBridge/` |

## Manual Test Matrix

| ID | Test | Steps | Expected result | Status | Evidence |
|---|---|---|---|---|---|
| T-01 | Homepage loads | Open local Tomcat URL. | Homepage appears with navigation. | `[TODO]` | `[TODO: screenshot]` |
| T-02 | Navigation works | Click Home, Jobs, Login, Register. | Each page opens without 404. | `[TODO]` | `[TODO: screenshot]` |
| T-03 | Job search by keyword | Search for `Cafe`. | Cafe job remains visible. | `[TODO]` | `[TODO: screenshot]` |
| T-04 | Job search by location | Select `Ulsan`. | Ulsan jobs remain visible. | `[TODO]` | `[TODO: screenshot]` |
| T-05 | Empty search state | Search/filter combination with no match. | "No jobs found" message appears. | `[TODO]` | `[TODO: screenshot]` |
| T-06 | Register valid user | Submit valid registration form. | User inserted into MySQL and redirected to login. | `[TODO]` | `[TODO: DB screenshot]` |
| T-07 | Register password mismatch | Submit different password/confirm password. | Error message appears; no user is created. | `[TODO]` | `[TODO: screenshot]` |
| T-08 | Login valid user | Submit correct email/password. | Session is created and user redirects to homepage. | `[TODO]` | `[TODO: screenshot]` |
| T-09 | Login invalid user | Submit wrong password. | User is redirected back to login error path. | `[TODO]` | `[TODO: screenshot]` |
| T-10 | Apply button | Click Apply on a job card. | Placeholder alert appears; no database action yet. | `[TODO]` | `[TODO: screenshot]` |
| T-11 | Mobile layout | Test homepage and jobs page at narrow width. | Content remains readable and usable. | `[TODO]` | `[TODO: screenshot]` |
| T-12 | Deploy script | Run `./deploy.sh`. | Java compiles and files copy to Tomcat. | `[TODO]` | `[TODO: terminal screenshot]` |

## Database Verification

Run these checks after registration testing:

```sql
USE studentbridge;
SHOW TABLES;
SELECT id, name, email, phone, created_at FROM users ORDER BY id DESC LIMIT 5;
```

Do not screenshot or publish real passwords.

## Known Issues From Testing

| Issue | Severity | How to reproduce | Next action |
|---|---|---|---|
| Register/login form action paths may be wrong under Tomcat context | P1 | Deploy under `/StudentBridge`, submit forms, check requested URL. | Fix action paths or document correct context path. |
| Hard-coded DB credentials | P1 | Open `Backend/DBConnection.java`. | Move credentials out of committed source. |
| Plain-text password storage | P1 | Register a user and inspect `users` table. | Add password hashing before real use. |
| Job application not connected | P1 | Click Apply on job card. | Build application form/servlet/table. |
| Job search data is hard-coded | P2 | Open `frontend/jobsearch.html`. | Add jobs table and backend loading strategy. |

## Definition of Done for Testing Evidence

Testing evidence is complete when:

- Each high-priority flow has at least one screenshot or video clip.
- Tomcat deployment output is captured.
- MySQL table verification is captured without exposing passwords.
- Bugs have GitHub issues.
- Sprint packet links the evidence.

## Evidence Placeholders

| Evidence | Link |
|---|---|
| Test run screenshot folder | `[TODO: link]` |
| Tomcat deploy output | `[TODO: link]` |
| MySQL verification screenshot | `[TODO: link]` |
| Bug issue list | `[TODO: link]` |
| QA checklist issue | `[TODO: link]` |
