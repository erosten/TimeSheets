
package cps.core;

import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;

import java.util.List;

public interface EmployeeDAO {

  void save();

  boolean addNewEmployee(final Employee employee);

  void terminateEmployee(final String employeeId);

  void terminateEmployee(final Employee employee);

  boolean containsEmployee(final Employee employee);

  boolean containsEmployee(final String employeeId);

  boolean containsEmployeeByAbbreviation(final String abbreviation);

  boolean addNewEmployeeWage(final EmployeeWage wage);

  boolean removeEmployeeWage(final EmployeeWage removedWage);

  boolean removeEmployeeWage(final String wageId);

  List<Employee> findAllEmployees();

  List<Employee> findActiveEmployees();

  Employee findEmployeeById(final String employeeId);

  Employee findEmployeeByAbbreviation(final String abbreviation);

  EmployeeWage findWageById(final String wageId);

  EmployeeWage findWageByName(final String employeeAbbreviation, final String wageName);

  EmployeeWage findWageByName(final Employee employee, final String wageName);

}
