
package cps.console.menus;

import cps.core.model.employee.Employee;

class NameMenu extends AbstractMenu {

  Employee relatedEmployee_;

  public NameMenu(Employee employee) {
    super();
    relatedEmployee_ = employee;
  }

  @Override
  public void setBanner() {
    this.bannerText = relatedEmployee_.getName() + "'s Name Menu";
  }

  @Override
  public void setOptions() {
    super.addOption("Edit Prefix");
    super.addOption("Edit First Name");
    super.addOption("Edit Middle Name");
    super.addOption("Edit Last Name");
    super.addOption("Edit Suffix");
  }

  @Override
  public void doOption(int userChoice) {
    switch (userChoice) {
      case 1 :
        System.out.println("Current Prefix is " + relatedEmployee_.getPrefix());
        final String newPrefix = this.ui.getUserString("Enter new Prefix: ", false);
        relatedEmployee_.setName(relatedEmployee_.getName().withPrefix(newPrefix));
        DatabaseConnector.programPortal.save();
        System.out.println(ui.getPrintable(relatedEmployee_.toString(), "Updated"));

        this.ui.pause();
        break;
      case 2 :
        System.out.println("Current First Name is " + relatedEmployee_.getFirstName());
        final String newFirstName = this.ui.getUserString("Enter new First Name: ", false);
        relatedEmployee_.setName(relatedEmployee_.getName().withFirstName(newFirstName));
        DatabaseConnector.programPortal.save();
        System.out.println(ui.getPrintable(relatedEmployee_.toString(), "Updated"));

        this.ui.pause();
        break;
      case 3 :
        System.out.println("Current Middle Name is " + relatedEmployee_.getMiddleName());
        final String newMiddleName = this.ui.getUserString("Enter new Middle Name: ", false);
        relatedEmployee_.setName(relatedEmployee_.getName().withMiddleName(newMiddleName));
        DatabaseConnector.programPortal.save();
        System.out.println(ui.getPrintable(relatedEmployee_.toString(), "Updated"));
        this.ui.pause();
        break;
      case 4 :
        System.out.println("Current Last Name is " + relatedEmployee_.getLastName());
        final String newLastName = this.ui.getUserString("Enter new Last Name: ", true);
        relatedEmployee_.setName(relatedEmployee_.getName().withLastName(newLastName));
        System.out.println(ui.getPrintable(relatedEmployee_.toString(), "Updated"));
        this.ui.pause();
        break;
      case 5 :
        System.out.println("Current Suffix is " + relatedEmployee_.getSuffix());
        final String newSuffix = this.ui.getUserString("Enter new Suffix: ", true);
        relatedEmployee_.setName(relatedEmployee_.getName().withSuffix(newSuffix));
        System.out.println(ui.getPrintable(this.relatedEmployee_.toString(), "Updated"));
        this.ui.pause();
        break;
      default :
        throw new IllegalStateException(
            "Sub-Menu user option given by Menu superclass was not valid");
    }
  }
}
