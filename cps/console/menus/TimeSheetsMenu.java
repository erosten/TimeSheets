
package cps.console.menus;

import cps.core.model.timeSheet.Addition;
import cps.core.model.timeSheet.Deduction;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.TimeSheet;

import org.joda.time.LocalDate;

class TimeSheetsMenu extends AbstractMenu {

  public TimeSheetsMenu() {
    super();
  }

  @Override
  public void setBanner() {
    this.bannerText = "TimeSheets Menu";
  }

  @Override
  public void setOptions() {
    super.addOption("TimeSheet Directory");
    super.addOption("Run Current TimeSheet");
    super.addOption("Add new Job");
    super.addOption("Add Advance");
    super.addOption("Add Gas Advance");
    super.addOption("Add Bonus");
  }

  @Override
  public void doOption(int userOptionChoice) {
    switch (userOptionChoice) {
      case 1 :
        new TimeSheetsDirectory().run();
        break;
      case 2 :
        // Main.getTimesheets().runCurrentTimeSheet();
        super.ui.pause("Not Scripted");
        break;
      case 3 :
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
      case 4 :
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
      case 5 :
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
      case 6 :
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
      default :
        throw new IllegalStateException(
            "Sub-Menu user option given by Menu superclass was not valid");
    }
  }
}
