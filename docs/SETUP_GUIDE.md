# StudentBridge Setup Guide

Last updated: 2026-05-01

## Project

StudentBridge runs as a Java Servlet web application on Apache Tomcat with a MySQL database. The frontend is plain HTML/CSS/JavaScript.

## Requirements

| Requirement | Recommended |
|---|---|
| Java | JDK 17 or newer |
| Server | Apache Tomcat 10.1.x |
| Database | MySQL 8.x |
| Browser | Chrome, Edge, or Firefox |
| JDBC driver | Included in `server/mysql-connector-j-9.3.0.jar` |
| Servlet API jar | Included in `server/servlet-api.jar` |

Tomcat 10 uses Jakarta Servlet packages, which match the current servlet imports.

## Repository Layout

| Path | Purpose |
|---|---|
| `index.html` | Homepage copied to Tomcat webapp root. |
| `frontend/` | Login, register, jobs, CSS, and frontend JavaScript. |
| `Backend/` | Java Servlet and database connection source files. |
| `server/` | Local jar dependencies. |
| `bin/` | Compiled `.class` files. |
| `deploy.sh` | Compile/copy script for local Tomcat deployment. |
| `docs/` | Capstone documentation and evidence. |

## Database Setup

Create the local database and current required table:

```sql
CREATE DATABASE IF NOT EXISTS studentbridge;
USE studentbridge;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  phone VARCHAR(30),
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

Optional demo user:

```sql
INSERT INTO users (name, email, phone, password)
VALUES ('Demo Student', 'demo.student@example.com', '010-0000-0000', 'demo1234')
ON DUPLICATE KEY UPDATE name = VALUES(name);
```

Security note: this schema matches the current demo code, but plain-text passwords are not safe for real users.

## Configure Database Credentials

The current code connects in `Backend/DBConnection.java`.

Before running locally, make sure the database URL, username, and password match your machine.

Do not publish real database passwords in screenshots, slides, or documentation. A future improvement should move credentials out of source code.

## Deploy To Local Tomcat

The current `deploy.sh` expects Tomcat at:

```text
/Users/mezbahuddin/Desktop/apache-tomcat-10.1.54/webapps/StudentBridge
```

If your Tomcat path is different, update the `TOMCAT` variable locally before running.

Run:

```bash
chmod +x deploy.sh
./deploy.sh
```

The script does four main things:

1. Creates the Tomcat `StudentBridge` webapp folders.
2. Copies `index.html` and `frontend/`.
3. Compiles Java source files from `Backend/` into `bin/`.
4. Copies compiled classes and MySQL driver into `WEB-INF/`.

## Start Tomcat

Start Tomcat from your local installation. Example:

```bash
/Users/mezbahuddin/Desktop/apache-tomcat-10.1.54/bin/startup.sh
```

Then open:

```text
http://localhost:8080/StudentBridge/
```

To stop Tomcat:

```bash
/Users/mezbahuddin/Desktop/apache-tomcat-10.1.54/bin/shutdown.sh
```

## Manual Compile Alternative

If you need to compile without the deploy script:

```bash
javac -cp "server/mysql-connector-j-9.3.0.jar:server/servlet-api.jar" -d bin Backend/*.java
```

Then copy compiled classes into Tomcat:

```bash
cp bin/*.class "/path/to/apache-tomcat/webapps/StudentBridge/WEB-INF/classes/"
cp server/mysql-connector-j-9.3.0.jar "/path/to/apache-tomcat/webapps/StudentBridge/WEB-INF/lib/"
```

## Test The Setup

1. Open the homepage from Tomcat.
2. Open the Jobs page and test filters.
3. Open Register and create a demo account.
4. Check MySQL:

```sql
SELECT id, name, email, phone, created_at FROM users ORDER BY id DESC LIMIT 5;
```

5. Open Login and sign in with the demo account.

## Common Problems

| Problem | Likely cause | Fix |
|---|---|---|
| 404 on homepage | Tomcat webapp path is wrong | Confirm files copied to `webapps/StudentBridge/`. |
| Servlet 404 | Classes not copied to `WEB-INF/classes` or Tomcat not restarted | Run `./deploy.sh` and restart Tomcat. |
| Database connection failed | MySQL is stopped, schema missing, or credentials mismatch | Start MySQL, create schema, update local DB credentials. |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | MySQL jar missing from Tomcat app | Copy connector jar into `WEB-INF/lib`. |
| Compile error for `jakarta.servlet` | Servlet API jar missing or wrong Tomcat version | Use Tomcat 10 and `server/servlet-api.jar`. |
| Form posts to wrong URL | Absolute form action misses context path | Test under `/StudentBridge` and update form action if needed. |

## Demo Account / Seed Data

Use test-only data:

| Field | Value |
|---|---|
| Email | `demo.student@example.com` |
| Password | `demo1234` |
| Name | `Demo Student` |

Do not use real student personal information in demo data.

## Last Verified

| Item | Value |
|---|---|
| Date | `[TODO: add verification date]` |
| Verified by | `[TODO: add name]` |
| Machine | `[TODO: add OS/Tomcat/MySQL versions]` |
| Evidence | `[TODO: add screenshot or terminal output link]` |
