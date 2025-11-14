package app.entities;

import java.sql.Date;

import app.entities.Class;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Attendance {
	
	@Id
	@Column
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long AttendancePk;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "beadlepk")
	private Beadle BeadlePk;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "classpk")
	private app.entities.Class classPk;
	
	@Column
	private Date Date;

	public Long getAttendancePk() {
		return AttendancePk;
	}

	public void setAttendancePk(Long attendancePk) {
		AttendancePk = attendancePk;
	}

	public Beadle getBeadlePk() {
		return BeadlePk;
	}

	public void setBeadlePk(Beadle beadlePk) {
		BeadlePk = beadlePk;
	}

	public Class getClassPk() {
		return classPk;
	}

	public void setClassPk(Class ClassPk) {
		classPk = ClassPk;
	}

	public Date getDate() {
		return Date;
	}

	public void setDate(Date date) {
		Date = date;
	}
	
	

}
