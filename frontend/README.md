<div align="center">

# 🚀 User Management System

A modern **Full Stack User Management System** built with **Spring Boot**, **React**, **JWT Authentication**, and **MySQL**.

<p>

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-6DB33F?style=for-the-badge&logo=springboot)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Authentication-red?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-success?style=for-the-badge)

</p>

</div>

---

# 📖 Overview

This project is a complete **User Management System** developed using modern backend and frontend technologies.

It demonstrates authentication, authorization, CRUD operations, pagination, searching, sorting, validation, and secure REST APIs following best practices.

---

# ✨ Features

## 🔐 Authentication

- JWT Authentication
- Refresh Token
- Secure Login
- Logout
- Password Encryption (BCrypt)
- Protected Routes

---

## 👥 User Management

- Create User
- Update User
- Delete User
- View User Details
- User List
- Pagination
- Search
- Sorting

---

## 🛡 Security

- Spring Security
- JWT Access Token
- Refresh Token
- BCrypt Password Encryption
- Role-Based Authorization
- Protected API Endpoints

---

## ✅ Validation

- Required Fields
- Email Validation
- Password Validation
- Username Validation
- Duplicate Email Protection
- Duplicate Username Protection

---

## ⚠ Error Handling

- Global Exception Handler
- Validation Errors
- Authentication Errors
- JWT Errors
- Custom Exceptions
- HTTP Status Codes

---

# 🛠 Tech Stack

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- JWT
- Maven
- MySQL
- Swagger / OpenAPI

---

## Frontend

- React
- Vite
- React Router
- Axios
- Bootstrap 5
- Context API
- React Toastify
- SweetAlert2

---

# 📂 Project Structure

```
user-management-system
│
├── frontend
│   ├── components
│   ├── pages
│   ├── context
│   ├── services
│   ├── styles
│   └── api
│
├── src
│   └── main
│       └── java
│           └── com.nazar.usermanagementsystem
│               ├── config
│               ├── controller
│               ├── dto
│               ├── entity
│               ├── exception
│               ├── mapper
│               ├── repository
│               ├── security
│               └── service
│
└── README.md
```

---

# 🌐 REST API

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | `/auth/login` | Login |
| POST | `/auth/refresh` | Refresh Token |
| POST | `/auth/logout` | Logout |
| GET | `/users` | Get Users |
| GET | `/users/{id}` | Get User |
| POST | `/users` | Create User |
| PUT | `/users/{id}` | Update User |
| DELETE | `/users/{id}` | Delete User |

---

# 📸 Screenshots

## Login

> *(Add screenshot here)*

---

## Dashboard

> *(Add screenshot here)*

---

## User Management

> *(Add screenshot here)*

---

# ⚙ Installation

## Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/user-management-system.git
```

---

## Backend

```bash
cd user-management-system
```

Configure your database inside:

```
src/main/resources/application.properties
```

Run:

```bash
mvn spring-boot:run
```

Backend:

```
http://localhost:8080
```

Swagger:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend:

```
http://localhost:5173
```

---

# 🚀 Future Improvements

- Docker
- Docker Compose
- Email Verification
- Password Reset
- User Profile Images
- Unit Tests
- Integration Tests
- CI/CD

---

# 👨‍💻 Author

### Nazar Svytka

Junior Full Stack Developer

Java • Spring Boot • React • MySQL

---

<div align="center">

⭐ If you like this project, don't forget to leave a star!

</div>