package app.components;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import app.entities.Attendance;
import app.entities.AttendanceEntry;
import app.entities.Beadle;
import app.entities.Class;
import app.entities.Student;

@Stateless
public class AttendanceComponent {

	@PersistenceContext
	private EntityManager em;

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

		em.persist(attendance);
		em.flush();

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
		Attendance attendance = em.find(Attendance.class, attendanceId);

		if (attendance == null) {
			System.out.println("Attendance record not found with ID: " + attendanceId);
			return new ArrayList<>();
		}

		// Query for all attendance entries with status "Present"
		TypedQuery<AttendanceEntry> query = em.createQuery(
			"SELECT ae FROM AttendanceEntry ae WHERE ae.attendancePK = :attendance AND ae.attendanceStatus = 'Present'",
			AttendanceEntry.class
		);
		query.setParameter("attendance", attendance);

		List<AttendanceEntry> entries = query.getResultList();

		// Extract the Student entities from the entries
		List<Student> presentStudents = new ArrayList<>();
		for (AttendanceEntry entry : entries) {
			presentStudents.add(entry.getStudentPK());
		}

		System.out.println("Found " + presentStudents.size() + " present students for attendance ID: " + attendanceId);

		return presentStudents;
	}
}
