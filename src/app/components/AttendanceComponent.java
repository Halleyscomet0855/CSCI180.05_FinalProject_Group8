package app.components;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entities.Attendance;
import app.entities.AttendanceEntry;
import app.entities.Beadle;
import app.entities.Class;
import app.entities.Student;
import app.repositories.AttendanceEntryRepository;
import app.repositories.AttendanceRepository;

@Component
public class AttendanceComponent {

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private AttendanceEntryRepository attendanceEntryRepository;

	/**
	 * Stores attendance record linked to schedule/date
	 * @param beadle The beadle recording attendance
	 * @param classEntity The class session
	 * @param date The date of the session
	 * @return The created Attendance entity
	 */
	public Attendance createAttendanceRecord(Beadle beadle, Class classEntity, Date date) {
		Attendance attendance = new Attendance();
		attendance.setBeadlePk(beadle);
		attendance.setClassPk(classEntity);
		attendance.setDate(date);

		attendance = attendanceRepository.save(attendance);

		System.out.println("Attendance record created for class: " + classEntity.getClassName() +
			" on date: " + date +
			" (Attendance PK: " + attendance.getAttendancePk() + ")");

		return attendance;
	}

	/**
	 * Returns students that attended a specific class session
	 * @param attendanceId The attendance record primary key
	 * @return List of students who were present
	 */
	public List<Student> getListOfPresentStudents(Long attendanceId) {
		Optional<Attendance> attendanceOpt = attendanceRepository.findById(attendanceId);

		if (!attendanceOpt.isPresent()) {
			System.out.println("Attendance record not found with ID: " + attendanceId);
			return new ArrayList<>();
		}

		Attendance attendance = attendanceOpt.get();

		// Query for all attendance entries with status "Present" using repository
		List<AttendanceEntry> entries = attendanceEntryRepository.findByAttendancePKAndAttendanceStatus(attendance, "Present");

		// Extract the Student entities from the entries
		List<Student> presentStudents = entries.stream()
			.map(AttendanceEntry::getStudentPK)
			.collect(Collectors.toList());

		System.out.println("Found " + presentStudents.size() + " present students for attendance ID: " + attendanceId);

		return presentStudents;
	}
}
