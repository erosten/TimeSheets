
package cps.err.logging;

import java.util.ArrayList;

public class ErrorHandler {

  private static ArrayList<String> program_Log = new ArrayList<String>();
  private static ArrayList<String> error_Log = new ArrayList<String>();
  private static boolean checkErrorLog = false;
  private static ErrorHandler instance = null;

  // public ErrorHandler(AbstractDatabase db) {
  // super(db);
  // instance = this;
  // }

  public static ErrorHandler getInstance() {
    return instance;
  }

  /**
   * Prints errorLog to the console.
   * 
   */
  public static void printErrorLog() {
    // TODO move this logic to the console package
    if (error_Log.size() == 0) {
      System.out.println("Error Log is clear");
    } else {
      for (int i = 0; i < error_Log.size(); i++) {
        System.out.println(error_Log.get(i));
      }
    }
    checkErrorLog = false;
  }

  public static void clearErrorLog() {
    checkErrorLog = false;
    error_Log.clear();
  }

  /**
   * Adds an errorMessage to the error list.
   * 
   * @param errorMessage
   *          errorMessage String to be added.
   */
  public static void addError(String errorMessage) {
    error_Log.add(errorMessage);
    if (!checkErrorLog) {
      checkErrorLog = true;
    }
  }

  public static void log(String logMessage) {
    program_Log.add(logMessage);
  }

  public static boolean isNewError() {
    return checkErrorLog;
  }
}
