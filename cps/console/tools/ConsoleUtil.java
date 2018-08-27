
package cps.console.tools;

import cps.core.model.employee.Employee;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.joda.time.LocalDate;

public class ConsoleUtil {

  Scanner inputReader_ = new Scanner(new InputStreamReader(System.in));

  public void banner() {
    System.out.println("--------------------------------------------------");
  }

  /**
   * Prints centered text around dashes for 50 total characters, e.g.
   * -------------------test------------------- (not 50 characters).
   * 
   * @param middleText
   *          the text to be printed in the middle of the banner
   */
  public void banner(String middleText) {
    final int middleTextLength = middleText.length();
    final int dashes = (50 - (middleTextLength)) / 2;
    for (int i = 0; i < dashes; i++) {
      System.out.print("-");
    }
    System.out.print(middleText);
    for (int i = 0; i < dashes; i++) {
      System.out.print("-");
    }
    System.out.print("\n");
  }

  /**
   * Reads a string from the user, and returns it as a String, trimmed.
   * 
   * @param query
   *          the string prompt to tell the user what to enter
   * @param catchEnter
   *          a boolean denoting whether pressing enter sends back the query prompt, or returns an
   *          empty string (""), not null.
   *
   * @return the string the user typed into the console
   */
  public String getUserString(String query, boolean catchEnter) {
    String userInput = "";
    System.out.print(query);
    try {
      userInput = this.inputReader_.nextLine().trim();
    } catch (NoSuchElementException nsee) {
      if (catchEnter) {
        return this.getUserString(query, catchEnter);
      }
    }
    if (catchEnter && userInput.length() == 0) {
      return getUserString(query, true);
    }
    return userInput.trim();
  }

  /**
   * Reads a number from the user, and returns it as a BigDecimal, scaled.
   * 
   * @param query
   *          the string prompt to tell the user what to enter
   * @param decimalDigits
   *          the number of decimal digits to scale the number to upon returning
   * @return a BigDecimal representation of the user's number, scaled to the given number of digits
   */
  public BigDecimal getUserBigDecimal(String query, int decimalDigits) {
    BigDecimal returnValue = null;
    String userInput = null;
    System.out.print(query);
    try {
      userInput = this.inputReader_.nextLine().trim();
    } catch (NoSuchElementException nsee) {
      return this.getUserBigDecimal(query, decimalDigits);
    }
    try {
      returnValue = new BigDecimal(userInput);
    } catch (NumberFormatException nfe) {
      return this.getUserBigDecimal(query, decimalDigits);
    }
    returnValue = returnValue.setScale(decimalDigits);
    return returnValue;
  }

  /**
   * Reads a number from the user, and returns it as a integer.
   * 
   * @param query
   *          the string prompt to tell the user what to enter
   * @return an integer representation of the users number, and -1 if the integer could not be
   *         parsed
   */
  public int getUserInteger(String query) {
    // TODO add error handling instead of returning -1
    int userInput = -1;
    System.out.print(query);
    try {
      userInput = Integer.parseInt(this.inputReader_.nextLine().trim());
    } catch (final NumberFormatException nfe) {
      return getUserInteger(query);
    } catch (final NoSuchElementException nsee) {
      return getUserInteger(query);
    }
    return userInput;
  }

  /**
   * Reads user input to return a valid Employee given their name or abbreviation, ALWAYS.
   * 
   * @param employees
   *          the List of employees to check against for names and abbreviations
   * @return a valid Employee object
   */
  public Employee getvalidEmployee(List<Employee> employees) {
    // TODO script way to get out, and display employee names with choice
    String searchable = getUserString("Enter employee's name or abbreviation: ", false);
    for (Employee employee : employees) {
      if (employee.getAbbreviation().equalsIgnoreCase(searchable)) {
        return employee;
      }
      if (employee.getFirstLastName().equalsIgnoreCase(searchable)) {
        return employee;
      }
    }
    System.out.println("Couldn't find an employee with that searchable term");
    return this.getvalidEmployee(employees);
  }

  // delays everything until user presses enter
  public void pause() {
    System.out.println("Press ENTER to continue......");
    this.inputReader_.nextLine();
  }

  // delays everything until user presses enter while displaying chosen text
  public void pause(String displayedText) {
    System.out.println(displayedText);
    this.inputReader_.nextLine();
  }

  /**
   * Creates a String with initialText...addedText with a total length of 50
   * 
   * @param initialText
   *          text to be in the beginning of the String
   * @param addedText
   *          text to be at the end of the String
   * @return a String with initialText...addedText with a total length of 50
   * 
   */
  public String getPrintable(String initialText, String addedText) {
    final int initialTextLength = initialText.length();
    final int addedTextLength = addedText.length();
    if (((initialTextLength + addedTextLength)) > 50) {
      throw new IllegalArgumentException("initial+added texts are too long..");
    }
    String printable = initialText;
    for (int k = 0; k < (50 - addedTextLength - initialTextLength); k++) {
      printable += ".";
    }
    printable += addedText;
    return printable;
  }

  /**
   * Reads user input from a given query to determine a yes or no answer.
   * 
   * @param query
   *          the string prompt to tell the user what to enter
   * @return a boolean value with true denoting yes and false denoting no
   */
  public boolean saidYes(String query) {
    String answer = "";
    System.out.print(query + " (Y/N):  ");
    try {
      answer = this.inputReader_.nextLine().toUpperCase().trim();
    } catch (NoSuchElementException nsee) {
      return this.saidYes(query);
    }
    if (answer.equals("Y") || answer.equals("YES")) {
      return true;
    } else if (answer.equals("N") || answer.equals("NO")) {
      return false;
    } else {
      System.out.println("Not a valid input..");
      saidYes(query);
      return false;
    }
  }

  /**
   * Prompts the user for a string and converts it do a date. Valid strings are dd/mm/yy,
   * dd/mm/yyyy, dd-mm-yy, dd-mm-yyyy.
   * 
   * @param query
   *          the string to prompt the user for the correct data
   * @return a LocalDate representing the user's input
   */
  public LocalDate getUserDate(String query) {
    java.util.Date date = null;
    // formats that are readable
    final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    formatter.setLenient(false);
    final SimpleDateFormat sm2 = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
    // while no valid date, get one
    while (date == null || date.equals(null)) {
      final String userInput = getUserString((query), true);
      try {
        // catch input of only last 2 year digits?
        date = formatter.parse(userInput);
        final Calendar jobDate = Calendar.getInstance();
        jobDate.setTime(date);
        if ((String.valueOf(jobDate.get(Calendar.YEAR)).length() == 2)) {
          jobDate.set(Calendar.YEAR, Integer.parseInt("20" + String.valueOf(jobDate.get(
              Calendar.YEAR))));
          date = jobDate.getTime();
        }
      } catch (final ParseException pe1) {
        // attempt to catch a dashed input
        try {
          // catch input of only last 2 year digits?
          date = sm2.parse(userInput);
          final Calendar jobDate = Calendar.getInstance();
          jobDate.setTime(date);
          if ((String.valueOf(jobDate.get(Calendar.YEAR)).length() == 2)) {
            jobDate.set(Calendar.YEAR, Integer.parseInt("20" + String.valueOf(jobDate.get(
                Calendar.YEAR))));
            date = jobDate.getTime();
          }
        } catch (final ParseException pe2) {
          System.out.println("Couldn't parse date..");
        }
      }
    }
    final java.sql.Date jobDate = new Date(date.getTime());
    return new LocalDate(jobDate.getTime());
  }

}
