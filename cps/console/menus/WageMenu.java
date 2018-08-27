
package cps.console.menus;

import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;

class WageMenu extends AbstractMenu {

  Employee employee_;

  EmployeeWage wage_;

  public WageMenu(Employee employee, EmployeeWage wage) {
    super();
    this.employee_ = employee;
    this.wage_ = wage;
  }

  @Override
  public void setBanner() {
    this.bannerText = this.employee_ + "'s " + wage_.getName() + " Wage Menu";
  }

  @Override
  public void setOptions() {
    super.addOption("Edit " + wage_.getName() + " rate");
    super.addOption("Print related Employee's info");
    super.addOption("Remove Wage");
  }

  @Override
  public void doOption(int userOptionChoice) {
    switch (userOptionChoice) {
      case 1 :
        System.out.println("Current " + wage_.getName().toUpperCase() + " rate is " + wage_
            .getRate());
        wage_.setRate(ui.getUserBigDecimal("Enter the new " + wage_.getName().toUpperCase()
            + " rate: ", 2));
        DatabaseConnector.programPortal.save();
        System.out.println(ui.getPrintable(wage_.getName() + " Rate", "Updated"));

        this.ui.pause();
        break;
      case 2 :
        // this.ui.display(this.wage_.getEmployee());
        break;
      case 3 :
        if (DatabaseConnector.programPortal.removeEmployeeWage(wage_)) {
          System.out.println(ui.getPrintable("Employee", "Removed"));
        } else {
          // TODO ADD ERROR HANDLING
        }
        this.ui.pause();
        this.shouldExitMenu = true;
        break;
      default :
        throw new IllegalStateException(
            "Sub-Menu user option given by Menu superclass was not valid");
    }
  }
}
