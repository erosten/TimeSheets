
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;
import cps.core.db.tools.JodaLocalDateConverter;
import cps.core.model.employee.Employee;
import cps.core.model.frame.Wage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

// putting a space on decimal names results in sql errors
@Entity(name = "JobEntry")
@Table(name = "\"JobEntry\"")
@Access(AccessType.FIELD)
public class JobEntry implements Comparable<JobEntry>, BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  // editable relationships
  @OneToOne(orphanRemoval = false, targetEntity = Employee.class)
  @JoinColumn(name = "Employee_id", nullable = false, referencedColumnName = "id")
  private Employee employee_;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = false, targetEntity = JobWage.class)
  @JoinColumn(name = "JobWage_ID", nullable = false, referencedColumnName = "id")
  private JobWage wageUsed_;

  @Converter(converterClass = JodaLocalDateConverter.class, name = "jobDateConverter")
  @Convert("jobDateConverter")
  @Column(name = "JobEntryDate")
  private LocalDate jobDate_;

  @OneToOne(cascade = CascadeType.ALL, targetEntity = JobCode.class)
  @JoinColumn(name = "JobCode_ID", nullable = false, referencedColumnName = "id")
  private JobCode jobCode_;

  @OneToOne(cascade = CascadeType.ALL, targetEntity = JobTravel.class)
  @JoinColumn(name = "JobTravel_ID", nullable = false, referencedColumnName = "id")
  private JobTravel jobTravel_;

  @OneToOne(cascade = CascadeType.ALL, targetEntity = StandardDeduction.class)
  @JoinColumn(name = "StdDeduc_ID", nullable = true, referencedColumnName = "id")
  private StandardDeduction stdDeduction_;

  // non editable relationships
  @OneToOne(cascade = CascadeType.ALL, targetEntity = JobTimes.class)
  @JoinColumn(name = "JobTimes_ID", nullable = false, referencedColumnName = "id")
  private JobTimes jobTimes_;

  @OneToOne(cascade = CascadeType.ALL, targetEntity = JobPay.class)
  @JoinColumn(name = "JobPay_ID", nullable = false, referencedColumnName = "id")
  private JobPay jobPay_ = new JobPay();

  // context data

  // defining @entity requires default constructor
  public JobEntry() {
  }

  private JobEntry(Builder jb) {
    this.employee_ = jb.employee;
    this.wageUsed_ = jb.wageUsed;
    this.jobDate_ = jb.date;
    this.jobCode_ = jb.jobCode;
    this.jobTravel_ = jb.jobTravel;
    this.jobTimes_ = jb.jobTimes;
    this.stdDeduction_ = jb.stdDeduction;
  }

  protected void updateTimeAndPay(BigDecimal smr, List<JobEntry> sameDayJobs,
      List<JobEntry> workWeekJobs) {
    System.out.println("Calling update on " + this.getCode());
    BigDecimal jobTimeHours = BigDecimal.ZERO;
    // must use i indexing, or concurrent modification error will be thrown on
    // updating job time values
    List<BigDecimal> sameDayHours = this.getSameDayHours(sameDayJobs);
    List<BigDecimal> sameWeekHours = this.getSameWeekHours(workWeekJobs);
    boolean hasWorkedSix = this.hasWorkedPrevSixDays(workWeekJobs);
    for (int i = 0; i < jobTimes_.get().size(); i++) {
      BigDecimal sameDay = sameDayHours.get(i);
      BigDecimal sameWeek = sameWeekHours.get(i);
      jobTimes_.get().get(i).updateTimeDistribution(sameDay, sameWeek, hasWorkedSix);
      jobTimeHours = jobTimeHours.add(jobTimes_.get().get(i).getDuration());
    }
    jobTimes_.updateValues();
    // if you do not call this.Mileage() and call jobTravel_.getMileage(), it
    // will not take into account the std deduction mileage
    jobPay_.updateValues(jobTimes_, wageUsed_, getMileage(), smr, employee_.getLanguageBonus());

  }

  // context methods
  // context methods
  // precondition: valid job entry context given
  // postcondition: updates job entry context with proper same work week values
  // for
  // each jobTime
  private List<BigDecimal> getSameWeekHours(List<JobEntry> workWeekJobs) {
    List<BigDecimal> sameWeekHours = new ArrayList<>();
    BigDecimal initializationTime = BigDecimal.ZERO;
    for (int i = 0; i < getTimes().size(); i++) {
      LocalDate jobDate = getDate();
      BigDecimal previousHoursOnWorkWeek = initializationTime;
      // days of week are 1-7 (mon-sun)
      final int daysToWorkWeekStart = jobDate.getDayOfWeek() - TimeSheetConstants.WORK_WEEK_START;
      LocalDate incrementDate = jobDate;
      for (int j = 0; j <= daysToWorkWeekStart; j++) {
        for (JobEntry workWeekJob : workWeekJobs) {
          if (!workWeekJob.getDate().equals(incrementDate)) {
            continue;
          } else if (workWeekJob.getDate().equals(jobDate)) {
            previousHoursOnWorkWeek = previousHoursOnWorkWeek.add(workWeekJob.getRegularHoursBefore(
                getTimes().get(i)));
          } else {
            previousHoursOnWorkWeek = previousHoursOnWorkWeek.add(workWeekJob.getRegularTime());
          }
        }
        incrementDate = incrementDate.minusDays(1);
      }
      sameWeekHours.add(previousHoursOnWorkWeek);
      initializationTime = initializationTime.add(getTimes().get(i).getDuration());
    }
    return sameWeekHours;
  }

  // precondition: valid job entry context given
  // postcondition: updates job entry context with proper same day values for
  // each jobTime
  private List<BigDecimal> getSameDayHours(List<JobEntry> sameDayJobs) {
    List<BigDecimal> sameDayHours = new ArrayList<>();
    BigDecimal initializationTime = BigDecimal.ZERO;
    for (int i = 0; i < getTimes().size(); i++) {
      BigDecimal previousHours = initializationTime;
      for (JobEntry sameDayJob : sameDayJobs) {
        previousHours = previousHours.add(sameDayJob.getHoursBefore(getTimes().get(i)));
      }
      sameDayHours.add(previousHours);
      System.out.println("same day hours for " + getTimes().get(i) + " are " + previousHours);
      initializationTime = initializationTime.add(getTimes().get(i).getDuration());
    }
    return sameDayHours;
  }

  private boolean hasWorkedPrevSixDays(List<JobEntry> workWeekJobs) {
    // local variables
    LocalDate jobDate = getDate();
    final int daysToWorkWeekStart = jobDate.getDayOfWeek() - TimeSheetConstants.WORK_WEEK_START;
    // logic
    if (jobDate.dayOfWeek().get() != TimeSheetConstants.WORK_WEEK_END) {
      return false;
    } else {
      for (int i = 0; i < daysToWorkWeekStart; i++) {
        boolean match = false;
        jobDate = jobDate.minusDays(1);
        for (JobEntry workWeekJob : workWeekJobs) {
          if (workWeekJob.getDate().equals(jobDate)) {
            match = true;
            break;
          }
        }
        if (!match) {
          return false;
        }
      }
      return true;
    }
  }

  // only use when the times you are comparing are on the same day
  protected BigDecimal getHoursBefore(JobTime jobTime) {
    return jobTimes_.getTotalTimeBefore(jobTime);
  }

  protected BigDecimal getRegularHoursBefore(JobTime jobTime) {
    return jobTimes_.getRegularTimeBefore(jobTime);
  }

  public boolean isFlatRate() {
    return jobTimes_.areFlatRate();
  }

  public LocalDate getDate() {
    return this.jobDate_;
  }

  public String getDateString() {
    return (jobDate_.getMonthOfYear() + "/" + jobDate_.getDayOfMonth() + "/" + String.valueOf(
        jobDate_.getYear()).substring(2));
  }

  public String getCode() {
    return this.jobCode_.toString();
  }

  public BigDecimal getDoublePay() {
    return this.jobPay_.getDoublePay();
  }

  public BigDecimal getDoubleTime() {
    return this.jobTimes_.getDoubleTime();
  }

  public BigDecimal getDoubleHalfPay() {
    return this.jobPay_.getDoubleHalfPay();
  }

  public BigDecimal getDoubleHalfTime() {
    return this.jobTimes_.getDoubleAndaHalfTime();
  }

  public Employee getEmployee() {
    return this.employee_;
  }

  public BigDecimal getOverPay() {
    return this.jobPay_.getOverPay();
  }

  public BigDecimal getOverTime() {
    return this.jobTimes_.getOverTime();
  }

  public BigDecimal getRegularPay() {
    return this.jobPay_.getRegularPay();
  }

  public BigDecimal getRegularTime() {
    return this.jobTimes_.getRegularTime();
  }

  public BigDecimal getFlatRatePay() {
    return (jobTimes_.areFlatRate()) ? jobPay_.getTotalWagePay() : BigDecimal.ZERO;
  }

  public List<JobTime> getTimes() {
    return jobTimes_.get();
  }

  public List<Time> getTimesOut() {
    return jobTimes_.getTimesOut();
  }

  public List<Time> getTimesIn() {
    return jobTimes_.getTimesIn();
  }

  public boolean isMultiLineEntry() {
    return jobTimes_.hasMultipleTimes();
  }

  public BigDecimal getTotalHours() {
    return jobTimes_.getTotalHours();
  }

  public BigDecimal getTotalPay() {
    return this.jobPay_.getTotalPay();
  }

  public BigDecimal getTotalWagePay() {
    return this.jobPay_.getTotalWagePay();
  }

  /**
   * Return a BigDecimal representation of Mileage for this JobEntry. If there is a std deduction
   * object and the mileage is above zero, the std deduction mileage is subtracted from the returned
   * BigDecimal mileage value
   * 
   * @return a BigDecimal total mileage representation for the jobEntry (for calculation purposes)
   */
  public BigDecimal getMileage() {
    boolean mileageOverZero = jobTravel_.getMileage().compareTo(BigDecimal.ZERO) > 0;
    boolean stdDeducExists = stdDeduction_ != null;
    return (mileageOverZero && stdDeducExists)
        ? this.jobTravel_.getMileage().subtract(stdDeduction_.getStdDeducMileage())
        : this.jobTravel_.getMileage();
  }

  public String getTravel() {
    return this.jobTravel_.toString();
  }

  public String getTravelMethod() {
    return this.jobTravel_.getMethod();
  }

  public BigDecimal getMileagePay() {
    return this.jobPay_.getMileagePay();
  }

  public Wage getWage() {
    return this.wageUsed_;
  }

  public StandardDeduction getStdDeduction() {
    return this.stdDeduction_;
  }

  public boolean hasStdDeduction() {
    return this.stdDeduction_ != null;
  }

  public boolean hasUncertainTime() {
    return this.jobTimes_.hasUncertainTime();
  }

  @Override
  public String getId() {
    return this.id;
  }

  protected boolean hasSameDayTimeBefore(JobEntry otherJob) {
    boolean hasTimeBefore = this.jobTimes_.hasTimeBefore(otherJob.jobTimes_);
    boolean sameDate = otherJob.getDate().getDayOfYear() == this.getDate().getDayOfYear();
    return sameDate && hasTimeBefore;
  }

  // setters
  public void setCode(String code) {
    jobCode_.setCode(code);
  }

  public void setTravel(String travel) {
    jobTravel_.setTravel(travel);
  }

  public void setDate(LocalDate date) {
    jobDate_ = date;
  }

  public void setTime(JobTime jobTime, Time timeIn, Time timeOut) {
    jobTimes_.setTime(jobTime, timeIn, timeOut);
  }

  // public builder class
  public static class Builder {

    private final Employee employee;

    private JobWage wageUsed;

    private LocalDate date;

    private JobCode jobCode;

    private JobTimes jobTimes = new JobTimes();

    private JobTravel jobTravel;

    private StandardDeduction stdDeduction;
    private boolean stdDeductionIsBefore;
    private boolean hasDeduc;

    private BigDecimal flatRateHrs = BigDecimal.ZERO;

    public Builder(final Employee employee) {
      this.employee = employee;
    }

    public Builder wage(final JobWage wage) {
      this.wageUsed = wage;
      return this;
    }

    public Builder date(final LocalDate date) {
      this.date = date;
      return this;
    }

    public Builder code(final String jobCode) {
      this.jobCode = new JobCode(jobCode);
      return this;
    }

    public Builder addTime(JobTime jobTime) {
      this.jobTimes.addTime(jobTime);
      return this;
    }

    public Builder addTime(LocalTime timeIn, LocalTime timeOut) {
      this.jobTimes.addTime(timeIn, timeOut);
      return this;
    }

    public Builder addTime(LocalTime timeIn, boolean timeInUncertain, LocalTime timeOut,
        boolean timeOutUncertain) {
      this.jobTimes.addTime(timeIn, timeInUncertain, timeOut, timeOutUncertain);
      return this;
    }

    public Builder flatRateHours(BigDecimal totalHours) {
      this.flatRateHrs = totalHours;
      return this;
    }

    public Builder travelString(String travelString) {
      this.jobTravel = new JobTravel(travelString);
      return this;
    }

    /**
     * 
     * @param isBefore
     *          denotes whether the deduction should be BEFORE the job or after, e.g.
     *          stdDeduction(true) places a std deduction object before this job entries times
     * @return the job builder with the updated stdDeduction boolean value
     */
    public Builder stdDeduction(boolean isBefore) {
      stdDeductionIsBefore = isBefore;
      this.hasDeduc = true;
      return this;
    }

    /**
     * Attempts to build a JobEntry object with whatever data the builder has been given. Also sets
     * std deduction and flat rate parameters for job object creation
     * 
     * @return a valid JobEntry object if all fields have been filled correctly.
     * @throws IllegalArgumentException
     *           if any field is null or empty
     */
    public JobEntry build() {
      if (this.employee == null) {
        throw new IllegalArgumentException("Employee for job cannot be null");
      }
      if (this.wageUsed == null) {
        throw new IllegalArgumentException("Wage used for job cannot be null");
      }
      if (this.date == null) {
        throw new IllegalArgumentException("Date of job cannot be null");
      }
      if (this.jobCode == null) {
        throw new IllegalArgumentException("Job Code cannot be null");
      }
      if (this.jobTravel == null) {
        this.jobTravel = new JobTravel("no travel");
      }
      if (this.jobTimes == null) {
        throw new IllegalArgumentException("Job must have times (were null)");
      }
      // handle standard deduction creation based on before/after job
      if (hasDeduc) {
        if (stdDeductionIsBefore) {
          stdDeduction = new StandardDeduction(jobTimes.get().get(0), stdDeductionIsBefore);
        } else {
          stdDeduction = new StandardDeduction(
              jobTimes.get().get(jobTimes.get().size() - 1),
              stdDeductionIsBefore);
        }
      }
      // handle flat rate attatchment to jobtime
      // if this is before and the user attatches flat rate hours before a
      // jobtime, an out of index error will be thrown
      if (this.flatRateHrs.compareTo(BigDecimal.ZERO) > 0) {
        jobTimes.flatRateHours(this.flatRateHrs);
      }
      return new JobEntry(this);
    }
  }

  @Override
  public String toString() {
    return this.getCode();
  }

  public String dumpInfo() {
    String outString = "";
    for (JobTime jobTime : jobTimes_.get()) {
      outString += jobTime.toString();
    }
    outString += "Hour delegations:" + "\n";
    outString += "Total: " + getTotalHours() + "\n";
    outString += "Reg: " + getRegularTime() + "\n";
    outString += "Over: " + getOverTime() + "\n";
    outString += "Double: " + getDoubleTime() + "\n";
    outString += "double and a half: " + getDoubleHalfTime() + "\n";
    outString += "Pay delegations:" + "\n";
    outString += "Total: " + getTotalPay() + "\n";
    outString += "Wage Total: " + getTotalWagePay() + "\n";
    outString += "Mileage pay: " + getMileagePay() + "\n";
    outString += "Reg Pay: " + getRegularPay() + "\n";
    outString += "Over Pay: " + getOverPay() + "\n";
    outString += "Double Pay: " + getDoublePay() + "\n";
    outString += "Double Half Pay: " + getDoubleHalfPay() + "\n";
    return outString;
  }

  // sorts for employeeSheet display
  @Override
  public int compareTo(JobEntry other) {
    // index bugs should not happen...
    if (jobTimes_.get().isEmpty()) {
      System.out.println("job is " + jobCode_.toString());
      return 1;
    }
    if (other.jobTimes_.get().isEmpty()) {
      System.out.println("other is " + jobCode_.toString());
      return -1;
    }
    Time timeOut = this.jobTimes_.get().get(0).getTimeIn();
    Time otherTimeOut = other.jobTimes_.get().get(0).getTimeIn();
    // THIS one is after or before or equal
    final int before = -1;
    final int equal = 0;
    final int after = 1;
    int result = 0;
    if (!this.getDate().equals(other.getDate())) {
      result = this.getDate().compareTo(other.getDate());
    } else {
      result = (timeOut).compareTo(otherTimeOut);
      if (result > 0) {
        result = after;
      } else if (result == 0) {
        result = equal;
      } else if (result < 0) {
        result = before;
      }
    }
    return result;
  }
}
