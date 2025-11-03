# Personal Finance Manager (Android App)

A comprehensive, offline-first Android application designed for personal expense tracking, budget management, and savings goal monitoring. This project was developed as coursework for the Data Management 2 module, demonstrating advanced database concepts including a dual-database architecture, data synchronization, and enterprise-grade backend practices.

## Features

- **User Authentication:** Secure user registration and login system.
- **Offline-First Expense Tracking:** Add, view, and manage expenses even without an internet connection.
- **Monthly Budgeting:** Set and view monthly budget limits to control spending.
- **Savings Goal Management:** Create, update, delete, and track progress towards your financial goals.
- **Robust Data Synchronization:**
    - **Manual Sync:** A user-triggered button to immediately back up local data to the central server.
    - **Periodic Sync:** Automatic, battery-friendly background synchronization using Android's WorkManager.
- **Polished User Interface:** A modern, intuitive UI with features like a splash screen, loading indicators, and clean layouts for data entry and display.

## System Architecture

The application is built on a modern, scalable three-tier architecture:

1.  **Android Client (Front-End):**
    -   Written in **Kotlin**.
    -   Utilizes **MVVM (Model-View-ViewModel)** architecture for a clean separation of concerns.
    -   **Room Persistence Library** for managing the local **SQLite** database.
    -   **Retrofit & OkHttp** for making network requests to the backend API.
    -   **WorkManager** for scheduling reliable, periodic background sync tasks.

2.  **Backend API (Middleware):**
    -   A lightweight REST API built with **Python** and the **Flask** framework.
    -   Acts as a secure bridge between the Android app and the Oracle database.
    -   Receives JSON data from the app and executes PL/SQL procedures on the database.

3.  **Oracle Database (Back-End):**
    -   An enterprise-grade **Oracle Database** serves as the central "source of truth."
    -   Stores all user data, including profiles, expenses, budgets, and savings goals.
    -   Utilizes **PL/SQL Stored Procedures** (e.g., `MERGE` statements) to encapsulate business logic and enhance security.
    -   Implements a "Last Write Wins" conflict resolution strategy using timestamps.

 

## Database Schema

The project uses two database schemas with compatible structures to facilitate synchronization.

#### SQLite (Local)
Designed for speed and offline use, with additional columns for tracking sync status.

#### Oracle (Central)
Designed for data integrity and powerful analytics, with advanced constraints and PL/SQL procedures.


## Getting Started

To set up and run this project, you will need:
-   Android Studio (latest version)
-   An Oracle Database instance (e.g., Oracle XE)
-   Python 3.x installed
-   Oracle SQL Developer
-   Postman (for API testing)

### 1. Database Setup

1.  Run the `oracle_schema.sql` and `oracle_procedures.sql` scripts in SQL Developer to create the tables and stored procedures.
2.  (Optional) Run the `oracle_data.sql` script to populate the database with sample data.

### 2. Backend API Setup

1.  Navigate to the `api_backend` directory.
2.  Install the required Python libraries:
    ```bash
    pip install Flask
    pip install oracledb
    ```
3.  Configure your database credentials in `app.py`.
4.  If required, download the Oracle Instant Client and configure the path in `app.py`.
5.  Run the server:
    ```bash
    python app.py
    ```
    The server will start on `http://127.0.0.1:5000`.

### 3. Android App Setup

1.  Open the project in Android Studio.
2.  Update the `BASE_URL` in `network/RetrofitInstance.kt`:
    -   For the Android Emulator, use `http://10.0.2.2:5000/`.
    -   For a physical device, use your computer's local network IP address (e.g., `http://192.168.1.100:5000/`).
3.  Build and run the app on an emulator or a physical device.


## Coursework Details

This project fulfills all requirements for the **Data Management 2** module, including:
-   Logical and Physical Database Design
-   Implementation of SQLite and Oracle Databases
-   Development of PL/SQL Stored Procedures
-   Manual and Periodic Data Synchronization
-   Conflict Resolution
-   Generation of 5 Insightful Financial Reports
-   Detailed discussion of Security, Privacy, Backup, and Migration strategies.
