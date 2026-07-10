# AI Gmail Management System - Backend

## 📌 Overview

The AI Gmail Management System Backend is a Spring Boot application that provides secure APIs for managing Gmail operations with AI-powered automation. It supports email management, authentication, categorization, priority detection, spam filtering, and analytics to improve productivity.

---

## 🚀 Features

* 🔐 JWT Authentication & Authorization
* 👤 User Registration and Login
* 📧 Gmail Integration
* 📥 Email Synchronization
* 🤖 AI-Based Email Categorization
* ⭐ Priority Detection
* 🛡️ Spam Detection
* 🏷️ Smart Email Labels
* 📂 Department-Based Email Assignment
* 📎 Attachment Management
* 🔍 Search and Filter Emails
* 📊 Dashboard Analytics
* 📈 Email Statistics
* 🔔 Notification Support
* 📜 RESTful APIs
* 📝 Swagger API Documentation

---

## 🛠️ Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* Maven

### Database

* MySQL

### Authentication

* JWT (JSON Web Token)
* OAuth 2.0 (Google)

### AI Integration

* Google Gemini API

### Documentation

* Swagger / OpenAPI

---

## 📂 Project Structure

```text
src/
 ├── main/
 │   ├── java/
 │   ├── resources/
 │   └── application.properties
 └── test/
```

---

## ⚙️ Prerequisites

Before running the project, install:

* Java JDK 21 or later
* Maven
* MySQL
* Git
* IntelliJ IDEA or VS Code

---

## 🔧 Installation

### Clone Repository

```bash
git clone https://github.com/aman123-morya/postmark_ai_backend.git
```

### Navigate to Project

```bash
cd postmark_ai_backend
```

### Configure Database

Update `application.properties` with your MySQL credentials.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Configure Environment Variables

Create the required environment variables instead of storing secrets in the repository.

Example:

```text
GEMINI_API_KEY=YOUR_API_KEY
GOOGLE_CLIENT_ID=YOUR_CLIENT_ID
GOOGLE_CLIENT_SECRET=YOUR_CLIENT_SECRET
```

### Run the Project

Using Maven:

```bash
mvn spring-boot:run
```

Or:

```bash
./mvnw spring-boot:run
```

---

## 📚 API Documentation

After starting the application, open:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 📌 Main Modules

* Authentication
* User Management
* Gmail Integration
* Email Management
* AI Categorization
* Spam Detection
* Priority Detection
* Analytics Dashboard
* Notifications

---

## 🔒 Security

* JWT Authentication
* Password Encryption
* Role-Based Authorization
* Secure REST APIs
* Environment Variables for Secrets

---

## 📈 Future Enhancements

* AI Email Reply Generation
* Email Summarization
* Voice Commands
* Multi-Account Gmail Support
* Sentiment Analysis
* Calendar Integration
* Mobile Application
* Real-Time Notifications

---

## 👨‍💻 Author

**Aman Kumar**

B.Tech Computer Science Engineering

GitHub: https://github.com/aman123-morya

---

## ⭐ Support

If you like this project, please give it a ⭐ on GitHub.

Contributions, issues, and feature requests are always welcome.
