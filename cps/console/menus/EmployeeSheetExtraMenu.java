
package cps.console.menus;

import cps.core.model.timeSheet.Addition;
import cps.core.model.timeSheet.EmployeeSheet;

class EmployeeSheetExtraMenu extends AbstractMenu {

  EmployeeSheet relatedEmployeeSheet_;

  Addition relatedExtra_;

  public EmployeeSheetExtraMenu(EmployeeSheet employeeSheet, Addition extra) {
    super();
    this.relatedEmployeeSheet_ = employeeSheet;
    this.relatedExtra_ = extra;
  }

  @Override
  public void setBanner() {
    this.bannerText = this.relatedEmployeeSheet_.getEmployee().getAbbreviation() + "'s "
        + this.relatedExtra_.getDate() + this.relatedExtra_.getName() + " for $"
        + this.relatedExtra_.getAmount() + " Menu";
  }

  @Override
  public void setOptions() {
    super.addOption("Remove " + this.relatedExtra_.getName());
  }

  @Override
  public void doOption(int userOptionChoice) {
    switch (userOptionChoice) {
      case 1 :
        // final Extra removedExtra = this.relatedExtra_;
        // if (ProgramPortal.timeSheetHandler().removeExtra(this.relatedExtra_)) {
        // super.ui.printMessage(removedExtra.getName().getName(), "Removed");
        // } else {
        // TODO ADD ERROR HANDLING AND SCRIPT EXTRA REMOVAL
        // }
        super.ui.pause();
        this.shouldExitMenu = true;
        break;
      default :
        throw new IllegalStateException(
            "Sub-Menu user option given by Menu superclass was not valid");
    }
  }
}
