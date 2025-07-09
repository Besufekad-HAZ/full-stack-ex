# M-PESA Acquisition Portal

A full-stack web application for M-PESA merchant acquisition with a responsive frontend and robust backend API.

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Frontend Pages](#frontend-pages)
- [Project Structure](#project-structure)
- [Screenshots](#screenshots)

## Project Overview

The M-PESA Acquisition Portal is a comprehensive web application designed to streamline the merchant onboarding process for M-PESA services. The application features a modern, responsive frontend built with Next.js and a robust backend API powered by Spring Boot.

## Features

### Frontend Features
- **Responsive Login Page**: Email and password validation with comprehensive password requirements
- **KYC Form**: Dynamic bank and branch selection with file upload capabilities
- **Confirmation Page**: Data review and submission with draft/submit options
- **Dashboard**: Application status tracking and management
- **Mobile-Responsive**: Optimized for all device sizes
- **Real-time Validation**: Form validation with user-friendly error messages
- **File Upload**: Support for PDF, PNG, and JPG bank account proofs

### Backend Features
- **RESTful API**: Comprehensive API endpoints for all operations
- **Database Integration**: PostgreSQL with JPA/Hibernate
- **Transaction Management**: Full transaction processing with reversal capabilities
- **Data Validation**: Server-side validation and error handling
- **CORS Support**: Cross-origin resource sharing for frontend integration
- **Application Management**: Draft and submitted application handling

## Tech Stack

### Frontend
- **Next.js 15**: React framework with TypeScript
- **Tailwind CSS**: Utility-first CSS framework
- **Headless UI**: Unstyled, accessible UI components
- **Heroicons**: Beautiful hand-crafted SVG icons
- **Axios**: HTTP client for API requests

### Backend
- **Spring Boot 3.2.0**: Java framework
- **Spring Data JPA**: Data access layer
- **Spring Web**: REST API development
- **Spring Validation**: Data validation
- **PostgreSQL**: Database
- **Maven**: Build tool

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17 or higher**
- **Maven 3.6 or higher**
- **Node.js 18 or higher**
- **npm or yarn**
- **PostgreSQL 12 or higher**

## Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd full-stack-ex
```

### 2. Backend Setup

```bash
cd backend/mpesa-backend
mvn clean install
```

### 3. Frontend Setup

```bash
cd frontend
npm install
```

## Database Setup

### 1. Install PostgreSQL

```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

### 2. Start PostgreSQL Service

```bash
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### 3. Create Database and User

```bash
sudo -u postgres psql
```

In the PostgreSQL shell:

```sql
CREATE DATABASE mpesa_db;
CREATE USER mpesa_user WITH PASSWORD 'mpesa_pass';
GRANT ALL PRIVILEGES ON DATABASE mpesa_db TO mpesa_user;
\q
```

### 4. Initialize Database Schema

The application will automatically create the tables and populate sample data on startup.

## Running the Application

### 1. Start the Backend Server

```bash
cd backend/mpesa-backend
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080`

### 2. Start the Frontend Server

```bash
cd frontend
npm run dev
```

The frontend will be available at `http://localhost:3000`

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication
The current implementation uses session-based authentication for demo purposes.

### Endpoints

#### Banks API
- **GET /api/banks**: Get all banks
- **GET /api/banks/{id}**: Get bank by ID

#### Branches API
- **GET /api/branches?bank_id={id}**: Get branches for a specific bank
- **GET /api/branches/{id}**: Get branch by ID

#### Applications API
- **POST /api/applications/submit**: Submit an application
- **GET /api/applications/{id}**: Get application by ID
- **GET /api/applications**: Get all applications
- **GET /api/applications/account/{accountNumber}**: Get application by account number

#### Transactions API
- **POST /api/transaction**: Process a transaction
- **POST /api/reverse**: Reverse a transaction
- **GET /api/transactions/{accountNumber}**: Get transaction history

### Request/Response Examples

#### Submit Application
```bash
curl -X POST http://localhost:8080/api/applications/submit \
  -H "Content-Type: application/json" \
  -d '{
    "bankId": 1,
    "branchId": 1,
    "bankName": "Commercial Bank of Ethiopia",
    "branchName": "Addis Ababa Main Branch",
    "accountName": "John Doe",
    "accountNumber": "1234567890",
    "proofOfBankAccount": "bank_statement.pdf",
    "status": "SUBMITTED"
  }'
```

#### Response
```json
{
  "status": "SUCCESS",
  "message": "Application submitted successfully",
  "applicationId": 1,
  "accountNumber": "1234567890",
  "applicationStatus": "Submitted",
  "submissionDate": "2024-01-15T10:30:00"
}
```

## Frontend Pages

### 1. Login Page (`/`)
- Email and password validation
- Password requirements display
- Show/hide password toggle
- Responsive design

### 2. KYC Form (`/kyc-form`)
- Bank selection dropdown
- Dynamic branch selection
- Account information fields
- File upload for bank account proof
- Form validation

### 3. Confirmation Page (`/confirmation`)
- Review entered data
- Submit or save as draft options
- Success/error feedback

### 4. Dashboard (`/dashboard`)
- Application status display
- Quick actions
- Status guide

## Project Structure

```
full-stack-ex/
├── backend/
│   └── mpesa-backend/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   │   └── com/mpesa/acquisition/
│       │   │   │       ├── controller/
│       │   │   │       ├── dto/
│       │   │   │       ├── entity/
│       │   │   │       ├── repository/
│       │   │   │       └── MpesaBackendApplication.java
│       │   │   └── resources/
│       │   │       ├── application.properties
│       │   │       └── schema.sql
│       │   └── test/
│       └── pom.xml
├── frontend/
│   ├── app/
│   │   ├── confirmation/
│   │   ├── dashboard/
│   │   ├── kyc-form/
│   │   ├── globals.css
│   │   ├── layout.tsx
│   │   └── page.tsx
│   ├── public/
│   ├── package.json
│   └── tailwind.config.ts
└── README.md
```

## Database Schema

### Tables

#### tbl_bank
- `id` (Primary Key, Auto Increment)
- `value` (VARCHAR, Bank Name)
- `created_at` (TIMESTAMP)

#### tbl_branch
- `id` (Primary Key, Auto Increment)
- `value` (VARCHAR, Branch Name)
- `bank_id` (Foreign Key)
- `created_at` (TIMESTAMP)

#### tbl_application
- `id` (Primary Key, Auto Increment)
- `bank_name` (VARCHAR)
- `branch_name` (VARCHAR)
- `account_name` (VARCHAR)
- `account_number` (VARCHAR, Unique)
- `proof_of_bank_account` (VARCHAR)
- `status` (ENUM: Draft, Submitted)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

#### tbl_transaction_history
- `id` (Primary Key, Auto Increment)
- `transaction_id` (VARCHAR, Unique)
- `account_number` (VARCHAR)
- `amount` (DECIMAL)
- `narration` (TEXT)
- `status` (ENUM: SUCCESS, FAILED, REVERSED)
- `created_at` (TIMESTAMP)

## Testing

### Backend Tests
```bash
cd backend/mpesa-backend
mvn test
```

### Frontend Build
```bash
cd frontend
npm run build
```

## Deployment

### Backend
```bash
cd backend/mpesa-backend
mvn clean package
java -jar target/mpesa-backend-1.0-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm run build
npm start
```

## Features Implementation

### ✅ Login Page
- [x] Responsive design
- [x] Email validation
- [x] Password validation (8+ chars, uppercase, lowercase, number, special char)
- [x] Show/hide password
- [x] Login button disabled until validation passes
- [x] Redirect to dashboard on successful login

### ✅ KYC Form
- [x] Bank dropdown populated from API
- [x] Branch dropdown based on bank selection
- [x] Account name and number validation
- [x] File upload (PDF, PNG, JPG)
- [x] Form validation
- [x] Next button to proceed to confirmation

### ✅ Confirmation Page
- [x] Display entered data for review
- [x] Submit button (saves with "Submitted" status)
- [x] Save as Draft button (saves with "Draft" status)
- [x] Success/error feedback

### ✅ Backend APIs
- [x] GET /api/banks - Fetch all banks
- [x] GET /api/branches - Fetch branches by bank ID
- [x] POST /api/transaction - Process transactions
- [x] POST /api/reverse - Reverse transactions
- [x] POST /api/applications/submit - Submit applications

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.

## Support

For support, please contact the development team or create an issue in the repository.

---

**Note**: This is a demonstration application built for educational purposes. For production use, additional security measures, authentication, and error handling should be implemented.
