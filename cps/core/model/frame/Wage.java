
package cps.core.model.frame;

import cps.core.model.timeSheet.TimeSheetConstants;

import java.math.BigDecimal;

public interface Wage extends Comparable<Wage> {

  // expected to implement setters, but may be one wage always

  public String getName();

  public BigDecimal getRate();

  /**
   * Equals must return a comparison based on name.
   * 
   * @param other
   *          the other wage to compare to
   * @return a true value if the two names are identical, false otherwise
   */
  @Override
  public boolean equals(Object other);

  default boolean hasSameNameAs(Wage other) {
    return this.getName().equals(other.getName());
  }

  @Override
  default int compareTo(Wage other) {
    // THIS one is after or before or equal
    final int before = -1;
    final int equal = 0;
    final int after = 1;
    if (this.getName().equals(other.getName())) {
      return equal;
    }
    // check regular wage
    if (this.getName().equals(TimeSheetConstants.REGULAR_WAGE)) {
      if (!other.getName().equals(TimeSheetConstants.REGULAR_WAGE)) {
        return before;
      }
    }
    if (other.getName().equals(TimeSheetConstants.REGULAR_WAGE)) {
      if (!this.getName().equals(TimeSheetConstants.REGULAR_WAGE)) {
        return after;
      }
    }
    // check wood wage
    if (this.getName().equals(TimeSheetConstants.WOOD_WAGE)) {
      if (!other.getName().equals(TimeSheetConstants.WOOD_WAGE)) {
        return before;
      }
    }
    if (other.getName().equals(TimeSheetConstants.WOOD_WAGE)) {
      if (!this.getName().equals(TimeSheetConstants.WOOD_WAGE)) {
        return after;
      }
    }
    // check prem wage
    if (this.getName().equals(TimeSheetConstants.PREMIUM_WAGE)) {
      if (!other.getName().equals(TimeSheetConstants.PREMIUM_WAGE)) {
        return before;
      }
    }
    if (other.getName().equals(TimeSheetConstants.PREMIUM_WAGE)) {
      if (!this.getName().equals(TimeSheetConstants.PREMIUM_WAGE)) {
        return after;
      }
    }
    // check travel wage
    if (this.getName().equals(TimeSheetConstants.TRAVEL_WAGE)) {
      if (!other.getName().equals(TimeSheetConstants.TRAVEL_WAGE)) {
        return before;
      }
    }
    if (other.getName().equals(TimeSheetConstants.TRAVEL_WAGE)) {
      if (!this.getName().equals(TimeSheetConstants.TRAVEL_WAGE)) {
        return after;
      }
    }
    return this.getName().compareTo(other.getName());
  }

}
