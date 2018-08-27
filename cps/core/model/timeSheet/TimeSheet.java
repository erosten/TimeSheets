
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;
import cps.core.db.tools.JodaLocalDateConverter;
import cps.core.model.employee.Employee;
import cps.core.model.frame.Wage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.LocalDate;

@Entity(name = "TimeSheet")
@Table(name = "\"TimeSheet\"")
@Access(AccessType.FIELD)
public class TimeSheet implements Comparable<TimeSheet>, BaseEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private final String id = createId();

  // basic relationships
  @Column(length = 15, name = "TimeSheetName")
  private String timeSheetName_;

  @Column(precision = 31, scale = 3, name = "StandardMileageRate")
  private BigDecimal standardMileageRate_ = BigDecimal.ZERO; // editable

  @Column(precision = 31, scale = 4, name = "GrandTotal")
  private BigDecimal grandTotal_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "EmployerCost")
  private BigDecimal employerCost_ = BigDecimal.ZERO;

  // employer cost multiplier should be editable
  @Column(precision = 31, scale = 3, name = "EmployerCostMultiplier")
  private BigDecimal employerCostMultiplier_ = TimeSheetConstants.EMPLYR_COST_MULT;

  @Column(precision = 31, scale = 4, name = "AvePaidPerHour")
  private BigDecimal averagePaidPerHour_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "AveCostPerHour")
  private BigDecimal averageCostPerHour_ = BigDecimal.ZERO;

  // converters
  @Converter(converterClass = JodaLocalDateConverter.class, name = "TimeSheetStartDateConverter")
  @Convert("TimeSheetStartDateConverter")
  @Column(name = "TimeSheetStartDate")
  private LocalDate startDate_;

  @Converter(converterClass = JodaLocalDateConverter.class, name = "TimeSheetEndDateConverter")
  @Convert("TimeSheetEndDateConverter")
  @Column(name = "TimeSheetEndDate")
  private LocalDate endDate_;

  // object relationships
  @OneToOne(cascade = CascadeType.ALL, targetEntity = Extras.class)
  @JoinColumn(name = "Extras_ID", referencedColumnName = "id")
  private final Extras extras_ = new Extras();

  @OneToOne(cascade = CascadeType.ALL, targetEntity = TimeSheetWage.class)
  @JoinColumn(name = "\"travelWage_ID\"", referencedColumnName = "id")
  private TimeSheetWage travelWage_; // editable

  @OneToMany(cascade = CascadeType.ALL, targetEntity = EmployeeSheet.class)
  @JoinColumn(name = "\"timeSheet_ID\"", referencedColumnName = "id")
  @OrderBy("employee_ ASC")
  private final List<EmployeeSheet> employeeSheets_ = new ArrayList<EmployeeSheet>();

  @OneToOne(cascade = CascadeType.ALL, targetEntity = JobEntries.class)
  @JoinColumn(name = "TimeSheet_Jobs_ID", referencedColumnName = "id")
  private JobEntries timeSheetJobs_ = new JobEntries();

  @OneToOne(orphanRemoval = false, targetEntity = TimeSheet.class)
  @JoinColumn(name = "Next_TimeSheet_ID", referencedColumnName = "id")
  private TimeSheet nextTimeSheet_;

  @OneToOne(orphanRemoval = false, targetEntity = TimeSheet.class)
  @JoinColumn(name = "Previous_TimeSheet_ID", referencedColumnName = "id")
  private TimeSheet previousTimeSheet_;

  // defining @entity requires default constructor
  public TimeSheet() {
  }

  // initializes sheet to be used
  private TimeSheet(Builder tb) {
    this.startDate_ = tb.startDate;
    this.endDate_ = tb.endDate;
    this.timeSheetName_ = tb.timeSheetName;
    this.standardMileageRate_ = tb.standardMileageRate;
    this.travelWage_ = tb.travelWage_;
    this.previousTimeSheet_ = tb.previousTimeSheet;
    this.nextTimeSheet_ = tb.nextTimeSheet;
    // avoid null exceptions
    // if they are null, method calls will throw null pointer exceptions
    if (previousTimeSheet_ != null) {
      this.previousTimeSheet_.setNext(this);
    }
    if (nextTimeSheet_ != null) {
      this.nextTimeSheet_.setPrevious(this);
    }
  }

  public void remove() {
    if (this.previousTimeSheet_ != null) {
      this.previousTimeSheet_.setNext(null);
    }
    if (this.nextTimeSheet_ != null) {
      this.nextTimeSheet_.setPrevious(null);
    }
    this.nextTimeSheet_ = null;
    this.previousTimeSheet_ = null;
  }

  private void setNext(TimeSheet nextSheet) {
    this.nextTimeSheet_ = nextSheet;
  }

  private void setPrevious(TimeSheet previousSheet) {
    this.previousTimeSheet_ = previousSheet;
  }

  // used by jobEntry object when updating other jobs from a new job's context
  void update() {
    for (EmployeeSheet eSheet : this.employeeSheets_) {
      eSheet.update();
    }
    timeSheetJobs_.update();
    updateValues();
    checkValues();
  }

  public void addJob(JobEntry jobEntry) {
    jobEntry.updateTimeAndPay(this.standardMileageRate_, getJobsOnSameDayWithTimeBefore(jobEntry),
        getEmployeeWorkWeekJobsBefore(jobEntry));
    EmployeeSheet employeeSheet = getEmployeeSheetFor(jobEntry.getEmployee());
    employeeSheet.addJob(jobEntry);
    timeSheetJobs_.add(jobEntry);
    this.update();
    updateAffectedJobsBasedOn(jobEntry);
    this.update();
  }

  public void removeJob(JobEntry jobEntry) {
    EmployeeSheet employeeSheet = this.getEmployeeSheetFor(jobEntry.getEmployee());
    this.timeSheetJobs_.remove(jobEntry);
    employeeSheet.removeJob(jobEntry);
    this.update();
  }

  public void updateJob(JobEntry jobEntry) {
    jobEntry.updateTimeAndPay(this.standardMileageRate_, getJobsOnSameDayWithTimeBefore(jobEntry),
        getEmployeeWorkWeekJobsBefore(jobEntry));
    EmployeeSheet employeeSheet = getEmployeeSheetFor(jobEntry.getEmployee());
    employeeSheet.update();
    this.update();
    updateAffectedJobsBasedOn(jobEntry);
    this.update();
  }

  protected EmployeeSheet addEmployeeSheet(Employee employee) {
    EmployeeSheet employeeSheet = new EmployeeSheet(employee);
    if (employeeSheets_.add(employeeSheet)) {
      return employeeSheet;
    } else {
      // TODO add custom error handling
      throw new IllegalStateException(
          "EmployeeSheet creation for " + employee.getAbbreviation() + " failed");
    }
  }

  // precondition: Valid Extra object given
  // postcondition: extra added to correct employeeSheet, timeSheet updated
  public void addExtra(Extra extra) {
    extras_.addExtra(extra);
    EmployeeSheet employeeSheet = getEmployeeSheetFor(extra.getEmployee());
    employeeSheet.addExtra(extra);
    updateValues();
  }

  // precondition: valid extra object given belongs to timesheet
  // postcondition: return true if extra removed from esheet & sheet updated
  // protected boolean removeExtra(Extra extra) {
  // for (final EmployeeSheet employeeSheet : employeeSheets_) {
  // if (extra.belongsTo(employeeSheet)) {
  // return employeeSheet.removeExtra(extra);
  // }
  // }
  // return false;
  // }

  // precondition: employeeSheet is a valid object
  // postcondition: if sheet removed, return true & update numbers, else false
  protected void removeEmployeeSheet(EmployeeSheet employeeSheet) {
    if (employeeSheets_.remove(employeeSheet)) {
      this.update();
    } else {
      throw new IllegalStateException("Employee Sheet given was not found on the TimeSheet");
    }
  }

  /**
   * Parses EmployeeSheet list for Employee objects and returns them as a list, sorted by last name.
   * 
   * @return a List/<Employee/> of all Employees on the TimeSheet
   */
  public List<Employee> getEmployees() {
    List<Employee> employees = new ArrayList<>();
    for (EmployeeSheet es : this.employeeSheets_) {
      employees.add(es.getEmployee());
    }
    Collections.sort(employees, new Employee.LastNameCompare());
    return employees;
  }

  /**
   * Finds an EmployeeSheet for the given Employee. If one does not exist, it is created.
   * 
   * @param employee
   *          to find or create a sheet for
   * @throws IllegalArgumentException
   *           if EmployeeSheet did not exist.
   */
  public EmployeeSheet getEmployeeSheetFor(Employee employee) {
    for (EmployeeSheet esheet : this.getEmployeeSheets()) {
      if (esheet.getEmployee().equals(employee)) {
        return esheet;
      }
    }
    this.addEmployeeSheet(employee);
    return getEmployeeSheetFor(employee);
  }

  private void updateValues() {
    employerCost_ = getWagePay().multiply(employerCostMultiplier_);
    final BigDecimal totalMoneyOutOfEmployer = ((getWagePay().add(getMileagePay())).add(
        employerCost_));
    // grand total = wage pay + mileage pay + employer cost - advances
    // advances are stored positive
    grandTotal_ = (totalMoneyOutOfEmployer.subtract(getTotalAdvances()));
    if (!getTotalHours().equals(BigDecimal.ZERO)) {
      final MathContext mc = MathContext.DECIMAL128;
      averagePaidPerHour_ = getWagePay().divide(getTotalHours(), mc).multiply(new BigDecimal(
          "100"));
      averageCostPerHour_ = totalMoneyOutOfEmployer.divide(getTotalHours(), mc).multiply(
          new BigDecimal("100"));
      checkValues();
    }
  }

  private void checkValues() {
    // BigDecimal eSheetTotalHours = BigDecimal.ZERO;
    // BigDecimal eSheetTotalPay = BigDecimal.ZERO;
    // BigDecimal eSheetMileagePay = BigDecimal.ZERO;
    // BigDecimal eSheetBonuses = BigDecimal.ZERO;
    // for (EmployeeSheet eSheet : employeeSheets_) {
    // eSheetTotalHours = eSheetTotalHours.add(eSheet.getTotalHours());
    // eSheetTotalPay = eSheetTotalPay.add(eSheet.getTotalPay());
    // eSheetMileagePay = eSheetMileagePay.add(eSheet.getMileagePay());
    // eSheetBonuses = eSheetBonuses.add(eSheet.getBonuses());
    // }
    // if (!getTotalHours().equals(eSheetTotalHours)) {
    // throw new IllegalStateException("Hour Values from TimeSheet and
    // EmployeeSheet do not match.");
    // }
    // if (!getMileagePay().equals(eSheetMileagePay)) {
    // throw new IllegalStateException("Mileage Pay Values from TimeSheet and
    // EmployeeSheet do not match.");
    // }
    // if (getWagePay().compareTo(eSheetTotalPay.subtract(eSheetMileagePay)) !=
    // 0) {
    // throw new IllegalStateException(
    // "Pay Values from TimeSheet and EmployeeSheet do not match.\n " + "Wage
    // Pay for TimeSheet was : " + getWagePay()
    // + "\n EmployeeSheet values are " + eSheetTotalPay + " - " +
    // eSheetMileagePay);
    // }
  }

  /*
   * public API methods
   */

  // precondition: none
  // postcondition: return employee sheets
  public List<EmployeeSheet> getEmployeeSheets() {
    return employeeSheets_;
  }

  // precondition: none
  // postcondition: return pyrl "yr mo end day" e.g Pyrl170715
  public String getName() {
    return timeSheetName_;
  }

  public LocalDate getStartDate() {
    return startDate_;
  }

  public LocalDate getEndDate() {
    return endDate_;
  }

  /**
   * Returns a String representation of the TimeSheetPeriod, e.g. 1/15 - 1/31/17
   * 
   * @return a String representation of the timeSheet Period
   */
  public String getTimeSheetPeriodString() {
    return startDate_.getMonthOfYear() + "/" + startDate_.getDayOfMonth() + " - " + endDate_
        .getMonthOfYear() + "/" + endDate_.getDayOfMonth() + "/" + endDate_.getYear();

  }

  // precondition: none
  // postcondition: return std mileage rate used
  public BigDecimal getStandardMileageRate() {
    return standardMileageRate_;
  }

  // precondition: none
  // postcondition: return travel wage for this timesheet
  public BigDecimal getTravelWageRate() {
    return travelWage_.getRate();
  }

  public TimeSheetWage getTravelWage() {
    return travelWage_;
  }

  public BigDecimal getMileage() {
    return timeSheetJobs_.getMileage();
  }

  // precondition: none
  // postcondition: get the mileage pay
  public BigDecimal getMileagePay() {
    return timeSheetJobs_.getMileagePay();
  }

  // precondition: none
  // postcondition: get the total advances
  public BigDecimal getTotalAdvances() {
    return extras_.getTotalAdvances();
  }

  public List<Extra> getAdvances() {
    return extras_.getAdvances();
  }

  // precondition: none
  // postcondition: get total wages pay
  public BigDecimal getWagePay() {
    return this.timeSheetJobs_.getWagePay();
  }

  // precondition: none
  // postcondition: get employer costF
  public BigDecimal getEmployerCost() {
    return this.employerCost_;
  }

  public BigDecimal getEmployerCostMultiplier() {
    return employerCostMultiplier_;
  }

  // precondition: none
  // postcondition: get the grand total
  public BigDecimal getGrandTotal() {
    return this.grandTotal_;
  }

  // precondition: none
  // postcondition: get total hours
  public BigDecimal getTotalHours() {
    return timeSheetJobs_.getTotalHours();
  }

  // precondition: none
  // postcondition: get average cost/hour
  public BigDecimal getAverageCostPerHour() {
    return this.averageCostPerHour_;
  }

  // precondition: none
  // postcondition: get average paid/hour
  public BigDecimal getAveragePaidPerHour() {
    return this.averagePaidPerHour_;
  }

  public TimeSheet getPrevious() {
    return this.previousTimeSheet_;
  }

  public TimeSheet getNext() {
    return this.nextTimeSheet_;
  }

  public List<JobEntry> getJobs() {
    return timeSheetJobs_.get();
  }

  public List<Wage> getWagesUsed() {
    return timeSheetJobs_.getUniqueWages();
  }

  public BigDecimal getTotalTimeOn(Wage wage) {
    return timeSheetJobs_.getTotalTimeOn(wage);
  }

  public BigDecimal getRegularTimeOn(Wage wage) {
    return timeSheetJobs_.getRegularTimeOn(wage);
  }

  public BigDecimal getOverTimeOn(Wage wage) {
    return timeSheetJobs_.getOverTimeOn(wage);
  }

  public BigDecimal getDoubleTimeOn(Wage wage) {
    return timeSheetJobs_.getDoubleTimeOn(wage);
  }

  public BigDecimal getDoubleHalfTimeOn(Wage wage) {
    return timeSheetJobs_.getDoubleHalfTimeOn(wage);
  }

  public BigDecimal getTotalPayOn(Wage wage) {
    return timeSheetJobs_.getTotalPayOn(wage);
  }

  public BigDecimal getRegularPayOn(Wage wage) {
    return timeSheetJobs_.getRegularPayOn(wage);
  }

  public BigDecimal getOverPayOn(Wage wage) {
    return timeSheetJobs_.getOverPayOn(wage);

  }

  public BigDecimal getDoublePayOn(Wage wage) {
    return timeSheetJobs_.getDoublePayOn(wage);

  }

  public BigDecimal getDoubleHalfPayOn(Wage wage) {
    return timeSheetJobs_.getDoubleHalfPayOn(wage);

  }

  // precondition: none
  // postcondition: return jpa id
  @Override
  public String getId() {
    return id;
  }

  public static class Builder {

    private LocalDate startDate;
    private LocalDate endDate;
    private String timeSheetName;
    private BigDecimal standardMileageRate = BigDecimal.ZERO;
    private TimeSheetWage travelWage_;
    private TimeSheet nextTimeSheet;
    private TimeSheet previousTimeSheet;

    /**
     * Creates a TimeSheet Builder object, to fill with necessary parameters to create a timeSheet
     * through the .build() method.
     * 
     * @param initializationDate
     *          a date within the timeSheetPeriod, split up into two per month, the 1-15, and 16-end
     *          of the month
     */
    public Builder(LocalDate initializationDate) {
      this.startDate = determineStartDate(initializationDate);
      this.endDate = determineEndDate(initializationDate);
      this.timeSheetName = determineTimeSheetName(initializationDate);
    }

    public Builder nextSheet(TimeSheet nextTimeSheet) {
      this.nextTimeSheet = nextTimeSheet;
      return this;
    }

    public Builder previousSheet(TimeSheet previousTimeSheet) {
      this.previousTimeSheet = previousTimeSheet;
      return this;
    }

    public Builder stdMileageRate(BigDecimal stdMileageRate) {
      this.standardMileageRate = stdMileageRate;
      return this;
    }

    public Builder travelWage(TimeSheetWage travelWage) {
      this.travelWage_ = travelWage;
      return this;
    }
    // private methods

    private String determineTimeSheetName(LocalDate jobDate) {
      String timeSheetName;
      final int yearOfJob = jobDate.getYear();
      final int dayOfJob = jobDate.getDayOfMonth();
      // months are 0 based
      final int monthOfJob = jobDate.getMonthOfYear();
      // creates new calendar of that date and finds the amount of days to
      // append at the end of the name of the second payroll
      if (dayOfJob > 15) {
        timeSheetName = "Pyrl " + String.valueOf(yearOfJob).substring(2) + String.format("%02d",
            monthOfJob) + new GregorianCalendar(yearOfJob, (monthOfJob - 1), 1).getActualMaximum(
                Calendar.DAY_OF_MONTH);
      } else {
        timeSheetName = "Pyrl " + String.valueOf(yearOfJob).substring(2) + String.format("%02d",
            monthOfJob) + "15";
      }
      return timeSheetName;
    }

    private LocalDate determineStartDate(LocalDate initializationDate) {
      final int dayOfInitialization = initializationDate.getDayOfMonth();
      if (dayOfInitialization > 15) {
        return initializationDate.withDayOfMonth(16);
      } else {
        return initializationDate.withDayOfMonth(1);
      }
    }

    private LocalDate determineEndDate(LocalDate jobDate) {
      final int dayOfJob = jobDate.getDayOfMonth();
      // increment job date to next month, 1st day, then subtract a day to get
      // last day of job
      // month
      final int lastDay = jobDate.plusMonths(1).withDayOfMonth(1).minusDays(1).getDayOfMonth();
      if (dayOfJob > 15) {
        return jobDate.withDayOfMonth(lastDay);
      } else {
        return jobDate.withDayOfMonth(15);
      }
    }

    /**
     * Attempts to build an TimeSheet object with whatever data the builder has been given.
     * Determines the start and end date given an initializationDate
     * 
     * @return a valid TimeSheet object if all fields have been filled correctly.
     * @throws IllegalArgumentException
     *           if any field is null or empty
     */
    public TimeSheet build() {
      if (this.startDate == null) {
        throw new IllegalStateException("Start Date for TimeSheet cannot be null");
      }
      if (this.endDate == null) {
        throw new IllegalStateException("End Date for TimeSheet cannot be null");
      }
      if (this.timeSheetName == null) {
        throw new IllegalStateException("TimeSheetName cannot be null");
      }
      if (this.standardMileageRate.compareTo(BigDecimal.ZERO) == 0) {
        throw new IllegalStateException("TimeSheet standard mileage rate cannot be zero");
      }
      if (this.travelWage_ == null) {
        throw new IllegalStateException("TimeSheet travelWage cannot be null");
      }
      return new TimeSheet(this);
    }
  }

  // overriden methods
  @Override
  public String toString() {
    return this.timeSheetName_;
  }

  @Override
  // organizes timesheet objects by pyrl date - first to last -
  public int compareTo(TimeSheet other) {
    // if tmsht code - other code > 0 this is later, if <0 other is later
    return Integer.parseInt(this.timeSheetName_.substring(5)) - Integer.parseInt(other.getName()
        .substring(5));
  }

  // timesheet equals another if it's names are the same
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TimeSheet other = (TimeSheet) obj;
    if (this.timeSheetName_ == null) {
      if (other.timeSheetName_ != null) {
        return false;
      }
    } else if (!this.timeSheetName_.equals(other.timeSheetName_)) {
      return false;
    }
    return true;
  }

  // automatic overriden hashcode for equals
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.timeSheetName_ == null) ? 0 : this.timeSheetName_.hashCode());
    return result;
  }

  private List<JobEntry> getEmployeeWorkWeekJobsBefore(JobEntry jobEntry) {
    System.out.println("grabbing workweek jobs from tsheet");
    // TimeSheet tsheet = jobEntry.getTimeSheet();
    List<JobEntry> workWeekJobs = new ArrayList<>();
    LocalDate jobDate = jobEntry.getDate();
    // start constants
    final LocalDate workWeekStart = jobDate.withDayOfWeek(TimeSheetConstants.WORK_WEEK_START);
    final int daysToWorkWeekStart = jobDate.getDayOfWeek() - workWeekStart.getDayOfWeek();
    boolean willEatIntoPreviousSheet = jobDate.minusDays(daysToWorkWeekStart).isBefore(startDate_);
    // gather same day entries
    workWeekJobs.addAll(getJobsOnSameDayWithTimeBefore(jobEntry));
    // gather previous entries
    if (!willEatIntoPreviousSheet) {
      // all jobs are on this sheet
      for (int i = 0; i <= daysToWorkWeekStart; i++) {
        for (JobEntry job : getEmployeeSheetFor(jobEntry.getEmployee()).getJobs()) {
          if ((job.getDate().getDayOfYear() == jobDate.getDayOfYear())) {
            if (!workWeekJobs.contains(job) && !job.equals(jobEntry)) {
              workWeekJobs.add(job);
            }
          }
        }
        jobDate = jobDate.minusDays(1);
      }
    } else {
      // gather remaining jobs on this sheet
      final int remainingDaysOnThisTimeSheet = jobEntry.getDate().getDayOfMonth() - startDate_
          .getDayOfMonth();
      for (int i = 0; i <= daysToWorkWeekStart; i++) {
        if (i <= remainingDaysOnThisTimeSheet) {
          for (JobEntry job : getEmployeeSheetFor(jobEntry.getEmployee()).getJobs()) {
            if (job.getDate().getDayOfYear() == jobDate.getDayOfYear()) {
              if (!workWeekJobs.contains(job) && !job.equals(jobEntry)) {
                workWeekJobs.add(job);
              }
            }
          }
        } else {
          if (previousTimeSheet_ == null) {
            break;
          } else {
            for (JobEntry job : previousTimeSheet_.getEmployeeSheetFor(jobEntry.getEmployee())
                .getJobs()) {
              if (job.getDate().equals(jobDate)) {
                if (!workWeekJobs.contains(job) && !job.equals(jobEntry)) {
                  workWeekJobs.add(job);
                }
              }
            }
          }
        }
        jobDate = jobDate.minusDays(1);
      }
    }
    return workWeekJobs;
  }

  /**
   * Creates a job list of all jobs on the jobs with time before the given job.
   * 
   * @param jobEntry
   *          the given job to find jobs before
   * @return a list of with EVEN ONE TIME before the jobEntry
   */
  private List<JobEntry> getJobsOnSameDayWithTimeAfter(JobEntry jobEntry) {
    List<JobEntry> sameDayEntries = new ArrayList<>();
    for (JobEntry job : getEmployeeSheetFor(jobEntry.getEmployee()).getJobs()) {
      if (!job.equals(jobEntry) && jobEntry.hasSameDayTimeBefore(job)) {
        sameDayEntries.add(job);
      }
    }
    return sameDayEntries;
  }

  // used to get same day hours
  private List<JobEntry> getJobsOnSameDayWithTimeBefore(JobEntry jobEntry) {
    List<JobEntry> sameDayEntries = new ArrayList<>();
    for (JobEntry job : getEmployeeSheetFor(jobEntry.getEmployee()).getJobs()) {
      if (!job.equals(jobEntry) && job.hasSameDayTimeBefore(jobEntry)) {
        sameDayEntries.add(job);
      }
    }
    return sameDayEntries;
  }

  private void updateAffectedJobsBasedOn(JobEntry jobEntry) {
    System.out.println("Updating Other jobs in system");
    LocalDate jobDate = jobEntry.getDate();
    final LocalDate workWeekEnd = jobDate.withDayOfWeek(TimeSheetConstants.WORK_WEEK_END);
    final int daysUntilWorkWeekEnds = workWeekEnd.getDayOfWeek() - jobDate.getDayOfWeek();
    boolean willEatIntoNextSheet = jobDate.plusDays(daysUntilWorkWeekEnds).isAfter(endDate_);
    List<JobEntry> jobsToUpdate = new ArrayList<>();
    jobsToUpdate.addAll(getJobsOnSameDayWithTimeAfter(jobEntry));
    jobDate = jobDate.plusDays(1);
    while (!jobDate.isAfter(workWeekEnd)) {
      if (!willEatIntoNextSheet) {
        for (JobEntry employeeSheetJob : getEmployeeSheetFor(jobEntry.getEmployee()).getJobs()) {
          if (employeeSheetJob.getDate().equals(jobDate) && !employeeSheetJob.equals(jobEntry)) {
            jobsToUpdate.add(employeeSheetJob);
          }
        }
      } else {
        if (nextTimeSheet_ != null) {
          for (JobEntry nextTimeSheetEmployeeSheetJob : nextTimeSheet_.getEmployeeSheetFor(jobEntry
              .getEmployee()).getJobs()) {
            if (nextTimeSheetEmployeeSheetJob.getDate().equals(jobDate)
                && !nextTimeSheetEmployeeSheetJob.equals(jobEntry)) {
              jobsToUpdate.add(nextTimeSheetEmployeeSheetJob);
            }
          }
        }
      }
      jobDate = jobDate.plusDays(1);
    }
    // update necessary jobs
    for (JobEntry jobToUpdate : jobsToUpdate) {
      if (jobToUpdate.getDate().isBefore(endDate_)) {
        jobToUpdate.updateTimeAndPay(standardMileageRate_, getJobsOnSameDayWithTimeBefore(
            jobToUpdate), getEmployeeWorkWeekJobsBefore(jobToUpdate));

      } else {
        if (nextTimeSheet_ != null) {
          jobToUpdate.updateTimeAndPay(nextTimeSheet_.standardMileageRate_, nextTimeSheet_
              .getJobsOnSameDayWithTimeBefore(jobToUpdate), nextTimeSheet_
                  .getEmployeeWorkWeekJobsBefore(jobToUpdate));
        }
      }
    }
  }
}
