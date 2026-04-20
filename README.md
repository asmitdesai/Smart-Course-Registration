# Smart Course Registration System

A modern, multi-layered JavaFX application for managing university course registrations, student grades, attendance, and faculty scheduling.

## 🚀 Features

- **Role-Based Authentication**: Secure login for Students, Faculty, and Admins.
- **Student Dashboard**: 
  - Browse available courses.
  - Real-time registration with prerequisite and seat availability checks.
  - View grades and attendance records.
- **Faculty Dashboard**:
  - Manage assigned courses.
  - Record student attendance.
  - Submit student grades.
- **Admin Dashboard**:
  - Manage course schedules.
  - Conflict detection for faculty assignments and time slots.
  - Overview of all registrations and courses.
- **Modern UI**: Dark-themed, card-based interface with smooth transitions.
- **Persistent Storage**: SQLite database integration for data integrity.

## 🛠️ Tech Stack

- **Language**: Java 21
- **Framework**: JavaFX 21
- **Build Tool**: Maven
- **Database**: SQLite
- **Security**: jBCrypt for password hashing

## 📋 Prerequisites

- **Java Development Kit (JDK) 21** (e.g., Amazon Corretto 21)
- **Maven** 3.8+

## ⚙️ Setup & Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/asmitdesai/Smart-Course-Registration.git
   cd Smart-Course-Registration
   ```

2. **Run the application**:
   ```bash
   mvn javafx:run
   ```

## 🔐 Login Credentials (Seed Data)

The application automatically seeds a few accounts on the first run:

| Role | Email | Password |
|------|-------|----------|
| **Student** | `student@uni.edu` | `password123` |
| **Faculty** | `faculty@uni.edu` | `password123` |
| **Admin** | `admin@uni.edu` | `password123` |

## 🏗️ Architecture

The project follows a clean multi-layered architecture:
- **`com.smartcourse.model`**: Domain entities.
- **`com.smartcourse.dao`**: Data access objects using JDBC.
- **`com.smartcourse.service`**: Business logic layer.
- **`com.smartcourse.controller`**: JavaFX UI controllers.
- **`com.smartcourse.util`**: Utility classes.
- **`src/main/resources/fxml`**: UI layout files.
- **`src/main/resources/css`**: Application styling.

---
Developed as part of the Smart Course Registration System project.