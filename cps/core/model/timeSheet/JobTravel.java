
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;

import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity(name = "JobTravel")
@Access(AccessType.FIELD)
@Table(name = "\"JobTravel\"")
public class JobTravel implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @Column(name = "TravelMileage", precision = 31, scale = 2)
  private BigDecimal travelMileage_ = BigDecimal.ZERO;

  @OneToOne(cascade = CascadeType.ALL, targetEntity = JobTravelMethod.class)
  @JoinColumn(name = "job_travel_method_id", nullable = false, referencedColumnName = "id")
  private JobTravelMethod travelMethod_;

  public JobTravel() {
    // @entity requires no arg constructor
  }

  protected JobTravel(String travelString) {
    setTravel(travelString);
  }

  protected BigDecimal getMileage() {
    return travelMileage_;
  }

  protected String getMethod() {
    if (travelMethod_.getMethod().equals("no travel")) {
      return "";
    } else {
      return travelMethod_.getMethod();
    }
  }

  protected boolean exists() {
    return !travelMileage_.equals(BigDecimal.ZERO) || !travelMethod_.getMethod().equals(
        "no travel");
  }

  protected void setTravel(String travelString) {
    travelMethod_ = new JobTravelMethod(travelString);
    try {
      travelMileage_ = new BigDecimal(travelString);
    } catch (final NumberFormatException nfe) {
      travelMileage_ = BigDecimal.ZERO;
    }
  }

  @Override
  public String toString() {
    String returnString = "";
    if (!exists()) {
      return returnString;
    } else {
      if (travelMethod_.exists()) {
        return travelMethod_.getMethod();
      } else {
        if (travelMileage_.compareTo(BigDecimal.ZERO) == 0) {
          return "";
        } else {
          return travelMileage_.setScale(1).toString();
        }
      }
    }
  }

  @Override
  public String getId() {
    return id;
  }

}
