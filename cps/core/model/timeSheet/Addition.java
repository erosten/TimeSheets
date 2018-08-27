
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;
import cps.core.db.tools.JodaLocalDateConverter;
import cps.core.model.employee.Employee;

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

import org.eclipse.persistence.annotations.Converter;
import org.joda.time.LocalDate;

@Entity(name = "Addition")
@Table(name = "\"Addition\"")
@Access(AccessType.FIELD)
public class Addition implements BaseEntity, Extra {

  @Id
  @Column(name = "id")
  private final String id = createId();

  // all fields below should be editable
  @OneToOne(orphanRemoval = false, targetEntity = Employee.class)
  @JoinColumn(name = "\"Employee_ID\"", referencedColumnName = "id")
  private Employee employee_;

  @Column(precision = 31, scale = 4, name = "Amount")
  private BigDecimal amount_;

  @Column(name = "jobDate")
  @Converter(converterClass = JodaLocalDateConverter.class, name = "additionDate")
  private LocalDate date_;

  @OneToOne(cascade = CascadeType.ALL, targetEntity = ExtraName.class)
  @JoinColumn(name = "\"ExtraName_ID\"", referencedColumnName = "id")
  private ExtraName name_;

  public Addition() {
    // @entity requires no arg constructor
  }

  /**
   * Creates an Addition to be added to a timeSheet.
   * 
   * @param employee
   *          the Employee the addition is meant for
   * @param name
   *          description of the addition "Bonus", "Salary", etc.
   * @param amount
   *          the amount to be added to the Employee's TimeSheet
   * @param date
   *          the date the addition was issued
   * 
   */
  public Addition(Employee employee, String name, BigDecimal amount, LocalDate date) {
    this.employee_ = employee;
    this.name_ = new ExtraName(name);
    this.amount_ = amount;
    this.date_ = date;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public Employee getEmployee() {
    return this.employee_;
  }

  @Override
  public BigDecimal getAmount() {
    return this.amount_;
  }

  @Override
  public LocalDate getDate() {
    return this.date_;
  }

  @Override
  public String getName() {
    return this.name_.getName();
  }

  @Override
  public String toString() {
    return this.date_ + " for " + this.amount_.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.amount_ == null) ? 0 : this.amount_.hashCode());
    result = prime * result + ((this.date_ == null) ? 0 : this.date_.hashCode());
    result = prime * result + ((this.employee_ == null) ? 0 : this.employee_.hashCode());
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.name_ == null) ? 0 : this.name_.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Addition)) {
      return false;
    }
    final Addition other = (Addition) obj;
    if (this.amount_ == null) {
      if (other.amount_ != null) {
        return false;
      }
    } else if (!this.amount_.equals(other.amount_)) {
      return false;
    }
    if (this.date_ == null) {
      if (other.date_ != null) {
        return false;
      }
    } else if (!this.date_.equals(other.date_)) {
      return false;
    }
    if (this.employee_ == null) {
      if (other.employee_ != null) {
        return false;
      }
    } else if (!this.employee_.equals(other.employee_)) {
      return false;
    }
    if (this.id != other.id) {
      return false;
    }
    if (this.name_ == null) {
      if (other.name_ != null) {
        return false;
      }
    } else if (!this.name_.equals(other.name_)) {
      return false;
    }
    return true;
  }
}
