
package cps.console.menus;

import cps.core.ProgramPortal;
import cps.core.db.frame.DerbyDatabase;

import java.sql.SQLException;

class DatabaseMenu extends AbstractMenu {

  private DerbyDatabase ad = null;

  public DatabaseMenu(DerbyDatabase ad) {
    super();
    this.ad = ad;
  }

  @Override
  public void setBanner() {
    this.bannerText = "Database Menu";
  }

  @Override
  public void setOptions() {
    super.addOption("Initialize Program Portal");
    super.addOption("Print Database");
    super.addOption("Print Database Table Names");
    super.addOption("Print table name by user entry");
    super.addOption("Print Managed Entities");
    super.addOption("Drop Tables");
  }

  @Override
  public void doOption(int userChoice) {
    switch (userChoice) {
      case 1 :
        try {
          DatabaseConnector.programPortal = new ProgramPortal(ad);
        } catch (SQLException sqle) {
          // TODO add error handling
          sqle.printStackTrace();
        }
        super.ui.pause();
        break;
      case 2 :
        ad.printTables();
        super.ui.pause();
        break;
      case 3 :
        ad.printTableNames();
        super.ui.pause();
        break;
      case 4 :
        ad.printTable(ui.getUserString("Enter table name: ", true));
        break;
      case 5 :
        ad.printManagedEntities();
        super.ui.pause();
        break;
      case 6 :
        if (super.ui.saidYes("Are you sure?")) {
          ad.reset();
        }
        super.ui.pause();
        break;
      default :
        throw new IllegalStateException(
            "Sub-Menu user option given by Menu superclass was not valid");
    }
  }
}
