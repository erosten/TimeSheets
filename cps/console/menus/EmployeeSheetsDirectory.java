
package cps.console.menus;

import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.TimeSheet;

class EmployeeSheetsDirectory extends AbstractMenu {

  private final TimeSheet relatedTimeSheet_;

  public EmployeeSheetsDirectory(TimeSheet relatedTimeSheet) {
    super();
    this.relatedTimeSheet_ = relatedTimeSheet;
  }

  @Override
  public void setBanner() {
    this.bannerText = this.relatedTimeSheet_.getName() + " Employee Sheets Directory";
  }

  @Override
  public void setOptions() {
    for (final EmployeeSheet employeeSheet : this.relatedTimeSheet_.getEmployeeSheets()) {
      super.addOption(employeeSheet.getEmployee().getFirstLastName() + " [" + employeeSheet
          .getEmployee().getAbbreviation() + "]");
    }
  }

  @Override
  public void doOption(int userOptionChoice) {
    new EmployeeSheetMenu(
        this.relatedTimeSheet_.getEmployeeSheets().get(userOptionChoice - 1),
        relatedTimeSheet_).run();
  }
}
