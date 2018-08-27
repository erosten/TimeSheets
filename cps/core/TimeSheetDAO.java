
package cps.core;

import cps.core.model.employee.Employee;
import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.Extra;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.TimeSheet;

import java.util.List;

import org.joda.time.LocalDate;

public interface TimeSheetDAO {

  void save();

  void addJobEntry(final JobEntry jobEntry);

  void updateJobEntry(final JobEntry jobEntry);

  void addExtra(final Extra extra);

  boolean removeTimeSheet(final String timeSheetId);

  boolean removeTimeSheet(final TimeSheet timeSheet);

  TimeSheet findTimeSheetById(String id);

  TimeSheet findTimeSheetByDate(LocalDate date);

  List<TimeSheet> findAllTimeSheets();

  List<JobEntry> findAllJobs();

  List<JobEntry> findJobsOn(LocalDate date);

  List<JobEntry> findJobsFor(Employee employee);

  List<JobEntry> findJobsFor(Employee employee, TimeSheet timeSheet);

  JobEntry findJobById(String id);

  boolean removeJobEntry(String id);

  EmployeeSheet findEmployeeSheetById(String id);

  TimeSheet findTimeSheetFor(JobEntry jobEntry);

  void addTimeSheet(final TimeSheet timeSheet);

  // boolean hasJobOn(LocalDate date);

  // void addExtra(final Addition extra);
}
