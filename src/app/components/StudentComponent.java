package app.components;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import app.entities.Student;

@Stateless
public class StudentComponent {

	@PersistenceContext
	private EntityManager em;

	/**
	 * Stores student registration information
	 * @param idNumber Student ID number
	 * @param name Student name
	 * @param phoneNumber Student phone number
	 * @return The created Student entity
	 */
	public Student addStudentProfile(Long idNumber, String name, String phoneNumber) {
		Student student = new Student();
		student.setIDNumber(idNumber);
		student.setName(name);
		student.setPhoneNumber(phoneNumber);

		em.persist(student);
		em.flush();

		return student;
	}

	/**
	 * Triggered when Twilio sends SMS to student
	 * @param studentId The student's primary key
	 * @param message The message content received
	 */
	public void receiveMessage(Long studentId, String message) {
		Student student = em.find(Student.class, studentId);

		if (student != null) {
			// Process the received message
			// This could trigger various actions based on message content
			System.out.println("Message received for student " + student.getName() + ": " + message);

			// Add additional logic here for processing messages
			// e.g., responding to confirmations, updating preferences, etc.
		} else {
			System.out.println("Student not found with ID: " + studentId);
		}
	}
}
