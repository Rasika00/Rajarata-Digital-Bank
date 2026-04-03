# Rajarata Digital Bank

A desktop banking application built with Java Swing, providing a full-featured digital banking experience for customers, staff, and administrators.

## Features

- **Authentication** – Secure login with password hashing, failed-attempt tracking, and account locking
- **Account Management** – Open and manage Savings, Checking, Student, and Fixed Deposit accounts
- **Transactions** – Deposits, withdrawals, fund transfers, and full transaction history
- **Bill Payments** – Pay utility and other bills directly from your account
- **Loans** – Apply for Personal, Home, Vehicle, Education, and Business loans with instalment tracking
- **Currency Converter** – Real-time currency conversion utility
- **Statements** – Generate and view account statements
- **Notifications** – In-app alerts including upcoming loan instalment reminders
- **Audit Log** – Full audit trail of system actions (Admin/Staff)
- **Reports** – Summary reports for administrators
- **Fraud Detection** – Background service to flag suspicious activity
- **Role-Based Access** – Separate dashboards and permissions for Customers, Staff, and Admins

## Prerequisites

- [Eclipse Adoptium JDK 21](https://adoptium.net/) (or any JDK 21+)
- Windows OS (the provided scripts are `.bat` files)

## Getting Started

### Run from Source

1. Clone the repository:
   ```
   git clone https://github.com/Rasika00/Rajarata-Digital-Bank.git
   cd Rajarata-Digital-Bank
   ```

2. Open `RunApp.bat` and update the `JAVA_HOME` path if your JDK is installed in a different location:
   ```bat
   set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot"
   ```

3. Double-click `RunApp.bat` or run it from the command line:
   ```
   RunApp.bat
   ```
   This will compile all sources and launch the application.

### Run the Installer

A pre-built Windows installer is included:

```
RajarataDigitalBank-1.0.3.exe
```

Double-click it to install the application, then launch it from the Start Menu or desktop shortcut.

## Default Credentials

The application seeds the following accounts on first run:

| Role  | Email                        | Password    |
|-------|------------------------------|-------------|
| Admin | admin@rajaratabank.lk        | Admin@1234  |
| Staff | kamal@rajaratabank.lk        | Staff@1234  |

New customer accounts can be registered from the login screen.

## Project Structure

```
src/
├── Main.java                  # Application entry point
└── bank/
    ├── exception/             # Custom exceptions
    ├── model/                 # Domain models (User, Account, Loan, etc.)
    ├── service/               # Business logic and data persistence
    ├── ui/                    # Swing UI panels and frames
    └── util/                  # Utilities (file I/O, password hashing)
data/
├── accounts.dat               # Persisted account data
├── users.dat                  # Persisted user data
├── loans.dat                  # Persisted loan data
├── bills.dat                  # Persisted bill payment data
├── notifications.dat          # Persisted notifications
├── auditlog.dat               # Audit log entries
└── statements/                # Generated account statements
```

## Building an Installer

To rebuild the Windows installer, run:

```
BuildInstaller.bat
```

## Technology Stack

- **Language**: Java 21
- **UI Framework**: Java Swing
- **Data Persistence**: Java object serialization (`.dat` files)
- **Build**: Manual compilation via `javac` (no external build tool required)
