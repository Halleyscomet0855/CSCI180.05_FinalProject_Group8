package app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.ClassEntry;

@Repository
public interface ClassEntryRepository extends JpaRepository<ClassEntry, Long>{
	
	public ClassEntry findByClassEntryPK(Long id);

}
