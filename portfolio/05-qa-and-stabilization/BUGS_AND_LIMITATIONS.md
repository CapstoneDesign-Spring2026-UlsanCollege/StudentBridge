# Bugs And Limitations

| Bug or limitation | Severity | Status | Evidence | Workaround or next step |
|---|---|---|---|---|
| Application submission depends on logged-in student session and CV link in profile. | P1 | Mitigated | [Apply servlet](../../Backend/ApplyServlet.java) | Seed demo student profile/CV data and test before presentation. |
| Passwords are not production-secure. | P1 | Accepted for demo | [AI audit](../../docs/AI_CODE_OWNERSHIP_AUDIT.md) | Add hashing before real users. |
| Database/demo depends on environment variables and MySQL availability. | P1 | Mitigated | [Setup guide](../04-final-product/SETUP_AND_RUN_GUIDE.md) | Test hosted and local DB before presentation. |
| Render app can fail if hosted database is not configured. | P1 | Mitigated | [Deployment plan](../04-final-product/DEPLOYMENT_AND_DEMO_PLAN.md) | Use local Tomcat or screenshots as backup. |
| Some historical weeks do not have formal sprint packets. | P2 | Documented | [Semester journey](../02-semester-journey/SEMESTER_JOURNEY.md) | Use honest notes and link available evidence. |
| Some contribution evidence lacks exact PR/issue links. | P2 | Documented | [Individual portfolios](../08-individual-portfolios/MEZBAH_UDDIN.md) | Team members should add exact PR/commit links if time allows. |
| Full accessibility audit is not complete. | P3 | Open | [QA report](QA_REPORT.md) | Run keyboard, label, contrast, and responsive checks. |
| Backup video was not found in repo. | P3 | Open | [Backup demo](../07-final-presentation/BACKUP_DEMO.md) | Record a 2-3 minute screen capture if possible. |

## Severity Guide

- P0: final demo cannot work.
- P1: core feature broken or unreliable.
- P2: important but workaround exists.
- P3: polish or nice improvement.
