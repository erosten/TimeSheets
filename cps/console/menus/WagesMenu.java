
package cps.console.menus;

import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;

class WagesMenu extends AbstractMenu {

  private final Employee employee_;

  public WagesMenu(Employee employee) {
    super();
    this.employee_ = employee;
  }

  @Override
  public void setBanner() {
    this.bannerText = this.employee_ + "'s Wage Structure";
  }

  @Override
  public void setOptions() {
    for (final EmployeeWage wage : this.employee_.getWages()) {
      super.addOption(ui.getPrintable(wage.getName(), wage.getRate().toString()));
    }
    super.addOption("Add a new Wage");
  }

  @Override
  public void doOption(int userOptionChoice) {
    if (userOptionChoice == (this.employee_.getWages().size() + 1)) {
      final String wageName = this.ui.getUserString("Enter the name of the new Wage: ", true);
      final EmployeeWage.Builder newWage = new EmployeeWage.Builder(
          wageName,
          this.ui.getUserBigDecimal("Enter the rate per hour of this new Wage: ", 2));
      newWage.employee(this.employee_);
      if (DatabaseConnector.programPortal.addNewEmployeeWage(newWage.build())) {
        System.out.println(ui.getPrintable(wageName, "Added"));
      } else {
        // TODO ADD ERROR HANDLING
        System.out.println(ui.getPrintable(wageName + " Add", "Failed"));

      }
    } else {
      new WageMenu(this.employee_, this.employee_.getWages().get(userOptionChoice - 1)).run();
    }
  }
}
