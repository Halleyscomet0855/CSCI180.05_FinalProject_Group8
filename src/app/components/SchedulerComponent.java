package app.components;

import java.sql.Timestamp;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import app.entities.Attendance;
import app.entities.AttendanceEntry;
import app.entities.Class;

@Stateless
public class SchedulerComponent {

	@PersistenceContext
	private EntityManager em;

	@EJB
	private MessagingComponent messagingComponent;

	/**
	 * Scans for classes starting within 10 minutes
	 * @return List of classes starting soon
	 */
	public List<Class> checkUpcomingClasses() {
		// Get current time
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		// Calculate time 10 minutes from now
		Timestamp tenMinutesLater = new Timestamp(currentTime.getTime() + (10 * 60 * 1000));

		// Query for classes starting within the next 10 minutes
		TypedQuery<Class> query = em.createQuery(
			"SELECT c FROM Class c WHERE c.Time > :currentTime AND c.Time <= :tenMinutesLater",
			Class.class
		);
		query.setParameter("currentTime", currentTime);
		query.setParameter("tenMinutesLater", tenMinutesLater);

		return query.getResultList();
	}

	/**
	 * Hands off class details to the Messaging Component to send reminders
	 * @param classEntity The class for which to send reminders
	 */
	public void sendClassReminder(Class classEntity) {
		if (classEntity == null) {
			return;
		}

		// Find all students enrolled in this class
		// This would require a join table or enrollment entity in a real implementation
		// For now, we'll trigger the messaging component with class details

		String classDetails = "Reminder: " + classEntity.getClassName() +
			" with Professor " + classEntity.getProfessorName() +
			" starts at " + classEntity.getTime();

		// In a real implementation, you would query enrolled students and send to each
		// messagingComponent.queueMessage(studentPhone, classDetails);

		System.out.println("Sending class reminder: " + classDetails);
	}

	/**
	 * Provides attendance summary for a class
	 * @param classPk The class primary key
	 * @return Summary string of attendance report
	 */
	public String retrieveAttendanceReport(Long classPk) {
		Class classEntity = em.find(Class.class, classPk);

		if (classEntity == null) {
			return "Class not found";
		}

		// Query attendance records for this class
		TypedQuery<Attendance> attendanceQuery = em.createQuery(
			"SELECT a FROM Attendance a WHERE a.ClassPk = :classEntity",
			Attendance.class
		);
		attendanceQuery.setParameter("classEntity", classEntity);
		List<Attendance> attendanceRecords = attendanceQuery.getResultList();

		StringBuilder report = new StringBuilder();
		report.append("Attendance Report for: ").append(classEntity.getClassName()).append("\n");
		report.append("Professor: ").append(classEntity.getProfessorName()).append("\n");
		report.append("Total Sessions: ").append(attendanceRecords.size()).append("\n");

		for (Attendance attendance : attendanceRecords) {
			report.append("\nDate: ").append(attendance.getDate()).append("\n");

			// Count present students for this attendance session
			TypedQuery<Long> countQuery = em.createQuery(
				"SELECT COUNT(ae) FROM AttendanceEntry ae WHERE ae.attendancePK = :attendance AND ae.attendanceStatus = 'Present'",
				Long.class
			);
			countQuery.setParameter("attendance", attendance);
			Long presentCount = countQuery.getSingleResult();

			report.append("Students Present: ").append(presentCount).append("\n");
		}

		return report.toString();
	}
}
