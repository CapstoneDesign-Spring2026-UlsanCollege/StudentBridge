# Midterm Pitch Notes

Last updated: 2026-05-01

## Pitch Goal

Present StudentBridge as an investor-style capstone product: a focused web platform that helps international students in South Korea find part-time jobs and gives employers a clearer way to reach student workers.

Time target: 8 minutes pitch + 2 minutes Q&A.

## Recommended Pitch Structure

| Section | Time | Speaker | Key point |
|---|---:|---|---|
| 1. Hook / intro | 0:30 | `[TODO: assign]` | International students need safe, understandable part-time job access. |
| 2. Problem | 1:00 | `[TODO: assign]` | Current job search is scattered, language-heavy, and not student-specific. |
| 3. Users | 0:45 | `[TODO: assign]` | Primary users are international students; secondary users are local employers. |
| 4. Solution | 1:00 | `[TODO: assign]` | StudentBridge centralizes job discovery, account creation, and future applications. |
| 5. Core features | 1:00 | `[TODO: assign]` | Homepage, job search, register/login, planned application storage. |
| 6. Current progress | 0:45 | `[TODO: assign]` | Frontend works; servlet/MySQL account flow is partially integrated. |
| 7. Demo | 1:30 | `[TODO: assign]` | Show homepage, job search filters, register/login path. |
| 8. Roadmap | 0:45 | `[TODO: assign]` | Connect applications, add job database, improve security/testing. |
| 9. Team roles | 0:30 | `[TODO: assign]` | Each member owns a clear area. |
| 10. Ask | 0:15 | `[TODO: assign]` | Ask for pilot users, employer contacts, and technical mentorship. |

## Problem

International students in South Korea often need part-time jobs but face language barriers, limited local networks, unclear job requirements, and scattered information. General job platforms do not focus on international student constraints such as part-time availability, beginner-friendly work, and English-friendly communication.

## Target Users

| User | Need |
|---|---|
| International students | Find part-time jobs they can realistically apply for. |
| Local employers | Reach nearby student workers more easily. |
| Universities/support offices | Recommend a clearer employment-support tool. |

## Solution

StudentBridge provides a simple web platform where students can search job listings, create an account, and eventually apply directly. Employers will be able to post jobs and review student applications in a later MVP phase.

## Core Features To Present

| Feature | Demo status |
|---|---|
| Homepage and product explanation | Ready |
| Job search/filtering | Ready as frontend demo |
| Register/login UI | Ready |
| Servlet + MySQL account backend | Partially ready; must verify before pitch |
| Job application storage | Planned next |

## Current Progress

- Homepage and job search interface are implemented.
- Register and login servlets exist.
- MySQL connection helper exists.
- Local Tomcat deployment script exists.
- Documentation now includes setup, testing, demo, roadmap, roles, and AI ownership audit.

## Demo

Use `docs/DEMO_PLAN.md` as the source of truth for the live demo and backup.

Main flow:

1. Homepage.
2. Job search filters.
3. Register/login through Tomcat.
4. Explain next application milestone.

## Roadmap Summary

| Phase | Focus |
|---|---|
| Midterm | Prove the problem, show current product, demonstrate job search and account flow. |
| Next sprint | Stabilize Tomcat/MySQL setup and connect application form. |
| Final demo | Show end-to-end student application flow and basic employer posting/review. |
| After final | Add language support, security hardening, notifications, and richer employer tools. |

## Team Roles Slide

Use `docs/TEAM_ROLES.md` for the full table. Keep the slide simple:

- Mezbah: backend, database, homepage support.
- Sami: frontend/job search support.
- Ali: login/job page support.
- Guramg: documentation and evidence.
- Dipesh: testing and QA.

Verify spelling, responsibilities, and presentation roles before the pitch.

## Ask

StudentBridge asks for:

- Pilot feedback from international students.
- Local employer contacts willing to share real part-time job examples.
- Technical mentorship for secure authentication and reliable deployment.
- Permission/support to test the platform with a small university audience.

## Brochure / QR Code

The brochure should include:

- Project name and one-sentence value proposition.
- Problem and target users.
- Three core features.
- Screenshot or QR code to demo/video.
- Team members and repo link.
- Honest current status and next milestone.

QR code destination: `[TODO: add final QR link]`

## Repo Evidence To Show

| Evidence | Link |
|---|---|
| GitHub repository | <https://github.com/CapstoneDesign-Spring2026-UlsanCollege/StudentBridge> |
| Sprint packet issue | `[TODO: link]` |
| Demo video | `[TODO: link]` |
| Screenshots | `[TODO: link]` |
| Pull requests | `[TODO: links]` |
| Testing notes | `docs/TESTING_NOTES.md` |
| AI audit | `docs/AI_CODE_OWNERSHIP_AUDIT.md` |

## Q&A Prep

| Likely question | Suggested answer |
|---|---|
| What makes this different from a normal job board? | It focuses only on international-student constraints: part-time availability, beginner-friendly roles, language expectations, and university-area employers. |
| What works today? | Homepage, job search filters, register/login code, local Tomcat deployment path, and MySQL connection setup. |
| What is not done yet? | Job application storage, employer posting dashboard, password hashing, and production deployment. |
| Why is the roadmap realistic? | The team is focusing on one main flow instead of building every feature at once. |
| What support do you need? | Pilot users, employer sample listings, and technical review for authentication/deployment. |
