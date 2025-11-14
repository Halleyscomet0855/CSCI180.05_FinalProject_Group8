package app.components;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import app.entities.Attendance;
import app.entities.Class;
import app.repositories.AttendanceEntryRepository;
import app.repositories.AttendanceRepository;
import app.repositories.ClassRepository;

@Component
public class SchedulerComponent {

	private final ClassRepository classRepository;
	private final AttendanceRepository attendanceRepository;
	private final AttendanceEntryRepository attendanceEntryRepository;
	private final MessagingComponent messagingComponent;

	public SchedulerComponent(ClassRepository classRepository,
			AttendanceRepository attendanceRepository,
			AttendanceEntryRepository attendanceEntryRepository,
			MessagingComponent messagingComponent) {
		this.classRepository = classRepository;
		this.attendanceRepository = attendanceRepository;
		this.attendanceEntryRepository = attendanceEntryRepository;
		this.messagingComponent = messagingComponent;
	}

	/**
	 * Scans for classes starting within 10 minutes
	 * @return List of classes starting soon
	 */
	public List<Class> checkUpcomingClasses() {
		// Get current time
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		// Calculate time 10 minutes from now
		Timestamp tenMinutesLater = new Timestamp(currentTime.getTime() + (10 * 60 * 1000));

		// Query for classes starting within the next 10 minutes using repository
		return classRepository.findUpcomingClasses(currentTime, tenMinutesLater);
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
		Optional<Class> classOpt = classRepository.findById(classPk);

		if (!classOpt.isPresent()) {
			return "Class not found";
		}

		Class classEntity = classOpt.get();

		// Query attendance records for this class using repository
		List<Attendance> attendanceRecords = attendanceRepository.findByClassPk(classEntity);

		StringBuilder report = new StringBuilder();
		report.append("Attendance Report for: ").append(classEntity.getClassName()).append("\n");
		report.append("Professor: ").append(classEntity.getProfessorName()).append("\n");
		report.append("Total Sessions: ").append(attendanceRecords.size()).append("\n");

		for (Attendance attendance : attendanceRecords) {
			report.append("\nDate: ").append(attendance.getDate()).append("\n");

			// Count present students for this attendance session using repository
			Long presentCount = attendanceEntryRepository.countByAttendancePKAndAttendanceStatus(attendance, "Present");

			report.append("Students Present: ").append(presentCount).append("\n");
		}

		return report.toString();
	}
}
