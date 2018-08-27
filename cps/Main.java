
package cps;

import cps.gui.core.Loader;

public class Main {

  /**
   * Starts the program. must have String[] args to be recognized as main
   *
   * @throws Exception
   *           from stuff :(
   */
  public static void main(String[] args) {
    // try (ProgramPortal portal = new ProgramPortal(DerbyDatabase.ProgramDB)) {
    // Menus.start(portal);
    // } catch (SQLException sqle) {
    // String errorString = "Database is booted in another instance.";
    // JOptionPane.showMessageDialog(null, errorString, "Error", JOptionPane.ERROR_MESSAGE);
    // }

    Loader.start();
  }
}