
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "JobCode")
@Access(AccessType.FIELD)
@Table(name = "\"JobCode\"")
public class JobCode implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @Basic
  @Column(name = "JobCode")
  private String jobCode_;

  public JobCode() {
  }

  protected JobCode(String jobCode) {
    this.jobCode_ = jobCode;
  }

  // make smarter.. read until numbers start
  // currently takes first 3 character places
  private String getIdentifier() {
    return jobCode_.substring(0, 2);
  }

  protected boolean hasSameBaseAs(JobCode otherJobCode) {
    return otherJobCode.getIdentifier().equals(this.getIdentifier());
  }

  protected void setCode(String code) {
    jobCode_ = code;
  }

  @Override
  public String toString() {
    return jobCode_;
  }

  // eclipse generated
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((jobCode_ == null) ? 0 : jobCode_.hashCode());
    return result;
  }

  // eclipse generated
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    JobCode other = (JobCode) obj;
    if (jobCode_ == null) {
      if (other.jobCode_ != null) {
        return false;
      }
    } else if (!jobCode_.equals(other.jobCode_)) {
      return false;
    }
    return true;
  }

  @Override
  public String getId() {
    return id;
  }
}
