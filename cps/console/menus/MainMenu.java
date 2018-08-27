
package cps.console.menus;

import cps.err.logging.ErrorHandler;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalTime;

class MainMenu extends AbstractMenu {

  public MainMenu() {
    super();
  }

  @Override
  public void setBanner() {
    this.bannerText = "Main Menu";
  }

  @Override
  public void setOptions() {
    super.addOption("Employees Menu");
    super.addOption("TimeSheets Menu");
    super.addOption("Database Menu");
    super.addOption("Calculate time");
    super.addOption("Print Error Log");
    super.addOption("Clear Error Log");
  }

  @Override
  public void takeBackAction() {
    // do not go back since it is the main menu
  }

  @Override
  public void doOption(int userOptionChoice) {
    switch (userOptionChoice) {
      case 1 :
        new EmployeesMenu().run();
        break;
      case 2 :
        new TimeSheetsMenu().run();
        break;
      case 3 :
        new DatabaseDirectory().run();
        break;
      case 4 :
        this.calculateTime();
        break;
      case 5 :
        ErrorHandler.printErrorLog();
        this.ui.pause();
        break;
      case 6 :
        ErrorHandler.clearErrorLog();
        System.out.println(this.ui.getPrintable("System.admin", "Error Log Cleared"));
        this.ui.pause();
        break;
      default :
        throw new IllegalStateException(
            "Sub-Menu user option given by Menu superclass was not valid");
    }
  }

  public void calculateTime() {
    System.out.println(
        "Enter the starting, then ending time of job in 4 digit military time e.g 0900 or 1810");
    System.out.println("To end the time collection, press  ENTER  on the next start time prompt");
    this.getJobTimesFromUser();
  }

  public void printMainMenuMessage() {
    System.out.println("Type \"exit\" at any time to quit, "
        + "or \"back\" at any time to return to the previous menu");
    System.out.println("Type \"mm\" at any time to return to the Main Menu");
  }

  @Override
  public void run() {
    super.shouldExitMenu = false;
    super.exitMenus = false;
    printMainMenuMessage();
    super.run();
  }

  public ArrayList<ArrayList<LocalTime>> getJobTimesFromUser() {
    // temporary testing
    // BigDecimal previousFormula = BigDecimal.ZERO;
    boolean hasMoreTimes = true;
    final ArrayList<ArrayList<LocalTime>> jobTimes = new ArrayList<ArrayList<LocalTime>>();
    final ArrayList<LocalTime> timesIn = new ArrayList<LocalTime>();
    final ArrayList<LocalTime> timesOut = new ArrayList<LocalTime>();
    while (hasMoreTimes) {
      final String startTime = this.ui.getUserString(("Enter a starting time: "), false);
      // catch user pressing enter to exit time collection
      try {
        Integer.parseInt(startTime);
      } catch (final NumberFormatException nfe) {
        // if enter was pressed, exit this method
        if (startTime == null || startTime.length() == 0) {
          hasMoreTimes = false;
        } else {
          System.out.println("Not a valid input.. wasn't a valid integer!");
        }
        continue;
      }
      if (startTime.length() != 4) {
        System.out.println("Starting time must be 4 characters - e.g. 0330");
        continue;
      }
      final String endTime = this.ui.getUserString("Enter an ending time: ", true);
      try {
        Integer.parseInt(endTime);
      } catch (final NumberFormatException nfe) {
        System.out.println("Not a valid input.. wasn't a valid integer!");
        continue;
      }
      if (endTime.length() != 4) {
        System.out.println("Ending time must be 4 characters - e.g. 2130");
        continue;
      }
      final int startHours = Integer.parseInt(startTime.substring(0, 2));
      final int startMinutes = Integer.parseInt(startTime.substring(2));
      final int endHours = Integer.parseInt(endTime.substring(0, 2));
      final int endMinutes = Integer.parseInt(endTime.substring(2));
      timesIn.add(new LocalTime(startHours, startMinutes));
      timesOut.add(new LocalTime(endHours, endMinutes));
    }
    jobTimes.add(timesIn);
    jobTimes.add(timesOut);
    System.out.println("Current Formula: " + calculateTotalDecimalHours(jobTimes.get(0), jobTimes
        .get(1)));
    return jobTimes;
  }

  public BigDecimal previousFormula(int givenTimeIn, int givenTimeOut) {
    BigDecimal totalHours = BigDecimal.ZERO;
    String timeIn = String.valueOf(givenTimeIn);
    String timeOut = String.valueOf(givenTimeOut);
    if (timeIn.length() != 4) {
      timeIn = "0" + timeIn;
    }
    if (timeOut.length() != 4) {
      timeOut = "0" + timeOut;
    }
    final BigDecimal timeInFirstHalf = BigDecimal.valueOf(Double.parseDouble(timeIn.substring(0,
        2)));
    final BigDecimal timeInSecondHalf = BigDecimal.valueOf(Double.parseDouble(timeIn.substring(2,
        4)));
    final BigDecimal timeOutFirstHalf = BigDecimal.valueOf(Double.parseDouble(timeOut.substring(0,
        2)));
    final BigDecimal timeOutSecondHalf = BigDecimal.valueOf(Double.parseDouble(timeOut.substring(2,
        4)));
    // if minutes of 2nd end time is greater than first parts
    if (timeOutSecondHalf.intValue() >= timeInSecondHalf.intValue()) {
      totalHours = totalHours.add(((((timeOutFirstHalf.subtract(timeInFirstHalf))).add(
          ((timeOutSecondHalf.subtract(timeInSecondHalf).divide(new BigDecimal(60), new MathContext(
              500,
              RoundingMode.HALF_DOWN))))).multiply(new BigDecimal(100), new MathContext(
                  0,
                  RoundingMode.UNNECESSARY))).divide(new BigDecimal("100"))));
    } else {
      // else the minutes of the 2nd end time is less than the first
      final BigDecimal extraMinutes = ((timeInSecondHalf.subtract(timeOutSecondHalf))).divide(
          new BigDecimal(60), new MathContext(500, RoundingMode.HALF_DOWN));
      totalHours = totalHours.add((((((timeOutFirstHalf).subtract(timeInFirstHalf)).subtract(
          extraMinutes)).multiply(new BigDecimal(100), new MathContext(
              0,
              RoundingMode.UNNECESSARY)))).divide(new BigDecimal("100")));
    }
    return totalHours.multiply(new BigDecimal("100"));
  }

  public BigDecimal calculateTotalDecimalHours(ArrayList<LocalTime> timesIn,
      ArrayList<LocalTime> timesOut) {
    if (timesIn.size() != timesOut.size()) {
      throw new IllegalArgumentException(
          "in times size mismatch with out times during total hour calculation");
    }
    BigDecimal totalHours = BigDecimal.ZERO;
    for (int i = 0; i < timesIn.size(); i++) {
      final DateTime datedTimeIn = timesIn.get(i).toDateTimeToday();
      final DateTime datedTimeOut = timesOut.get(i).toDateTimeToday();
      final Duration durationBetween = new Duration(
          datedTimeIn.toInstant(),
          datedTimeOut.toInstant());
      totalHours = totalHours.add(new BigDecimal(durationBetween.toStandardMinutes().getMinutes())
          .divide(new BigDecimal("60"), new MathContext(500, RoundingMode.HALF_DOWN)).multiply(
              new BigDecimal("100")));
      if (totalHours.compareTo(BigDecimal.ZERO) < 0) {
        totalHours = new BigDecimal("2400").add(totalHours);
      }
    }
    return totalHours;
  }
}
