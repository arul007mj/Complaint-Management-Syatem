# Complaint-Management-Syatem
🚀 A production-ready Spring Boot REST API for Complaint Management featuring JWT Authentication, Role-Based Access Control, Complaint Tracking, Pagination, Filtering, Email Notifications, MySQL, Docker, and RESTful API design .

# Registry — Complaint Management System

A role-based complaint / ticket management system. Customers file complaints,
team leads and managers work them through a status lifecycle, unresolved
high-priority complaints auto-escalate, and admins manage the user roster.

- **Backend:** Java 17, Spring Boot 3.3.5, Spring Security + JWT, Spring Data
  JPA, MySQL, scheduled escalation job, email notifications, file attachments.
- **Frontend:** Vanilla HTML/CSS/JS single-page app (no build step, no
  framework) — already included in `frontend/`.
- **Database:** MySQL 8.

## Contents

- [Architecture](#architecture)
- [Features](#features)
- [Project structure](#project-structure)
- [Quick start with Docker Compose](#quick-start-with-docker-compose)
- [Manual local setup](#manual-local-setup)
- [Configuration reference](#configuration-reference)
- [First login](#first-login)
- [API reference](#api-reference)
- [Deploying to production (3-tier)](#deploying-to-production-3-tier)
- [Alternative: everything on Railway](#alternative-everything-on-railway)
- [Security notes before you deploy](#security-notes-before-you-deploy)
- [What was fixed in this pass](#what-was-fixed-in-this-pass)

## Architecture

Three independent tiers, each deployable on its own:

```
Browser  ─────────►  Frontend (static HTML/JS)
                         │  fetch() with JWT Bearer token
                         ▼
                     Backend (Spring Boot REST API)
                         │  JDBC
                         ▼
                     Database (MySQL)
```

The frontend never talks to MySQL directly — it only calls the backend's
REST API over HTTPS, carrying a JWT it got from `/Auth/Login`. That's what
makes the three pieces independently deployable: the frontend just needs to
know the backend's URL, and the backend just needs to know the database's
URL.

## Features

- JWT-based login (`/Auth/Login`), stateless sessions
- Roles: `CUSTOMER`, `TEAM_LEAD`, `MANAGER`, `ADMIN`, enforced both at the
  URL level and the method level
- Complaint lifecycle: `OPEN → IN_PROGRESS → RESOLVED → CLOSED`, plus
  `ESCALATED` for unresolved high-priority items
- File attachments on complaints (up to 50MB)
- Complaint history/audit trail
- Search and filter (by keyword, priority, status), pagination and sorting
- Scheduled job that auto-escalates unresolved complaints, with email
  notifications
- Dashboard with aggregate stats
- User administration (create/update/delete, admin-only deletion)

## Project structure

```
├── src/main/java/com/arul/complaint_management/
│   ├── controller/       REST endpoints
│   ├── service/           interfaces
│   ├── serviceImp/        implementations
│   ├── entity/             JPA entities (User, Complaint, ComplaintHistory)
│   ├── dtos/                request/response payloads
│   ├── security/           JWT filter, JWT util, UserDetailsService
│   ├── config/              SecurityConfig, DataSeeder
│   ├── enums/                Role, ComplaintStatus, Priority
│   └── Exception/, ExceptionHandler/
├── src/main/resources/application.properties
├── frontend/                static SPA (HTML/CSS/vanilla JS)
├── Dockerfile                multi-stage build for the backend
├── docker-compose.yml     mysql + backend + frontend, wired together
└── pom.xml
```

## Quick start with Docker Compose

This runs all three tiers together on your machine — the fastest way to see
the whole thing working.

```bash
docker compose up --build
```

- Frontend: http://localhost:8081
- Backend API: http://localhost:8080
- MySQL: localhost:3306 (root / root — compose-only, not for production)

The frontend container is just nginx serving the static files in
`frontend/`, already pointed at `http://localhost:8081` via
`CORS_ALLOWED_ORIGINS` on the backend. Log in at the frontend URL using the
[seeded admin account](#first-login).

## Manual local setup

**Backend**

1. Create a MySQL database (or let `createDatabaseIfNotExist=true` do it).
2. Copy `.env.example`-style values as real environment variables, or just
   run with defaults for local dev (see [Configuration reference](#configuration-reference)).
3. `mvn spring-boot:run` (or run the `ComplaintManagementApplication` main
   class from your IDE).

**Frontend**

The frontend is static — any static file server works:

```bash
cd frontend
python3 -m http.server 5500
```

Open http://localhost:5500. On first load it points at
`http://localhost:8080` (see `frontend/js/config.js`). If your backend runs
somewhere else, open the app, click the gear icon at the bottom of the
sidebar, and set the API base URL — it's saved in the browser's
localStorage, no rebuild needed.

## Configuration reference

Everything below is read from environment variables, each with a sensible
local-dev default baked into `application.properties`.

| Variable | Purpose | Local default |
|---|---|---|
| `PORT` | Port the backend listens on (hosts like Render/Railway inject this) | `8080` |
| `DB_URL` | JDBC URL for MySQL | `jdbc:mysql://localhost:3306/complaint_db?createDatabaseIfNotExist=true` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | `root` |
| `JWT_SECRET` | HMAC signing key for JWTs (32+ random chars) | dev-only placeholder |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of frontend origins allowed to call the API | `http://localhost:8081,http://localhost:5500` |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | Credentials for the auto-seeded first admin (see below) | `admin@example.com` / `ChangeMe123!` |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | SMTP credentials for escalation emails | empty |
| `MAIL_HOST` / `MAIL_PORT` | SMTP server | `smtp.gmail.com` / `587` |
| `FILE_UPLOAD_DIR` | Where complaint attachments are stored | `uploads/` |

**Set `JWT_SECRET`, `DB_PASSWORD`, and `ADMIN_PASSWORD` to real, random
values before deploying anywhere public.** The defaults exist purely so
`docker compose up` works out of the box on your machine.

## First login

A fresh database has no users, and creating a user normally requires being
logged in already — so on first startup, if the `Users` table is empty, the
app seeds one `ADMIN` account automatically (see `config/DataSeeder.java`),
using `ADMIN_EMAIL` / `ADMIN_PASSWORD`.

1. Set `ADMIN_EMAIL` / `ADMIN_PASSWORD` before the very first boot (or accept
   the defaults for local testing).
2. Log in with that account.
3. Create your real users from the Users screen (or via
   `POST /arul/Users/Save`), then change or retire the seeded admin.

This only runs once — it's a no-op on every later restart once at least one
user exists.

## API reference

Base path is whatever host/port the backend runs on. All non-`/Auth`
endpoints require an `Authorization: Bearer <token>` header.

| Method | Path | Roles | Purpose |
|---|---|---|---|
| POST | `/Auth/Login` | public | Log in, returns a JWT |
| GET | `/arul/Users/Me` | any authenticated user | Current user's profile |
| POST | `/arul/Users/Save` | any authenticated user | Create a user |
| GET | `/arul/Users/Getall` | ADMIN, TEAM_LEAD, MANAGER | List users |
| PUT | `/arul/Users/UpdateUser` | ADMIN, MANAGER | Update a user |
| DELETE | `/arul/Users/DeleteUser` | ADMIN | Delete a user |
| GET | `/DashBoard` | any authenticated user | Aggregate stats |
| POST | `/Complaint/Save` | CUSTOMER | File a complaint (multipart, with attachment) |
| GET | `/Complaint/AllComplaints` | ADMIN, TEAM_LEAD, MANAGER | Paginated list |
| GET | `/Complaint/GetComplaintById` | any authenticated user | Single complaint |
| PUT | `/Complaint/UpdateComplaint` | TEAM_LEAD, MANAGER | Edit + change status |
| DELETE | `/Complaint/DeleteComplaint` | ADMIN | Delete a complaint |
| PUT | `/Complaint/Start/{id}` | TEAM_LEAD, MANAGER | Mark in progress |
| PUT | `/Complaint/Resolve/{id}` | TEAM_LEAD, MANAGER | Mark resolved |
| PUT | `/Complaint/Close/{id}` | CUSTOMER, MANAGER | Close a complaint |
| GET | `/Complaint/{id}/attachment` | any authenticated user | Download attachment |
| GET | `/Complaint/{id}/history` | any authenticated user | Audit trail |
| GET | `/Complaint/Filter` | ADMIN, MANAGER, TEAM_LEAD | Filter by priority/status |
| GET | `/Complaint/Search` | any authenticated user | Keyword search |
| GET | `/Complaint/TriggerEscalation` | ADMIN | Manually run the escalation job |

## Deploying to production (3-tier)

There are two honest paths here — pick based on what you already have running.

### Simplest path: bundle the frontend into the backend (one service)

If you already have the **backend alone** deployed somewhere (this is exactly
the Railway-backend-only situation) and just want to add the UI without
standing up a second service, this is the path: `pom.xml` now has a
`copy-frontend` build step that copies `frontend/` into the jar's static
resources at build time. Spring Boot serves static content from its own
root automatically, so once you `git add frontend/` (or pull these
changes into your existing repo) and push:

- the same domain that already serves your API now also serves the UI at
  `/`
- same origin, so **no CORS setup needed at all**
- `frontend/js/config.js` defaults to same-origin (`""`) for exactly this
  reason
- nothing else to configure — commit, push, and whatever's already
  redeploying your backend (Railway, Render, etc.) picks it up on the next
  build

This is genuinely the easiest option and is what I'd recommend unless you
specifically want the frontend on a CDN-backed static host separate from
the API.

### Alternative: separate frontend and backend services

Yes — this can be deployed exactly as three separate pieces: a static
frontend host, a backend host, and a managed database. That's actually the
more common setup than running all three in one place, because each layer
scales and redeploys independently, and most free/cheap tiers are
specialized (e.g. static hosts don't run long-lived Java processes).

Push this repo to GitHub first — both Render and Netlify deploy by
connecting to a Git repo.

### Step 1 — Database (Aiven for MySQL, free tier)

Render's own managed database offering is Postgres-only, so for MySQL
specifically use [Aiven](https://aiven.io), which has an always-free MySQL
plan (no credit card).

1. Sign up at aiven.io, then click **Create service**.
2. Select **MySQL**, choose the **Free** plan, pick a region, name the
   service (e.g. `complaint-db`), click **Create service**.
3. Wait for the status indicator to turn solid green (provisioning takes a
   couple of minutes).
4. Open the service's **Overview** tab — it has your host, port, username,
   password, and a ready-made connection string. Aiven connections require
   SSL; use the exact connection string format shown there rather than
   guessing at the flags.

### Step 2 — Backend (Render, using the Dockerfile already in this repo)

1. At [dashboard.render.com](https://dashboard.render.com), sign up/log in
   (GitHub login is easiest) and click **New → Web Service**.
2. Connect GitHub and select this repository. Render should detect the
   root-level `Dockerfile` automatically; set **Runtime** to `Docker` if it
   doesn't.
3. Pick an instance type (Free works for testing — it cold-starts after
   ~15 minutes of inactivity, so the first request after a break takes
   40–60s).
4. Under **Advanced**, add these environment variables (values from Step 1
   and the [configuration table](#configuration-reference) above):
   - `DB_URL` — your Aiven connection string
   - `DB_USERNAME`, `DB_PASSWORD` — from Aiven
   - `JWT_SECRET` — a long random string, not the repo default
   - `ADMIN_EMAIL`, `ADMIN_PASSWORD` — your real first-login credentials
   - `CORS_ALLOWED_ORIGINS` — a placeholder for now (e.g.
     `http://localhost:5500`); you'll update this in Step 4
   - `MAIL_USERNAME`, `MAIL_PASSWORD` — optional, for escalation emails
5. Click **Create Web Service**. Render builds the Docker image and
   deploys; watch progress on the service's **Events** tab.
6. Once live, you'll have a URL like `https://your-service.onrender.com`.
   Confirm it works end-to-end (and that `DataSeeder` ran) with:
   ```bash
   curl -X POST https://your-service.onrender.com/Auth/Login \
     -H "Content-Type: application/json" \
     -d '{"email":"<ADMIN_EMAIL>","password":"<ADMIN_PASSWORD>"}'
   ```
   A JSON response with a `token` means the backend and database are
   correctly wired together.

### Step 3 — Frontend (Netlify)

1. At [app.netlify.com](https://app.netlify.com), sign up/log in and click
   **Add new project → Import an existing project → GitHub**, then select
   this repo.
2. In the site configuration: set **Base directory** to `frontend`,
   leave **Build command** empty (there's no build step), and set
   **Publish directory** to `.` (relative to the base directory).
3. Click **Deploy site**. You'll get a URL like
   `https://random-name.netlify.app`.
4. Point it at your backend: either edit `frontend/js/config.js`'s
   `DEFAULT_API_BASE_URL` to your Render URL and push (Netlify
   auto-redeploys on every push), or just open the deployed site, click the
   gear icon at the bottom of the sidebar, and paste the Render URL —
   saved instantly in that browser's localStorage, no redeploy needed.

### Step 4 — Close the loop: update CORS

Go back to Render → your backend service → **Environment**, and set
`CORS_ALLOWED_ORIGINS` to your real Netlify URL (e.g.
`https://your-app.netlify.app`). Save — Render redeploys automatically.
Refresh the frontend and log in with your admin credentials; it should now
work end-to-end.

**Reference walkthroughs**, since seeing it done end-to-end helps more than
prose:
- ["Deploy Spring Boot MySQL on Render"](https://www.youtube.com/watch?v=UZ8Q-X3AGHA) — Dockerfile, Render web service, and a free hosted MySQL via Aiven. Covers Steps 1–2 above.
- ["Host Frontend on Netlify & Backend on Render"](https://www.youtube.com/watch?v=VbEws_C4QYY) — hosting the two tiers on separate platforms and connecting them, including the CORS step. Covers Steps 3–4.

Neither video uses this exact repo, so concrete file/service names will
differ — but the sequence (DB → backend, verify with `curl` → frontend →
CORS) is exactly what applies here.

## Alternative: everything on Railway

[Railway](https://railway.com) is a genuinely good fit for this project
specifically, because — unlike Render — it has **native managed MySQL**, so
all three tiers can live in a single Railway project with fast private
networking between them, instead of stitching together three separate
platforms.

**Trade-off to know up front:** Railway no longer has a permanent free
tier. New accounts get a $5 usage credit (roughly a 30-day trial for a
small project like this), after which you're on the Hobby plan (from
$5/month). Aiven's MySQL free tier and Netlify's static hosting, by
contrast, are free indefinitely. So: Railway is simpler to wire up and
nicer to operate, the Render/Aiven/Netlify combo above is cheaper to leave
running long-term. Either is a legitimate choice.

### 1. Create a project and add MySQL

1. At [railway.com/new](https://railway.com/new), create a new project.
2. Click **+ New → Database → Add MySQL**. Railway provisions it in
   seconds — no separate signup needed.

### 2. Add the backend service

1. In the same project, click **+ New → GitHub Repo** and select this
   repository. Railway auto-detects the root-level `Dockerfile` and builds
   from it.
2. Open the service's **Variables** tab and add:
   - `DB_URL` = `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}`
   - `DB_USERNAME` = `${{MySQL.MYSQLUSER}}`
   - `DB_PASSWORD` = `${{MySQL.MYSQLPASSWORD}}`
   - `JWT_SECRET`, `ADMIN_EMAIL`, `ADMIN_PASSWORD` — same as any other host
   - `CORS_ALLOWED_ORIGINS` — placeholder for now, update after step 3

   The `${{ServiceName.VAR}}` syntax is Railway's reference-variable
   system — it pulls values live from the MySQL service rather than you
   copy-pasting a password around. Note this is *not* the same as
   `${{MySQL.MYSQL_URL}}`, which Railway also provides but in
   `mysql://...` form — Spring needs the `jdbc:mysql://...` form built from
   the individual host/port/database variables above.
3. Go to **Settings → Networking** and click **Generate Domain** to get a
   public URL like `https://your-backend.up.railway.app`. Services aren't
   publicly reachable until you do this — by default they only talk to
   each other over Railway's private network.
4. Confirm it's alive the same way as the Render walkthrough: `POST
   /Auth/Login` with your seeded admin credentials should return a token.

### 3. Add the frontend

**If you already have the backend service above running** (e.g. you
started with a backend-only repo, like the Railway setup this section
started from), the [bundled single-service path](#simplest-path-bundle-the-frontend-into-the-backend-one-service)
is the easiest way to get the UI live: pull `frontend/` and the `pom.xml`
build-step changes into your repo, push, let Railway redeploy — the same
backend service now also serves the frontend at its existing domain. No
second service, no CORS setup. This is the recommended default; skip the
rest of this step if you go this route.

**If you'd rather keep them as separate services** (e.g. for a CDN-backed
static frontend, independent scaling, or a different framework later),
point a second service at the same repo, with its **Root Directory** set
to `/frontend` (Settings → this is Railway's monorepo support — each
service in a project can build from its own subfolder). Two ways to
build it:

- **Try zero-config static hosting first**: Railway has a static-site
  build path that needs no Dockerfile — just point Root Directory at
  `/frontend` and deploy. If it detects the plain HTML/CSS/JS and serves
  it, you're done.
- **If that doesn't pick it up**, this repo already includes a
  `frontend/Dockerfile` (a two-line nginx image) for exactly this case.
  Railway will use it automatically once it finds it in the service's root
  directory. One thing to set manually: nginx listens on port 80 and
  doesn't read Railway's `PORT` variable, so when you generate the domain
  in **Settings → Networking**, set the target port to `80`.

Either way, generate a domain for this service too, then point it at your
backend the same way as before — edit `frontend/js/config.js` and push, or
set the URL from the app's Settings gear icon.

### 4. Close the loop

Copy the frontend's Railway domain into the backend service's
`CORS_ALLOWED_ORIGINS` variable. Railway redeploys the backend
automatically on variable changes.



- **Rotate the Gmail app password.** The original `application.properties`
  had a real Gmail address and app password committed in plain text. It's
  been removed from this repo and replaced with an environment variable,
  but if this code was ever pushed anywhere (even a private repo), treat
  that password as compromised and generate a new App Password in your
  Google Account settings.
- **Change `JWT_SECRET` and `DB_PASSWORD`** away from the defaults for any
  deployment reachable from the internet.
- **Change the seeded admin's password** immediately after first login, or
  set `ADMIN_PASSWORD` to something real before the first boot.
- `spring.jpa.hibernate.ddl-auto=update` is convenient for a small project
  but auto-alters your schema on every startup; fine here, worth knowing
  before scaling this up.

## What was fixed in this pass

The `frontend/` folder, `Dockerfile`, and `docker-compose.yml` already
existed in the uploaded project, wired to call a specific set of backend
endpoints. Tracing that wiring against the actual controllers turned up a
few gaps that would have broken a real deployment; all are now fixed in
this codebase:

- **No way to create the first user at all.** `/arul/Users/Save` requires
  authentication, but nothing could authenticate yet on an empty database.
  Added `DataSeeder` to seed one admin account on first boot.
- **Missing `/arul/Users/Me` endpoint.** The frontend already called it
  (for session restore and role-based UI) but it didn't exist on the
  backend. Added it.
- **No CORS configuration anywhere**, despite `docker-compose.yml` already
  defining a `CORS_ALLOWED_ORIGINS` variable for it. Without this, a
  frontend on a different origin than the backend (i.e. any real
  deployment) would get every request blocked by the browser. Added a
  proper CORS configuration wired to that variable.
- **`@PreAuthorize` annotations were silently ignored** — Spring Security 6
  requires `@EnableMethodSecurity` for them to take effect, and it was
  missing. This meant any authenticated user, regardless of role, could
  start/resolve/close complaints or trigger escalation. Now enforced.
- **Two role-matching bugs from case-sensitive path matching**:
  `/arul/Users/GetAll` (config) vs. the actual `/arul/Users/Getall`
  (controller), and a `TEAMM_LEAD` typo instead of `TEAM_LEAD`. Both meant
  the intended role restriction silently never applied. Fixed.
- **Database URL, JWT secret, and mail credentials were hardcoded** in
  `application.properties` instead of reading the environment variables
  `docker-compose.yml` already declared for them. Now all wired through, so
  the same image can be deployed anywhere just by changing env vars.
- **Server port was hardcoded to 8080**, which breaks on hosts (Render,
  Railway) that inject a `PORT` env var and require the app to bind to it.
  Now reads `${PORT:8080}`.
