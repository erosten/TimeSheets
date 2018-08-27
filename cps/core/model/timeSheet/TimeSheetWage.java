
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

@Entity(name = "TimeSheetWage")
@Access(AccessType.FIELD)
@Table(name = "\"TimeSheetWage\"")
public class TimeSheetWage implements Wage, BaseEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private final String id = createId();

  @Basic
  private String rateName_ = TimeSheetConstants.TRAVEL_WAGE;

  @Basic
  @Column(precision = 31, scale = 2)
  private BigDecimal ratePerHour_;

  public TimeSheetWage() {
    // @Entity annotation requires non-private empty argument constructor
  }

  // private builder constructor
  private TimeSheetWage(WageBuilder wb) {
    rateName_ = wb.rateName;
    ratePerHour_ = wb.ratePerHour;
  }

  // protected setters
  protected void setRate(BigDecimal newWageRate) {
    this.ratePerHour_ = newWageRate;
    // TODO when rate is changed, create a wage change!
  }

  protected void setName(String newWageName) {
    rateName_ = newWageName;
  }

  // public getters & overridden methods
  @Override
  public String getName() {
    return this.rateName_;
  }

  @Override
  public BigDecimal getRate() {
    return this.ratePerHour_;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public int compareTo(Wage other) {
    // THIS one is after or before or equal
    final int equal = 0;
    final int after = 1;
    if (other.hasSameNameAs(this)) {
      return equal;
    }
    return after;
  }

  @Override
  public String toString() {
    return this.rateName_ + " rate";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.rateName_ == null) ? 0 : this.rateName_.hashCode());
    return result;
  }

  // compares name and rate
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Wage)) {
      return false;
    }
    final Wage other = (Wage) obj;
    if (this.getName() == null) {
      if (other.getName() != null) {
        return false;
      }
    } else if (!this.getName().equals(other.getName())) {
      return false;
    }
    if (this.getRate() == null) {
      if (other.getRate() != null) {
        return false;
      }
    } else if (!this.getRate().equals(other.getRate())) {
      return false;
    }
    return true;
  }

  // static inner builder class
  public static class WageBuilder {

    private String rateName;

    private final BigDecimal ratePerHour;

    public WageBuilder(final BigDecimal ratePerHour) {
      this.rateName = "Travel";
      this.ratePerHour = ratePerHour;
    }

    public WageBuilder name(final String rateName) {
      this.rateName = rateName;
      return this;
    }

    /**
     * Attempts to build a TimeSheetWage object with whatever data the builder has been given.
     * 
     * @return a valid TimeSheetWage object if all fields have been filled correctly.
     * @throws IllegalArgumentException
     *           if any field is null or empty
     */
    public TimeSheetWage build() {
      if (this.rateName == null) {
        throw new IllegalArgumentException("Wage's name was null");
      } else if (this.rateName.length() == 0) {
        throw new IllegalArgumentException("Wage name was empty");
      }
      if (this.ratePerHour == null) {
        throw new IllegalArgumentException("Wage's rate was null");
      }
      if (this.ratePerHour.equals(BigDecimal.ZERO)) {
        throw new IllegalArgumentException("Wage rate cannot be 0");
      }
      return new TimeSheetWage(this);
    }
  }

}
