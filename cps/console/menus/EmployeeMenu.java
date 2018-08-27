
package cps.console.menus;

import cps.core.model.employee.Employee;

class EmployeeMenu extends AbstractMenu {

  Employee employee_;

  public EmployeeMenu(Employee employee) {
    super();
    this.employee_ = employee;
  }

  @Override
  public void setBanner() {
    this.bannerText = employee_.getFirstLastName() + " [" + employee_.getAbbreviation() + "]"
        + "'s Menu";
  }

  @Override
  public void setOptions() {
    super.addOption("Edit Employee's Data");
    super.addOption("Dump employee info");
    super.addOption("Terminate employee");
    super.addOption("Delete Employee");
  }

  @Override
  public void doOption(int userOptionChoice) {
    switch (userOptionChoice) {
      case 1 :
        new EditMenu(this.employee_).run();
        break;
      case 2 :
        System.out.println(employee_.toString());
        this.ui.pause();
        break;
      case 3 :
        if (this.ui.saidYes("Are you sure you want to terminate " + this.employee_.getFullName())) {
          DatabaseConnector.programPortal.terminateEmployee(this.employee_);
          System.out.println(this.ui.getPrintable(this.employee_.getFirstLastName(), "Terminated"));
          this.shouldExitMenu = true;
        }
        break;
      case 4 :
        if (this.ui.saidYes("Are you sure you want to delete " + this.employee_.getFullName())) {
          DatabaseConnector.programPortal.deleteEmployee(this.employee_);
          System.out.println(this.ui.getPrintable(this.employee_.getFirstLastName(), "Deleted"));
          this.shouldExitMenu = true;
        }
        break;
      default :
        throw new IllegalStateException(
            "Sub-Menu user option given by Menu superclass was not valid");
    }
  }
}
