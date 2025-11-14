package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entities.Beadle;

public interface BeadleRepository extends JpaRepository<Beadle, Long>{

	public List<Beadle> findByBeadlepk(Long BeadlePK);
	
}
