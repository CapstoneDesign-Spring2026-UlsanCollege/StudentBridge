# Architecture Planning

## Available Evidence

- [Main architecture document](../../../docs/ARCHITECTURE.md)
- [Architecture folder copy](../../../docs/Architecture/ARCHITECTURE.md)
- [Final architecture](../../04-final-product/ARCHITECTURE_FINAL.md)
- [Database schema](../../../database_schema.sql)
- [Deployment guide](../../../docs/DEPLOY_RENDER.md)

## Architecture Summary

StudentBridge uses a simple capstone-friendly architecture:

- HTML, CSS, and JavaScript frontend.
- Java Servlets on Apache Tomcat.
- MySQL database accessed with JDBC.
- Render deployment support with environment variables.

## Decision

The team kept the stack intentionally understandable so members can explain, test, and debug the work during technical defense.

