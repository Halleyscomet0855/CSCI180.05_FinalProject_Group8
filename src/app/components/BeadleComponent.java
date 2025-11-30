package app.components;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entities.Attendance;
import app.entities.AttendanceEntry;
import app.entities.Beadle;
import app.entities.Class;
import app.entities.ClassEntry;
import app.entities.ClassEntryPK;
import app.entities.Student;
import app.repositories.AttendanceEntryRepository;
import app.repositories.BeadleRepository;
import app.repositories.ClassEntryRepository;
import app.repositories.ClassRepository;
import app.repositories.StudentRepository;

@Component
public class BeadleComponent {

	private final BeadleRepository beadleRepository;
	private final ClassRepository classRepository;
	private final StudentRepository studentRepository;
	private final AttendanceEntryRepository attendanceEntryRepository;
	private final AttendanceComponent attendanceComponent;
	
	@Autowired
	ClassEntryRepository classEntryRepository;

	public BeadleComponent(BeadleRepository beadleRepository, ClassRepository classRepository,
			StudentRepository studentRepository, AttendanceEntryRepository attendanceEntryRepository,
			AttendanceComponent attendanceComponent) {
		this.beadleRepository = beadleRepository;
		this.classRepository = classRepository;
		this.studentRepository = studentRepository;
		this.attendanceEntryRepository = attendanceEntryRepository;
		this.attendanceComponent = attendanceComponent;
	}

	public int addClassList(app.entities.Class classObj, List<Long> presentStudentIds) {
		
		int numStudents = 0;
		for (Long studentID : presentStudentIds) {
			Optional<Student> studentOpt = studentRepository.findById(studentID);
			ClassEntry entry = new ClassEntry(classObj, studentOpt.get(), 0);
			classEntryRepository.save(entry);
			numStudents += 1;
		}
		
		return numStudents;
		
	}
	
	public ClassEntry incrementLateCount(Long studentId, Long classId) {
		ClassEntryPK pk = new ClassEntryPK(studentId, classId);
		Optional<ClassEntry> classEntryOpt = Optional.ofNullable(classEntryRepository.findByClassEntryPK(pk));
		
		if(classEntryOpt.isPresent()) {
			ClassEntry classEntry = classEntryOpt.get();
			classEntry.setNumberOfLate(classEntry.getNumberOfLate() + 1);
			return classEntryRepository.save(classEntry);
		}
		else {
			throw new IllegalArgumentException("Student not found in this class");
		}
	}
	/**
	 * Records attendance for students with various statuses (Present, Late, Absent).
	 * @param beadleId The beadle's primary key.
	 * @param classId The class primary key.
	 * @param attendanceStatus A map of student IDs to their attendance status.
	 * @param date The date of the class session.
	 * @return The primary key of the created attendance record.
	 */
	public Long logAttendance(Long beadleId, Long classId, Map<Long, String> attendanceStatus, Date date) {
		// Find the beadle using repository
		Optional<Beadle> beadleOpt = beadleRepository.findById(beadleId);
		if (!beadleOpt.isPresent()) {
			throw new IllegalArgumentException("Beadle not found with ID: " + beadleId);
		}
		Beadle beadle = beadleOpt.get();

		// Find the class using repository
		Optional<Class> classOpt = classRepository.findById(classId);
		if (!classOpt.isPresent()) {
			throw new IllegalArgumentException("Class not found with ID: " + classId);
		}
		Class classEntity = classOpt.get();

		// Create the attendance record using AttendanceComponent
		Attendance attendance = attendanceComponent.createAttendanceRecord(beadle, classEntity, date);

		// Record each student's attendance status
		for (Map.Entry<Long, String> entry : attendanceStatus.entrySet()) {
			Long studentId = entry.getKey();
			String status = entry.getValue();

			Optional<Student> studentOpt = studentRepository.findById(studentId);
			if (studentOpt.isPresent()) {
				// Create attendance entry for the student with the given status
				AttendanceEntry attendanceEntry = new AttendanceEntry(studentOpt.get(), attendance, status);
				attendanceEntryRepository.save(attendanceEntry);

				// If the student is late, increment their late count
				if ("Late".equalsIgnoreCase(status)) {
					incrementLateCount(studentId, classId);
				}
			}
		}

		System.out.println("Attendance logged by beadle " + beadle.getName() +
			" for class " + classEntity.getClassName() +
			" on " + date);

		return attendance.getAttendancePk();
	}
}
