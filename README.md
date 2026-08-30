# 🏦 Rajarata Digital Bank (RDB)

[![Java Version](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Platform](https://img.shields.io/badge/Platform-Windows%20Desktop-0078D6?style=for-the-badge&logo=windows&logoColor=white)](https://microsoft.com/windows)
[![UI](https://img.shields.io/badge/UI-Java%20Swing%20%7C%20Glassmorphism-228B5E?style=for-the-badge)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/package-summary.html)
[![Architecture](https://img.shields.io/badge/Architecture-Modular%20Layered-4F46E5?style=for-the-badge)](#project-structure)
[![Security](https://img.shields.io/badge/Security-SHA--256%20%2B%20Salted-DC2626?style=for-the-badge)](#security--fraud-detection)

**Rajarata Digital Bank** is a desktop core banking and digital customer portal application developed with **Java 21** and **Java Swing**. It features a modern, glassmorphic UI, complete Role-Based Access Control (RBAC), multi-currency conversion, fraud detection heuristics, automated loan management, utility bill payments, and persistent transactional storage.

---

## 📑 Table of Contents

- [Key Features](#-key-features)
  - [Role-Based Access Control (RBAC)](#-role-based-access-control-rbac)
  - [Core Banking & Accounts](#-core-banking--accounts)
  - [Loans & Credit Management](#-loans--credit-management)
  - [Utility Bill Payments](#-utility-bill-payments)
  - [Security & Fraud Detection](#-security--fraud-detection)
  - [Analytics & Auditing](#-analytics--auditing)
- [Default Login Credentials](#-default-login-credentials)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Quick Run (Windows Batch)](#quick-run-windows-batch)
  - [Manual Compilation and Execution](#manual-compilation-and-execution)
- [Building the Windows Installer (.exe)](#-building-the-windows-installer-exe)
- [Data Storage & Configuration](#-data-storage--configuration)
- [UI & Design System](#-ui--design-system)

---

## ✨ Key Features

### 👥 Role-Based Access Control (RBAC)

The application provides dedicated portals tailored for three distinct user roles:

| Role | Capabilities |
| :--- | :--- |
| **👑 Admin** | System overview, user management (Admin, Staff, Customer), audit log inspection, global transaction surveillance, loan approvals, financial reports, and system settings. |
| **👔 Staff** | Customer onboarding, account directory, customer transaction review, statement generation, loan application evaluation and approvals/rejections. |
| **👤 Customer** | Multi-account dashboard, balance inquiries, deposit/withdrawal, inter-account transfers, transaction history, loan applications & repayment, utility bill payments, currency converter, account statements, and live notifications. |

---

### 💳 Core Banking & Accounts
- **Multiple Account Types**:
  - `SAVINGS` (Standard interest-bearing account)
  - `CHECKING` (Everyday transactional account with overdraft allowance)
  - `STUDENT` (Zero-minimum balance student tier)
  - `FIXED_DEPOSIT` (High-yield term deposit)
- **Financial Operations**:
  - Direct Deposits & Withdrawals with balance validation.
  - Real-time intra-bank fund transfers.
  - Periodic account statement generation and export.

---

### 📋 Loans & Credit Management
- **Loan Types**: Personal, Home, Education, Business, and Automobile Loans.
- **Automated EMI Calculation**: Interest rate computation and repayment schedule breakdown.
- **Approval Workflow**: Staff and Admins review submitted applications with full approval/rejection audit trail.
- **Repayment Tracking**: Real-time balance updates, installment tracking, and automated upcoming due date alerts.

---

### 💡 Utility Bill Payments
- Pay bills directly from available accounts for services including:
  - **Electricity** (CEB, LECO)
  - **Water Supply** (NWSDB)
  - **Telecom & Internet** (Dialog, SLT-Mobitel, Mobitel, Airtel)
  - **Insurance & Other Utilities**
- Instant transaction receipt generation and bill history logging.

---

### 🛡️ Security & Fraud Detection
- **Fraud Detection Engine**:
  - Alerts on large-value transactions exceeding threshold limits (e.g., LKR 500,000+).
  - Flags rapid-fire consecutive transactions within short time windows (e.g., 5+ within 5 minutes).
  - Automatic broadcast of fraud warnings to Staff and Admins.
- **Password Security**: Passwords hashed using SHA-256 with cryptographic salting (`PasswordUtil`).
- **Input Validation**: Sanitization and validation for National Identity Cards (NIC), emails, phone numbers, and amounts.

---

### 📊 Analytics & Auditing
- **Live Notification Feed**: Visual pulsating badge with unread counters for transactional events, loan updates, and fraud alerts.
- **Immutable Audit Log**: Records administrative operations, logins, withdrawals, and suspicious actions.
- **Management Reports**: Bank-wide summaries on assets, total deposits, active loans, and transaction volumes.
- **Currency Converter**: Real-time exchange rate conversions supporting USD, EUR, GBP, JPY, AUD, LKR, and more.

---

## 🔑 Default Login Credentials

On first run, the system automatically initializes default administrative and staff accounts:

| Role | Email / Username | Password |
| :--- | :--- | :--- |
| **System Admin** | `admin@rajaratabank.lk` | `Admin@1234` |
| **Bank Staff** | `kamal@rajaratabank.lk` | `Staff@1234` |
| **New Customer** | Click **Register** on the login screen to create a new customer account |

---

## 💻 Technology Stack

- **Language**: Java 21 (JDK 21 LTS)
- **UI Framework**: Java Swing & Java 2D Graphics
- **Design Language**: Custom Glassmorphic Dark/Light Palette with Segoe UI / Poppins typography
- **Data Persistence**: Java Object Serialization (`.dat` binary storage)
- **Packaging**: `jpackage` & WiX Toolset for standalone Windows installer generation

---

## 📁 Project Structure

```
Rajarata-Digital-Bank/
├── src/
│   ├── Main.java                     # Application entry point & look-and-feel config
│   └── bank/
│       ├── exception/                # Custom banking exceptions
│       │   ├── AuthenticationException.java
│       │   ├── BankingException.java
│       │   └── InsufficientFundsException.java
│       ├── model/                    # Domain models & entities
│       │   ├── Account.java          # Base account & account subclasses
│       │   ├── AccountFactory.java
│       │   ├── User.java             # User, Customer, Staff, Admin models
│       │   ├── Transaction.java      # Transaction records & types
│       │   ├── Loan.java             # Loan & installment models
│       │   ├── BillPayment.java      # Utility bill models
│       │   └── Notification.java     # System notification model
│       ├── service/                  # Business logic & services
│       │   ├── AccountService.java
│       │   ├── AuthService.java
│       │   ├── LoanService.java
│       │   ├── BillService.java
│       │   ├── FraudDetectionService.java
│       │   ├── StatementService.java
│       │   ├── CurrencyConverter.java
│       │   ├── NotificationService.java
│       │   ├── DataStore.java        # In-memory store & persistence sync
│       │   └── DataInitializer.java  # Default seed data
│       ├── ui/                       # Swing UI components & screens
│       │   ├── LoginFrame.java       # Authentication & registration UI
│       │   ├── MainFrame.java        # Main dashboard layout & navigation
│       │   ├── DashboardPanel.java   # Role-based dashboard widgets
│       │   ├── AccountsPanel.java    # Account management
│       │   ├── DepositWithdrawPanel.java
│       │   ├── TransferPanel.java
│       │   ├── LoansPanel.java
│       │   ├── BillPaymentPanel.java
│       │   ├── StatementPanel.java
│       │   ├── ReportsPanel.java
│       │   ├── AuditLogPanel.java
│       │   ├── UsersPanel.java
│       │   ├── UITheme.java          # Color palette, spacing, and fonts
│       │   └── UIComponents.java     # Custom glassmorphic cards, buttons, badges
│       └── util/                     # Utilities
│           ├── FileHandler.java      # Storage path resolver & object IO
│           ├── PasswordUtil.java     # SHA-256 password hashing
│           ├── IDGenerator.java      # Unique ID generator (ACC, TXN, LN, USR)
│           └── Validator.java        # Form and field validator
├── data/                             # Data directory & assets
│   ├── assets/                       # Application logos & icons (.ico, .png)
│   └── statements/                   # Generated account statements
├── RunApp.bat                        # One-click compile and run script
├── BuildInstaller.bat                # Windows EXE installer packaging script
└── RajarataDigitalBank-1.0.3.exe     # Standalone Windows installer (optional)
```

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK) 21** or later.
  - Download from [Eclipse Adoptium (Temurin 21)](https://adoptium.net/) or [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/).
  - Ensure `JAVA_HOME` environment variable is set and `java` / `javac` are in your `PATH`.

---

### Quick Run (Windows Batch)

Double-click `RunApp.bat` or run it from PowerShell/CMD:

```cmd
.\RunApp.bat
```

> **Note**: `RunApp.bat` will clean the previous `bin` directory, compile all source files with `--release 21`, and launch `Main`.

---

### Manual Compilation and Execution

1. **Compile all source files into the `bin` directory**:
   ```cmd
   mkdir bin
   javac --release 21 -d bin src\Main.java src\bank\exception\*.java src\bank\model\*.java src\bank\service\*.java src\bank\ui\*.java src\bank\util\*.java
   ```

2. **Run the application**:
   ```cmd
   java -cp bin Main
   ```

---

## 📦 Building the Windows Installer (.exe)

The project includes an automated script (`BuildInstaller.bat`) that compiles the application, packages it into a modular JAR, and uses `jpackage` to produce a standalone Windows `.exe` installer.

### Prerequisites for Installer Build
1. **JDK 21** (`jpackage` tool included).
2. **[WiX Toolset](https://wixtoolset.org/)** (v3.11+ or v4/v5 added to system `PATH`).
3. Application icon placed in `data\assets\logo.ico`.

### Build Steps
Run the installer script:
```cmd
.\BuildInstaller.bat
```
The output installer will be generated in `dist\output\RajarataDigitalBank-1.0.3.exe`.

---

## 💾 Data Storage & Configuration

By default, persistent data files (`users.dat`, `accounts.dat`, `loans.dat`, `bills.dat`, `notifications.dat`, `auditlog.dat`) are stored in your user application data directory:

- **Windows**: `%LOCALAPPDATA%\RajarataDigitalBank\data-v2\`
- **Linux / macOS**: `~/.RajarataDigitalBank/data-v2/`

### Custom Storage Location
To use a custom data directory, pass the `-Drdb.data.dir` system property when launching:

```cmd
java -Drdb.data.dir="C:\MyCustomBankData" -cp bin Main
```

---

## 🎨 UI & Design System

The UI uses a custom-built design system defined in [`UITheme.java`](file:///src/bank/ui/UITheme.java) and [`UIComponents.java`](file:///src/bank/ui/UIComponents.java):
- **Glassmorphic Cards**: Semi-transparent rounded card containers with subtle borders and shadows.
- **Dynamic Gradients**: Dual-stop linear and radial gradients on top navigation and hero headers.
- **Notification Pulse**: Animated radial pulse effect for real-time unread alerts.
- **Modern Typography & Tables**: Clean data tables with alternating zebra-striping and hover feedback.

---

## 📄 License

This project is developed for educational and digital banking management demonstrations. All rights reserved.
