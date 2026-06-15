# Backup Demo

## Backup Goal

Keep the final presentation moving if Render, internet, Tomcat, or MySQL fails.

## Backup Order

1. Try Render: <https://studentbridge-6jn2.onrender.com>
2. Try local Tomcat: <http://localhost:8080/StudentBridge/>
3. Use screenshots from [../../demo-screenshots](../../demo-screenshots).
4. Show code and database schema in the repository.
5. Explain the limitation using [../05-qa-and-stabilization/BUGS_AND_LIMITATIONS.md](../05-qa-and-stabilization/BUGS_AND_LIMITATIONS.md).

## Screenshot Backup Flow

| Step | Screenshot |
|---|---|
| Homepage | [01-home.png](../../demo-screenshots/01-home.png) |
| Login | [02-login.png](../../demo-screenshots/02-login.png) |
| Register | [03-register-student.png](../../demo-screenshots/03-register-student.png) |
| Job search | [04-jobsearch.png](../../demo-screenshots/04-jobsearch.png) |
| Employer dashboard | [05-employer-dashboard.png](../../demo-screenshots/05-employer-dashboard.png) |
| Post job | [06-post-job.png](../../demo-screenshots/06-post-job.png) |
| Student profile | [07-student-profile.png](../../demo-screenshots/07-student-profile.png) |

## Failure Responses

| Failure | Response |
|---|---|
| Render down | Switch to local Tomcat. |
| Tomcat down | Use screenshots and explain setup. |
| MySQL down | Show schema and servlet code. |
| Login fails | Show login servlet and known setup risks. |
| Job posting fails | Show `PostJobServlet`, `jobs` schema, and screenshot. |

## Final Reminder

Switch to backup quickly. Do not spend presentation time debugging live.

