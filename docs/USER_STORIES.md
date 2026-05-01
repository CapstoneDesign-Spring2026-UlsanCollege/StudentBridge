# StudentBridge User Stories

Last updated: 2026-05-01

## MVP Story Map

StudentBridge should first support one reliable student journey: find a job, create an account, log in, and apply. Employer posting is important, but it can be simplified for the midterm if the team needs to protect demo reliability.

## Student Stories

| ID | User story | Priority | Status | Acceptance criteria |
|---|---|---|---|---|
| SB-STU-01 | As an international student, I want to understand what StudentBridge does from the homepage so that I know whether it is useful for me. | High | Working | Homepage explains the problem, target users, and main action; navigation links work. |
| SB-STU-02 | As an international student, I want to search jobs by title and location so that I can find work near my city. | High | Working as frontend demo | Job search page filters visible job cards by title and location. |
| SB-STU-03 | As an international student, I want to filter by category and job type so that I can find student-friendly part-time work. | Medium | Working as frontend demo | Filters update results without console errors; empty search shows a clear message. |
| SB-STU-04 | As a new user, I want to register with name, email, phone, password, and account type so that I can create an account. | High | Partially working | Required fields validate in browser; servlet inserts into MySQL after setup; duplicate email behavior is documented. |
| SB-STU-05 | As a registered user, I want to log in with email and password so that I can access the platform. | High | Partially working | Correct credentials redirect to homepage; incorrect credentials return to login with an error state. |
| SB-STU-06 | As a student, I want to apply to a listed job so that an employer can review my interest. | High | Planned | Apply button opens/submits an application form; application is saved in MySQL; confirmation appears. |
| SB-STU-07 | As a student, I want clear error messages so that I understand what went wrong. | Medium | Partial | Login/register/database errors are displayed or redirected clearly. |

## Employer Stories

| ID | User story | Priority | Status | Acceptance criteria |
|---|---|---|---|---|
| SB-EMP-01 | As an employer, I want to register as an employer so that I can use StudentBridge for hiring. | Medium | Partial | Registration form includes account type; backend role storage still needs completion. |
| SB-EMP-02 | As an employer, I want to post a part-time job so that students can discover it. | High for final | Planned | Employer can enter title, location, category, type, salary, and description; job is saved in MySQL. |
| SB-EMP-03 | As an employer, I want to view student applications so that I can choose candidates. | Medium | Planned | Employer dashboard lists submitted applications for owned jobs. |

## Admin / Support Stories

| ID | User story | Priority | Status | Acceptance criteria |
|---|---|---|---|---|
| SB-ADM-01 | As a team member, I want seed data for jobs and demo users so that the demo is repeatable. | High | Planned | Setup guide includes SQL seed data; demo can be reset before presentation. |
| SB-ADM-02 | As a team member, I want a backup demo so that presentation can continue if Tomcat or MySQL fails. | High | Documented | Demo plan includes video, screenshots, and a 15-second switch rule. |

## Definition of Done for User Stories

A user story is done only when:

- The behavior works from the browser, not only in code.
- The team can explain the files involved.
- At least one teammate tests it.
- Evidence is linked in a GitHub issue, PR, screenshot, or sprint packet.
- Known limitations are documented instead of hidden.

## Evidence Placeholders

| Evidence needed | Link |
|---|---|
| Story issue links | `[TODO: link GitHub issues for each high-priority story]` |
| Pull requests | `[TODO: link PRs]` |
| Screenshots | `[TODO: link screenshots]` |
| Demo video | `[TODO: link backup demo video]` |
