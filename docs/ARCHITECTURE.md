# StudentBridge - Architecture Sketch (Design Doc v1)

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

### Fill this in
**Users**
- Student: Search jobs, view details, apply  
- Business Owner: Post jobs, manage listings, view applicants  
- Admin: Monitor system and manage users (future)  

**Main system**
- StudentBridge Web Application  

**External services / systems**
- Database (MySQL)  
- (Future) Email/Notification Service  
- (Future) Authentication Service  

---

### Simple text version
```md
[Student] --------> [StudentBridge Web App] --------> [Database]
[Business Owner] -> [StudentBridge Web App] --------> [Database]
[Admin] ----------> [StudentBridge Web App] --------> [Database]
