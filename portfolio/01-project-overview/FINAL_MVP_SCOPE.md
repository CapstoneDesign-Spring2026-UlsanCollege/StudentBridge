# Final MVP Scope

## Final Core User Flow

1. A visitor opens StudentBridge.
2. The visitor learns the problem and navigates to jobs, login, or registration.
3. A student searches and filters job listings.
4. A new user registers as a Student or Employer.
5. A returning user logs in.
6. A student can view profile-related information.
7. An employer can post job information.
8. Job listings are stored and loaded through MySQL-backed servlet flows when the database is configured.
9. The team demonstrates known limitations and explains the next technical step.

## Features Included In The Final MVP

| Feature | Status | Evidence |
|---|---|---|
| Homepage and main navigation | Included | [../../index.html](../../index.html), [../../frontend/style.css](../../frontend/style.css) |
| Job search page | Included | [../../frontend/jobsearch.html](../../frontend/jobsearch.html), [../../Backend/JobServlet.java](../../Backend/JobServlet.java) |
| Registration | Included | [../../frontend/register.html](../../frontend/register.html), [../../Backend/RegisterServlet.java](../../Backend/RegisterServlet.java) |
| Login/logout | Included | [../../frontend/login.html](../../frontend/login.html), [../../Backend/LoginServlet.java](../../Backend/LoginServlet.java), [../../Backend/LogoutServlet.java](../../Backend/LogoutServlet.java) |
| Student profile | Included | [../../frontend/student-profile.html](../../frontend/student-profile.html), [../../Backend/StudentProfileServlet.java](../../Backend/StudentProfileServlet.java) |
| Employer dashboard | Included | [../../frontend/employer-dashboard.html](../../frontend/employer-dashboard.html), [../../Backend/EmployerJobsServlet.java](../../Backend/EmployerJobsServlet.java) |
| Employer post-job flow | Included | [../../frontend/post-job.html](../../frontend/post-job.html), [../../Backend/PostJobServlet.java](../../Backend/PostJobServlet.java) |
| MySQL schema | Included | [../../database_schema.sql](../../database_schema.sql) |
| Render deployment support | Included | [../../render.yaml](../../render.yaml), [../../docs/DEPLOY_RENDER.md](../../docs/DEPLOY_RENDER.md) |
| Demo screenshots | Included | [../../demo-screenshots](../../demo-screenshots) |

## Features That Work Reliably Enough For Demo

- Homepage and visual navigation.
- Job search UI and job cards.
- Login and registration screens.
- Employer dashboard and post-job screens.
- Database schema documentation.
- Render and local setup documentation.
- Demo screenshot backup package.

## Features That Work With Limitations

| Feature | Limitation | Handling |
|---|---|---|
| Register/login | Requires correct Tomcat and MySQL environment variables or local database credentials. | Document setup clearly and use demo credentials only. |
| Job loading | Depends on `jobs` table and seeded data. | Use `database_schema.sql` and `docs/database_jobs.sql`. |
| Employer job posting | Requires authenticated Employer session and complete schema. | Demo after setup, with screenshots as backup. |
| Student profile | Reads student profile data when available. | Empty profile fields are acceptable for demo if no saved profile exists. |
| Render deployment | Depends on hosted database environment variables. | Use deployment guide and local fallback. |

## Features Excluded From The Final MVP

| Feature | Reason |
|---|---|
| Full student application submission storage | Important next step, but final scope prioritizes search, account, job data, and employer posting. |
| Password hashing | Required before real users, but documented as a security limitation for the class demo. |
| Resume upload | Useful stretch goal, not required for the core job discovery demo. |
| Employer review of applications | Depends on application storage first. |
| Email notifications | Stretch goal after core data flow. |
| AI job recommendations | Stretch goal after reliable job and application data. |
| Production-grade authentication and authorization | The project is a capstone MVP, not a production service. |

