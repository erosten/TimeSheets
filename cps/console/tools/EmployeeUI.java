
package cps.console.tools;

import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;
import cps.core.model.employee.PersonName;
import cps.core.model.timeSheet.TimeSheetConstants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EmployeeUI extends ConsoleUtil {

  public EmployeeUI() {
    super();
  }

  /**
   * Prompts the user for the required information to create a new Employee and returns that
   * employee.
   * 
   * @param employees
   *          the list of employees to check against for abbreviation violations
   * @return a valid Employee object
   */
  public Employee getNewEmployeeInfo(List<Employee> employees) {
    System.out.println("Please enter the following information.");
    final Employee.Builder newEmployee = new Employee.Builder(
        this.getEmployeeName(),
        this.getEmployeeAbbreviation(employees));
    newEmployee.langBonus(this.getEmployeeLang());
    return newEmployee.wages(this.getStandardWageSet()).build();
  }

  private PersonName getEmployeeName() {
    final PersonName.NameBuilder employeeName = new PersonName.NameBuilder(
        getUserString("Employee First Name: ", true),
        getUserString("Employee Last Name: ", true));
    employeeName.middleName(getUserString("Employee Middle Name: ", false));
    employeeName.prefix(getUserString("Employee Prefix (e.g. \"Mr.\"): ", false));
    employeeName.suffix(getUserString("Employee Suffix (e.g. \"Jr.\"): ", false));
    return employeeName.build();
  }

  private String getEmployeeAbbreviation(List<Employee> employees) {
    final String abbreviation = getUserString("Employee Abbreviation: ", true);
    for (final Employee employee : employees) {
      if (employee.getAbbreviation().equalsIgnoreCase(abbreviation)) {
        System.out.println("That abbreviation was already taken by " + employee.getName() + "!");
        System.out.println("Please enter a different abbreviation.");
        return this.getEmployeeAbbreviation(employees);
      }
    }
    return abbreviation;
  }

  private BigDecimal getEmployeeLang() {
    return saidYes("Does this employee know a language?")
        ? new BigDecimal("0.50")
        : BigDecimal.ZERO;
  }

  private List<EmployeeWage.Builder> getStandardWageSet() {
    final List<EmployeeWage.Builder> employeeWages = new ArrayList<EmployeeWage.Builder>();
    final BigDecimal regularRate = getUserBigDecimal("Enter the REGULAR wage: $", 2);
    EmployeeWage.Builder newWage = new EmployeeWage.Builder(
        TimeSheetConstants.REGULAR_WAGE,
        regularRate);
    employeeWages.add(newWage);
    newWage = new EmployeeWage.Builder(
        TimeSheetConstants.WOOD_WAGE,
        getUserBigDecimal("Enter the WOOD wage: $", 2));
    employeeWages.add(newWage);
    BigDecimal premWageBonus = getUserBigDecimal("Enter Premium bonus: $", 2);
    newWage = new EmployeeWage.Builder(
        TimeSheetConstants.PREMIUM_WAGE,
        regularRate.add(premWageBonus));
    employeeWages.add(newWage);
    return employeeWages;
  }
}
