# CollegeManagerServer

This is a Spring Boot application designed to manage college-related operations such as user registration, authentication, profile management, attendance, exams, fees, and more. It uses PostgreSQL as the database, JWT for authentication, and integrates email services for user verification.

## Prerequisites
- Java 21
- Maven 3.9.9
- PostgreSQL
- Gmail SMTP credentials for email sending

## Setup
1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```
2. Update `src/main/resources/application.properties` with your database and email credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/Learn_Free
   spring.datasource.username=<your-username>
   spring.datasource.password=<your-password>
   spring.mail.username=<your-email>
   spring.mail.password=<your-app-specific-password>
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Endpoints

Below are the key API endpoints with example requests and responses using `fetch` in JavaScript.

### 1. User Registration - Email Validation
- **Endpoint**: `GET /college-manager/registration/email-validation/{email}`
- **Description**: Validates if an email is available and sends an activation code.
- **Example Request**:
  ```javascript
  fetch('http://localhost:8080/college-manager/registration/email-validation/test@example.com', {
    method: 'GET',
  })
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error('Error:', error));
  ```
- **Example Response**:
  ```json
  {
    "status": true,
    "message": "Activation code sent to test@example.com"
  }
  ```

### 2. User Registration - Verify Activation Code
- **Endpoint**: `POST /college-manager/registration/verify`
- **Description**: Verifies the activation code and registers the user.
- **Example Request**:
  ```javascript
  fetch('http://localhost:8080/college-manager/registration/verify', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      firstName: "John",
      lastName: "Doe",
      gender: "Male",
      mobileNumber: "1234567890",
      email: "test@example.com",
      password: "password123",
      role: "STUDENT",
      department: "CSE",
      activationCode: "123456",
      academicYear: "2023",
      collegeId: 1
    })
  })
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error('Error:', error));
  ```
- **Example Response**:
  ```json
  {
    "status": true,
    "message": "User registered successfully"
  }
  ```

### 3. User Authentication
- **Endpoint**: `POST /college-manager/authentication`
- **Description**: Authenticates a user and returns a JWT token.
- **Example Request**:
  ```javascript
  fetch('http://localhost:8080/college-manager/authentication', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email: "test@example.com",
      password: "password123"
    })
  })
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error('Error:', error));
  ```
- **Example Response**:
  ```json
  {
    "userId": 1,
    "firstName": "John",
    "lastName": "Doe",
    "gender": "Male",
    "age": 20,
    "mobileNumber": "1234567890",
    "email": "test@example.com",
    "department": "CSE",
    "role": "STUDENT",
    "jwtToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "collegeId": 1,
    "collegeName": "Example College"
  }
  ```

### 4. Update Student Profile
- **Endpoint**: `POST /college-manager/profile/student/update`
- **Description**: Updates a student's profile (requires JWT authentication).
- **Example Request**:
  ```javascript
  fetch('http://localhost:8080/college-manager/profile/student/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'
    },
    body: JSON.stringify({
      firstName: "John",
      lastName: "Doe",
      gender: "Male",
      dateOfBirth: "2003-01-01",
      mobileNumber: "1234567890",
      department: "CSE",
      personalEmail: "john.doe@example.com",
      studentId: "STU001"
    })
  })
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error('Error:', error));
  ```
- **Example Response**:
  ```json
  {
    "status": true,
    "message": "Profile updated successfully"
  }
  ```

### 5. Submit Attendance
- **Endpoint**: `POST /college-manager/attendance/submit`
- **Description**: Submits attendance for students (requires JWT authentication).
- **Example Request**:
  ```javascript
  fetch('http://localhost:8080/college-manager/attendance/submit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'
    },
    body: JSON.stringify({
      date: "2025-03-02",
      department: "CSE",
      semester: 2,
      students: [
        { userId: 1, present: true },
        { userId: 2, present: false }
      ],
      academicYear: 2023
    })
  })
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error('Error:', error));
  ```
- **Example Response**:
  ```json
  {
    "status": true,
    "message": "Attendance submitted successfully"
  }
  ```

### 6. Get All Colleges
- **Endpoint**: `GET /college-manager/college/all`
- **Description**: Retrieves a list of all colleges (public endpoint).
- **Example Request**:
  ```javascript
  fetch('http://localhost:8080/college-manager/college/all', {
    method: 'GET'
  })
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error('Error:', error));
  ```
- **Example Response**:
  ```json
  [
    {
      "id": 1,
      "code": "EXC",
      "name": "Example College",
      "address": "123 College St",
      "contactEmail": "admin@example.com",
      "phoneNumber": "9876543210"
    }
  ]
  ```

## Database Schema

Below is a brief overview of key schema tables with example data.

### 1. `user_authentication`
- **Description**: Stores user authentication details.
- **Columns**: `auto_id (PK)`, `user_id`, `user_email`, `euser_password`, `user_role`
- **Example**:
  ```
  auto_id | user_id | user_email         | euser_password       | user_role
  --------+---------+--------------------+----------------------+-----------
  1       | 1       | test@example.com   | $2a$10$hashed...    | STUDENT
  ```

### 2. `user_account`
- **Description**: Stores user profile details.
- **Columns**: `user_id (PK)`, `first_name`, `last_name`, `gender`, `date_of_birth`, `mobile_number`, `department_id`, `college_id`, etc.
- **Example**:
  ```
  user_id | first_name | last_name | gender | date_of_birth | mobile_number | department_id | college_id
  --------+------------+-----------+--------+---------------+---------------+---------------+------------
  1       | John       | Doe       | Male   | 2003-01-01    | 1234567890    | 1             | 1
  ```

### 3. `college`
- **Description**: Stores college information.
- **Columns**: `id (PK)`, `code`, `name`, `address`, `contact_email`, `phone_number`, `principal_id`
- **Example**:
  ```
  id | code | name            | address         | contact_email    | phone_number | principal_id
  ---+------+-----------------+-----------------+------------------+--------------+--------------
  1  | EXC  | Example College | 123 College St  | admin@example.com| 9876543210   | 2
  ```

### 4. `department`
- **Description**: Stores department details.
- **Columns**: `id (PK)`, `code`, `name`, `description`, `total_years`, `semesters_per_year`, `college_id`, `hod_id`
- **Example**:
  ```
  id | code | name             | description          | total_years | semesters_per_year | college_id | hod_id
  ---+------+------------------+----------------------+-------------+--------------------+------------+--------
  1  | CSE  | Computer Science | CS Department        | 4           | 2                  | 1          | 3
  ```

### 5. `attendance`
- **Description**: Stores attendance records.
- **Columns**: `id (PK)`, `user_id`, `date`, `present`, `department`, `semester`, `academic_year`
- **Example**:
  ```
  id | user_id | date       | present | department | semester | academic_year
  ---+---------+------------+---------+------------+----------+---------------
  1  | 1       | 2025-03-02 | true    | CSE        | 2        | 2023
  ```

### 6. `exam`
- **Description**: Stores exam schedules.
- **Columns**: `id (PK)`, `exam_name`, `department`, `date`, `time`, `session`, `type`, `semester`, `subject_id`
- **Example**:
  ```
  id | exam_name    | department | date       | time  | session | type   | semester | subject_id
  ---+--------------+------------+------------+-------+---------+--------+----------+------------
  1  | Midterm CS101| CSE        | 2025-03-15 | 10:00 | FN      | Offline| 2        | 1
  ```

### 7. `fee`
- **Description**: Stores fee payment records.
- **Columns**: `id (PK)`, `student_id`, `amount`, `payment_date`, `is_paid`
- **Example**:
  ```
  id | student_id | amount | payment_date | is_paid
  ---+------------+--------+--------------+---------
  1  | 1          | 5000.0 | 2025-01-10   | true
  ```

## Contributing
Feel free to submit issues or pull requests to enhance the project.

## License
This project is licensed under the Apache License 2.0 - see the `LICENSE` file for details.