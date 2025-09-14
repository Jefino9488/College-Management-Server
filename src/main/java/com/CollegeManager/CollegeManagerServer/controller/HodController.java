package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.dto.RegistrationRequestDTO;
import com.CollegeManager.CollegeManagerServer.dto.StaffMemberDTO;
import com.CollegeManager.CollegeManagerServer.dto.StudentPerformanceDTO;
import com.CollegeManager.CollegeManagerServer.dto.SubjectDTO;
import com.CollegeManager.CollegeManagerServer.dto.SubjectDetailsDTO;
import com.CollegeManager.CollegeManagerServer.entity.*;
import com.CollegeManager.CollegeManagerServer.repository.AttendanceRepository;
import com.CollegeManager.CollegeManagerServer.repository.DepartmentRepository;
import com.CollegeManager.CollegeManagerServer.repository.SubjectRepository;
import com.CollegeManager.CollegeManagerServer.repository.UserAccountRepository;
import com.CollegeManager.CollegeManagerServer.service.grade.GradeService;
import com.CollegeManager.CollegeManagerServer.service.staff.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/college-manager/hod")
@RequiredArgsConstructor
public class HodController {
    private final UserAccountRepository userAccountRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final StaffService staffService;
    private final GradeService gradeService;
    private final AttendanceRepository attendanceRepository;

    @PostMapping("/add-subject")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<Subject> addSubject(
            @RequestBody SubjectDTO dto,
            @AuthenticationPrincipal UserAuthentication user
    ) {
        UserAccount hod = userAccountRepository.findById(user.getUserId()).orElseThrow();
        Department department = departmentRepository.findByHodId(hod.getId())
                .orElseThrow(() -> new IllegalArgumentException("HOD not assigned to any department"));
        Subject subject = Subject.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .semester(dto.getSemester())
                .year(dto.getYear())
                .credits(dto.getCredits())
                .department(department)
                .build();

        return ResponseEntity.ok(subjectRepository.save(subject));
    }

    @GetMapping("/subjects")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<List<SubjectDetailsDTO>> getSubjects(@AuthenticationPrincipal UserAuthentication user) {
        Department department = departmentRepository.findByHodId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("HOD not assigned to any department"));
        List<Subject> subjects = subjectRepository.findByDepartmentId(department.getId());

        List<SubjectDetailsDTO> dtoList = subjects.stream().map(subject ->
                SubjectDetailsDTO.builder()
                        .id(subject.getId())
                        .name(subject.getName())
                        .code(subject.getCode())
                        .credits(subject.getCredits())
                        .assignedTeacher(subject.getTeacher() != null ? subject.getTeacher().getFirstName() + " " + subject.getTeacher().getLastName() : "Not Assigned")
                        .enrolledStudents(userAccountRepository.countByDepartmentIdAndRole(department.getId(), RoleEnum.STUDENT))
                        .build()
        ).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/subjects/{subjectId}/assign-teacher")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<Subject> assignTeacherToSubject(
            @PathVariable Long subjectId,
            @RequestParam Long teacherId,
            @AuthenticationPrincipal UserAuthentication user) {

        UserAccount hod = userAccountRepository.findById(user.getUserId()).orElseThrow();
        Department department = departmentRepository.findByHodId(hod.getId())
                .orElseThrow(() -> new IllegalArgumentException("HOD not assigned to any department"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        UserAccount teacher = userAccountRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        if (!subject.getDepartment().getId().equals(department.getId()) || !teacher.getDepartment().getId().equals(department.getId())) {
            return ResponseEntity.status(403).build();
        }

        subject.setTeacher(teacher);
        return ResponseEntity.ok(subjectRepository.save(subject));
    }

    @PostMapping("/add-teacher")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<UserAccount> addTeacher(@RequestBody RegistrationRequestDTO dto,
                                                  @AuthenticationPrincipal UserAuthentication user) {
        return null;
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<List<StaffMemberDTO>> getDepartmentStaff(@AuthenticationPrincipal UserAuthentication user) {
        Department department = departmentRepository.findByHodId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("HOD not assigned to any department"));
        List<UserAccount> staffAccounts = staffService.getStaffByDepartment(department.getId());
        List<StaffMemberDTO> staffDtos = staffAccounts.stream()
                .map(staff -> StaffMemberDTO.builder()
                        .id(staff.getId())
                        .name(staff.getFirstName() + " " + staff.getLastName())
                        .email(staff.getPersonalEmail())
                        .phone(staff.getMobileNumber())
                        .specialization(staff.getQualifications())
                        .status("active")
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(staffDtos);
    }

    @GetMapping("/student-performance")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<List<StudentPerformanceDTO>> getStudentPerformance(
            @RequestParam Integer academicYear,
            @RequestParam Integer semester,
            @AuthenticationPrincipal UserAuthentication user) {
        Department department = departmentRepository.findByHodId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("HOD not assigned to any department"));
        List<UserAccount> students = userAccountRepository.findStudentsByDepartmentAndYear(department.getCode(), academicYear);
        List<StudentPerformanceDTO> performanceDtos = students.stream()
                .map(student -> {
                    double gpa = gradeService.calculateCGPA(student.getId());
                    List<Attendance> attendances = attendanceRepository.findByUserId(student.getId());
                    double attendancePercentage = attendances.isEmpty() ? 100.0 :
                            ((double) attendances.stream().filter(Attendance::isPresent).count() / attendances.size()) * 100;
                    String status = "good";
                    if (gpa > 3.5) status = "excellent";
                    if (gpa < 2.5 || attendancePercentage < 75) status = "average";
                    if (gpa < 2.0 || attendancePercentage < 60) status = "poor";
                    return StudentPerformanceDTO.builder()
                            .id(student.getId())
                            .name(student.getFirstName() + " " + student.getLastName())
                            .rollNumber(student.getRegistrationNumber())
                            .year(String.valueOf(student.getAcademicYear()))
                            .semester(String.valueOf(student.getSemester()))
                            .gpa(gpa)
                            .attendance(attendancePercentage)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(performanceDtos);
    }

    @PutMapping("/subjects/{id}")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @RequestBody SubjectDTO subjectDTO, @AuthenticationPrincipal UserAuthentication user) {
        Department department = departmentRepository.findByHodId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("HOD not assigned to any department"));
        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + id));
        if (!existingSubject.getDepartment().getId().equals(department.getId())) {
            return ResponseEntity.status(403).build();
        }
        existingSubject.setName(subjectDTO.getName());
        existingSubject.setCode(subjectDTO.getCode());
        existingSubject.setCredits(subjectDTO.getCredits());
        existingSubject.setSemester(subjectDTO.getSemester());
        existingSubject.setYear(subjectDTO.getYear());
        return ResponseEntity.ok(subjectRepository.save(existingSubject));
    }

    @DeleteMapping("/subjects/{id}")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id, @AuthenticationPrincipal UserAuthentication user) {
        Department department = departmentRepository.findByHodId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("HOD not assigned to any department"));
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + id));
        if (!subject.getDepartment().getId().equals(department.getId())) {
            return ResponseEntity.status(403).build();
        }
        subjectRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}