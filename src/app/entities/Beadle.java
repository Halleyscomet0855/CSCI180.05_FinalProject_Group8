package app.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Beadle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long beadlePK;

	@Column
	private Long IDNumber;

	@Column
	private String name;

	@Column
	private String phoneNumber;

	public Long getBeadlePK() {
		return beadlePK;
	}

	public void setBeadlePK(Long BeadlePK) {
		this.beadlePK = BeadlePK;
	}

	public Long getIDNumber() {
		return IDNumber;
	}

	public void setIDNumber(Long iDNumber) {
		IDNumber = iDNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	
}
