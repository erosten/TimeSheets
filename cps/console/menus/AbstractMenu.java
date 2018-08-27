
package cps.console.menus;

import cps.console.tools.ConsoleUtil;
import cps.console.tools.EmployeeUI;
import cps.console.tools.TimeSheetsUI;
import cps.err.logging.ErrorHandler;

import java.util.ArrayList;

abstract class AbstractMenu {

  // protected allows use in classes that extend Menu
  protected ConsoleUtil ui = new ConsoleUtil();

  protected EmployeeUI eui = new EmployeeUI();

  protected TimeSheetsUI tsui = new TimeSheetsUI();

  protected String bannerText = "Default Menu Header";

  private final ArrayList<String> options = new ArrayList<String>();

  protected boolean shouldExitMenu;

  protected boolean exitMenus;

  public AbstractMenu() {
    // default constructor does nothing
    // must set an option & implement the result of that option to use run
  }

  public void run() {
    // checks for exiting menu or whole program
    while (!this.shouldExitMenu && !this.exitMenus) {
      final int userOption = getOption();
      if (userOption != 0) {
        doOption(userOption);
      } else {
        // don't do option if 0
      }
    }
  }

  // protected methods to be overwritten
  protected void setBanner() {
    // programmer should overrwrite this
  }

  protected void setOptions() {
    // let programmer script
  }

  // garunteed to be a valid option YOU have set with addOption
  // an option's implementation should go here
  @SuppressWarnings("unused")
  protected void doOption(int userOptionChoice) {
    // let programmer script
  }

  // protected that should not be overwritten
  protected void addOption(String menuOption) {
    this.options.add(menuOption);
  }

  // allowed to override
  protected void takeExitAction() {
    this.shouldExitMenu = true;
    this.exitMenus = true;
  }

  // allowed to override
  protected void takeBackAction() {
    this.shouldExitMenu = true;
  }

  // private methods
  // if the list contains objects you modify with options
  // this function will update the list with new values
  // before the next menu list is displayed

  // standardized number options with cases for back & exit
  // postcondition: exits with an integer option representation
  private Integer getOption() {
    updateOptions();
    updateBanner();
    displayBanner();
    displayMenuOptions();
    displayErrorLine();
    // ui.getUserString with "true" checks for null or blank string
    final String userInputString = ui.getUserString("Enter your option: ", true);
    if (userInputString == null) {
      return 0;
    }
    if (userInputString.equalsIgnoreCase("exit") || userInputString.equalsIgnoreCase("quit")) {
      takeExitAction();
      return 0;
    } else if (userInputString.equalsIgnoreCase("back")) {
      takeBackAction();
      return 0;
    } else {
      try {

        final int userInput = Integer.parseInt(userInputString);
        options.get(userInput - 1);
        // if code reaches this point, valid option was chosen
        // if that option was the last one AND there is a new error
        // display the error log, regardless of menu
        if ((userInput == options.size()) && (ErrorHandler.isNewError())) {
          ErrorHandler.printErrorLog();
          ui.pause();
          // returning 0 skips doOption()
          return 0;
        } else {
          return userInput;
        }
      } catch (final NumberFormatException nfe) {
        // thrown by Integer.parseInt
        System.out.println("Your menu option must be a valid number from 1-" + options.size());
      } catch (final IndexOutOfBoundsException ioobe) {
        // thrown by ArrayList.get
        System.out.println("Your menu option must be a valid number from 1-" + options.size());
      }
      // i have yet to see an exception that isn't caught here
    }
    return 0;
  }

  // update the options to be displayed
  private void updateOptions() {
    clearOptions();
    setOptions();
    if (ErrorHandler.isNewError()) {
      options.add("Check Error Log");
    }
  }

  // update banner to be displayed
  private void updateBanner() {
    // let programmer script
    setBanner();
  }

  private void clearOptions() {
    options.clear();
  }

  // displays the error line if the checkerrorlog has been turned on since the
  // last doOption
  private void displayErrorLine() {
    if (ErrorHandler.isNewError()) {
      System.out.println(ui.getPrintable("System.message", "check Error Log"));
    }
  }

  private void displayMenuOptions() {
    int numOptions = 1;
    for (final String option : options) {
      System.out.println((numOptions++) + ") " + option);
    }
  }

  private void displayBanner() {
    ui.banner(bannerText);
  }
}
