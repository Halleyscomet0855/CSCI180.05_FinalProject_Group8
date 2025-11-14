package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Beadle;

@Repository
public interface BeadleRepository extends JpaRepository<Beadle, Long>{

	public List<Beadle> findByBeadlePK(Long BeadlePK);

}
