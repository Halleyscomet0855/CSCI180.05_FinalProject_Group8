package app.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class AttendanceEntry {
	
	@EmbeddedId
	private AttendanceEntryPK attendanceEntryPK;
	
	@ManyToOne
	@MapsId("studentPK")
	@JoinColumn(name = "studentpk")
	private Student studentPK;
	
	
	@ManyToOne
	@MapsId("attendancePK")
	@JoinColumn(name = "attendancepk")
	private Attendance attendancePK;
	
	@Column
	private String attendanceStatus;
	
	public AttendanceEntry() {}

	public AttendanceEntry(Student studentPK, Attendance attendancePK, String attendanceStatus) {
		super();
		this.studentPK = studentPK;
		this.attendancePK = attendancePK;
		this.attendanceEntryPK = new AttendanceEntryPK(studentPK.getStudentPK(), attendancePK.getAttendancePk());
		this.attendanceStatus = attendanceStatus;
	}

	public AttendanceEntryPK getAttendanceEntryPK() {
		return attendanceEntryPK;
	}

	public void setAttendanceEntryPK(AttendanceEntryPK attendanceEntryPK) {
		this.attendanceEntryPK = attendanceEntryPK;
	}

	public Student getStudentPK() {
		return studentPK;
	}

	public void setStudentPK(Student studentPK) {
		this.studentPK = studentPK;
	}

	public Attendance getAttendancePK() {
		return attendancePK;
	}

	public void setAttendancePK(Attendance attendancePK) {
		this.attendancePK = attendancePK;
	}

	public String getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(String attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}

	

}
