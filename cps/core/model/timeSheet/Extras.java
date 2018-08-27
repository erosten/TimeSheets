
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name = "Extras")
@Access(AccessType.FIELD)
@Table(name = "\"Extras\"")
public class Extras implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @OneToMany(cascade = CascadeType.ALL, targetEntity = Addition.class)
  private final List<Addition> additions_ = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, targetEntity = Deduction.class)
  private final List<Deduction> deductions_ = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, targetEntity = ExtraTotal.class)
  private final List<ExtraTotal> extraTotals_ = new ArrayList<>();

  @Column(precision = 31, scale = 2, name = "ExtraTotal")
  private BigDecimal extraTotal = BigDecimal.ZERO;

  public Extras() {
  }

  protected void addExtra(Extra extra) {
    if (extra instanceof Addition) {
      additions_.add((Addition) extra);
    } else if (extra instanceof Deduction) {
      deductions_.add((Deduction) extra);
    } else {
      throw new IllegalStateException("Extra is not of form addition or deduction");
    }
    boolean foundExtraTotal = false;
    for (ExtraTotal et : extraTotals_) {
      if (et.getName().equals(extra.getName())) {
        et.addExtra(extra);
        foundExtraTotal = true;
        break;
      }
    }
    if (!foundExtraTotal) {
      ExtraTotal et = new ExtraTotal(extra.getName());
      et.addExtra(extra);
      extraTotals_.add(et);
    }
  }

  protected BigDecimal getTotalAdvances() {
    BigDecimal advances = BigDecimal.ZERO;
    for (ExtraTotal et : this.extraTotals_) {
      if (et.getName().equals("Advance")) {
        advances = advances.add(et.getTotal());
      }
    }
    return advances;
  }

  protected BigDecimal getTotalGasAdvances() {
    BigDecimal gasAdvances = BigDecimal.ZERO;
    for (ExtraTotal et : this.extraTotals_) {
      if (et.getName().equals("Gas Advance")) {
        gasAdvances = gasAdvances.add(et.getTotal());
      }
    }
    return gasAdvances;
  }

  protected BigDecimal getTotalBonuses() {
    BigDecimal bonuses = BigDecimal.ZERO;
    for (ExtraTotal et : this.extraTotals_) {
      if (et.getName().equals("Bonus")) {
        bonuses = bonuses.add(et.getTotal());
      }
    }
    return bonuses;
  }

  protected BigDecimal getGasAdvances() {
    return BigDecimal.ZERO;
  }

  protected BigDecimal getBonuses() {
    return BigDecimal.ZERO;
  }

  protected BigDecimal getRetroPay() {
    return BigDecimal.ZERO;
  }

  protected List<Deduction> getDeductions() {
    return this.deductions_;
  }

  protected List<Addition> getAdditions() {
    return this.additions_;
  }

  /**
   * Finds the Deductions that are advances, and returns them in a list format.
   * 
   * @return returns a List/<Deduction/> object containing all the advances. (may be empty)
   */
  public List<Extra> getAdvances() {
    List<Extra> advances = new ArrayList<>();
    for (Deduction deduction : this.deductions_) {
      if (deduction.getName().equals("Advance")) {
        advances.add(deduction);
      }
    }
    return advances;
  }

  @Override
  public String getId() {
    return id;
  }

}
