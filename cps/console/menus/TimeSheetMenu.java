
package cps.console.menus;

import cps.console.tools.Display;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.TimeSheet;
import cps.excel.TimeSheetExporter;
import cps.excel.WorkBookType;
import cps.gui.tools.FileChooser;

import java.io.File;

import org.joda.time.LocalDate;

class TimeSheetMenu extends AbstractMenu {

  TimeSheet timeSheet_;

  public TimeSheetMenu(TimeSheet timeSheet) {
    super();
    this.timeSheet_ = timeSheet;
  }

  @Override
  public void setBanner() {
    this.bannerText = this.timeSheet_.getName() + " Menu";
  }

  @Override
  public void setOptions() {
    super.addOption("Employee Sheet Directory");
    super.addOption("Display TimeSheet");
    super.addOption("Add Job");
    super.addOption("Export to excel file (XLSX)");
    super.addOption("Export to excel file (XLS)");
    super.addOption("Delete TimeSheet");
  }

  @Override
  public void doOption(int userOptionChoice) {
    switch (userOptionChoice) {
      case 1 :
        new EmployeeSheetsDirectory(this.timeSheet_).run();
        break;
      case 2 :
        System.out.println(Display.display(timeSheet_));
        this.ui.pause();
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
        TimeSheetExporter tseXLSX = new TimeSheetExporter();
        File directoryXLSX = new FileChooser().getFile();
        tseXLSX.export(DatabaseConnector.programPortal.findTimeSheetByDate(timeSheet_.getEndDate()),
            directoryXLSX, WorkBookType.XLSX, DatabaseConnector.programPortal);
        break;
      case 5 :
        TimeSheetExporter tseXLS = new TimeSheetExporter();
        File directoryXLS = new FileChooser().getFile();
        tseXLS.export(DatabaseConnector.programPortal.findTimeSheetByDate(timeSheet_.getEndDate()),
            directoryXLS, WorkBookType.XLS, DatabaseConnector.programPortal);
        break;
      case 6 :
        System.out.println("This will delete all related employeesheets and jobs");
        if (this.ui.saidYes("Are you sure you want to delete " + this.timeSheet_.getName())) {
          if (DatabaseConnector.programPortal.removeTimeSheet(this.timeSheet_)) {
            System.out.println(super.ui.getPrintable(this.timeSheet_.getName(), "Removed"));
          } else {
            System.out.println(super.ui.getPrintable(this.timeSheet_.getName(), "Removal Failed"));
            throw new IllegalStateException("TimeSheet removal failed..");
            // TODO add error handling
          }
          this.shouldExitMenu = true;
        }
        break;
      default :
        throw new IllegalStateException(
            "Sub-Menu user option given by Menu superclass was not valid");
    }
  }
}
