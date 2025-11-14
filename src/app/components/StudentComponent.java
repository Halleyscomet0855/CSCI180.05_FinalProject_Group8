package app.components;

import java.util.Optional;

import org.springframework.stereotype.Component;

import app.entities.Student;
import app.repositories.StudentRepository;

@Component
public class StudentComponent {

	private final StudentRepository studentRepository;

	public StudentComponent(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

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

		return studentRepository.save(student);
	}

	/**
	 * Triggered when Twilio sends SMS to student
	 * @param studentId The student's primary key
	 * @param message The message content received
	 */
	public void receiveMessage(Long studentId, String message) {
		Optional<Student> studentOpt = studentRepository.findById(studentId);

		if (studentOpt.isPresent()) {
			Student student = studentOpt.get();
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
