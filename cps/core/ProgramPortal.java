
package cps.core;

import cps.core.db.frame.BaseEntity;
import cps.core.db.frame.DerbyDatabase;
// import cps.core.db.frame.AbstractDerbyDatabase;
import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;
import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.Extra;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.TimeSheet;
import cps.err.logging.LoggerGenerator;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.slf4j.Logger;

public class ProgramPortal implements AutoCloseable, EmployeeDAO, TimeSheetDAO {

  private static Logger LOG = LoggerGenerator.getLoggerFor(ProgramPortal.class,
      "src/programLog.log");

  // database portals are here
  private static EmployeePortal employeePortal = null;
  private static TimeSheetPortal timeSheetPortal = null;

  private static DerbyDatabase abstractDatabase = DerbyDatabase.ProgramDB;

  private static boolean initialized = false;

  /**
   * Create a ProgramPortal with an AbstractDerbyDatabase. It instantiates an EmployeePortal and a
   * TimeSheetportal with the same database.
   * 
   * @param abstractDatabase
   *          the AbstractDerbyDatabase to connect to.
   * @throws SQLException
   *           if the database could not be connected to
   */
  public ProgramPortal(DerbyDatabase abstractDatabase) throws SQLException {
    ProgramPortal.abstractDatabase = abstractDatabase;
    initialize();
  }

  public boolean isInitialized() {
    return initialized;
  }

  private void initialize() throws SQLException {
    LOG.info("Trying to initialize Program Portal..");
    // if initialized = true, this method does nothing
    if (!initialized) {
      employeePortal = new EmployeePortal(abstractDatabase);
      timeSheetPortal = new TimeSheetPortal(abstractDatabase);
      LOG.info("Program Portal initialized and ready for operations.");
      initialized = true;
    } else {
      LOG.info("Program Portal already initialized.");
    }
  }

  private static void checkInitialization() {
    if (!initialized) {
      throw new IllegalStateException("Program Portal not initialized before calling it's methods");
    }
  }

  /**
   * Restarts the AbstractDerbyDatabase that this ProgramPortal is connected to.
   * 
   * @return this ProgramPortal restarted
   * @throws ClassNotFoundException
   *           if cannot find org.apache.derby.jdbc.EmbeddedDriver, cannot proceed with the method
   * @throws SQLException
   *           if the database connection fails when restarting
   */
  public ProgramPortal restart() throws ClassNotFoundException, SQLException {
    checkInitialization();
    if (abstractDatabase.restart()) {
      initialized = false;
      initialize();
      return this;
    } else {
      return null;
    }
  }

  @Override
  public void save() {
    checkInitialization();
    timeSheetPortal.save();
    employeePortal.save();
  }

  @Override
  public void close() throws SQLException {
    checkInitialization();
    abstractDatabase.shutdown();
    initialized = false;
  }

  @Override
  public boolean addNewEmployee(Employee employee) {
    checkInitialization();
    return employeePortal.addNewEmployee(employee);
  }

  @Override
  public void terminateEmployee(String employeeId) {
    checkInitialization();
    employeePortal.terminateEmployee(employeeId);
  }

  @Override
  public void terminateEmployee(Employee employee) {
    checkInitialization();
    employeePortal.terminateEmployee(employee);
  }

  @Override
  public boolean containsEmployee(Employee employee) {
    checkInitialization();
    return employeePortal.containsEmployee(employee);
  }

  @Override
  public boolean containsEmployee(String employeeId) {
    checkInitialization();
    return employeePortal.containsEmployee(employeeId);
  }

  @Override
  public boolean containsEmployeeByAbbreviation(String abbreviation) {
    checkInitialization();
    return employeePortal.containsEmployeeByAbbreviation(abbreviation);
  }

  @Override
  public boolean addNewEmployeeWage(EmployeeWage wage) {
    checkInitialization();
    return employeePortal.addNewEmployeeWage(wage);
  }

  @Override
  public boolean removeEmployeeWage(final EmployeeWage removedWage) {
    checkInitialization();
    return employeePortal.removeEmployeeWage(removedWage);
  }

  @Override
  public boolean removeEmployeeWage(final String wageId) {
    checkInitialization();
    return employeePortal.removeEmployeeWage(wageId);
  }

  @Override
  public List<Employee> findAllEmployees() {
    checkInitialization();
    return employeePortal.findAllEmployees();
  }

  @Override
  public List<Employee> findActiveEmployees() {
    checkInitialization();
    return employeePortal.findActiveEmployees();
  }

  @Override
  public Employee findEmployeeById(String employeeId) {
    checkInitialization();
    return employeePortal.findEmployeeById(employeeId);
  }

  @Override
  public Employee findEmployeeByAbbreviation(String abbreviation) {
    checkInitialization();
    return employeePortal.findEmployeeByAbbreviation(abbreviation);
  }

  @Override
  public EmployeeWage findWageById(String wageId) {
    checkInitialization();
    return employeePortal.findWageById(wageId);
  }

  @Override
  public EmployeeWage findWageByName(String employeeAbbreviation, String wageName) {
    checkInitialization();
    return employeePortal.findWageByName(employeeAbbreviation, wageName);
  }

  @Override
  public EmployeeWage findWageByName(Employee employee, String wageName) {
    checkInitialization();
    return employeePortal.findWageByName(employee, wageName);
  }

  @Override
  public void addJobEntry(final JobEntry jobEntry) {
    checkInitialization();
    timeSheetPortal.addJobEntry(jobEntry);
  }

  @Override
  public List<TimeSheet> findAllTimeSheets() {
    checkInitialization();
    return timeSheetPortal.findAllTimeSheets();
  }

  // @Override
  // public void addTimeSheet(TimeSheet timeSheet) {
  // timeSheetPortal.addTimeSheet(timeSheet);
  // }

  // @Override
  // public boolean hasJobOn(LocalDate date) {
  // return timeSheetPortal.hasJobOn(date);
  // }

  @Override
  public JobEntry findJobById(String id) {
    return timeSheetPortal.findJobById(id);
  }

  @Override
  public TimeSheet findTimeSheetById(String id) {
    return timeSheetPortal.findTimeSheetById(id);
  }

  @Override
  public EmployeeSheet findEmployeeSheetById(String id) {
    return timeSheetPortal.findEmployeeSheetById(id);
  }

  @Override
  public List<JobEntry> findAllJobs() {
    return timeSheetPortal.findAllJobs();
  }

  @Override
  public List<JobEntry> findJobsOn(LocalDate date) {
    return timeSheetPortal.findJobsOn(date);
  }

  @Override
  public boolean removeTimeSheet(String timeSheetId) {
    return timeSheetPortal.removeTimeSheet(timeSheetId);
  }

  @Override
  public boolean removeTimeSheet(TimeSheet timeSheet) {
    return timeSheetPortal.removeTimeSheet(timeSheet);
  }

  @Override
  public List<JobEntry> findJobsFor(Employee employee) {
    return timeSheetPortal.findJobsFor(employee);
  }

  @Override
  public List<JobEntry> findJobsFor(Employee employee, TimeSheet timeSheet) {
    return timeSheetPortal.findJobsFor(employee, timeSheet);
  }

  @Override
  public TimeSheet findTimeSheetByDate(LocalDate date) {
    return timeSheetPortal.findTimeSheetByDate(date);
  }

  // @Override
  // public void addAddition(Addition addition) {
  // timeSheetPortal.addAddition(addition);
  // }
  //
  // @Override
  // public void addDeduction(Deduction deduction) {
  // timeSheetPortal.addDeduction(deduction);
  //
  // }

  @Override
  public TimeSheet findTimeSheetFor(JobEntry jobEntry) {
    return timeSheetPortal.findTimeSheetFor(jobEntry);
  }

  @Override
  public void addTimeSheet(TimeSheet timeSheet) {
    timeSheetPortal.addTimeSheet(timeSheet);
  }

  @Override
  public boolean removeJobEntry(String id) {
    return timeSheetPortal.removeJobEntry(id);
  }

  @Override
  public void addExtra(Extra extra) {
    timeSheetPortal.addExtra(extra);
  }

  public <T extends BaseEntity> List<T> findByAttributes(Class<T> clazz,
      Map<String, String> attributes) {
    return employeePortal.findByAttributes(clazz, attributes);
  }

  /**
   * Deletes an employee. Only should be usable by developer.
   */
  public boolean deleteEmployee(final Employee employee) {
    return employeePortal.deleteEmployee(employee);
  }

  @Override
  public void updateJobEntry(JobEntry job) {
    timeSheetPortal.updateJobEntry(job);
  }
}
