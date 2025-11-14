package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entities.AttendanceEntry;

public interface AttendanceEntryRepository extends JpaRepository<AttendanceEntry, Long>{

	public List<AttendanceEntry> findByAttendanceID(Long AttendanceID);
	
	public List<AttendanceEntry> findByAttendanceStatus(String status);
}
