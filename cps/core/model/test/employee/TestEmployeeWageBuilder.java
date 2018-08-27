
package cps.core.model.test.employee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;
import cps.core.model.employee.PersonName;

import java.math.BigDecimal;

import org.junit.Test;

public class TestEmployeeWageBuilder {

  // @Test
  // public void testCorrectInputsWithoutEmployee() {
  // EmployeeWage wage = new EmployeeWage.WageBuilder("testCorrectInputs",
  // new BigDecimal("5")).build();
  // assertNotNull(wage);
  // assertEquals("testCorrectInputs", wage.getName());
  // assertEquals(new BigDecimal("5"), wage.getRate());
  // }

  @Test(expected = IllegalArgumentException.class)
  public void testNullRateExceptionThrow() {
    new EmployeeWage.Builder("testNullRate", null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testZeroRateExceptionThrow() {
    new EmployeeWage.Builder("testNullRate", BigDecimal.ZERO).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullNameExceptionThrow() {
    new EmployeeWage.Builder(null, new BigDecimal(21)).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyNameExceptionThrow() {
    new EmployeeWage.Builder("", new BigDecimal(21)).build();
  }

  @Test
  public void testCorrectInputsWithEmployee() {
    final PersonName.NameBuilder employeeNameBuilder = new PersonName.NameBuilder(
        "wagetestfirst",
        "wagetestlast");
    final Employee.Builder employeeBuilder = new Employee.Builder(
        employeeNameBuilder.build(),
        "wte");
    final Employee wageEmployee = employeeBuilder.build();
    final EmployeeWage wage = new EmployeeWage.Builder("testCorrectInputs", new BigDecimal("5"))
        .employee(wageEmployee).build();
    assertNotNull(wage);
    assertEquals("Failed", wage.getName(), "testCorrectInputs");
    assertEquals(wage.getRate(), new BigDecimal("5"));
    assertEquals(wage.getEmployee(), wageEmployee);
  }
}
