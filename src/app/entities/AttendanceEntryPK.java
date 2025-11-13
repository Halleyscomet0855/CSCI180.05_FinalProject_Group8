package app.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class AttendanceEntryPK implements Serializable {
	
	private Long studentPK;
	private Long attendancePK;
	
	public AttendanceEntryPK() {}
	public AttendanceEntryPK(Long studentPK, Long attendancePK) {
		this.studentPK = studentPK;
		this.attendancePK = attendancePK;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AttendanceEntryPK)) return false;
		AttendanceEntryPK that = (AttendanceEntryPK) o;
		return Objects.equals(studentPK, that.studentPK) && 
				Objects.equals(attendancePK, that.attendancePK);
				
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(studentPK, attendancePK);
	}
	
	

}
