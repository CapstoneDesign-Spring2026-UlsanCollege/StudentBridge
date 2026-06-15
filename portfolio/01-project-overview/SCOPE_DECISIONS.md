# Scope Decisions

| Feature or idea | Final status | Why | Evidence |
|---|---|---|---|
| Homepage and product story | Included | Needed as the entry point for users and presentation. | [../../index.html](../../index.html) |
| Responsive shared navigation | Included | Makes the demo feel complete and usable across pages. | [../../frontend/style.css](../../frontend/style.css), [../../frontend/script.js](../../frontend/script.js) |
| Searchable job list | Included | This is the core student value flow. | [../../frontend/jobsearch.html](../../frontend/jobsearch.html), [../../Backend/JobServlet.java](../../Backend/JobServlet.java) |
| Student registration | Included | Required for account-based job platform flow. | [../../Backend/RegisterServlet.java](../../Backend/RegisterServlet.java) |
| Login/logout | Included | Required for role-aware student/employer experience. | [../../Backend/LoginServlet.java](../../Backend/LoginServlet.java), [../../Backend/LogoutServlet.java](../../Backend/LogoutServlet.java) |
| Student profile | Included | Shows that students can have platform identity beyond a static job page. | [../../frontend/student-profile.html](../../frontend/student-profile.html), [../../Backend/StudentProfileServlet.java](../../Backend/StudentProfileServlet.java) |
| Employer job posting | Included | Lets the platform support both sides of the marketplace. | [../../frontend/post-job.html](../../frontend/post-job.html), [../../Backend/PostJobServlet.java](../../Backend/PostJobServlet.java) |
| Employer dashboard | Included | Helps employers see the jobs connected to their account. | [../../frontend/employer-dashboard.html](../../frontend/employer-dashboard.html), [../../Backend/EmployerJobsServlet.java](../../Backend/EmployerJobsServlet.java) |
| MySQL-backed job data | Included | Moves the demo beyond static-only job listings. | [../../database_schema.sql](../../database_schema.sql), [../../docs/database_jobs.sql](../../docs/database_jobs.sql) |
| Render deployment | Working with limitations | Useful for public demo, but local setup remains the backup. | [../../render.yaml](../../render.yaml), [../../docs/DEPLOY_RENDER.md](../../docs/DEPLOY_RENDER.md) |
| Full application submission | Incomplete | It is the next important feature after employer job posting and job storage. | [../05-qa-and-stabilization/BUGS_AND_LIMITATIONS.md](../05-qa-and-stabilization/BUGS_AND_LIMITATIONS.md) |
| Password hashing | Cut | Security hardening is required before real users, but not completed for this class MVP. | [../05-qa-and-stabilization/BUGS_AND_LIMITATIONS.md](../05-qa-and-stabilization/BUGS_AND_LIMITATIONS.md) |
| Resume upload | Nice Later | Useful but not necessary for the 15-minute final demo. | [../../docs/ROADMAP.md](../../docs/ROADMAP.md) |
| Email notifications | Nice Later | Depends on stable account and application flow first. | [../../docs/ROADMAP.md](../../docs/ROADMAP.md) |
| AI recommendations | Nice Later | Interesting future feature, but not part of the reliable MVP. | [../../docs/ROADMAP.md](../../docs/ROADMAP.md) |

