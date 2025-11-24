package app.repositories;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.entities.Class;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long>{


	@Query("SELECT c FROM Class c WHERE c.Time > :currentTime AND c.Time <= :tenMinutesLater")
	public List<Class> findUpcomingClasses(@Param("currentTime") Timestamp currentTime, @Param("tenMinutesLater") Timestamp tenMinutesLater);
	
	public app.entities.Class findByClassPk(Long PK);

}
