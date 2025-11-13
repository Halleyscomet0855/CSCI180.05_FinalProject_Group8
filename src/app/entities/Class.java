package app.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Class {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long ClassPk ;
	@ManyToOne(fetch = FetchType.EAGER)
	private Beadle BeadlePk;
	@Column
	private String ClassName;
	@Column
	private String ProfessorName;
	@Column
	private Timestamp Time;
	
	public Long getClassPk() {
		return ClassPk;
	}
	public void setClassPk(Long classPk) {
		ClassPk = classPk;
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
	public Timestamp getTime() {
		return Time;
	}
	public void setTime(Timestamp time) {
		Time = time;
	}

	
}
