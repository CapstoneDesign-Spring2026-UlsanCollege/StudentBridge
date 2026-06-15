# Setup And Run Guide

## Required Software

| Tool | Recommended |
|---|---|
| Java | JDK 17 or newer |
| Server | Apache Tomcat 10.1.x |
| Database | MySQL 8.x or hosted MySQL-compatible database |
| Browser | Chrome, Edge, or Firefox |
| Shell | macOS/Linux terminal |

## Clone

```bash
git clone https://github.com/CapstoneDesign-Spring2026-UlsanCollege/StudentBridge.git
cd StudentBridge
```

## Database Setup

Run the schema in MySQL:

```bash
mysql -u root -p < database_schema.sql
```

For extra demo job rows, use:

```bash
mysql -u root -p studentbridge < docs/database_jobs.sql
```

## Environment Variables

The current `DBConnection` supports these variables:

| Variable | Purpose |
|---|---|
| `DB_URL` | Full JDBC URL. Optional if host, port, and name are set. |
| `DB_HOST` | MySQL host. |
| `DB_PORT` | MySQL port, usually `3306`. |
| `DB_NAME` | Database name, usually `studentbridge`. |
| `DB_USER` | Database username. |
| `DB_PASSWORD` | Database password. |

Use `.env.example` and deployment docs as references. Do not commit real passwords.

## Local Tomcat Run

The existing setup guide has the detailed Tomcat path notes:

- [../../docs/SETUP_GUIDE.md](../../docs/SETUP_GUIDE.md)

Typical run:

```bash
chmod +x deploy.sh
./deploy.sh
```

Then start Tomcat and open:

```text
http://localhost:8080/StudentBridge/
```

## Manual Compile Check

```bash
javac -cp "server/mysql-connector-j-9.3.0.jar:server/servlet-api.jar" -d bin Backend/*.java
```

## Test Commands

Frontend syntax checks:

```bash
node --check frontend/script.js
node --check frontend/platform.js
node --check frontend/runtime-config.js
```

Backend compile check:

```bash
javac -cp "server/mysql-connector-j-9.3.0.jar:server/servlet-api.jar" -d bin Backend/*.java
```

## Seed Data

Use demo-only users and jobs. Avoid real passwords and personal information in screenshots.

```sql
INSERT INTO users (name, email, phone, password, account_type)
VALUES ('Demo Student', 'demo.student@example.com', '010-0000-0000', 'demo1234', 'Student');
```

## Troubleshooting

| Problem | Likely cause | Fix |
|---|---|---|
| Homepage 404 | Tomcat app path is wrong. | Confirm files copied to `webapps/StudentBridge`. |
| Servlet 404 | Classes missing or Tomcat not restarted. | Run `./deploy.sh` and restart Tomcat. |
| Database error | MySQL stopped, schema missing, or env vars wrong. | Start MySQL and verify `DB_*` variables. |
| Jobs page empty | `jobs` table missing or not seeded. | Run `database_schema.sql` and `docs/database_jobs.sql`. |
| Render fails | Missing hosted DB vars or port/start issue. | Follow [deployment guide](../../docs/DEPLOY_RENDER.md). |

## Known Setup Limitations

- Local Tomcat path may need editing in `deploy.sh`.
- Passwords are demo-only and not production-secure.
- Hosted deployment depends on external database configuration.

