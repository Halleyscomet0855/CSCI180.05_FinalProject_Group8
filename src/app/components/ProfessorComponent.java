package app.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import app.entities.Attendance;
import app.entities.AttendanceEntry;
import app.entities.Class;
import app.entities.Student;

@Stateless
public class ProfessorComponent {

	@PersistenceContext
	private EntityManager em;

	@EJB
	private AttendanceComponent attendanceComponent;

	/**
	 * Displays logged attendance after class
	 * @param attendanceId The attendance record primary key
	 * @return Formatted string with attendance details
	 */
	public String viewAttendance(Long attendanceId) {
		Attendance attendance = em.find(Attendance.class, attendanceId);

		if (attendance == null) {
			return "Attendance record not found.";
		}

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

		// Get list of absent students
		TypedQuery<AttendanceEntry> absentQuery = em.createQuery(
			"SELECT ae FROM AttendanceEntry ae WHERE ae.attendancePK = :attendance AND ae.attendanceStatus != 'Present'",
			AttendanceEntry.class
		);
		absentQuery.setParameter("attendance", attendance);
		List<AttendanceEntry> absentEntries = absentQuery.getResultList();

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
		Class classEntity = em.find(Class.class, classId);

		if (classEntity == null) {
			System.out.println("Class not found with ID: " + classId);
			return new HashMap<>();
		}

		// Get all attendance records for this class
		TypedQuery<Attendance> attendanceQuery = em.createQuery(
			"SELECT a FROM Attendance a WHERE a.ClassPk = :classEntity",
			Attendance.class
		);
		attendanceQuery.setParameter("classEntity", classEntity);
		List<Attendance> attendanceRecords = attendanceQuery.getResultList();

		Map<String, Integer> cutReport = new HashMap<>();

		// For each attendance record, find students who were absent
		for (Attendance attendance : attendanceRecords) {
			TypedQuery<AttendanceEntry> absentQuery = em.createQuery(
				"SELECT ae FROM AttendanceEntry ae WHERE ae.attendancePK = :attendance AND ae.attendanceStatus != 'Present'",
				AttendanceEntry.class
			);
			absentQuery.setParameter("attendance", attendance);
			List<AttendanceEntry> absentEntries = absentQuery.getResultList();

			// Count cuts for each student
			for (AttendanceEntry entry : absentEntries) {
				Student student = entry.getStudentPK();
				String studentName = student.getName() + " (ID: " + student.getIDNumber() + ")";
				cutReport.put(studentName, cutReport.getOrDefault(studentName, 0) + 1);
			}
		}

		System.out.println("Cut Report generated for class: " + classEntity.getClassName());
		System.out.println("Total sessions: " + attendanceRecords.size());
		System.out.println("Students with cuts: " + cutReport.size());

		return cutReport;
	}
}
