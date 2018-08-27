
package cps.core;

import cps.core.db.frame.DerbyDatabase;
import cps.core.db.frame.DerbyDatabaseDAO;
import cps.core.model.employee.Employee;
import cps.core.model.timeSheet.Addition;
import cps.core.model.timeSheet.Deduction;
import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.Extra;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.JobEntry_;
import cps.core.model.timeSheet.TimeSheet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.LocalDate;

public class TimeSheetPortal extends DerbyDatabaseDAO implements TimeSheetDAO {

  public TimeSheetPortal(DerbyDatabase db) throws SQLException {
    super(db);
  }

  @Override
  public void save() {
    super.save();
  }

  @Override
  public void addTimeSheet(TimeSheet timeSheet) {
    super.add(TimeSheet.class, timeSheet);
  }

  @Override
  public void addJobEntry(JobEntry newJob) {
    TimeSheet jobTimeSheet = findTimeSheetFor(newJob);
    if (jobTimeSheet == null) {
      throw new IllegalStateException("Job added cannot have a null timeSheet");
    }
    jobTimeSheet.addJob(newJob);
    super.add(JobEntry.class, newJob);
  }

  @Override
  public void updateJobEntry(JobEntry job) {
    TimeSheet jobTimeSheet = findTimeSheetFor(job);
    jobTimeSheet.updateJob(job);
    super.save();
  }

  @Override
  public TimeSheet findTimeSheetFor(JobEntry jobEntry) {
    return findTimeSheetByDate(jobEntry.getDate());
  }

  public TimeSheet findTimeSheetFor(Extra extra) {
    findTimeSheetByDate(extra.getDate());
    return findTimeSheetByDate(extra.getDate());
  }

  @Override
  public void addExtra(final Extra extra) {
    TimeSheet extraTimeSheet = findTimeSheetFor(extra);
    if (extraTimeSheet == null) {
      throw new IllegalStateException("Addition added cannot have a null timeSheet");
    }
    extraTimeSheet.addExtra(extra);
    if (extra instanceof Addition) {
      super.add(Addition.class, (Addition) extra);
    } else {
      super.add(Deduction.class, (Deduction) extra);
    }

  }

  @Override
  public boolean removeTimeSheet(final String timeSheetId) {
    final TimeSheet timeSheet = this.findTimeSheetById(timeSheetId);
    // if you do not check the employeeWage here, the next line wll throw a
    // NullPointer if id given was bad
    if (timeSheet == null) {
      return false;
    }
    timeSheet.remove();
    return super.remove(timeSheet);
  }

  @Override
  public boolean removeTimeSheet(TimeSheet timeSheet) {
    return this.removeTimeSheet(timeSheet.getId());
  }

  @Override
  public boolean removeJobEntry(String id) {
    JobEntry jobEntry = this.findJobById(id);
    findTimeSheetFor(jobEntry).removeJob(jobEntry);
    return this.remove(this.findJobById(id));
  }

  @Override
  public List<TimeSheet> findAllTimeSheets() {
    List<TimeSheet> timeSheets = super.findAll(TimeSheet.class);
    Collections.sort(timeSheets);
    return timeSheets;
  }

  @Override
  public List<JobEntry> findAllJobs() {
    return super.findAll(JobEntry.class);
  }

  @Override
  public List<JobEntry> findJobsOn(LocalDate date) {
    final CriteriaBuilder cb = super.getCriteriaBuilder();
    final CriteriaQuery<JobEntry> query = cb.createQuery(JobEntry.class);
    final Root<JobEntry> from = query.from(JobEntry.class);
    query.select(from);
    final Predicate p1 = cb.equal(from.get(JobEntry_.jobDate_), date);
    query.where(p1);
    try {
      List<JobEntry> jobEntries = super.getMultiResultFrom(query);
      Collections.sort(jobEntries);
      return jobEntries;
      // array index out of bounds means the employee by given id did not exist
    } catch (final NoResultException nre) {
      return null;
    }
  }

  @Override
  public JobEntry findJobById(String id) {
    return super.findById(JobEntry.class, id);
  }

  @Override
  public TimeSheet findTimeSheetById(String id) {
    return super.findById(TimeSheet.class, id);
  }

  @Override
  public EmployeeSheet findEmployeeSheetById(String id) {
    return super.findById(EmployeeSheet.class, id);
  }

  @Override
  public List<JobEntry> findJobsFor(Employee employee) {
    final CriteriaBuilder cb = super.getCriteriaBuilder();
    final CriteriaQuery<JobEntry> query = cb.createQuery(JobEntry.class);
    final Root<JobEntry> from = query.from(JobEntry.class);
    query.select(from);
    final Predicate p1 = cb.equal(from.get(JobEntry_.employee_), employee);
    query.where(p1);
    try {
      return super.getMultiResultFrom(query);
      // array index out of bounds means the employee by given id did not exist
    } catch (final NoResultException nre) {
      return null;
    }
  }

  @Override
  public List<JobEntry> findJobsFor(Employee employee, TimeSheet timeSheet) {
    final CriteriaBuilder cb = super.getCriteriaBuilder();
    final CriteriaQuery<JobEntry> query = cb.createQuery(JobEntry.class);
    final Root<JobEntry> from = query.from(JobEntry.class);
    query.select(from);
    final Predicate p1 = cb.equal(from.get(JobEntry_.employee_), employee);
    List<Predicate> conditionsList = new ArrayList<Predicate>();
    Predicate onStart = cb.greaterThanOrEqualTo(from.get(JobEntry_.jobDate_), timeSheet
        .getStartDate());
    Predicate onEnd = cb.lessThanOrEqualTo(from.get(JobEntry_.jobDate_), timeSheet.getEndDate());
    conditionsList.add(onStart);
    conditionsList.add(onEnd);
    final Predicate isBetween = cb.and(onStart, onEnd);
    final Predicate p12 = cb.and(p1, isBetween);
    // querty.select(from).where(conditionsList.toArray(new Predicate[] {}));
    query.where(p12);
    try {
      return super.getMultiResultFrom(query);
      // array index out of bounds means the employee by given id did not exist
    } catch (final NoResultException nre) {
      return null;
    }
  }

  @Override
  public TimeSheet findTimeSheetByDate(LocalDate date) {
    List<TimeSheet> timeSheets = this.findAllTimeSheets();
    for (TimeSheet tsheet : timeSheets) {
      // test for inverse to cover the equal case
      boolean startBefore = !tsheet.getStartDate().isAfter(date);
      boolean endAfter = !tsheet.getEndDate().isBefore(date);
      if (startBefore && endAfter) {
        return tsheet;
      }
    }
    return null;
    // TODO create criteria builder solution
    // final CriteriaBuilder cb = super.getCriteriaBuilder();
    // final CriteriaQuery<TimeSheet> query = cb.createQuery(TimeSheet.class);
    // final Root<TimeSheet> from = query.from(TimeSheet.class);
    // query.select(from);
    // List<Predicate> conditionsList = new ArrayList<Predicate>();
    // Predicate onStart = cb.greaterThanOrEqualTo(from.get(TimeSheet_.startDate_), date);
    // Predicate onEnd = cb.lessThanOrEqualTo(from.get(TimeSheet_.endDate_), date);
    // conditionsList.add(onStart);
    // conditionsList.add(onEnd);
    // query.select(from).where(conditionsList.toArray(new Predicate[] {}));
    // try {
    // return super.getSingleResultFrom(query);
    // // array index out of bounds means the employee by given id did not exist
    // } catch (final NoResultException nre) {
    // return null;
    // }
  }

  // precondition: valid extra object given
  // postcondition: extra found & removed, DB saved return true
  // public boolean removeExtra(Extra extra) {
  // for (final TimeSheet timeSheet : this.timeSheets_) {
  // if (extra.belongsTo(timeSheet)) {
  // return timeSheet.removeExtra(extra) && this.remove(extra);
  // }
  // }
  // return false;
  // }

  // precondition: employeeSheet is a valid object w/ valid fields
  // postcondition: if employeeSheet found & removed, return true, else false
  // public boolean removeEmployeeSheet(EmployeeSheet employeeSheet) {
  // for (final TimeSheet timeSheet : this.timeSheets_) {
  // if (timeSheet.getEmployeeSheets().contains(employeeSheet)) {
  // return timeSheet.removeEmployeeSheet(employeeSheet) &&
  // this.remove(employeeSheet);
  // }
  // }
  // return false;
  // }

  // precondition: timeSheet is a valid object w/ valid fields
  // postcondition: if timeSheet found & removed, return true, else false
  // public boolean removeTimeSheet(TimeSheet timeSheet) {
  // if (this.timeSheets_.contains(timeSheet)) {
  // return this.timeSheets_.remove(timeSheet) && this.remove(timeSheet);
  // }
  // return false;
  // }

  // precondition: job is a valid object w/ valid fields
  // postcondition: if job found & removed, return true, else false
  // public boolean removeJob(JobEntry jobEntry) {
  // for (final TimeSheet timeSheet : this.timeSheets_) {
  // if (jobEntry.getTimeSheet().equals(timeSheet)) {
  // return timeSheet.removeJob(jobEntry) && this.jobs_.remove(jobEntry) &&
  // this.remove(
  // jobEntry);
  // }
  // }
  // return false;
  // }

}
