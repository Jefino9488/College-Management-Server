package com.CollegeManager.CollegeManagerServer.service.staff;

import com.CollegeManager.CollegeManagerServer.dto.ResponseDTO;
import com.CollegeManager.CollegeManagerServer.dto.StudentDTO;
import com.CollegeManager.CollegeManagerServer.entity.Department;
import com.CollegeManager.CollegeManagerServer.entity.RoleEnum;
import com.CollegeManager.CollegeManagerServer.entity.UserAccount;
import com.CollegeManager.CollegeManagerServer.entity.UserAuthentication;
import com.CollegeManager.CollegeManagerServer.repository.*;
import com.CollegeManager.CollegeManagerServer.service.grade.GradeService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final UserAccountRepository userAccountRepository;
    private final UserAuthenticationRepository userAuthenticationRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final GradeRepository gradeRepository;
    private final SubjectRepository subjectRepository;
    private final GradeService gradeService;

    @Override
    public ResponseDTO addStudents(MultipartFile file, String department, Integer academicYear) {
        if (!isValidExcelFile(file)) {
            return ResponseDTO.builder()
                    .status(false)
                    .message("Invalid Excel file format")
                    .build();
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            XSSFSheet sheet = workbook.getSheet(department);
            if (sheet == null) {
                return ResponseDTO.builder()
                        .status(false)
                        .message("Department sheet not found")
                        .build();
            }

            Iterator<Row> rowIterator = sheet.iterator();
            int rowIndex = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (rowIndex++ == 0) continue;

                UserAccount userAccount = new UserAccount();
                String email = null;
                String regNo = null;

                Iterator<Cell> cellIterator = row.cellIterator();
                int cellIndex = 0;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cellIndex) {
                        case 0 -> userAccount.setFirstName(getCellValueAsString(cell));
                        case 1 -> userAccount.setLastName(getCellValueAsString(cell));
                        case 2 -> userAccount.setGender(getCellValueAsString(cell));
                        case 3 -> userAccount.setMobileNumber(getCellValueAsString(cell));
                        case 4 -> email = getCellValueAsString(cell);
                        case 5 -> {
                            regNo = getCellValueAsString(cell);
                            userAccount.setRegistrationNumber(regNo);
                        }
                        case 6 -> userAccount.setAcademicYear(Integer.parseInt(getCellValueAsString(cell)));
                        case 7 -> userAccount.setSemester(Integer.parseInt(getCellValueAsString(cell)));
                    }
                    cellIndex++;
                }

                if (email == null || regNo == null || userAuthenticationRepository.existsByEmail(email)) {
                    continue;
                }

                Department departmentEntity = departmentRepository.findByCode(department)
                        .orElseThrow(() -> new IllegalArgumentException("Department not found"));
                userAccount.setDepartment(departmentEntity);
                userAccount.setAcademicYear(academicYear);

                UserAccount savedUser = userAccountRepository.save(userAccount);

                UserAuthentication auth = UserAuthentication.builder()
                        .userId(savedUser.getId())
                        .email(email)
                        .password(passwordEncoder.encode(regNo))
                        .role(RoleEnum.STUDENT)
                        .build();
                userAuthenticationRepository.save(auth);
            }

            return ResponseDTO.builder()
                    .status(true)
                    .message("Students added successfully")
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseDTO.builder()
                    .status(false)
                    .message("Error processing Excel file")
                    .build();
        }
    }

    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }

    private boolean isValidExcelFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase().endsWith(".xlsx");
    }

    @Override
    public List<StudentDTO> getStudentsByDepartmentAndYear(String department, Integer academicYear) {
        List<UserAccount> students = userAccountRepository.findStudentsByDepartmentAndYear(department, academicYear);
        List<StudentDTO> studentDTOs = new ArrayList<>();
        for (UserAccount student : students) {
            studentDTOs.add(StudentDTO.builder()
                    .id(student.getId())
                    .firstName(student.getFirstName())
                    .lastName(student.getLastName())
                    .gender(student.getGender())
                    .dateOfBirth(student.getDateOfBirth())
                    .mobileNumber(student.getMobileNumber())
                    .registrationNumber(student.getRegistrationNumber())
                    .academicYear(String.valueOf(student.getAcademicYear()))
                    .semester(String.valueOf(student.getSemester()))
                    .build());
        }
        return studentDTOs;
    }

    @Override
    public ResponseDTO addGrades(MultipartFile file, int semester, String department, int academicYear) {
        if (!isValidExcelFile(file)) {
            return ResponseDTO.builder()
                    .status(false)
                    .message("Invalid Excel file format")
                    .build();
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return ResponseDTO.builder()
                        .status(false)
                        .message("Sheet not found")
                        .build();
            }

            Iterator<Row> rowIterator = sheet.iterator();
            int rowIndex = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (rowIndex++ == 0) continue;

                String registrationNumber = null;
                String subjectCode = null;
                String grade = null;

                Iterator<Cell> cellIterator = row.cellIterator();
                int cellIndex = 0;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cellIndex) {
                        case 0 -> registrationNumber = getCellValueAsString(cell);
                        case 1 -> subjectCode = getCellValueAsString(cell);
                        case 2 -> grade = getCellValueAsString(cell);
                    }
                    cellIndex++;
                }

                if (registrationNumber == null || subjectCode == null || grade == null) {
                    continue;
                }

                gradeService.addGrade(registrationNumber, subjectCode, grade, semester);
            }

            return ResponseDTO.builder()
                    .status(true)
                    .message("Grades added successfully")
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseDTO.builder()
                    .status(false)
                    .message("Error processing Excel file")
                    .build();
        }
    }
    @Override
    public List<UserAccount> getStaffByCollege(Long collegeId) {
        return userAccountRepository.findAll().stream()
                .filter(user -> user.getCollege() != null && user.getCollege().getId().equals(collegeId))
                .filter(user -> user.getRole() == RoleEnum.STAFF || user.getRole() == RoleEnum.HOD)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserAccount> getStaffByDepartment(Long departmentId) {
        return userAccountRepository.findAll().stream()
                .filter(user -> user.getDepartment() != null && user.getDepartment().getId().equals(departmentId))
                .filter(user -> user.getRole() == RoleEnum.STAFF)
                .collect(Collectors.toList());
    }
}