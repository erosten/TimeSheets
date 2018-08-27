package cps.console.menus;

import cps.core.db.frame.DerbyDatabase;

class DatabaseDirectory extends AbstractMenu {

  public DatabaseDirectory() {
    super();
  }

  @Override
  public void setBanner() {
    this.bannerText = "Database Directory";
  }

  @Override
  public void setOptions() {
    for (final DerbyDatabase ad : DerbyDatabase.values()) {
      super.addOption(ad.getName());
    }
  }

  @Override
  public void doOption(int userOptionChoice) {
    new DatabaseMenu(DerbyDatabase.values()[userOptionChoice - 1]).run();
  }
}
