package app.entities;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Attendance {
	
	@Id
	@Column
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long AttendancePk;

	@ManyToOne(fetch=FetchType.EAGER)
	private Beadle BeadlePk;
	@ManyToOne(fetch=FetchType.EAGER)
	private Class ClassPk;
	
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
		return ClassPk;
	}

	public void setClassPk(Class classPk) {
		ClassPk = classPk;
	}

	public Date getDate() {
		return Date;
	}

	public void setDate(Date date) {
		Date = date;
	}
	
	

}
