# 📚 Library Management System (Java + MySQL)

A simple console-based Library Management System built using Java and MySQL.  
This project demonstrates basic Java programming concepts along with JDBC connectivity for database operations.

---

## 🚀 Features

- Add new books  
- View all books  
- Issue books  
- Return books  
- MySQL database integration  
- Menu-driven console application  

---

## 🛠 Technologies Used

- Java  
- MySQL  
- JDBC  

---

## ⚙️ Database Setup

Open MySQL and run the following commands:

```sql
CREATE DATABASE librarydb;
USE librarydb;

CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    author VARCHAR(100),
    quantity INT
);

CREATE TABLE issued_books (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT,
    issued_to VARCHAR(100),
    issue_date DATE
);