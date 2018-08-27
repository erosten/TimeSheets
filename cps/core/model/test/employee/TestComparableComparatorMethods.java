
package cps.core.model.test.employee;

import static org.junit.Assert.assertArrayEquals;

import cps.core.model.employee.Employee;
import cps.core.model.employee.Employee.LastNameCompare;
import cps.core.model.employee.PersonName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestComparableComparatorMethods {

  private static Employee employee1;
  private static Employee employee2;
  private static Employee employee3;
  private static Employee employee4;
  private static Employee employee5;

  /**
   * Sets up employees to test comparator methods with.
   */
  @BeforeClass
  public static void setUpEmployees() {
    employee1 = new Employee.Builder(
        new PersonName.NameBuilder("arnold", "schwarznagger").middleName("the").build(),
        "governator").build();
    employee2 = new Employee.Builder(new PersonName.NameBuilder("ben", "wyatt").build(), "bw")
        .build();
    employee3 = new Employee.Builder(
        new PersonName.NameBuilder("Arnold", "schwarznagger").middleName("the").build(),
        "governator").build();
    employee4 = new Employee.Builder(new PersonName.NameBuilder("erik", "rosten").build(), "bw")
        .build();
    employee5 = new Employee.Builder(
        new PersonName.NameBuilder("arnold", "governator").build(),
        "bw").build();
  }

  @Test
  public void testNormalSortDefaultsToFirstNameThenLastName() {
    final List<Employee> employees = new ArrayList<Employee>();
    employees.add(employee2);
    employees.add(employee3);
    employees.add(employee1);
    Collections.sort(employees, new Employee.FirstNameCompare());
    final Object[] test1 = { employee3, employee1, employee2 };
    assertArrayEquals(employees.toArray(), test1);
    employees.add(employee4);
    Collections.sort(employees, new Employee.FirstNameCompare());
    final Object[] test2 = { employee3, employee1, employee2, employee4 };
    assertArrayEquals(employees.toArray(), test2);
    employees.add(employee5);
    Collections.sort(employees, new Employee.FirstNameCompare());
    // capital A goes before the non capital, but g is before s
    // organizes by first name, THEN last name
    final Object[] test3 = { employee3, employee5, employee1, employee2, employee4 };
    assertArrayEquals(employees.toArray(), test3);
  }

  @Test
  // should behave like one above
  public void testSortByFirstNameSameResultsAsDefault() {
    final List<Employee> employees = new ArrayList<Employee>();
    employees.add(employee2);
    employees.add(employee3);
    employees.add(employee1);
    Collections.sort(employees, new Employee.FirstNameCompare());
    final Object[] test1 = { employee3, employee1, employee2 };
    assertArrayEquals(employees.toArray(), test1);
    employees.add(employee4);
    Collections.sort(employees, new Employee.FirstNameCompare());
    final Object[] test2 = { employee3, employee1, employee2, employee4, };
    assertArrayEquals(employees.toArray(), test2);
    employees.add(employee5);
    Collections.sort(employees, new Employee.FirstNameCompare());
    // capital A goes before the non capital, but g is before s
    // organizes by first name, THEN last name
    final Object[] test3 = { employee3, employee5, employee1, employee2, employee4 };
    assertArrayEquals(employees.toArray(), test3);
  }

  @Test
  public void testSortByLastName() {
    final List<Employee> employees = new ArrayList<Employee>();
    employees.add(employee2);
    employees.add(employee3);
    employees.add(employee1);
    Collections.sort(employees, new LastNameCompare());
    final Object[] test1 = { employee3, employee1, employee2 };
    assertArrayEquals(employees.toArray(), test1);
    employees.add(employee4);
    Collections.sort(employees, new LastNameCompare());
    final Object[] test2 = { employee4, employee3, employee1, employee2 };
    assertArrayEquals(employees.toArray(), test2);
    employees.add(employee5);
    Collections.sort(employees, new LastNameCompare());
    // capital A goes before the non capital, but g is before s
    // organizes by first name, THEN last name
    final Object[] test3 = { employee5, employee4, employee3, employee1, employee2 };
    assertArrayEquals(employees.toArray(), test3);
  }
}
