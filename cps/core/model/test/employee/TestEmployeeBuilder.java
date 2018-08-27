
package cps.core.model.test.employee;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;
import cps.core.model.employee.PersonName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestEmployeeBuilder {

  @Test(expected = IllegalArgumentException.class)
  public void testNullNameThrowsException() {
    new Employee.Builder(null, "valid").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullAbbreviationThrowsException() {
    new Employee.Builder(new PersonName.NameBuilder("first", "second").build(), null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullLangBonusThrowsException() {
    new Employee.Builder(new PersonName.NameBuilder("first", "second").build(), "abbreviation")
        .langBonus(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullWageAddedThrowsException() {
    new Employee.Builder(new PersonName.NameBuilder("first", "second").build(), "abbreviation")
        .wage(null).build();
  }

  @Test
  public void testCorrectInstantiationWithNoWages() {
    final Employee employee = new Employee.Builder(
        new PersonName.NameBuilder("Kevin", "Hanley").build(),
        "klh").build();
    assertEquals("Kevin Hanley", employee.getFullName());
    assertEquals("klh", employee.getAbbreviation());
    assertEquals(BigDecimal.ZERO, employee.getLanguageBonus());
  }

  @Test
  public void testCorrectInstantiationWithWages() {
    final EmployeeWage.Builder wage1 = new EmployeeWage.Builder("wage1", new BigDecimal("1"));
    final EmployeeWage.Builder wage2 = new EmployeeWage.Builder("wage2", new BigDecimal("11"));
    final EmployeeWage.Builder wage3 = new EmployeeWage.Builder("wage3", new BigDecimal("111"));
    final List<EmployeeWage.Builder> wages = new ArrayList<EmployeeWage.Builder>();
    wages.add(wage1);
    wages.add(wage2);
    wages.add(wage3);
    // adding the last 2 using individual add
    final EmployeeWage.Builder wage4 = new EmployeeWage.Builder("wage4", new BigDecimal("1111"));
    final EmployeeWage.Builder wage5 = new EmployeeWage.Builder("wage5", new BigDecimal("11111"));
    final Employee.Builder employeeBuilder = new Employee.Builder(
        new PersonName.NameBuilder("Kevin", "Hanley").build(),
        "klh");
    final Employee employee = employeeBuilder.wages(wages).wage(wage4).wage(wage5).build();
    assertNotNull(employee);
    // wages must be in same order
    wages.add(wage4);
    wages.add(wage5);
    final List<EmployeeWage> wagesBuilt = new ArrayList<EmployeeWage>();
    for (final EmployeeWage.Builder wb : wages) {
      wagesBuilt.add(wb.build());
    }
    assertArrayEquals(wagesBuilt.toArray(), employee.getWages().toArray());
    assertEquals(employee.getWages(), wagesBuilt);
    assertNotSame(wagesBuilt, employee.getWages());
  }
}
