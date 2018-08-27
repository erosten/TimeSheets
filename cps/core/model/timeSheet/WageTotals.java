
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;
import cps.core.model.frame.Wage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

// 1. package level empty & argument constructors
// 2. public getter for totals
// 3. VARIABLES: id, wagetotal list, unique wages list

@Entity(name = "WageTotals")
@Table(name = "\"WageTotals\"")
@Access(AccessType.FIELD)
public class WageTotals implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @OneToMany(cascade = CascadeType.ALL, targetEntity = WageTotal.class)
  @JoinColumn(name = "WageTotals_ID", referencedColumnName = "id")
  private final List<WageTotal> wageTotals_ = new ArrayList<WageTotal>();

  @Column(precision = 31, scale = 2, name = "DoubleHalfPay")
  private BigDecimal doubleHalfPay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "DoubleHalfTime")
  private BigDecimal doubleHalfTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "DoublePay")
  private BigDecimal doublePay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "DoubleTime")
  private BigDecimal doubleTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "OverPay")
  private BigDecimal overPay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "OverTime")
  private BigDecimal overTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "RegularPay")
  private BigDecimal regularPay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "RegularTime")
  private BigDecimal regularTime_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "WagePay")
  private BigDecimal wagePay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "TotalPay")
  private BigDecimal totalPay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "TotalTime")
  private BigDecimal totalHours_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "MileagePay")
  private BigDecimal mileagePay_ = BigDecimal.ZERO;

  @Column(precision = 31, scale = 2, name = "Mileage")
  private BigDecimal mileage_ = BigDecimal.ZERO;

  public WageTotals() {
    // @entity requires non private 0 arg constructor
  }

  protected void updateValues(List<JobEntry> jobEntries) {
    regularPay_ = BigDecimal.ZERO;
    overPay_ = BigDecimal.ZERO;
    doublePay_ = BigDecimal.ZERO;
    doubleHalfPay_ = BigDecimal.ZERO;
    regularTime_ = BigDecimal.ZERO;
    overTime_ = BigDecimal.ZERO;
    doubleTime_ = BigDecimal.ZERO;
    doubleHalfTime_ = BigDecimal.ZERO;
    wagePay_ = BigDecimal.ZERO;
    totalPay_ = BigDecimal.ZERO;
    totalHours_ = BigDecimal.ZERO;
    mileagePay_ = BigDecimal.ZERO;
    mileage_ = BigDecimal.ZERO;
    createNewWageTotals(jobEntries);
    for (WageTotal wageTotal : wageTotals_) {
      wageTotal.updateValues(jobEntries);
      regularPay_ = regularPay_.add(wageTotal.getRegularPay());
      overPay_ = overPay_.add(wageTotal.getOverPay());
      doublePay_ = doublePay_.add(wageTotal.getDoublePay());
      doubleHalfPay_ = doubleHalfPay_.add(wageTotal.getDoubleHalfPay());
      regularTime_ = regularTime_.add(wageTotal.getRegularTime());
      overTime_ = overTime_.add(wageTotal.getOverTime());
      doubleTime_ = doubleTime_.add(wageTotal.getDoubleTime());
      doubleHalfTime_ = doubleHalfTime_.add(wageTotal.getDoubleHalfTime());
      wagePay_ = wagePay_.add(wageTotal.getWagePay());
      totalPay_ = totalPay_.add(wageTotal.getTotalPay());
      totalHours_ = totalHours_.add(wageTotal.getTotalHours());
      mileagePay_ = mileagePay_.add(wageTotal.getMileagePay());
      mileage_ = mileage_.add(wageTotal.getMileage());
    }
    checkValues();
  }

  private void checkValues() {
    // checks
    boolean payCheck = wagePay_.compareTo(regularPay_.add(overPay_).add(doublePay_).add(
        doubleHalfPay_)) == 0;
    boolean payCheck2 = (wagePay_.add(mileagePay_)).compareTo(totalPay_) == 0;
    boolean hourCheck = totalHours_.compareTo(regularTime_.add(overTime_).add(doubleTime_).add(
        doubleHalfTime_)) == 0;
    if (!payCheck) {
      throw new IllegalStateException(
          "Pay did not add up properly in wageTotal\n Total: " + wagePay_ + " reg:     "
              + regularPay_ + " over: " + overPay_ + " dub: " + doublePay_ + " dub x 1.5 "
              + doubleHalfPay_ + "\n total time: " + totalHours_ + " reg time: " + regularTime_
              + "    over: " + overTime_ + " dub " + doubleTime_ + " dub x 1.5 " + doubleHalfPay_);
    }
    if (!payCheck2) {
      throw new IllegalStateException(
          "Pay did not add up properly in wageTotal\n Total: " + totalPay_ + " wage:     "
              + wagePay_ + " mileage: " + mileagePay_);
    }
    if (!hourCheck) {
      throw new IllegalStateException(
          "Hours did not add up properly in wageTotal\n Total: " + wagePay_ + "     reg: "
              + regularPay_ + " over: " + overPay_ + " dub: " + doublePay_ + " dub x 1.5 "
              + doubleHalfPay_ + "\n total time: " + totalHours_ + " reg time: " + regularTime_
              + "     over: " + overTime_ + " dub " + doubleTime_ + " dub x 1.5 " + doubleHalfPay_);
    }
  }

  private void createNewWageTotals(List<JobEntry> jobEntries) {
    List<WageTotal> wageTotalsWithJobs = new ArrayList<>();
    for (JobEntry jobEntry : jobEntries) {
      boolean wageTotalExisted = false;
      for (WageTotal wt : this.wageTotals_) {
        if (wt.getWage().hasSameNameAs(jobEntry.getWage())) {
          wageTotalExisted = true;
          wageTotalsWithJobs.add(wt);
          break;
        }
      }
      if (!wageTotalExisted) {
        WageTotal newWageTotal = new WageTotal(jobEntry);
        this.wageTotals_.add(newWageTotal);
        wageTotalsWithJobs.add(newWageTotal);
      }
    }
    // remove wage totals without jobs
    for (WageTotal wageTotal : this.wageTotals_) {
      if (!wageTotalsWithJobs.contains(wageTotal)) {
        this.wageTotals_.remove(wageTotal);
      }
    }
  }

  @Override
  public String getId() {
    return id;
  }

  protected List<Wage> getUniqueNamedWages() {
    List<Wage> uniqueWages = new ArrayList<>();
    List<Wage> holder = new ArrayList<>();
    boolean found = false;
    for (WageTotal wt : wageTotals_) {
      for (Wage wage : uniqueWages) {
        if (wt.getWage().hasSameNameAs(wage)) {
          found = true;
          break;
        }
      }
      if (uniqueWages.size() == 0 || !found) {
        holder.add(wt.getWage());
      }
      uniqueWages.addAll(holder);
      holder.clear();
      found = false;
    }
    Collections.sort(uniqueWages);
    return uniqueWages;
  }

  protected BigDecimal getTotalTimeOn(Wage wage) {
    for (WageTotal wt : wageTotals_) {
      if (wt.getWage().getName().equals(wage.getName())) {
        return wt.getTotalHours();
      }
    }
    return BigDecimal.ZERO;
  }

  protected BigDecimal getRegularTimeOn(Wage wage) {
    for (WageTotal wt : wageTotals_) {
      if (wt.getWage().getName().equals(wage.getName())) {
        return wt.getRegularTime();
      }
    }
    return BigDecimal.ZERO;
  }

  protected BigDecimal getOverTimeOn(Wage wage) {
    for (WageTotal wt : wageTotals_) {
      if (wt.getWage().getName().equals(wage.getName())) {
        return wt.getOverTime();
      }
    }
    return BigDecimal.ZERO;
  }

  protected BigDecimal getDoubleTimeOn(Wage wage) {
    for (WageTotal wt : wageTotals_) {
      if (wt.getWage().getName().equals(wage.getName())) {
        return wt.getDoubleTime();
      }
    }
    return BigDecimal.ZERO;
  }

  protected BigDecimal getDoubleHalfTimeOn(Wage wage) {
    for (WageTotal wt : wageTotals_) {
      if (wt.getWage().getName().equals(wage.getName())) {
        return wt.getDoubleHalfTime();
      }
    }
    return BigDecimal.ZERO;
  }

  protected BigDecimal getTotalPayOn(Wage wage) {
    for (WageTotal wt : wageTotals_) {
      if (wt.getWage().getName().equals(wage.getName())) {
        return wt.getWagePay();
      }
    }
    return BigDecimal.ZERO;
  }

  protected BigDecimal getRegularPayOn(Wage wage) {
    for (WageTotal wt : wageTotals_) {
      if (wt.getWage().getName().equals(wage.getName())) {
        return wt.getRegularPay();
      }
    }
    return BigDecimal.ZERO;
  }

  protected BigDecimal getOverPayOn(Wage wage) {
    for (WageTotal wt : wageTotals_) {
      if (wt.getWage().getName().equals(wage.getName())) {
        return wt.getOverPay();
      }
    }
    return BigDecimal.ZERO;
  }

  protected BigDecimal getDoublePayOn(Wage wage) {
    for (WageTotal wt : wageTotals_) {
      if (wt.getWage().getName().equals(wage.getName())) {
        return wt.getDoublePay();
      }
    }
    return BigDecimal.ZERO;
  }

  protected BigDecimal getDoubleHalfPayOn(Wage wage) {
    for (WageTotal wt : wageTotals_) {
      if (wt.getWage().getName().equals(wage.getName())) {
        return wt.getDoubleHalfPay();
      }
    }
    return BigDecimal.ZERO;
  }

  public BigDecimal getDoubleHalfPay() {
    return doubleHalfPay_;
  }

  public BigDecimal getDoubleHalfTime() {
    return doubleHalfTime_;
  }

  public BigDecimal getDoublePay() {
    return doublePay_;
  }

  public BigDecimal getDoubleTime() {
    return doubleTime_;
  }

  public BigDecimal getOverPay() {
    return overPay_;
  }

  public BigDecimal getOverTime() {
    return overTime_;
  }

  public BigDecimal getRegularPay() {
    return regularPay_;
  }

  public BigDecimal getRegularTime() {
    return regularTime_;
  }

  public BigDecimal getTotalPay() {
    return totalPay_;
  }

  public BigDecimal getWagePay() {
    return wagePay_;
  }

  public BigDecimal getTotalHours() {
    return totalHours_;
  }

  public BigDecimal getMileage() {
    return mileage_;
  }

  public BigDecimal getMileagePay() {
    return mileagePay_;
  }

  @Override
  public String toString() {
    String outString = "";
    final NumberFormat nf = TimeSheetConstants.currencyFormatter;
    List<Wage> doneWages = new ArrayList<>();
    for (WageTotal wageTotal : wageTotals_) {
      if (doneWages.size() == 0) {
        doneWages.add(wageTotal.getWage());
      } else {
        boolean alreadyThere = false;
        for (Wage doneWage : doneWages) {
          if (doneWage.hasSameNameAs(wageTotal.getWage())) {
            alreadyThere = alreadyThere || true;
            break;
          }
        }
        if (!alreadyThere) {
          doneWages.add(wageTotal.getWage());
        }
      }
    }
    for (Wage wage : doneWages) {
      outString += wage.getName() + " Pay: ";
      BigDecimal payTotal = BigDecimal.ZERO;
      for (WageTotal wageTotal : wageTotals_) {
        if (wageTotal.getWage().hasSameNameAs(wage)) {
          payTotal = payTotal.add(wageTotal.getWagePay());
        }
      }
      outString += nf.format(payTotal) + "\n";
    }
    return outString + "\n";
  }
}
