package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Class;
import app.entities.ClassEntry;
import app.entities.ClassEntryPK;

@Repository
public interface ClassEntryRepository extends JpaRepository<ClassEntry, Long>{
	
	public ClassEntry findByClassEntryPK(ClassEntryPK id);
	public List<ClassEntry> findByClassPK(Class classPk);

}
