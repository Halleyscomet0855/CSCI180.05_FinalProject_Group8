package app.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Class {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long classPk;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "beadlepk")
	private Beadle BeadlePk;
	@Column
	private String ClassName;
	@Column
	private String ProfessorName;
	@Column
	private String Time; //TODO: convert string to time
	
	public Long getClassPk() {
		return classPk;
	}
	public void setClassPk(Long classPk) {
		classPk = classPk;
	}
	public Beadle getBeadlePk() {
		return BeadlePk;
	}
	public void setBeadlePk(Beadle beadlePk) {
		BeadlePk = beadlePk;
	}
	public String getClassName() {
		return ClassName;
	}
	public void setClassName(String className) {
		ClassName = className;
	}
	public String getProfessorName() {
		return ProfessorName;
	}
	public void setProfessorName(String professorName) {
		ProfessorName = professorName;
	}
	public String getTime() {
		return Time;
	}
	public void setTime(String time) {
		Time = time;
	}

	
}
