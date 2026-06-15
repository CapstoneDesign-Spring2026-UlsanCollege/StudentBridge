# Mezbah Uddin Individual Portfolio

## Role

Backend, database connection, homepage support, deployment support, and final portfolio implementation.

## Main Responsibilities

- Helped build and maintain backend servlet/database flow.
- Worked on homepage and shared navigation polish.
- Helped configure MySQL and deployment-related documentation.
- Organized final portfolio evidence.

## Strongest Contributions

| Contribution | Evidence |
|---|---|
| Database connection setup | [../../Backend/DBConnection.java](../../Backend/DBConnection.java) |
| Registration backend support | [../../Backend/RegisterServlet.java](../../Backend/RegisterServlet.java) |
| Homepage and navigation | [../../index.html](../../index.html), [../../frontend/style.css](../../frontend/style.css) |
| Deployment configuration | [../../render.yaml](../../render.yaml), [../../docs/DEPLOY_RENDER.md](../../docs/DEPLOY_RENDER.md) |
| Final portfolio package | [../README.md](../README.md) |

## Technical Ownership Area

I can explain how `DBConnection` builds the JDBC connection using environment variables or local defaults, and how servlet files call the database during register, login, job loading, and job posting flows.

## One Possible Failure

If `DB_URL`, `DB_USER`, or `DB_PASSWORD` are wrong, the servlets cannot connect to MySQL. The fix is to verify environment variables and test with a simple query.

## AI Use

AI helped with code organization, documentation, and final portfolio drafting. I am responsible for reviewing the output, testing the app, and explaining the backend/database flow.

## Reflection

I learned how frontend pages, Java Servlets, Tomcat, MySQL, and deployment configuration fit together in a complete capstone project. I am proud that the project became a real platform structure instead of only static pages. I should have collected stronger PR and issue evidence earlier. Next, I would improve password security and complete the application submission flow.

## Skill To Continue Developing

Backend deployment and secure database-backed web applications.

