package app.components;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entities.Attendance;
import app.entities.AttendanceEntry;
import app.entities.Beadle;
import app.entities.Class;
import app.entities.ClassEntry;
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
	/**
	 * Records present students and returns attendancePK
	 * @param beadleId The beadle's primary key
	 * @param classId The class primary key
	 * @param presentStudentIds List of student IDs who are present
	 * @param date The date of the class session
	 * @return The primary key of the created attendance record
	 */
	public Long logAttendance(Long beadleId, Long classId, List<Long> presentStudentIds, Date date) {
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

		// Record each present student
		for (Long studentId : presentStudentIds) {
			Optional<Student> studentOpt = studentRepository.findById(studentId);
			if (studentOpt.isPresent()) {
				// Create attendance entry for present student
				AttendanceEntry entry = new AttendanceEntry(studentOpt.get(), attendance, "Present");
				attendanceEntryRepository.save(entry);
			}
		}

		System.out.println("Attendance logged by beadle " + beadle.getName() +
			" for class " + classEntity.getClassName() +
			" on " + date +
			". Students present: " + presentStudentIds.size());

		return attendance.getAttendancePk();
	}
}
