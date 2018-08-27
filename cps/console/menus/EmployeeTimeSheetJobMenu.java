
package cps.console.menus;

import cps.core.model.timeSheet.JobEntry;

class EmployeeTimeSheetJobMenu extends AbstractMenu {

  JobEntry relatedJobEntry_;

  public EmployeeTimeSheetJobMenu(JobEntry jobEntry) {
    super();
    this.relatedJobEntry_ = jobEntry;
  }

  @Override
  public void setBanner() {
    this.bannerText = this.relatedJobEntry_.getCode() + " on " + this.relatedJobEntry_
        .getDateString();
  }

  @Override
  public void setOptions() {
    super.addOption("Dump Job Info");
    super.addOption("Delete Job");
  }

  @Override
  public void doOption(int userOptionChoice) {
    switch (userOptionChoice) {
      case 1 :
        System.out.println(relatedJobEntry_.dumpInfo());
        break;
      case 2 :
        if (DatabaseConnector.programPortal.removeJobEntry(this.relatedJobEntry_.getId())) {
          System.out.println(super.ui.getPrintable("Job", "Removed"));
        } else {
          // TODO ADD ERROR HANDLING
          throw new IllegalStateException("Job removal failed");
        }
        this.shouldExitMenu = true;
        break;
      default :
        throw new IllegalStateException(
            "Sub-Menu user option given by Menu superclass was not valid");
    }
  }
}
