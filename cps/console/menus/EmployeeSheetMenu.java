
package cps.console.menus;

import cps.console.tools.Display;
import cps.core.model.timeSheet.Addition;
import cps.core.model.timeSheet.Deduction;
import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.TimeSheet;
import cps.core.model.timeSheet.TimeSheetConstants;

import org.joda.time.LocalDate;

class EmployeeSheetMenu extends AbstractMenu {

  private final EmployeeSheet employeeSheet_;
  private final TimeSheet timeSheet_;

  public EmployeeSheetMenu(EmployeeSheet employeeSheet, TimeSheet timeSheet) {
    super();
    this.employeeSheet_ = employeeSheet;
    this.timeSheet_ = timeSheet;
  }

  @Override
  public void setBanner() {
    this.bannerText = employeeSheet_.getEmployee().getAbbreviation() + "'s " + timeSheet_.getName()
        + " Sheet Menu";
  }

  @Override
  public void setOptions() {
    super.addOption("Job Directory");
    super.addOption("Advance Directory");
    super.addOption("Gas Advance Directory");
    super.addOption("Bonus Directory");
    super.addOption("Display Employee Sheet");
    super.addOption("Add Job");
    super.addOption("Add Advance");
    super.addOption("Add Gas Advance");
    super.addOption("Add Bonus");
    super.addOption("Add Extra");
  }

  @Override
  public void doOption(int userOptionChoice) {
    switch (userOptionChoice) {
      case 1 :
        new EmployeeTimeSheetJobsDirectory(this.employeeSheet_, this.timeSheet_).run();
        break;
      case 2 :
        new EmployeeSheetExtrasDirectory(
            this.employeeSheet_,
            TimeSheetConstants.ADVANCE,
            timeSheet_).run();
        break;
      case 3 :
        new EmployeeSheetExtrasDirectory(
            this.employeeSheet_,
            TimeSheetConstants.GAS_ADVANCE,
            timeSheet_).run();
        break;
      case 4 :
        new EmployeeSheetExtrasDirectory(this.employeeSheet_, TimeSheetConstants.BONUS, timeSheet_)
            .run();
        break;
      case 5 :
        System.out.println(Display.display(this.employeeSheet_));
        this.ui.pause();
        break;
      case 6 :
        System.out.println("Please enter the following information.");
        final LocalDate jobDate = super.tsui.getUserDate("Enter Job Date " + "(MM/dd/YY): ");
        if (DatabaseConnector.programPortal.findTimeSheetByDate(jobDate) == null) {
          TimeSheet timeSheet = super.tsui.getNewTimeSheet(jobDate, DatabaseConnector.programPortal
              .findAllTimeSheets());
          DatabaseConnector.programPortal.addTimeSheet(timeSheet);
        }
        final JobEntry je = super.tsui.getJob(DatabaseConnector.programPortal.findAllEmployees(),
            DatabaseConnector.programPortal.findTimeSheetByDate(jobDate), jobDate);
        DatabaseConnector.programPortal.addJobEntry(je);
        break;
      case 7 :
        final LocalDate advanceDate = super.tsui.getUserDate("Enter Date of Advance: ");
        if (DatabaseConnector.programPortal.findTimeSheetByDate(advanceDate) == null) {
          TimeSheet timeSheet = super.tsui.getNewTimeSheet(advanceDate,
              DatabaseConnector.programPortal.findAllTimeSheets());
          DatabaseConnector.programPortal.addTimeSheet(timeSheet);
        }
        Deduction advance = this.tsui.getUserAdvance(DatabaseConnector.programPortal
            .findAllEmployees(), advanceDate);
        DatabaseConnector.programPortal.addExtra(advance);
        break;
      case 8 :
        final LocalDate gasAdvanceDate = super.tsui.getUserDate("Enter Date of Gas Advance: ");
        if (DatabaseConnector.programPortal.findTimeSheetByDate(gasAdvanceDate) == null) {
          TimeSheet timeSheet = super.tsui.getNewTimeSheet(gasAdvanceDate,
              DatabaseConnector.programPortal.findAllTimeSheets());
          DatabaseConnector.programPortal.addTimeSheet(timeSheet);
        }
        Deduction gasAdvance = this.tsui.getUserGasAdvance(DatabaseConnector.programPortal
            .findAllEmployees(), gasAdvanceDate);
        DatabaseConnector.programPortal.addExtra(gasAdvance);
        break;
      case 9 :
        final LocalDate bonusDate = super.tsui.getUserDate("Enter Date of Bonus: ");
        if (DatabaseConnector.programPortal.findTimeSheetByDate(bonusDate) == null) {
          TimeSheet timeSheet = super.tsui.getNewTimeSheet(bonusDate,
              DatabaseConnector.programPortal.findAllTimeSheets());
          DatabaseConnector.programPortal.addTimeSheet(timeSheet);
        }
        Addition bonus = this.tsui.getUserBonus(DatabaseConnector.programPortal.findAllEmployees(),
            bonusDate);
        DatabaseConnector.programPortal.addExtra(bonus);
        break;
      case 10 :
        // Main.timeSheetHandler().addExtra(tsui.getUserE());
        this.ui.pause("Generic Extra addition is not scripted..");
        break;
      default :
        throw new IllegalStateException(
            "Sub-Menu user option given by Menu superclass was not valid");
    }
  }
}
