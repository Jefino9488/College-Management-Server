# LearnFree API Documentation

This document provides details on how to interact with the LearnFree API. Below are the available endpoints, their usage, and examples.

---

## Table of Contents
1. [View Public Profile](#view-public-profile)
2. [Add Students via Excel](#add-students-via-excel)
3. [Authenticate User](#authenticate-user)
4. [Update Student Profile](#update-student-profile)

---

## View Public Profile

### **GET** `/learn-free/profile/view/{userId}`

Retrieves the public profile of a user by their `userId`.

#### Request
- **URL**: `http://localhost:8080/learn-free/profile/view/2`
- **Method**: `GET`
- **Headers**:
    - `Authorization: Bearer <jwt_token_here>`

#### Example Request
```http
GET http://localhost:8080/learn-free/profile/view/2
Authorization: Bearer <jwt_token_here>
```

#### Example Response
```json
{
  "name": "Lucas Thompson",
  "registrationNumber": "REG4333",
  "personalEmail": "lucas.thompson56@outlook.com",
  "github": "github.com/lucas",
  "linkedIn": "linkedin.com/lucas"
}
```

---

## Add Students via Excel

### **POST** `/learn-free/staff/add-students`

Adds students to the system by uploading an Excel file. The file must contain student details, and the department must be specified.

#### Request
- **URL**: `http://localhost:8080/learn-free/staff/add-students`
- **Method**: `POST`
- **Headers**:
    - `Content-Type: multipart/form-data; boundary=boundary123`
    - `Authorization: Bearer <staff_jwt_token_here>`
- **Body**:
    - `students_data`: The Excel file containing student data.
    - `department`: The department to which the students belong.

#### Example Request
```http
POST http://localhost:8080/learn-free/staff/add-students
Content-Type: multipart/form-data; boundary=boundary123
Authorization: Bearer <staff_jwt_token_here>

--boundary123
Content-Disposition: form-data; name="students_data"; filename="student.xlsx"
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

< /home/jefino9488/IdeaProjects/Learn_Free_Server/student.xlsx
--boundary123
Content-Disposition: form-data; name="department"

ECE
--boundary123--
```

#### Example Response
```json
{
  "status": true,
  "message": "Students added successfully"
}
```

**Note:** A sample `student.xlsx` file is available at `doc/student.xlsx`.

---

## Authenticate User

### **POST** `/learn-free/authentication`

Authenticates a user and returns a JWT token for subsequent requests.

#### Request
- **URL**: `http://localhost:8080/learn-free/authentication`
- **Method**: `POST`
- **Headers**:
    - `Content-Type: application/json`
- **Body**:
    - `email`: The user's email.
    - `password`: The user's password.

#### Example Request
```http
POST http://localhost:8080/learn-free/authentication
Content-Type: application/json

{
  "email": "lucas.thompson56@outlook.com",
  "password": "REG4333"
}
```

#### Example Response
```json
{
  "userId": 2,
  "firstName": "Lucas",
  "lastName": "Thompson",
  "gender": "male",
  "mobileNumber": "9675324592",
  "email": "lucas.thompson56@outlook.com",
  "department": "ECE",
  "role": "STUDENT",
  "jwtToken": "<jwt_token_here>"
}
```

---

## Update Student Profile

### **POST** `/learn-free/profile/student/update`

Updates the profile of the authenticated student.

#### Request
- **URL**: `http://localhost:8080/learn-free/profile/student/update`
- **Method**: `POST`
- **Headers**:
    - `Content-Type: application/json`
    - `Authorization: Bearer <logedIn_student_jwt_token_here>`
- **Body**:
    - `firstName`: Updated first name.
    - `lastName`: Updated last name.
    - `gender`: Updated gender.
    - `dateOfBirth`: Updated date of birth.
    - `mobileNumber`: Updated mobile number.
    - `department`: Updated department.
    - `personalEmail`: Updated personal email.
    - `github`: Updated GitHub profile link.
    - `linkedIn`: Updated LinkedIn profile link.

#### Example Request
```http
POST http://localhost:8080/learn-free/profile/student/update
Content-Type: application/json
Authorization: Bearer <logedIn_student_jwt_token_here>

{
  "firstName": "Lucas",
  "lastName": "Thompson",
  "gender": "male",
  "dateOfBirth": "2",
  "mobileNumber": "9675324598",
  "department": "ECE",
  "personalEmail": "lucas@gmail.com",
  "github": "github.com",
  "linkedIn": "linkedin.com"
}
```

#### Example Response
```json
{
  "status": true,
  "message": "Profile updated successfully"
}
```

---

## Notes
1. **Authorization**: All endpoints (except `/learn-free/authentication`) require a valid JWT token in the `Authorization` header.
2. **File Upload**: The `/learn-free/staff/add-students` endpoint requires a valid Excel file with the correct format.
3. **Roles**: Some endpoints are restricted to specific roles (e.g., only staff can add students).


# DATABASE SCHEMA
## College Table
**Project Dir:** /db/college

| id | name         | address                | contact         |
|----|--------------|------------------------|-----------------|
| 1  | ABC College  | 123 Main St, Cityville | 9876543210      |

## User Table
**Project Dir:** /db/user

| id | email                | password     | role      |
|----|----------------------|--------------|-----------|
| 1  | principal@abc.com    | hashed_pass  | Principal |
| 2  | hod@abc.com          | hashed_pass  | HOD       |
| 3  | staff@abc.com        | hashed_pass  | Staff     |
| 4  | student@abc.com      | hashed_pass  | Student   |

## Department Table
**Project Dir:** /db/department

| id | name | description         | college_id |
|----|------|---------------------|------------|
| 1  | CSE  | Computer Science    | 1          |

## HOD Table
**Project Dir:** /db/hod

| id | user_id | department_id |
|----|---------|---------------|
| 1  | 2       | 1             |

## Staff Table
**Project Dir:** /db/staff

| id | user_id | department_id |
|----|---------|---------------|
| 1  | 3       | 1             |

## Student Table
**Project Dir:** /db/student

| id | user_id | registration_number | department_id | batch_id |
|----|---------|---------------------|---------------|----------|
| 1  | 4       | REG2021ABC          | 1             | 1        |

## Batch Table
**Project Dir:** /db/batch

| id | name       | start_year | end_year |
|----|------------|------------|----------|
| 1  | 2021 Batch | 2021       | 2025     |

## Subject Table
**Project Dir:** /db/subject

| id | name  | department_id |
|----|-------|---------------|
| 1  | CS101 | 1             |

## Exam Table
**Project Dir:** /db/exam

| id | name      | department_id | subject_id | date       | time  | session | type    | semester |
|----|-----------|---------------|------------|------------|-------|---------|---------|----------|
| 1  | Mid Term  | 1             | 1          | 2021-10-10 | 09:00 | FN      | Online  | 1st      |

## Grade Table
**Project Dir:** /db/grade

| id | student_id | subject_id | grade | semester |
|----|------------|------------|-------|----------|
| 1  | 1          | 1          | A     | 1st      |

## Attendance Table
**Project Dir:** /db/attendance

| id | student_id | date       | present | department_id | semester |
|----|------------|------------|---------|---------------|----------|
| 1  | 1          | 2021-09-01 | true    | 1             | 1st      |

## Fees Table
**Project Dir:** /db/fees

| id | student_id | amount | payment_date | status |
|----|------------|--------|--------------|--------|
| 1  | 1          | 1000   | 2021-08-15   | Paid   |

