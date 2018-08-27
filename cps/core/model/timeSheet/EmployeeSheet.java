
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;
import cps.core.model.employee.Employee;
import cps.core.model.frame.Wage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

@Entity(name = "EmployeeSheet")
@Table(name = "\"EmployeeSheet\"")
@Access(AccessType.FIELD)
public class EmployeeSheet implements Comparable<EmployeeSheet>, BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  // basic relationships

  @Column(precision = 31, scale = 4, name = "TotalCheck")
  private BigDecimal totalCheck_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "AvePaidPerHour")
  private BigDecimal averagePaidPerHour_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 4, name = "AveCostPerHour")
  private BigDecimal averageCostPerHour_ = BigDecimal.ZERO;

  // object relationships

  @OneToOne(orphanRemoval = false, targetEntity = Employee.class)
  @JoinColumn(name = "Employee_ID", referencedColumnName = "id")
  private Employee employee_;

  @OneToOne(cascade = CascadeType.ALL, targetEntity = JobEntries.class)
  @JoinColumn(name = "JobEntries_ID", referencedColumnName = "id")
  private JobEntries employeeSheetJobs_ = new JobEntries();

  @OneToOne(cascade = CascadeType.ALL, targetEntity = Extras.class)
  @JoinColumn(name = "Extras_ID", referencedColumnName = "id")
  private final Extras extras_ = new Extras();

  // package level methods & constructors
  public EmployeeSheet() {
    // defining @entity requires default constructor
  }

  // initializes values of employee sheet to be used..
  protected EmployeeSheet(Employee employee) {
    this.employee_ = employee;
  }

  // precondition: valid extra object given
  // postcondition: extra added, totals updated
  protected void addExtra(Extra extra) {
    extras_.addExtra(extra);
    updateValues();
  }

  // precondition: valid extra object given belongs to esheet
  // postcondition: extra removed from esheet & sheet updated
  // protected boolean removeExtra(Extra extra) {
  // return this.extras_.remove(extra);
  // }

  protected void addJob(JobEntry newJob) {
    employeeSheetJobs_.add(newJob);
    this.update();
  }

  protected void removeJob(JobEntry jobEntry) {
    employeeSheetJobs_.remove(jobEntry);
    this.update();
  }

  protected void update() {
    employeeSheetJobs_.update();
    updateValues();
  }

  private void updateValues() {
    // total check is wage pay + mileage pay + bonuses - advances
    totalCheck_ = getTotalPay().add(getTotalBonuses()).subtract(getTotalAdvances().add(
        getTotalGasAdvances()));
    final MathContext mc = MathContext.DECIMAL128;
    if (!getTotalHours().equals(BigDecimal.ZERO)) {
      averagePaidPerHour_ = getWagePay().divide(getTotalHours(), mc).multiply(new BigDecimal(
          "100"));
      averageCostPerHour_ = averagePaidPerHour_.multiply(
          TimeSheetConstants.EMPLYR_EMPLYEE_COST_MULT);
    }
    checkValues();
  }

  private void checkValues() {
    if (getTotalPay().compareTo(employeeSheetJobs_.getTotalPay()) != 0) {
      throw new IllegalStateException(
          "wage pay formula is incorrect - total: " + getTotalPay().toEngineeringString()
              + " wagetotal: " + employeeSheetJobs_.getTotalPay().toEngineeringString());
    }
    BigDecimal totalHours = BigDecimal.ZERO;
    BigDecimal totalPay = BigDecimal.ZERO;
    for (JobEntry jobEntry : employeeSheetJobs_.get()) {
      totalHours = totalHours.add(jobEntry.getTotalHours());
      totalPay = totalPay.add(jobEntry.getTotalPay());
    }
    if (!totalHours.equals(getTotalHours())) {
      throw new IllegalStateException("Job Hours did not add up");
    }
    if (totalPay.subtract(getMileagePay()).compareTo(employeeSheetJobs_.getRegularPay().add(
        employeeSheetJobs_.getOverPay()).add(employeeSheetJobs_.getDoublePay()).add(
            employeeSheetJobs_.getTotalDoubleHalfPay())) != 0) {
      throw new IllegalStateException(
          "total pay from jobs did not add up to value in employeeSheetJobs_\n     Total: "
              + totalPay + "\nMileage Pay: " + employeeSheetJobs_.getMileagePay() + "\nJobs Reg: "
              + employeeSheetJobs_.getRegularPay() + "\nJobs Over: " + employeeSheetJobs_
                  .getOverPay() + "\n Jobs dub: " + employeeSheetJobs_.getDoublePay()
              + "\n Jobs dub x1.5: " + employeeSheetJobs_.getTotalDoubleHalfPay());
    }
  }

  public BigDecimal getTotalTimeOn(Wage wage) {
    return employeeSheetJobs_.getTotalTimeOn(wage);
  }

  public BigDecimal getRegularTimeOn(Wage wage) {
    return employeeSheetJobs_.getRegularTimeOn(wage);
  }

  public BigDecimal getOverTimeOn(Wage wage) {
    return employeeSheetJobs_.getOverTimeOn(wage);
  }

  public BigDecimal getDoubleTimeOn(Wage wage) {
    return employeeSheetJobs_.getDoubleTimeOn(wage);
  }

  public BigDecimal getDoubleHalfTimeOn(Wage wage) {
    return employeeSheetJobs_.getDoubleHalfTimeOn(wage);
  }

  public BigDecimal getTotalPayOn(Wage wage) {
    return employeeSheetJobs_.getTotalPayOn(wage);
  }

  public BigDecimal getRegularPayOn(Wage wage) {
    return employeeSheetJobs_.getRegularPayOn(wage);
  }

  public BigDecimal getOverPayOn(Wage wage) {
    return employeeSheetJobs_.getOverPayOn(wage);

  }

  public BigDecimal getDoublePayOn(Wage wage) {
    return employeeSheetJobs_.getDoublePayOn(wage);

  }

  public BigDecimal getDoubleHalfPayOn(Wage wage) {
    return employeeSheetJobs_.getDoubleHalfPayOn(wage);

  }

  public Employee getEmployee() {
    return this.employee_;
  }

  // precondition: none
  // postcondition: returns job entries for sheet
  public List<JobEntry> getJobs() {
    return this.employeeSheetJobs_.get();
  }

  // precondition: none
  // postcondition: returns total employee check for pay period
  public BigDecimal getTotalCheck() {
    return this.totalCheck_.setScale(2, RoundingMode.HALF_UP);
  }

  // precondition: none
  // postcondition: returns total advances for pay period
  public BigDecimal getTotalAdvances() {
    return extras_.getTotalAdvances();
  }

  public BigDecimal getTotalGasAdvances() {
    return extras_.getTotalGasAdvances();
  }

  public BigDecimal getTotalBonuses() {
    return extras_.getTotalBonuses();
  }

  public List<Extra> getAdvances() {
    return this.extras_.getAdvances();
  }

  // precondition: none
  // postcondition: return total mileage PAY
  public BigDecimal getMileagePay() {
    return this.employeeSheetJobs_.getMileagePay();
  }

  // precondition: none
  // postcondition: return total mileage
  public BigDecimal getMileage() {
    return this.employeeSheetJobs_.getMileage();
  }

  // precondition: none
  // postcondition: return total hours
  public BigDecimal getTotalHours() {
    return employeeSheetJobs_.getTotalHours();
  }

  // precondition: none
  // postcondition: return wages pay + mileage pay
  public BigDecimal getTotalPay() {
    return employeeSheetJobs_.getTotalPay();
  }

  public BigDecimal getWagePay() {
    return employeeSheetJobs_.getWagePay();
  }

  // precondition: none
  // postcondition: return ave cost/hour
  public BigDecimal getAverageCostPerHour() {
    return this.averageCostPerHour_;
  }

  // precondition: none
  // postcondition: return ave paid/hour
  public BigDecimal getAveragePaidPerHour() {
    return this.averagePaidPerHour_;
  }

  public List<Wage> getWagesUsed() {
    return this.employeeSheetJobs_.getUniqueWages();
  }

  public List<Addition> getAdditions() {
    return this.extras_.getAdditions();
  }

  public List<Deduction> getDeductions() {
    return this.extras_.getDeductions();
  }

  // precondition: none
  // postcondition: return id
  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public int compareTo(EmployeeSheet other) {
    return this.employee_.getAbbreviation().compareTo(other.employee_.getAbbreviation());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((employee_ == null) ? 0 : employee_.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

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
    EmployeeSheet other = (EmployeeSheet) obj;
    if (employee_ == null) {
      if (other.employee_ != null) {
        return false;
      }
    } else if (!employee_.equals(other.employee_)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }
}
