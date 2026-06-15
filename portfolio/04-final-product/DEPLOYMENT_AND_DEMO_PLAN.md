# Deployment And Demo Plan

## Demo Environments

| Environment | Purpose | Link |
|---|---|---|
| Render | Primary public demo if healthy. | <https://studentbridge-6jn2.onrender.com> |
| Local Tomcat | Backup and developer demo. | <http://localhost:8080/StudentBridge/> |
| Screenshots | Backup if live app or internet fails. | [../../demo-screenshots](../../demo-screenshots) |

## Required Accounts And Devices

- One laptop with Chrome.
- GitHub access to the repository.
- Render dashboard access if deployment needs checking.
- MySQL access for local or hosted database.
- Demo student and employer accounts.

## Demo Data

| Data | Source |
|---|---|
| Demo users | Register through app or seed manually. |
| Demo jobs | [../../docs/database_jobs.sql](../../docs/database_jobs.sql) |
| Schema | [../../database_schema.sql](../../database_schema.sql) |

## Known Demo Risks

| Risk | Backup |
|---|---|
| Render app unavailable | Use local Tomcat. |
| Local database unavailable | Use screenshots and explain database setup. |
| Login/register fails | Show code, setup guide, and screenshots. |
| Job posting fails | Show schema, servlet code, and post-job screenshot. |
| Internet fails | Use local screenshots and repository files. |

## Final Rehearsal Checklist

- [ ] Open Render app.
- [ ] Open local Tomcat app.
- [ ] Confirm jobs page loads.
- [ ] Confirm login/register pages load.
- [ ] Confirm employer dashboard and post-job pages load.
- [ ] Confirm screenshots open locally.
- [ ] Confirm presentation script fits 15 minutes.
- [ ] Confirm each member can answer their technical ownership question.

