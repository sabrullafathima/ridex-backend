
Ridex Backend
=============
Ridex is a mini Uber-like ride-hailing backend application 
built with Spring Boot, Java 21, Spring Security (JWT), WebSockets, Redis, and MySQL.
It supports user registration, ride requests, ride acceptance, ride start, ride completion, ride cancellation, 
real-time notifications, simulated payment processing, and email notifications.

Latest Updates
==============
[v1.2.0] - 2025-10-17
-> Added Redis caching for rides to improve read performance and reduce database hits.
-> Minor code cleanup and logging improvements.

Features
=========
-> User registration and login with role-based access (RIDER / DRIVER)
-> Request, accept, start, complete, and cancel rides
-> Real-time notifications using WebSockets (STOMP)
-> Email notifications for ride requests (Mailtrap)
-> Simulated payment processing and fare calculation
-> JWT-based authentication and role-based authorization
-> Event-driven architecture for ride notifications
-> Redis caching for ride entities to reduce DB load and improve performance

Tech Stack
==========
-> Backend: Java 21, Spring Boot, Spring Data JPA, Hibernate
-> Database: MySQL
-> Security: Spring Security, JWT
-> WebSockets: STOMP protocol
-> Cache: Redis
-> Email: JavaMailSender, Mailtrap
-> Build Tool: Maven

Setup & Installation
====================
1. Clone the repository:
   ---------------------
   git clone https://github.com/sabrullafathima/ridex-backend.git
   cd ridex-backend

2. Configure environment variables (.env or application.properties):
   -----------------------------------------------------------------
   # JWT
   JWT_SECRET=<your_base64_secret>
   JWT_EXPIRATION=86400000

   # Mailtrap
   MAILTRAP_HOST=sandbox.smtp.mailtrap.io
   MAILTRAP_PORT=2525
   MAILTRAP_USER_NAME=<username>
   MAILTRAP_PASSWORD=<password>
   MAILTRAP_EMAIL=<from_email>

   # Redis
   REDIS_HOST=localhost
   REDIS_PORT=6379

3. Create database:
   ----------------
   CREATE DATABASE ridex_db;

4. Run the backend:
   ----------------
   mvn spring-boot:run

5. Access:
   ------
   API: http://localhost:8080/api
   WebSocket endpoint: ws://localhost:8080/ws


API Endpoints
=============
| Endpoint                            | Method | Description                                          | Auth Required |
| ----------------------------------- | ------ | ---------------------------------------------------- | ------------- |
| `/api/auth/register`                | POST   | Register a new user                                  | No            |
| `/api/auth/login`                   | POST   | User login and JWT token generation                  | No            |
| `/api/user/rides/request`           | POST   | Request a new ride                                   | Yes           |
| `/api/user/rides/{rideId}/cancel`   | POST   | Cancel a ride (use `?cancelledBy=RIDER` or `DRIVER`) | Yes           |
| `/api/user/rides/{rideId}/start`    | POST   | Start an accepted ride                               | Yes (Driver)  |
| `/api/user/rides/{rideId}/accept`   | POST   | Accept a requested ride                              | Yes (Driver)  |
| `/api/user/rides/{rideId}/complete` | POST   | Complete a ride                                      | Yes (Driver)  |


WebSocket & Notifications
=========================
-> Endpoint: /ws
-> Topics: /topic/driver/{driverId}, /topic/rider/{riderId}
-> Notifications: RIDE_REQUEST, RIDE_ACCEPTED, RIDE_STARTED, RIDE_COMPLETED, RIDE_CANCELLED

Payment
=======
   Simulated fare calculation:
   ---------------------------
    Payment entity stores status, amount, paymentMethod, and related ride.

Security
========
-> JWT-based authentication with Authorization: Bearer <token>
-> Role-based access (RIDER / DRIVER)
-> Passwords hashed with BCrypt

Author
=====
Sabrulla Fathima – Software Engineer
GitHub: https://github.com/sabrullafathima/