
package cps.console.menus;

import cps.core.model.employee.Employee;

import java.math.BigDecimal;

class EditMenu extends AbstractMenu {

  Employee employee_;

  public EditMenu(Employee employee) {
    super();
    this.employee_ = employee;
  }

  @Override
  public void setBanner() {
    this.bannerText = this.employee_.getFirstLastName() + "'s Edit Menu";
  }

  @Override
  public void setOptions() {
    super.addOption("Name");
    super.addOption("Abbreviation");
    super.addOption("Whether the employee knows a language");
    super.addOption("View Wage Structure");
  }

  @Override
  public void doOption(int userOptionChoice) {
    switch (userOptionChoice) {
      case 1 :
        new NameMenu(this.employee_).run();
        break;
      case 2 :
        System.out.println("Current Abbreviation is " + this.employee_.getAbbreviation());
        final String newAbbreviation = this.ui.getUserString("Enter the new chosen abbreviation: ",
            true);
        employee_.setAbbreviation(newAbbreviation);
        DatabaseConnector.programPortal.save();
        System.out.println(ui.getPrintable(this.employee_.getFirstLastName(), "Updated"));
        this.ui.pause();
        break;
      case 3 :
        BigDecimal oldLang = this.employee_.getLanguageBonus();
        oldLang = oldLang.compareTo(BigDecimal.ZERO.setScale(1)) <= 0
            ? BigDecimal.ZERO
            : new BigDecimal(0.5);
        final String formattedOldLang = String.format("%.02f", Double.parseDouble(oldLang
            .toString()));
        System.out.println(this.employee_.getAbbreviation() + "'s current language bonus is + $"
            + formattedOldLang);
        final String variableString = oldLang.equals(BigDecimal.ZERO) ? "add a " : "remove the ";
        final String query = "Would you like to " + variableString + "$.50 language bonus for "
            + this.employee_.toString();
        if (this.ui.saidYes(query)) {
          final BigDecimal newLang = oldLang.equals(BigDecimal.ZERO)
              ? new BigDecimal(0.5)
              : BigDecimal.ZERO;
          employee_.setLang(newLang);
          DatabaseConnector.programPortal.save();
          System.out.println(ui.getPrintable(this.employee_.getFirstLastName(), "Updated"));
          this.ui.pause();
        }
        break;
      case 4 :
        new WagesMenu(this.employee_).run();
        break;
      default :
        throw new IllegalStateException(
            "Sub-Menu user option given by Menu superclass was not valid");
    }
  }
}
