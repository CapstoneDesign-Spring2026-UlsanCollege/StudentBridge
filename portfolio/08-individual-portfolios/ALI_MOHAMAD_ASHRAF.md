# Ali Mohamad Ashraf Individual Portfolio

## Role

Login flow, job page support, and frontend/backend integration support.

## Main Responsibilities

- Supported login and account-related behavior.
- Helped connect job page work with backend expectations.
- Shared ownership of the student/employer platform flow.

## Strongest Contributions

| Contribution | Evidence |
|---|---|
| Login page | [../../frontend/login.html](../../frontend/login.html) |
| Login servlet | [../../Backend/LoginServlet.java](../../Backend/LoginServlet.java) |
| Logout servlet | [../../Backend/LogoutServlet.java](../../Backend/LogoutServlet.java) |
| Job servlet | [../../Backend/JobServlet.java](../../Backend/JobServlet.java) |
| Login screenshot | [../../demo-screenshots/02-login.png](../../demo-screenshots/02-login.png) |

## Technical Ownership Area

I can explain how login collects credentials, how the servlet validates a user, and how session data supports the role-aware flow.

## One Possible Failure

If a user is not in the database or the database connection fails, login cannot succeed. The workaround is to seed demo users and verify MySQL before presenting.

## AI Use

AI helped with code examples and documentation organization. I am responsible for checking the flow, understanding the servlet behavior, and explaining limitations honestly.

## Reflection

I learned how login is more than a form: it depends on database schema, servlet routing, sessions, and redirects. I am proud of supporting the account flow. I should have captured more test evidence earlier. Next, I would add stronger validation and password hashing.

## Skill To Continue Developing

Authentication flow design and backend/frontend integration.

