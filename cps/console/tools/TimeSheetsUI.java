
package cps.console.tools;

import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;
import cps.core.model.timeSheet.Addition;
import cps.core.model.timeSheet.Deduction;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.JobWage;
import cps.core.model.timeSheet.TimeSheet;
import cps.core.model.timeSheet.TimeSheetConstants;
import cps.core.model.timeSheet.TimeSheetWage;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class TimeSheetsUI extends ConsoleUtil {

  public TimeSheetsUI() {
    // super constructor does nothing
    super();
  }

  // precondition: valid employee w/ valid wages existed beforehand
  // postcondition: valid arraylist of job entries returned w/ valid data
  // postcondition: timesheet for job entry is created if does not exist
  /**
   * Prompts the user for all variables for a JobEntry object to be created, then creates and
   * returns that object.
   * 
   * @param employees
   *          The employees to choose from for a JobEntry.
   * @param jobTimeSheet
   *          An existing timeSheet to add a job to
   * @return a valid JobEntry object containing the information the user gave
   */
  public JobEntry getJob(List<Employee> employees, TimeSheet jobTimeSheet, LocalDate jobDate) {
    // create & add new timesheet if needed while in ui, but before wages
    final Employee employee = super.getvalidEmployee(employees);
    // create the builder
    final JobEntry.Builder jb = new JobEntry.Builder(employee);
    jb.date(jobDate);// add date
    // get job code from user
    final String jobCode = super.getUserString("Job Code: ", true);
    jb.code(jobCode);
    // run travel checker, get from user if positive
    jb.travelString(this.getTravelString(jobCode));
    // get wage to be used for job from user
    jb.wage(this.getWageUsedForJob(employee, jobTimeSheet));
    final boolean isFlatRate = super.saidYes("Is this job FLAT RATE?");
    final String flatString = "Please enter reference times for overtime calculation purposes";
    final String nonFlatString = "Enter the starting, then ending time of job in "
        + "4 digit military time," + " e.g 0900 or 1810\n"
        + "To end the time collection, press  ENTER  on the next start time prompt";
    System.out.println(isFlatRate ? flatString : nonFlatString);
    this.getJobTimesFromUser(jb);
    if (isFlatRate) {
      jb.flatRateHours(getUserBigDecimal("Enter the equivalent hours paid: ", 2));
    }
    if (saidYes("Is there a Std Deduction for this Job?")) {
      jb.stdDeduction(saidYes("Is it before the job?"));
    }
    return jb.build();
  }

  /**
   * Grabs the job times for a job from the user.
   * 
   * @param jb
   *          the job entry builder to add times to
   * @return the job entry builder to continue using
   */
  public JobEntry.Builder getJobTimesFromUser(JobEntry.Builder jb) {
    boolean hasMoreTimes = true;
    while (hasMoreTimes) {
      final String startTime = getUserString(("Enter a starting time: "), false);
      final boolean startTimeUncertain = saidYes("Is this time Uncertain?");
      // catch user pressing enter to exit time collection
      try {
        Integer.parseInt(startTime);
      } catch (final NumberFormatException nfe) {
        // if enter was pressed, exit this method
        if (startTime == null || startTime.length() == 0) {
          hasMoreTimes = false;
        } else {
          System.out.println("The input could not be parsed to a valid integer");
        }
        continue;
      }
      if (startTime.length() != 4) {
        System.out.println("Starting time must be 4 characters - e.g. \"0330\"");
        continue;
      }
      final String endTime = getUserString("Enter an ending time: ", true);
      final boolean endTimeUncertain = saidYes("Is this time Uncertain?");
      try {
        Integer.parseInt(endTime);
      } catch (final NumberFormatException nfe) {
        System.out.println("The input could not be parsed to a valid integer");
        continue;
      }
      if (endTime.length() != 4) {
        System.out.println("Ending time must be 4 characters - e.g. \"2130\"");
        continue;
      }
      int startHours = Integer.parseInt(startTime.substring(0, 2));
      final int startMinutes = Integer.parseInt(startTime.substring(2));
      int endHours = Integer.parseInt(endTime.substring(0, 2));
      final int endMinutes = Integer.parseInt(endTime.substring(2));
      // throws an error if 24 is entered
      if (startHours == 24 || endHours == 24) {
        System.out.println("The hours may not be 24, only 0-23");
        continue;
      }
      final LocalTime timeIn = new LocalTime(startHours, startMinutes);
      final LocalTime timeOut = new LocalTime(endHours, endMinutes);
      if (timeIn.isAfter(timeOut)) {
        System.out.println("Time In must be before Time Out");
        continue;
      }
      jb.addTime(timeIn, startTimeUncertain, timeOut, endTimeUncertain);
    }
    return jb;
  }

  // precondition: valid employee w/ valid wages is given
  // precondition: timesheet for date exists
  // postcondition: valid employee wage is returned
  private JobWage getWageUsedForJob(Employee employee, TimeSheet associatedTimeSheet) {
    try {
      super.banner("Available Wages");
      int numOptions = 0;
      for (final EmployeeWage wage : employee.getWages()) {
        numOptions++;
        System.out.println(super.getPrintable(((numOptions) + ") " + wage.getName() + " Rate"), wage
            .getRate().toString()));
      }
      System.out.println(super.getPrintable((numOptions + 1) + ") Travel Rate", associatedTimeSheet
          .getTravelWageRate().toString()));
      final int userInput = super.getUserInteger("Enter your wage by number: ");
      if (userInput > (numOptions + 1)) {
        System.out.println("Not a valid input!");
        System.out.println("You must enter a valid number 1-" + numOptions);
        return this.getWageUsedForJob(employee, associatedTimeSheet);
      }
      if (userInput == (numOptions + 1)) {
        return new JobWage(
            associatedTimeSheet.getTravelWage().getName(),
            associatedTimeSheet.getTravelWage().getRate());
      } else {
        return new JobWage(
            employee.getWages().get(userInput - 1).getName(),
            employee.getWages().get(userInput - 1).getRate());
      }
    } catch (final ArrayIndexOutOfBoundsException aiooe) {
      // user input wage outside of bounds, run it back
      return this.getWageUsedForJob(employee, associatedTimeSheet);
    }
  }

  private String getTravelString(String jobCode) {
    boolean hasTravel = false;
    hasTravel = hasTravel || jobCode.toLowerCase().contains("travel");
    hasTravel = hasTravel || jobCode.toLowerCase().contains("incl t");
    hasTravel = hasTravel || jobCode.toLowerCase().contains("take hm");
    hasTravel = hasTravel || jobCode.toLowerCase().contains("take home");
    hasTravel = hasTravel || jobCode.toLowerCase().contains("crew pick up");
    hasTravel = hasTravel || jobCode.toLowerCase().contains("drop off");
    if (hasTravel) {
      // do not reprompt user, perhaps the checker was in error
      return super.getUserString("Enter Travel mileage or method: ", false);
    }
    return "no travel";
  }

  /**
   * Creates a new TimeSheet from user input, given an initialization date and a list of previous
   * timeSheets (to set previous and next timeSheets).
   * 
   * @param initializationDate
   *          the date to start the new TimeSheet range in
   * @param timeSheets
   *          the list of timeSheets to set previous and next timeSheets from
   * @return a new TimeSheet based on the user input
   */
  public TimeSheet getNewTimeSheet(LocalDate initializationDate, List<TimeSheet> timeSheets) {
    final TimeSheet.Builder tb = new TimeSheet.Builder(initializationDate);
    TimeSheet previousChronologicalTimeSheet = null;
    TimeSheet nextChronologicalTimeSheet = null;
    BigDecimal stdMileageRate = BigDecimal.ZERO;
    TimeSheetWage.WageBuilder travelWage = null;
    // find the previous timesheet (if it does not exist, value is null)
    for (int i = 0; i < timeSheets.size(); i++) {
      if (timeSheets.get(i).getEndDate().isBefore(initializationDate)) {
        // if the second if is not triggered and it is the end of the list, the
        // last element is the previous sheet
        if (timeSheets.size() == (i + 1)) {
          previousChronologicalTimeSheet = timeSheets.get(i);
        } else {
          // if the current indexed sheet is before the proposed sheet's
          // initialization AND
          // the next index sheet's end date is after the proposed sheet's
          // initialization,
          // this sheet should go in the middle
          if (timeSheets.get(i + 1).getEndDate().isAfter(initializationDate)) {
            previousChronologicalTimeSheet = timeSheets.get(i);
            nextChronologicalTimeSheet = timeSheets.get(i + 1);
            break;
          }
        }
      }
    }
    // if next is null, previous was never found..
    if (nextChronologicalTimeSheet == null) {
      for (int i = 0; i < timeSheets.size(); i++) {
        if (timeSheets.get(i).getStartDate().isAfter(initializationDate)) {
          if ((nextChronologicalTimeSheet == null) || timeSheets.get(i).getStartDate().isBefore(
              nextChronologicalTimeSheet.getStartDate())) {
            nextChronologicalTimeSheet = timeSheets.get(i);
          }
        }
      }
    }
    if (timeSheets.size() == 0 || previousChronologicalTimeSheet == null) {
      stdMileageRate = getUserBigDecimal("Enter the standard mileage rate for this timesheet: ", 4);
      travelWage = new TimeSheetWage.WageBuilder(
          getUserBigDecimal("Enter the travel rate for this timesheet: $", 2));
    } else {
      System.out.println("The most recent TimeSheet - " + previousChronologicalTimeSheet.getName()
          + " - had a standard mileage rate of " + previousChronologicalTimeSheet
              .getStandardMileageRate());
      if (super.saidYes("Import this value to the new TimeSheet?")) {
        stdMileageRate = previousChronologicalTimeSheet.getStandardMileageRate();
      } else {
        stdMileageRate = getUserBigDecimal("Enter the standard mileage rate for this timesheet: ",
            4);
      }
      System.out.println("The most recent TimeSheet - " + previousChronologicalTimeSheet.getName()
          + " - had a Travel Wage rate of " + previousChronologicalTimeSheet.getTravelWageRate());
      if (super.saidYes("Import this value to the new TimeSheet?")) {
        travelWage = new TimeSheetWage.WageBuilder(
            previousChronologicalTimeSheet.getTravelWageRate());
      } else {
        travelWage = new TimeSheetWage.WageBuilder(
            getUserBigDecimal("Enter the travel rate for this timesheet: $", 2));
      }
    }
    tb.nextSheet(nextChronologicalTimeSheet);
    tb.previousSheet(nextChronologicalTimeSheet);
    tb.stdMileageRate(stdMileageRate);
    tb.travelWage(travelWage.build());
    return tb.build();
  }

  /**
   * Prompts the user for the information to create an Advance, and returns the proper deduction
   * object.
   * 
   * @param employees
   *          the Employees for the user to choose from
   * @return a valid Deduction object with the information the user gave
   */
  public Deduction getUserAdvance(List<Employee> employees, LocalDate date) {
    Employee employee = super.getvalidEmployee(employees);
    BigDecimal amount = super.getUserBigDecimal("Enter Advance amount: $", 2);
    return new Deduction(employee, TimeSheetConstants.ADVANCE, amount, date);
  }

  /**
   * Prompts the user for the information to create a GasAdvance, and returns the proper deduction
   * object.
   * 
   * @param employees
   *          the Employees for the user to choose from
   * @return a valid Deduction object with the information the user gave
   */
  public Deduction getUserGasAdvance(List<Employee> employees, LocalDate date) {
    Employee employee = super.getvalidEmployee(employees);
    BigDecimal amount = super.getUserBigDecimal("Enter Gas Advance amount: $", 2);
    return new Deduction(employee, TimeSheetConstants.GAS_ADVANCE, amount, date);
  }

  /**
   * Prompts the user for the information to create a Bonus, and returns the proper Addition object.
   * This method does not add a bonus to the database.
   * 
   * @param employees
   *          the Employees for the user to choose from
   * @return a valid Addition object with the information the user gave
   */
  public Addition getUserBonus(List<Employee> employees, LocalDate date) {
    Employee employee = super.getvalidEmployee(employees);
    BigDecimal amount = super.getUserBigDecimal("Enter Bonus amount: $", 2);
    return new Addition(employee, TimeSheetConstants.BONUS, amount, date);
  }
}
