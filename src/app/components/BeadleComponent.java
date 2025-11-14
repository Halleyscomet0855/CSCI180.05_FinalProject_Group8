package app.components;

import java.sql.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import app.entities.Attendance;
import app.entities.AttendanceEntry;
import app.entities.Beadle;
import app.entities.Class;
import app.entities.Student;

@Stateless
public class BeadleComponent {

	@PersistenceContext
	private EntityManager em;

	@EJB
	private AttendanceComponent attendanceComponent;

	/**
	 * Records present students and returns attendancePK
	 * @param beadleId The beadle's primary key
	 * @param classId The class primary key
	 * @param presentStudentIds List of student IDs who are present
	 * @param date The date of the class session
	 * @return The primary key of the created attendance record
	 */
	public Long logAttendance(Long beadleId, Long classId, List<Long> presentStudentIds, Date date) {
		// Find the beadle
		Beadle beadle = em.find(Beadle.class, beadleId);
		if (beadle == null) {
			throw new IllegalArgumentException("Beadle not found with ID: " + beadleId);
		}

		// Find the class
		Class classEntity = em.find(Class.class, classId);
		if (classEntity == null) {
			throw new IllegalArgumentException("Class not found with ID: " + classId);
		}

		// Create the attendance record using AttendanceComponent
		Attendance attendance = attendanceComponent.createAttendanceRecord(beadle, classEntity, date);

		// Record each present student
		for (Long studentId : presentStudentIds) {
			Student student = em.find(Student.class, studentId);
			if (student != null) {
				// Create attendance entry for present student
				AttendanceEntry entry = new AttendanceEntry(student, attendance, "Present");
				em.persist(entry);
			}
		}

		em.flush();

		System.out.println("Attendance logged by beadle " + beadle.getName() +
			" for class " + classEntity.getClassName() +
			" on " + date +
			". Students present: " + presentStudentIds.size());

		return attendance.getAttendancePk();
	}
}
