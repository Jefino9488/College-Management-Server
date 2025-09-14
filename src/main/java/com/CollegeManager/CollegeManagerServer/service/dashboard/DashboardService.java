package com.CollegeManager.CollegeManagerServer.service.dashboard;

import com.CollegeManager.CollegeManagerServer.dto.dashboard.HodDashboardStatsDTO;
import com.CollegeManager.CollegeManagerServer.dto.dashboard.PrincipalDashboardStatsDTO;
import com.CollegeManager.CollegeManagerServer.dto.dashboard.RecentActivityDTO;
import com.CollegeManager.CollegeManagerServer.dto.dashboard.StaffDashboardStatsDTO;
import com.CollegeManager.CollegeManagerServer.dto.dashboard.StudentDashboardStatsDTO;
import com.CollegeManager.CollegeManagerServer.entity.*;
import com.CollegeManager.CollegeManagerServer.repository.*;
import com.CollegeManager.CollegeManagerServer.service.grade.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final GradeService gradeService;
    private final FeeRepository feeRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;

    public PrincipalDashboardStatsDTO getPrincipalStats() {
        long totalColleges = collegeRepository.count();
        long totalDepartments = departmentRepository.count();
        long totalStaff = userAccountRepository.countByRole(RoleEnum.STAFF) + userAccountRepository.countByRole(RoleEnum.HOD);
        long totalStudents = userAccountRepository.countByRole(RoleEnum.STUDENT);
        long totalHODs = userAccountRepository.countByRole(RoleEnum.HOD);
        return PrincipalDashboardStatsDTO.builder()
                .totalColleges(totalColleges)
                .totalDepartments(totalDepartments)
                .totalStaff(totalStaff)
                .totalStudents(totalStudents)
                .totalHODs(totalHODs)
                .build();
    }

    public HodDashboardStatsDTO getHodStats(UserAuthentication user) {
        Department department = departmentRepository.findByHodId(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("User is not an HOD of any department."));

        // Find all student IDs in the HOD's department.
        List<Long> studentIdsInDept = userAccountRepository.findAll().stream()
                .filter(ua -> ua.getDepartment() != null
                        && department.getId().equals(ua.getDepartment().getId())
                        && ua.getRole() == RoleEnum.STUDENT)
                .map(UserAccount::getId)
                .toList();

        if (studentIdsInDept.isEmpty()) {
            // If there are no students, return an empty stats DTO.
            return HodDashboardStatsDTO.builder()
                    .totalStudents(0)
                    .totalStaff(userAccountRepository.countByDepartmentIdAndRole(department.getId(), RoleEnum.STAFF))
                    .totalSubjects(subjectRepository.countByDepartmentId(department.getId()))
                    .averageAttendance(100.0)
                    .averageGPA(0.0)
                    .studentsAtRisk(0L)
                    .highPerformers(0L)
                    .recentActivities(new ArrayList<>())
                    .build();
        }

        // --- REFACTORED: Perform efficient, aggregated calculations ---
        double averageAttendance = attendanceRepository.getAverageAttendanceForStudents(studentIdsInDept);
        double averageGPA = gradeRepository.getAverageGpaForStudents(studentIdsInDept);

        // --- REFACTORED: Single pass in-memory calculation for At-Risk & High Performers ---
        // Fetch all data in single queries to avoid N+1
        Map<Long, List<Grade>> gradesByStudent = gradeRepository.findByStudentIdIn(studentIdsInDept)
                .stream().collect(Collectors.groupingBy(g -> g.getStudent().getId()));
        Map<Long, List<Attendance>> attendanceByStudent = attendanceRepository.findByUserIdIn(studentIdsInDept)
                .stream().collect(Collectors.groupingBy(Attendance::getUserId));

        long studentsAtRisk = 0;
        long highPerformers = 0;

        for (Long studentId : studentIdsInDept) {
            double gpa = gradeService.calculateCGPAForGrades(gradesByStudent.getOrDefault(studentId, List.of()));

            List<Attendance> studentAttendances = attendanceByStudent.getOrDefault(studentId, List.of());
            long presentCount = studentAttendances.stream().filter(Attendance::isPresent).count();
            double attendancePercentage = studentAttendances.isEmpty() ? 100.0 : ((double) presentCount / studentAttendances.size()) * 100;

            if ((gpa > 0 && gpa < 2.0) || attendancePercentage < 75.0) {
                studentsAtRisk++;
            }
            if (gpa > 3.5) {
                highPerformers++;
            }
        }

        // --- ENHANCED: Generate Recent Activities ---
        List<RecentActivityDTO> recentActivities = new ArrayList<>();
        // Fetch recent subjects
        subjectRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"))).getContent().stream()
                .filter(s -> s.getDepartment().getId().equals(department.getId()))
                .forEach(subject -> recentActivities.add(RecentActivityDTO.builder()
                        .id("subject-" + subject.getId())
                        .type("Subject Added")
                        .message(String.format("New subject '%s' (%s) was created.", subject.getName(), subject.getCode()))
                        .timestamp(LocalDateTime.now()) // Placeholder, as Subject has no timestamp
                        .build()));

        // Fetch recent attendance submissions
        attendanceRepository.findAll(PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "date"))).getContent().stream()
                .filter(a -> a.getDepartment().equals(department.getCode()))
                .map(Attendance::getDate).distinct().limit(5)
                .forEach(date -> recentActivities.add(RecentActivityDTO.builder()
                        .id("attendance-" + date.toString())
                        .type("Attendance Marked")
                        .message(String.format("Attendance was submitted for %s.", date))
                        .timestamp(date.atStartOfDay())
                        .build()));

        // NEW: Fetch recent exams scheduled
        examRepository.findTop5ByDepartmentOrderByDateDesc(department.getCode())
                .forEach(exam -> recentActivities.add(RecentActivityDTO.builder()
                        .id("exam-" + exam.getId())
                        .type("Exam Scheduled")
                        .message(String.format("Exam '%s' scheduled for %s.", exam.getExamName(), exam.getDate()))
                        .timestamp(exam.getDate().atStartOfDay())
                        .build()));

        List<RecentActivityDTO> sortedActivities = recentActivities.stream()
                .sorted(Comparator.comparing(RecentActivityDTO::getTimestamp).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return HodDashboardStatsDTO.builder()
                .totalStudents(studentIdsInDept.size())
                .totalStaff(userAccountRepository.countByDepartmentIdAndRole(department.getId(), RoleEnum.STAFF))
                .totalSubjects(subjectRepository.countByDepartmentId(department.getId()))
                .averageAttendance(averageAttendance)
                .averageGPA(averageGPA)
                .studentsAtRisk(studentsAtRisk)
                .highPerformers(highPerformers)
                .recentActivities(sortedActivities)
                .build();
    }

    public StaffDashboardStatsDTO getStaffStats(UserAuthentication user) {
        List<Subject> subjectsTaught = subjectRepository.findByTeacherId(user.getUserId());
        long totalStudents = subjectsTaught.stream()
                .map(subject -> subject.getDepartment().getId())
                .distinct()
                .mapToLong(deptId -> userAccountRepository.countByDepartmentIdAndRole(deptId, RoleEnum.STUDENT))
                .sum();
        long upcomingExams = examRepository.countByDateAfter(LocalDate.now().minusDays(1));
        long pendingGrades = 0L;
        long classesToday = subjectsTaught.stream()
                .flatMap(subject -> examRepository.findAll().stream()
                        .filter(exam -> exam.getSubject() != null && exam.getSubject().getId().equals(subject.getId()) && exam.getDate().equals(LocalDate.now())))
                .count();
        List<Long> departmentIds = subjectsTaught.stream()
                .map(subject -> subject.getDepartment().getId())
                .distinct()
                .toList();
        List<UserAccount> studentsInTaughtDepts = userAccountRepository.findAll().stream()
                .filter(ua -> ua.getDepartment() != null && departmentIds.contains(ua.getDepartment().getId()) && ua.getRole() == RoleEnum.STUDENT)
                .toList();
        List<Long> studentIds = studentsInTaughtDepts.stream().map(UserAccount::getId).toList();
        double averageAttendance = studentIds.isEmpty() ? 100.0 : attendanceRepository.getAverageAttendanceForStudents(studentIds);

        return StaffDashboardStatsDTO.builder()
                .totalStudents(totalStudents)
                .averageAttendance(averageAttendance)
                .upcomingExams(upcomingExams)
                .pendingGrades(pendingGrades)
                .classesToday(classesToday)
                .build();
    }

    public StudentDashboardStatsDTO getStudentStats(UserAuthentication user) {
        double currentGPA = gradeService.calculateCGPA(user.getUserId());
        Double pendingFees = feeRepository.findTotalDueAmountByStudentId(user.getUserId()).orElse(0.0);
        long upcomingExams = examRepository.countByDateAfter(LocalDate.now().minusDays(1));
        List<Attendance> attendances = attendanceRepository.findByUserId(user.getUserId());
        long presentCount = attendances.stream().filter(Attendance::isPresent).count();
        double overallAttendance = attendances.isEmpty() ? 100.0 : ((double) presentCount / attendances.size()) * 100;
        List<Grade> grades = gradeRepository.findByStudentId(user.getUserId());
        long completedCredits = grades.stream()
                .map(Grade::getSubject)
                .mapToInt(Subject::getCredits)
                .sum();
        return StudentDashboardStatsDTO.builder()
                .currentGPA(currentGPA)
                .overallAttendance(overallAttendance)
                .upcomingExams(upcomingExams)
                .pendingFees(pendingFees)
                .completedCredits(completedCredits)
                .build();
    }
}