package app.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import app.entities.Attendance;
import app.entities.AttendanceEntry;
import app.entities.Class;
import app.entities.Student;
import app.repositories.AttendanceEntryRepository;
import app.repositories.AttendanceRepository;
import app.repositories.ClassRepository;

@Component
public class ProfessorComponent {

	private final AttendanceRepository attendanceRepository;
	private final AttendanceEntryRepository attendanceEntryRepository;
	private final ClassRepository classRepository;
	private final AttendanceComponent attendanceComponent;

	public ProfessorComponent(AttendanceRepository attendanceRepository,
			AttendanceEntryRepository attendanceEntryRepository,
			ClassRepository classRepository,
			AttendanceComponent attendanceComponent) {
		this.attendanceRepository = attendanceRepository;
		this.attendanceEntryRepository = attendanceEntryRepository;
		this.classRepository = classRepository;
		this.attendanceComponent = attendanceComponent;
	}

	/**
	 * Displays logged attendance after class
	 * @param attendanceId The attendance record primary key
	 * @return Formatted string with attendance details
	 */
	public String viewAttendance(Long attendanceId) {
		Optional<Attendance> attendanceOpt = attendanceRepository.findById(attendanceId);

		if (!attendanceOpt.isPresent()) {
			return "Attendance record not found.";
		}

		Attendance attendance = attendanceOpt.get();

		StringBuilder view = new StringBuilder();
		view.append("=== Attendance Report ===\n");
		view.append("Class: ").append(attendance.getClassPk().getClassName()).append("\n");
		view.append("Professor: ").append(attendance.getClassPk().getProfessorName()).append("\n");
		view.append("Date: ").append(attendance.getDate()).append("\n");
		view.append("Beadle: ").append(attendance.getBeadlePk().getName()).append("\n\n");

		// Get list of present students
		List<Student> presentStudents = attendanceComponent.getListOfPresentStudents(attendanceId);

		view.append("Students Present (").append(presentStudents.size()).append("):\n");
		for (Student student : presentStudents) {
			view.append("  - ").append(student.getName())
				.append(" (ID: ").append(student.getIDNumber()).append(")\n");
		}

		// Get list of absent students using repository
		List<AttendanceEntry> absentEntries = attendanceEntryRepository.findByAttendancePKAndAttendanceStatus(attendance, "Absent");

		view.append("\nStudents Absent (").append(absentEntries.size()).append("):\n");
		for (AttendanceEntry entry : absentEntries) {
			Student student = entry.getStudentPK();
			view.append("  - ").append(student.getName())
				.append(" (ID: ").append(student.getIDNumber())
				.append(") - Status: ").append(entry.getAttendanceStatus()).append("\n");
		}

		return view.toString();
	}

	/**
	 * Identifies students who cut (were absent from) a class
	 * @param classId The class primary key
	 * @return Map of student names to their cut counts
	 */
	public Map<String, Integer> generateCutReport(Long classId) {
		Optional<Class> classOpt = classRepository.findById(classId);

		if (!classOpt.isPresent()) {
			System.out.println("Class not found with ID: " + classId);
			return new HashMap<>();
		}

		Class classEntity = classOpt.get();

		// Get all attendance records for this class using repository
		List<Attendance> attendanceRecords = attendanceRepository.findByClassPk(classEntity);

		Map<String, Integer> cutReport = new HashMap<>();

		// For each attendance record, find students who were absent
		for (Attendance attendance : attendanceRecords) {
			// Get absent entries using repository (any status that is not "Present")
			List<AttendanceEntry> allEntries = attendanceEntryRepository.findByAttendancePK(attendance);

			// Count cuts for each student who was not present
			for (AttendanceEntry entry : allEntries) {
				if ("Present".equals(entry.getAttendanceStatus())) {
					Student student = entry.getStudentPK();
					String studentName = student.getName() + " (ID: " + student.getIDNumber() + ")";
					cutReport.put(studentName, cutReport.getOrDefault(studentName, 0) + 1);
				}
			}
		}

		System.out.println("Cut Report generated for class: " + classEntity.getClassName());
		System.out.println("Total sessions: " + attendanceRecords.size());
		System.out.println("Students with cuts: " + cutReport.size());

		return cutReport;
	}
}
