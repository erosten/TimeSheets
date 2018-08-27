
package cps.console.menus;

import cps.core.model.timeSheet.Addition;
import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.TimeSheet;

import java.util.ArrayList;

class EmployeeSheetExtrasDirectory extends AbstractMenu {

  private final EmployeeSheet relatedEmployeeSheet_;
  private final TimeSheet timeSheet_;

  private final ArrayList<Addition> extras_ = new ArrayList<Addition>();

  private final String extraName_;

  public EmployeeSheetExtrasDirectory(EmployeeSheet employeeSheet, String extraName,
      TimeSheet timeSheet) {
    super();
    this.relatedEmployeeSheet_ = employeeSheet;
    this.extraName_ = extraName;
    this.timeSheet_ = timeSheet;
  }

  @Override
  public void setBanner() {
    this.bannerText = this.relatedEmployeeSheet_.getEmployee().getAbbreviation() + "'s "
        + this.timeSheet_.getName() + extraName_ + "es";
  }

  @Override
  public void setOptions() {
    super.addOption("Options not scripted");
    // for (final Addition extra : this.relatedEmployeeSheet_.getExtras()) {
    // if (extra.getName().equals(this.extraName_)) {
    // super.addOption(extra.toString());
    // this.extras_.add(extra);
    // }
    // }
  }

  @Override
  public void doOption(int userOptionChoice) {
    new EmployeeSheetExtraMenu(this.relatedEmployeeSheet_, this.extras_.get(userOptionChoice - 1))
        .run();
  }
}
