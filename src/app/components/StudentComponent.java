package app.components;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entities.Class;
import app.entities.ClassEntry;
import app.entities.ClassEntryPK;
import app.entities.Student;
import app.repositories.ClassEntryRepository;
import app.repositories.ClassRepository;
import app.repositories.StudentRepository;

@Component
public class StudentComponent {

	private final StudentRepository studentRepository;
	private final ClassRepository classRepository;
	private final ClassEntryRepository classEntryRepository;

	public StudentComponent(StudentRepository studentRepository, ClassRepository classRepository, ClassEntryRepository classEntryRepository) {
		this.studentRepository = studentRepository;
		this.classRepository = classRepository;
		this.classEntryRepository = classEntryRepository;
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
	 * Adds a student to a class schedule.
	 * @param studentId The student's primary key.
	 * @param classId The class's primary key.
	 * @return The created or updated ClassEntry.
	 * @throws IllegalArgumentException if student or class is not found.
	 */
	public ClassEntry addStudentToClass(Long studentId, Long classId) {
		Optional<Student> studentOpt = studentRepository.findById(studentId);
		if (!studentOpt.isPresent()) {
			throw new IllegalArgumentException("Student not found with ID: " + studentId);
		}
		Student student = studentOpt.get();

		Optional<Class> classOpt = classRepository.findById(classId);
		if (!classOpt.isPresent()) {
			throw new IllegalArgumentException("Class not found with ID: " + classId);
		}
		Class classEntity = classOpt.get();

		ClassEntryPK pk = new ClassEntryPK(studentId, classId);
		Optional<ClassEntry> existingClassEntry = Optional.ofNullable(classEntryRepository.findByClassEntryPK(pk));

		if (existingClassEntry.isPresent()) {
			return existingClassEntry.get(); // Student already in class, return existing entry
		} else {
			ClassEntry newClassEntry = new ClassEntry(classEntity, student, 0);
			return classEntryRepository.save(newClassEntry);
		}
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
