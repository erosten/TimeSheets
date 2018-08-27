
package cps.core.model.employee;

import cps.core.db.frame.BaseEntity;
import cps.core.db.tools.JodaLocalDateConverter;

import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

@Entity(name = "EmployeeWageChanges")
@Access(AccessType.FIELD)
@Table(name = "\"EmployeeWageChanges\"")

public class EmployeeWageChange implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @Converter(converterClass = JodaLocalDateConverter.class, name = "wageChangeDateConverter")
  @Convert("jobDateConverter")
  @Column(name = "DateOfWageChange")
  private final LocalDate dateChanged_ = DateTime.now().toLocalDate();

  @Column(name = "NewWageRate", precision = 31, scale = 2)
  private BigDecimal newWageRate_;

  protected EmployeeWageChange() {
    // @Entity requires empty constructor
    // setting private disables lazy loading
  }

  public EmployeeWageChange(BigDecimal newRate) {
    newWageRate_ = newRate;
  }

  public LocalDate getDate() {
    return dateChanged_;
  }

  public BigDecimal getNewWageRate() {
    return newWageRate_;
  }

  @Override
  public String getId() {
    return this.id;
  }
}
