# AI Use Summary

## Rule

AI-assisted work only counts if the team can run it, explain it, test it, debug it, and link evidence.

## AI Tools Used

| Tool | Use |
|---|---|
| ChatGPT / Codex-style assistant | Helped draft code, organize documentation, debug setup, improve UI, and prepare final portfolio structure. |
| GitHub Copilot or similar AI assistance | May have supported code drafting and refactoring; team should verify exact usage. |

## What AI Helped With

- UI copy and layout ideas.
- Java Servlet and JDBC examples.
- Documentation organization.
- Setup and deployment notes.
- QA checklist drafting.
- Final portfolio structure.

## What Humans Reviewed Or Changed

- Team members kept the project stack as Java Servlet, Tomcat, MySQL, HTML, CSS, and JavaScript.
- Team members tested or prepared to test flows locally and through deployment.
- Team members adjusted UI pages, backend servlets, database schema, and demo documentation.
- Team members remain responsible for explaining their owned areas during technical defense.

## How AI-Assisted Work Was Tested

- Backend compile checks documented in [../../docs/TESTING_NOTES.md](../../docs/TESTING_NOTES.md).
- Frontend syntax checks documented in [../../docs/TESTING_NOTES.md](../../docs/TESTING_NOTES.md).
- Manual demo screenshots stored in [../../demo-screenshots](../../demo-screenshots).
- Final QA checklist in [../05-qa-and-stabilization/QA_REPORT.md](../05-qa-and-stabilization/QA_REPORT.md).

## Code Areas Students Can Explain

| Student | Area |
|---|---|
| Mezbah Uddin | Backend setup, database connection, homepage, deployment configuration. |
| Sami Ul Alim | Job search UI and frontend behavior. |
| Ali Mohamad Ashraf | Login flow, job page support, servlet/frontend integration. |
| Guramg Roman | Documentation, sprint evidence, project history. |
| Dipesh Chaulagain | QA checklist, test evidence, demo validation. |

## Confusing Or Risky Code Areas

| Area | Risk | Next step |
|---|---|---|
| Session and account-type routing | Demo can fail if session state is not created correctly. | Rehearse student and employer login. |
| Database environment variables | Hosted app fails if variables are missing. | Verify Render config before demo. |
| Password handling | Not safe for real users. | Add hashing after class demo. |
| Application submission | Not finished. | Add applications table and servlet. |

## Representative Evidence

- [AI code ownership audit](../../docs/AI_CODE_OWNERSHIP_AUDIT.md)
- [Backend source](../../Backend)
- [Frontend source](../../frontend)
- [Testing notes](../../docs/TESTING_NOTES.md)
- [Individual portfolios](../08-individual-portfolios/MEZBAH_UDDIN.md)

