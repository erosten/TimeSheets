
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "JobTravelMethod")
@Access(AccessType.FIELD)
@Table(name = "\"JobTravelMethod\"")
public class JobTravelMethod implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @Basic
  @Column(name = "TravelMethod")
  private String travelMethod_;

  public JobTravelMethod() {
  }

  protected JobTravelMethod(String travelMethod) {
    this.travelMethod_ = travelMethod;
  }

  protected String getMethod() {
    return this.travelMethod_;
  }

  protected boolean exists() {
    return !this.travelMethod_.equals("no travel");
  }

  @Override
  public String getId() {
    return id;
  }
}
