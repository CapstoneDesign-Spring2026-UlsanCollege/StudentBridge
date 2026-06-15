# Technical Defense Prep

## Questions The Team Should Be Ready For

| Question | Strong answer |
|---|---|
| What problem does StudentBridge solve? | It helps international students in South Korea find student-friendly part-time jobs and helps employers reach them. |
| What is the final MVP? | Homepage, job search, register/login, student profile, employer dashboard, post-job flow, MySQL schema, and deployment docs. |
| What does the frontend use? | Plain HTML, CSS, and JavaScript. |
| What does the backend use? | Java Servlets running on Tomcat. |
| What database do you use? | MySQL, accessed through JDBC. |
| How are jobs loaded? | `JobServlet` reads rows from the `jobs` table and returns JSON. |
| How do employers post jobs? | `PostJobServlet` validates employer session data and inserts a job into MySQL. |
| How does login work? | `LoginServlet` checks email/password and creates servlet session values. |
| What is incomplete? | Password hashing, full accessibility audit, and stronger exact PR/issue evidence. |
| What did AI help with? | Drafting, debugging, documentation organization, and portfolio structure. Humans remain responsible for testing and explaining. |

## Ownership Areas

| Student | Explain this area |
|---|---|
| Mezbah Uddin | `DBConnection`, deployment variables, homepage, backend setup. |
| Sami Ul Alim | Job search UI and frontend filtering/display. |
| Ali Mohamad Ashraf | Login flow and job page integration. |
| Guramg Roman | Documentation, sprint packet evidence, portfolio organization. |
| Dipesh Chaulagain | QA checklist, test evidence, demo validation. |

## Honest Unknown Answer Template

If we do not know something:

> I do not want to guess. What I can explain is the part I worked on: [area]. The current risk is documented in the portfolio, and our next step would be [specific next step].

## High-Risk Questions

| Risk question | Answer direction |
|---|---|
| Why are passwords plain text? | It is a known demo limitation. Before real users, we would add hashing such as BCrypt and update login tests. |
| What does application submission require? | A logged-in Student session, an existing job, and a student profile with a CV link. |
| What if Render fails? | We have local Tomcat and screenshot backup. |
| Can every member explain the code? | Every member owns at least one area listed in the individual portfolios. |
