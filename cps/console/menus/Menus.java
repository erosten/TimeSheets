
package cps.console.menus;

import cps.core.ProgramPortal;

public class Menus {

  static boolean exitMenus = false;

  public static void start(ProgramPortal pp) {
    DatabaseConnector.setPortal(pp);
    new MainMenu().run();
  }
}
