# SkillSprint — Student Career Tracking System

SkillSprint is a Java web application for students to keep their career preparation information in one place. It includes account access, a dashboard, and trackers for internships, coding practice, certificates, learning goals, and profile details.

## Technology

- Java 17
- Maven WAR
- Jakarta Servlet 6.0 and JSP
- JDBC with MySQL Connector/J
- MySQL
- Apache Tomcat 10.1

The application follows this request flow:

```text
Browser → Servlet → DAO → JDBC → MySQL
                     ↓
                  Model → JSP response
```

## Features

- Registration and login with password hashing
- Session-based authentication and logout
- Authentication and CSRF filters
- Dashboard statistics loaded from the database
- Internship application records with search and status filtering
- Coding progress records with platform, difficulty, and status filters
- Certificates with optional credential links
- Learning goals with status filtering and completion actions
- Student profile viewing and editing

Tracker and profile records are associated with the signed-in user. Tracker pages are served through authenticated routes.

## Requirements

Install or have available:

- JDK 11 or later
- Maven 3.6 or later
- MySQL 8
- Apache Tomcat 10.1

Tomcat 10.1 supports the Jakarta Servlet 6.0 API used by this project. The Servlet API dependency is provided by the servlet container; the MySQL JDBC driver is packaged as a runtime dependency.

## Database setup

1. Start MySQL.
2. Run [`database/schema.sql`](database/schema.sql) using a MySQL account allowed to create the `skillsprint` database and its tables.
3. Configure the application's database connection as described below.

The schema creates the `skillsprint` database and these tables: `users`, `internship_applications`, `coding_progress`, `certificates`, and `goals`.

### Database connection configuration

`DBConnection` reads connection settings from Java system properties first, then environment variables:

| Setting | System property | Environment variable |
| --- | --- | --- |
| JDBC URL | `skillsprint.db.url` | `SKILLSPRINT_DB_URL` |
| Database username | `skillsprint.db.username` | `SKILLSPRINT_DB_USERNAME` |
| Database password | `skillsprint.db.password` | `SKILLSPRINT_DB_PASSWORD` |

For example, set these environment variables in the environment used to launch Tomcat (replace the values for your local MySQL setup):

```text
SKILLSPRINT_DB_URL=jdbc:mysql://localhost:3306/skillsprint
SKILLSPRINT_DB_USERNAME=your_mysql_username
SKILLSPRINT_DB_PASSWORD=your_mysql_password
```

Do not commit real database credentials. If your local MySQL server needs additional JDBC URL options, add them to `SKILLSPRINT_DB_URL`.

## Build and run

From the project root, build the WAR:

```bash
mvn clean package
```

The WAR is written to:

```text
target/student_tracking.war
```

Deploy the WAR to Tomcat 10.1, configure the database settings in Tomcat's process environment, then start or restart Tomcat. With the default WAR name, Tomcat normally uses `/student_tracking` as the context path. The public landing page is the application's welcome page; sign-in and account creation are linked from there.

If using IntelliJ IDEA, configure a Tomcat 10.1 run configuration for this Maven web application and provide the same database environment variables to that run configuration.

## Main routes

| Purpose | Route |
| --- | --- |
| Landing page | `/` |
| Register | `/register` |
| Login | `/login` |
| Dashboard | `/dashboard` |
| Internship applications | `/applications/list` |
| Coding progress | `/coding/list` |
| Certificates | `/certificates/list` |
| Learning goals | `/goals/list` |
| Student profile | `/profile` |
| Logout | `/logout` (state-changing POST) |

Prefix routes with the deployed context path when entering them directly in a browser. For example, the default local deployment may use `http://localhost:8080/student_tracking/`.

## Project structure

```text
src/main/java/org/example/skillsprint/
├── dao/       Database queries and persistence
├── filter/    Authentication and CSRF request filters
├── model/     Data objects used by the application
├── servlet/   HTTP route handling and request coordination
└── util/      Shared JDBC connection utility

src/main/webapp/
├── assets/                 Shared CSS and JavaScript
├── WEB-INF/views/          JSP pages and shared view fragments
├── WEB-INF/web.xml         Servlet and filter configuration
└── index.jsp               Public landing page

database/schema.sql         Database and table definitions
pom.xml                     Maven project and dependency configuration
```

JSP views are kept under `WEB-INF` so they are reached through the application's servlets rather than requested directly. Servlets handle HTTP input and forwarding; DAOs contain SQL and use JDBC; models carry data between these layers and the JSP views.

## Security notes

- Authentication state is kept in the HTTP session and protected routes use the authentication filter.
- State-changing forms use CSRF tokens checked by the CSRF filter.
- Tracker operations use the current signed-in user's identity to scope records.
- Configure credentials outside source control and use a dedicated MySQL account with only the permissions the application needs.

## Current scope

This project is a student career tracking application. Coding activity is entered manually; the application does not fetch activity from external coding platforms. Certificate details and credential URLs are entered by the student; the application does not verify certificate authenticity or upload certificate files.
