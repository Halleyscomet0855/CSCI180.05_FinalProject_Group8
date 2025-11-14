package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Attendance;
import app.entities.Class;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

	public List<Attendance> findByClassPk(Class classPk);

}
