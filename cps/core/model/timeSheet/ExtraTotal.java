
package cps.core.model.timeSheet;

import cps.core.db.frame.BaseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name = "ExtraTotal")
@Table(name = "\"ExtraTotal\"")
@Access(AccessType.FIELD)
public class ExtraTotal implements BaseEntity {

  @Id
  @Column(name = "id")
  private final String id = createId();

  @OneToMany(cascade = CascadeType.ALL, targetEntity = Addition.class)
  private List<Addition> additions_ = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, targetEntity = Deduction.class)
  private List<Deduction> deductions_ = new ArrayList<>();

  @Basic
  @Column(name = "ExtraName")
  private String extraName_;

  @Column(precision = 31, scale = 4, name = "Total")
  private BigDecimal total_ = BigDecimal.ZERO;

  public ExtraTotal() {
  }

  protected ExtraTotal(String extraName) {
    extraName_ = extraName;
  }

  /**
   * An Extra represents an Addition OR Deduction object, and is created by passing one of the two
   * in as parameters.
   * 
   * @param extra
   *          the Addition or Deduction object to create an extra out of
   */
  protected void addExtra(Extra extra) {
    if (!extra.getName().equals(extraName_)) {
      throw new IllegalStateException("Incorrect extra given to ExtraTotal");
    }
    if (additions_.isEmpty() && !deductions_.isEmpty()) {
      if (extra instanceof Addition) {
        throw new IllegalStateException("Addition given to an extraTotal for deductions");
      }
    }
    if (deductions_.isEmpty() && !additions_.isEmpty()) {
      if (extra instanceof Deduction) {
        throw new IllegalStateException("Deduction given to an extraTotal for additions");
      }
    }
    if (extra instanceof Addition) {
      additions_.add((Addition) extra);
    } else if (extra instanceof Deduction) {
      deductions_.add((Deduction) extra);
    } else {
      throw new IllegalStateException("Extra is not of form addition or deduction");
    }
    total_ = total_.add(extra.getAmount());
  }

  protected String getName() {
    return this.extraName_;
  }

  protected BigDecimal getTotal() {
    return this.total_;
  }

  @Override
  public String getId() {
    return this.id;
  }
}
