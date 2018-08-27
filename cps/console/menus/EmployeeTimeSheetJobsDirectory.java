
package cps.console.menus;

import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.TimeSheet;

class EmployeeTimeSheetJobsDirectory extends AbstractMenu {

  private final EmployeeSheet employeeSheet_;
  private final TimeSheet timeSheet_;

  public EmployeeTimeSheetJobsDirectory(EmployeeSheet employeeSheet, TimeSheet timeSheet) {
    super();
    this.employeeSheet_ = employeeSheet;
    this.timeSheet_ = timeSheet;
  }

  @Override
  public void setBanner() {
    this.bannerText = this.employeeSheet_.getEmployee() + "'s " + this.timeSheet_.getName()
        + " Jobs Directory";
  }

  @Override
  public void setOptions() {
    for (final JobEntry jobEntry : this.employeeSheet_.getJobs()) {
      super.addOption(jobEntry.getDateString() + " : " + jobEntry.getCode());
    }
  }

  @Override
  public void doOption(int userOptionChoice) {
    new EmployeeTimeSheetJobMenu(this.employeeSheet_.getJobs().get(userOptionChoice - 1)).run();
  }
}
