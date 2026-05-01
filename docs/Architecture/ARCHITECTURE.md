# StudentBridge - Architecture Sketch (Legacy)

> Canonical architecture handoff: [`../ARCHITECTURE.md`](../ARCHITECTURE.md). This legacy file is kept because earlier design docs referenced `docs/Architecture/ARCHITECTURE.md`.

---

## 1) Architecture summary
**System name:**
StudentBridge

**Main users:**
- Student (job seeker)
- Business Owner / Employer (posts jobs)
- Admin (system manager - optional for now)

**Main job of the system:**
Help students find part-time jobs and allow employers to post and manage job listings.

---

## 2) Context view

### Users
**Users**
- Student: Search jobs, view details, apply
- Business Owner: Post jobs, manage listings, view applicants
- Admin: Monitor system and manage users (future)

**Main system**
- StudentBridge Web Application

**External services / systems**
- Database (MySQL)
- Apache Tomcat runtime
- (Future) Email/Notification Service

---

### Simple text version
```md
[Student] --------> [StudentBridge Web App] --------> [Database]
[Business Owner] -> [StudentBridge Web App] --------> [Database]
[Admin] ----------> [StudentBridge Web App] --------> [Database]
