package app.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long studentPK;

  @Column
  private Long IDNumber;

  @Column
  private String name;

  @Column
  private String phoneNumber;

  public Long getStudentPK() {
    return studentPK;
  }

  public void setStudentPK(Long StudentPK) {
    studentPK = StudentPK;
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

