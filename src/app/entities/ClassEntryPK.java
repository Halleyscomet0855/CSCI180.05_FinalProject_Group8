package app.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class ClassEntryPK implements Serializable {
	private Long studentPK;
	private Long ClassPK;
	
	public ClassEntryPK() {}
	public ClassEntryPK(Long studentPK, Long ClassPK) {
		this.studentPK = studentPK;
		this.ClassPK = ClassPK;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClassEntryPK)) return false;
		ClassEntryPK that = (ClassEntryPK) o;
		return Objects.equals(studentPK, that.studentPK) && 
				Objects.equals(ClassPK, that.ClassPK);
				
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(studentPK, ClassPK);
	}
}
