package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Attendance;
import app.entities.AttendanceEntry;

@Repository
public interface AttendanceEntryRepository extends JpaRepository<AttendanceEntry, Long>{

	public List<AttendanceEntry> findByAttendancePK(Long AttendanceID);

	public List<AttendanceEntry> findByAttendanceStatus(String status);

	public List<AttendanceEntry> findByAttendancePK(Attendance attendance);

	public List<AttendanceEntry> findByAttendancePKAndAttendanceStatus(Attendance attendance, String status);

	public Long countByAttendancePKAndAttendanceStatus(Attendance attendance, String status);
}
