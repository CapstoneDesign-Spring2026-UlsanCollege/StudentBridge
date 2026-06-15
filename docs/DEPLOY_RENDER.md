# Deploy StudentBridge on Render

StudentBridge deploys as a Docker-backed Tomcat web service. The app is served from `/`, so these URLs work after deploy:

- `/`
- `/frontend/register.html`
- `/frontend/login.html`
- `/frontend/jobsearch.html`

## 1. Prepare MySQL

Render does not provision MySQL from `render.yaml`, so use an external MySQL database provider. Create a database named `studentbridge`, then run these SQL files:

```sql
docs/database_update_auth.sql
docs/database_jobs.sql
docs/database_final_demo_flow.sql
```

## 2. Fill Render Environment Variables

When Render opens the Blueprint, fill these secrets:

| Key | Example |
|---|---|
| `DB_URL` | `jdbc:mysql://host:3306/studentbridge?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8` |
| `DB_USER` | `studentbridge_user` |
| `DB_PASSWORD` | database password |

If your database provider requires SSL, add the SSL option to `DB_URL`, for example:

```text
jdbc:mysql://host:3306/studentbridge?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&sslMode=REQUIRED
```

## 3. Deploy

Commit and push these deployment files:

```bash
git add Backend/DBConnection.java Dockerfile .dockerignore render.yaml .env.example docs/DEPLOY_RENDER.md
git commit -m "Add Render deployment configuration"
git push origin main
```

Then open:

```text
https://dashboard.render.com/blueprint/new?repo=https://github.com/Mezbah143/StudentBridge
```

Choose the Blueprint, fill the environment variables, and click **Apply**.

## 4. Verify

After deploy finishes, open the Render URL and test:

1. Homepage loads at `/`.
2. Job search page loads at `/frontend/jobsearch.html`.
3. Register submits without a database connection error.
4. Employer can post a job with an address.
5. Student can apply once and sees the duplicate-application message on the second try.
6. Employer can view the applicant and send a message.
7. Student can open Messages and see the employer message.
