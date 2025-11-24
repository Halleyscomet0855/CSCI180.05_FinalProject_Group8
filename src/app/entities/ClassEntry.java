package app.entities;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class ClassEntry {
	
	@EmbeddedId
	private ClassEntryPK classEntryPK;
	
	@ManyToOne
	@MapsId("studentPK")
	@JoinColumn(name = "studentPK")
	private Student studentPK;
	
	@ManyToOne
	@MapsId("ClassPK")
	@JoinColumn(name = "classPK")
	private app.entities.Class ClassPK;

	@Column
	private int numberOfLate;


	public ClassEntry() {};
	public ClassEntry(app.entities.Class classPK, Student studentPK, int numberOfLate) {
		super();
		this.studentPK = studentPK;
		this.ClassPK = classPK;
		classEntryPK = new ClassEntryPK(studentPK.getStudentPK(), classPK.getClassPk());
		this.numberOfLate = numberOfLate;
	}

	public ClassEntryPK getClassEntryPK() {
		return classEntryPK;
	}

	public void setClassEntryPK(ClassEntryPK classEntryPK) {
		classEntryPK = classEntryPK;
	}

	public Student getStudentPK() {
		return studentPK;
	}

	public void setStudentPK(Student studentPK) {
		this.studentPK = studentPK;
	}

	public app.entities.Class getClassPK() {
		return ClassPK;
	}

	public void setClassPK(app.entities.Class classPK) {
		ClassPK = classPK;
	}

	public int getNumberOfLate() {
		return numberOfLate;
	}

	public void setNumberOfLate(int numberOfLate) {
		this.numberOfLate = numberOfLate;
	}
	
	

}
