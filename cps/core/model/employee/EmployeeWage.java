
package cps.core.model.employee;

import cps.core.db.frame.BaseEntity;
import cps.core.model.frame.Wage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.joda.time.LocalDate;

@Entity(name = "EmployeeWages")
@Access(AccessType.FIELD)
@Table(name = "\"EmployeeWage\"")
public class EmployeeWage implements Wage, BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @Basic
  @Column(name = "RateName")
  private String rateName_;

  @Basic
  @Column(name = "RatePerHour", precision = 31, scale = 2)
  private BigDecimal ratePerHour_;

  @ManyToOne(targetEntity = Employee.class)
  @JoinColumn(foreignKey = @ForeignKey(name = "Employee_Wage_Employee_ID",
      foreignKeyDefinition = "FOREIGN KEY (EMPLOYEE_ID) REFERENCES \"Employee\" (id)"),
      name = "Employee_ID",
      referencedColumnName = "id")
  private Employee employee_;

  @OneToMany(cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      targetEntity = EmployeeWageChange.class)
  @JoinTable(name = "Wage_WageChanges",
      joinColumns = @JoinColumn(name = "EmployeeWage_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "WageChange_id", referencedColumnName = "id"))
  private final List<EmployeeWageChange> wageChanges_ = new ArrayList<EmployeeWageChange>();

  protected EmployeeWage() {
    // @Entity notation requires non-private empty argument constructor
  }

  // private builder constructor
  private EmployeeWage(Builder wb) {
    rateName_ = wb.rateName;
    ratePerHour_ = wb.ratePerHour;
    employee_ = wb.employee;
    // first wage change is the initial construction of the wage
    wageChanges_.add(new EmployeeWageChange(ratePerHour_));
  }

  // public/protected setters
  public void setRate(BigDecimal newWageRate) {
    this.ratePerHour_ = newWageRate;
    // when rate is changed, create a wage change!
    this.wageChanges_.add(new EmployeeWageChange(newWageRate));
  }

  public void setName(String newWageName) {
    rateName_ = newWageName;
  }

  public void setEmployee(Employee employee) {
    employee_ = employee;
  }

  // public getters
  public Employee getEmployee() {
    return this.employee_;
  }

  @Override
  public String getName() {
    return this.rateName_;
  }

  @Override
  public BigDecimal getRate() {
    return this.ratePerHour_;
  }

  public LocalDate getDateCreated() {
    // first entry in array is the date created, set on object constructor
    return this.wageChanges_.get(0).getDate();
  }

  public LocalDate getDateOfLastRateChange() {
    // returns the last entry in array (could be index 0)
    return this.wageChanges_.get(this.wageChanges_.size() - 1).getDate();
  }

  public List<EmployeeWageChange> getChanges() {
    return this.wageChanges_;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String toString() {
    return this.rateName_ + " rate";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    // result = prime * result + ((this.employee_.getId() == null) ? 0 :
    // this.employee_.getId().hashCode());
    // <-don't compare employee object hashcode - results in infinite loop
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.rateName_ == null) ? 0 : this.rateName_.hashCode());
    return result;
  }

  // compares name and employee
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
  public static class Builder {

    private final String rateName;

    private final BigDecimal ratePerHour;

    private Employee employee;

    public Builder(final String rateName, final BigDecimal ratePerHour) {
      this.rateName = rateName;
      this.ratePerHour = ratePerHour;
    }

    public Builder employee(final Employee employee) {
      this.employee = employee;
      return this;
    }

    /**
     * Attempts to build an EmployeeWage object with whatever data the builder has been given.
     * 
     * @return a valid EmployeeWage object if all fields have been filled correctly.
     * @throws IllegalArgumentException
     *           if any field is null or empty
     */
    public EmployeeWage build() {
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
      if (this.employee == null) {
        throw new IllegalArgumentException("employee cannot be null");

      }
      return new EmployeeWage(this);
    }
  }
}
