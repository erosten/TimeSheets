
package cps.core.model.timeSheet;

import cps.core.model.employee.Employee;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public interface Extra {

  public BigDecimal getAmount();

  public String getName();

  public LocalDate getDate();

  public Employee getEmployee();
}
