
package cps.core;

import cps.core.db.frame.BaseEntity;
import cps.core.db.frame.DerbyDatabase;
import cps.core.db.frame.DerbyDatabaseDAO;
import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;
import cps.core.model.employee.EmployeeWage_;
import cps.core.model.employee.Employee_;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class EmployeePortal extends DerbyDatabaseDAO implements EmployeeDAO {

  public EmployeePortal(DerbyDatabase db) throws SQLException {
    super(db);
  }

  @Override
  public void save() {
    super.save();
  }

  /**
   * Add a unique new Employee object to the Handler and saves changes to DB. Unique is defined by
   * not having the same "abbreviation" as any other employee, and not having the same ID defined at
   * time of object creation.
   * 
   * @param employee
   *          a unique employee to be added
   * @return true if new employee saved to database, false if the employee had the same ID or
   *         abbreviation as another employee (not added)
   * 
   */
  @Override
  public boolean addNewEmployee(final Employee employee) {
    // use protected GenericDAO method
    return super.add(Employee.class, employee);
  }

  /**
   * Deletes an employee. Only should be usable by developer.
   */
  public boolean deleteEmployee(final Employee employee) {
    return super.remove(employee);
  }

  /**
   * Removes an employee from the system.
   * 
   * @param id
   *          a id of a valid, existing employee to be removed
   */

  @Override
  public void terminateEmployee(final String id) {
    if (id == null) {
      throw new NullPointerException("Employee id given for termination was null");
    }
    final Employee employee = this.findEmployeeById(id);
    if (employee == null) {
      throw new NullPointerException("Employee found from id was null");
    }
    if (!employee.isTerminated()) {
      employee.terminate();
    }
    super.save();
  }

  /**
   * Removes an employee from the system.
   * 
   * @param employee
   *          a valid, existing employee to be removed
   */
  @Override
  public void terminateEmployee(final Employee employee) {
    this.terminateEmployee(employee.getId());
  }

  /**
   * Tests the managed entity list for the existence of a given employee.
   * 
   * @param employee
   *          a valid employee object that is managed by the Database entityManager
   * @return true iff employee collection contains that employee
   */
  @Override
  public boolean containsEmployee(final Employee employee) {
    return this.contains(Employee.class, employee);
  }

  /**
   * Tests the managed entity list for the existence of a given employee.
   * 
   * @param id
   *          a valid employee id that is managed by the Database entityManager
   * @return true iff employee collection contains that employee
   */
  @Override
  public boolean containsEmployee(final String id) {
    return this.contains(Employee.class, id);
  }

  /**
   * Tests the managed entity list for the existence of a given employee.
   * 
   * @param abbreviation
   *          an abbreviation of a valid employee object that is managed by the Database
   *          entityManager
   * @return true iff employee collection contains that employee
   */

  @Override
  public boolean containsEmployeeByAbbreviation(final String abbreviation) {
    return (this.findEmployeeByAbbreviation(abbreviation) != null);

  }

  /**
   * Add a new EmployeeWage to the database.
   * 
   * @param newWage
   *          this is an EmployeeWage object, set with an existing, managed Employee Object with a
   *          unique name (for this employee)
   * 
   */
  @Override
  public boolean addNewEmployeeWage(final EmployeeWage newWage) {
    final Employee employee = newWage.getEmployee();
    // if you do not check this, passing an unmanaged wage will throw a
    // rollBackException
    // from a the underlying Database implementation which is impossible to
    // generically avoid
    if (!super.contains(Employee.class, employee)) {
      return false;
    }
    // check if the employee already had a wage by that name
    if (employee.containsWage(newWage.getName())) {
      return false;
    }
    employee.addWage(newWage);
    // use protected GenericDAO method
    // method will persist the wage, and upon commit, the employee wage is
    // updated
    return super.add(EmployeeWage.class, newWage);
  }

  /**
   * Removes an employeeWage from the database If the the EmployeeWage or it's attached employee are
   * not managed, null is returned. null cannot be an input - must have an ID
   * 
   * @param removedWage
   *          is the wage to remove from the database (must have a valid employee attached)
   * 
   */
  @Override
  public boolean removeEmployeeWage(final EmployeeWage removedWage) {
    return this.removeEmployeeWage(removedWage.getId());
  }

  /**
   * Removes an employeeWage from the database, given it's ID - an immutable, automatically
   * generated UUID, created at the inception of an employeeWage. If the the EmployeeWage or it's
   * attached employee are not managed, null is returned. null cannot be an input
   * 
   * @param wageId
   *          is the wage id of the wage to remove from the database (must have a valid employee
   *          attached)
   * 
   */
  @Override
  public boolean removeEmployeeWage(final String wageId) {
    final EmployeeWage employeeWage = this.findWageById(wageId);
    // if you do not check the employeeWage here, the next line wll throw a
    // NullPointer if id given was bad
    if (employeeWage == null) {
      return false;
    }
    final Employee employee = employeeWage.getEmployee();
    // check to avoid non-managed employee related errors
    if (!this.containsEmployee(employee)) {
      return false;
    }
    if (employee.removeWage(employeeWage)) {
      super.save();
      super.remove(employeeWage);
    } else {
      throw new IllegalStateException(
          "Failed to remove employeeWage from employee before deleting");
    }
    return true;
  }

  /**
   * Returns all the employees in an alphabetical list.
   * 
   * @return a List of all valid employees in the database
   */
  @Override
  public List<Employee> findAllEmployees() {
    // use protected GenericDAO method
    return super.findAll(Employee.class);
  }

  /**
   * Returns active employees in a non-organized list.
   * 
   * @return a List of all active employees in the database
   */
  @Override
  public List<Employee> findActiveEmployees() {
    final CriteriaBuilder cb = super.getCriteriaBuilder();
    final CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
    final Root<Employee> from = query.from(Employee.class);
    query.select(from);
    final Predicate p1 = cb.equal(from.get(Employee_.terminated), false);
    query.where(p1);
    try {
      return super.getMultiResultFrom(query);
      // array index out of bounds means the employee by given id did not exist
    } catch (final NoResultException nre) {
      return null;
    }
  }

  /**
   * Returns an employee based upon an employeeId, or null if no such employee was found. A Null id
   * will return a null employee
   * 
   * @param employeeId
   *          an employee identifier - an immutable, automatically generated UUID, created at the
   *          inception of an employee
   * @return A valid employee represented by employeeId or null if no such employee existed. returns
   *         a null if a null id is given
   */

  @Override
  public Employee findEmployeeById(final String employeeId) {
    // use protected GenericDAO method
    return super.findById(Employee.class, employeeId);
  }

  /**
   * Searches for an employee with a given abbreviation. Abbreviations are assumed unique. If the
   * employee could not be found, null is returned. Returns null if the given search string is null
   * Treats the search term as case insensitive when comparing to the unique abbreviation
   * 
   * @param abbreviation
   *          an string representing a valid employee in the system (case insensitive)
   * @return a valid employee object if the employee matches the given abbreviation, or null if not
   *         found
   * 
   */
  @Override
  public Employee findEmployeeByAbbreviation(final String abbreviation) {
    if (abbreviation == null) {
      return null;
    }
    final CriteriaBuilder cb = super.getCriteriaBuilder();
    final CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
    final Root<Employee> from = query.from(Employee.class);
    query.select(from);
    query.where(cb.equal(cb.upper(from.get(Employee_.abbreviation_)), abbreviation.toUpperCase()));
    try {
      return super.getSingleResultFrom(query);
      // array index out of bounds means the employee by given id did not exist
    } catch (final NoResultException nre) {
      return null;
    }
  }

  /**
   * Returns an employeeWage based upon an employeeWageId, or null if no such employeeWage was
   * found. Returns null if id given is null
   * 
   * @param wageId
   *          an employee identifier - an immutable, automatically generated UUID, created at the
   *          inception of an employee
   * @return A valid employeeWage represented by employeeWageId or null if no such employeeWage
   *         existed, or if the id given was null
   */
  @Override
  public EmployeeWage findWageById(final String wageId) {
    // use protected GenericDAO method
    return super.findById(EmployeeWage.class, wageId);
  }

  /**
   * Returns an employeeWage based upon an employeeWage Name and an employee abbreviation, or null
   * if no such employeeWage was found. If null is given, it returns null. This method is case
   * insensitive
   * 
   * @param wageName
   *          a string representing an employee Wage Name
   * @param employeeAbbreviation
   *          a string representing an employee abbreviation of the wage owning employee
   * 
   * 
   * @return EmployeeWage A valid employeeWage represented by employeeWageId or null if no such
   *         employeeWage existed
   */
  @Override
  public EmployeeWage findWageByName(final String employeeAbbreviation, final String wageName) {
    if (employeeAbbreviation == null || wageName == null) {
      return null;
    }
    final CriteriaBuilder cb = super.getCriteriaBuilder();
    final CriteriaQuery<EmployeeWage> query = cb.createQuery(EmployeeWage.class);
    final Root<EmployeeWage> wage = query.from(EmployeeWage.class);
    final Predicate p1 = cb.equal(cb.upper(wage.get(EmployeeWage_.rateName_)), wageName
        .toUpperCase());
    // findEmployeeByAbbreviation already lowercases the input, so doing so here
    // would be redundant - comparing objects here
    final Predicate p2 = cb.equal(wage.get(EmployeeWage_.employee_), this
        .findEmployeeByAbbreviation(employeeAbbreviation));
    final Predicate p12 = cb.and(p1, p2);
    query.select(wage).where(p12);
    try {
      return super.getSingleResultFrom(query);
      // array index out of bounds means the employee by given id did not exist
    } catch (final NoResultException nre) {
      return null;
    }
  }

  /**
   * Returns an employeeWage based upon an employeeWage Name, or null if no such employeeWage was
   * found. Returns null if employee given is null.
   * 
   * @param wageName
   *          a string representing an employee Wage Name
   * @param employee
   *          a valid, managed employee object that owns the wage
   * @return A valid, managed employeeWage or null if no such employeeWage existed
   */
  @Override
  public EmployeeWage findWageByName(final Employee employee, final String wageName) {
    if (employee == null || wageName == null) {
      return null;
    }
    return this.findWageByName(employee.getAbbreviation(), wageName);
  }

  @Override
  public <T extends BaseEntity> List<T> findByAttributes(Class<T> clazz,
      Map<String, String> attributes) {
    return super.findByAttributes(clazz, attributes);
  }
}
