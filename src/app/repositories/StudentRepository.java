package app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
	
	public Student findByStudentPK(Long PK);

}
