
package cps.console.menus;

import cps.core.model.timeSheet.TimeSheet;

class TimeSheetsDirectory extends AbstractMenu {

  public TimeSheetsDirectory() {
    super();
  }

  @Override
  public void setBanner() {
    this.bannerText = "TimeSheets Directory";
  }

  @Override
  public void setOptions() {
    for (final TimeSheet timeSheet : DatabaseConnector.programPortal.findAllTimeSheets()) {
      super.addOption(timeSheet.getName() + " - " + timeSheet.getStartDate() + " - " + timeSheet
          .getEndDate());
    }
  }

  @Override
  public void doOption(int userOptionChoice) {
    new TimeSheetMenu(DatabaseConnector.programPortal.findAllTimeSheets().get(userOptionChoice - 1))
        .run();
  }
}
