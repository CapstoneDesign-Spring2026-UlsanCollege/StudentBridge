# StudentBridge - Architecture Sketch (Design Doc v1)

---

## 1) Architecture summary
**System name:**  
StudentBridge  

**Main users:**  
- Student (job seeker)  
- Admin (manage jobs - optional for now)  

**Main job of the system:**  
Help international students find and apply for part-time jobs.

---

## 2) Context view

### Fill this in
**Users**
- User 1: Student (search and apply for jobs)  
- User 2: Admin (add/manage job listings)  

**Main system**
- StudentBridge Web Application  

**External services / systems**
- Database (MySQL)  
- (Future) Notification Service  

### Simple text version
```md
[Student] ---> [StudentBridge Web App] ---> [Database]
[Admin] ---> [StudentBridge Web App] ---> [Database]
