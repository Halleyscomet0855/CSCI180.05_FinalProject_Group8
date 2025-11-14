package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import app.entities.Class;

public interface ClassRepository extends JpaRepository<Class, Long>{
	
	public List<Class> findByTime(String Time);

}
