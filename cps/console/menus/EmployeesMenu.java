
package cps.console.menus;

import cps.core.model.employee.Employee;

class EmployeesMenu extends AbstractMenu {

  public EmployeesMenu() {
    super();
  }

  @Override
  public void setBanner() {
    this.bannerText = "Employees Menu";
  }

  @Override
  public void setOptions() {
    super.addOption("Add new employee");
    for (final Employee employee : DatabaseConnector.programPortal.findAllEmployees()) {
      super.addOption(employee.getFullName());

    }
  }

  @Override
  public void doOption(int userOptionChoice) {
    if (userOptionChoice == 1) {
      // assumes valid wage data while grabbing employee info
      // already scripted internal checks for string inputs
      if (DatabaseConnector.programPortal.addNewEmployee(this.eui.getNewEmployeeInfo(
          DatabaseConnector.programPortal.findAllEmployees()))) {
        System.out.println(this.ui.getPrintable("System.admin", "Employee Added"));
      }
    } else {
      new EmployeeMenu(
          (Employee) DatabaseConnector.programPortal.findAllEmployees().toArray()[userOptionChoice
              - 2]).run();
    }
  }
}
