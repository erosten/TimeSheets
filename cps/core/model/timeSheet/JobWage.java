
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;
import cps.core.model.frame.Wage;

import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "JobWage")
@Access(AccessType.FIELD)
@Table(name = "\"JobWage\"")
public class JobWage implements Wage, BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @Basic
  @Column(name = "JobWageName")
  private String wageName_;

  @Basic
  @Column(name = "JobWageRate", precision = 31, scale = 2)
  private BigDecimal rateUsed_;

  public JobWage() {
  }

  public JobWage(String wageName, BigDecimal rateUsed) {
    this.wageName_ = wageName;
    this.rateUsed_ = rateUsed;
  }

  @Override
  public String getName() {
    return wageName_;
  }

  @Override
  public BigDecimal getRate() {
    return rateUsed_;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((rateUsed_ == null) ? 0 : rateUsed_.hashCode());
    result = prime * result + ((wageName_ == null) ? 0 : wageName_.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null) {
      return false;
    }
    if (!(other instanceof JobWage)) {
      return false;
    }
    JobWage otherWage = (JobWage) other;
    if (rateUsed_ == null) {
      if (otherWage.rateUsed_ != null) {
        return false;
      }
    } else if (!rateUsed_.equals(otherWage.rateUsed_)) {
      return false;
    }
    if (wageName_ == null) {
      if (otherWage.wageName_ != null) {
        return false;
      }
    } else if (!wageName_.equals(otherWage.wageName_)) {
      return false;
    }
    return true;
  }

  @Override
  public boolean hasSameNameAs(Wage other) {
    return other.getName().equals(this.wageName_);
  }

  @Override
  public String getId() {
    return id;
  }

}
