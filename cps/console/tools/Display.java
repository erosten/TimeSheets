
package cps.console.tools;

import cps.core.model.frame.Wage;
import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.JobTime;
import cps.core.model.timeSheet.TimeSheet;
import cps.core.model.timeSheet.TimeSheetConstants;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Display {

  private static int longestEmployeeName = 0;

  public static String display(EmployeeSheet employeeSheet) {
    return (employeeSheetString(employeeSheet));
  }

  public static String display(TimeSheet timeSheet) {
    return (timeSheetString(timeSheet));
  }

  private static String displayWageTotals(EmployeeSheet employeeSheet) {
    String soff = "";
    int varyingWagesOffset = 21;
    String outString = "";
    for (int i = 0; i < employeeSheet.getWagesUsed().size(); i++) {
      varyingWagesOffset *= i;
      soff = (varyingWagesOffset == 0) ? "%s" : "%" + (varyingWagesOffset) + "s";
      outString += String.format((soff) + "%-21s", "", displayWageTotal(employeeSheet, employeeSheet
          .getWagesUsed().get(i)));
    }
    return outString;
  }

  private static String displayWageTotals(TimeSheet timeSheet) {
    String soff = "";
    int varyingWagesOffset = 21;
    String outString = "";
    for (int i = 0; i < timeSheet.getWagesUsed().size(); i++) {
      varyingWagesOffset *= i;
      soff = (varyingWagesOffset == 0) ? "%s" : "%" + (varyingWagesOffset) + "s";
      outString += String.format((soff) + "%-21s", "", displayWageTotal(timeSheet, timeSheet
          .getWagesUsed().get(i)));
    }
    return outString;
  }

  private static String displayWageTotal(EmployeeSheet employeeSheet, Wage wage) {
    final String formattedRegularTime = getTimeString(employeeSheet.getRegularTimeOn(wage));
    final String formattedOverTime = getTimeString(employeeSheet.getOverTimeOn(wage));
    final String formattedDoubleTime = getTimeString(employeeSheet.getDoubleTimeOn(wage));
    return String.format("%-7s%-7s%-7s", formattedRegularTime, formattedOverTime,
        formattedDoubleTime);
  }

  private static String displayWageTotal(TimeSheet timeSheet, Wage wage) {
    final String formattedRegularTime = getTimeString(timeSheet.getRegularTimeOn(wage));
    final String formattedOverTime = getTimeString(timeSheet.getOverTimeOn(wage));
    final String formattedDoubleTime = getTimeString(timeSheet.getDoubleTimeOn(wage));
    return String.format("%-7s%-7s%-7s", formattedRegularTime, formattedOverTime,
        formattedDoubleTime);
  }

  private static String displayWageTotalsString(EmployeeSheet employeeSheet) {
    String outString = "";
    final NumberFormat nf = TimeSheetConstants.currencyFormatter;
    for (Wage wage : employeeSheet.getWagesUsed()) {
      outString += wage.getName() + " Pay: ";
      BigDecimal payTotal = BigDecimal.ZERO;
      payTotal = payTotal.add(employeeSheet.getTotalPayOn(wage));
      outString += nf.format(payTotal) + "\n";
    }
    return outString + "\n";
  }

  private static String displayWageTotalsString(TimeSheet timeSheet) {
    String outString = "";
    final NumberFormat nf = TimeSheetConstants.currencyFormatter;
    for (Wage wage : timeSheet.getWagesUsed()) {
      outString += wage.getName() + " Pay: ";
      BigDecimal payTotal = BigDecimal.ZERO;
      payTotal = payTotal.add(timeSheet.getTotalPayOn(wage));
      outString += nf.format(payTotal) + "\n";
    }
    return outString + "\n";
  }

  private static String employeeSheetString(EmployeeSheet employeeSheet) {
    final int numWages = employeeSheet.getWagesUsed().size();
    final NumberFormat nf = TimeSheetConstants.currencyFormatter;
    DecimalFormat df = new DecimalFormat();
    df.setMaximumFractionDigits(1);
    df.setMinimumFractionDigits(1);
    df.setGroupingUsed(true);
    DecimalFormat df2 = new DecimalFormat();
    df2.setMaximumFractionDigits(2);
    df2.setMinimumFractionDigits(2);
    df2.setGroupingUsed(true);
    String outString = "";
    outString += "\n" + employeeSheet.getEmployee().getName() + "'s Sheet Data - ";
    outString += employeeSheet.getJobs().size() + " job(s)\n" + banner(100);
    // 3 gap space for the job numbering
    outString += "   " + generateEmployeeSheetHeader(numWages) + "\n";
    outString += generateJobEntryStrings(employeeSheet.getJobs());
    outString += banner(100);
    outString += String.format("%-50s%s%4s%n", "Totals: ", "    " + displayWageTotals(
        employeeSheet), df.format(employeeSheet.getMileage()));
    outString += banner(100);

    outString += displayWageTotalsString(employeeSheet);

    outString += banner(100);
    outString += String.format("%22s%s%n", "Total Wages Pay", ": " + nf.format(employeeSheet
        .getWagePay()));
    outString += banner(100);
    outString += String.format("%22s%s%n", "Mileage Pay", ": " + nf.format(employeeSheet
        .getMileagePay()));
    outString += String.format("%22s%s%n", "Total Bonuses", ": " + nf.format(employeeSheet
        .getTotalBonuses()));
    outString += String.format("%22s%s%n", "Total Advances", ": -" + nf.format(employeeSheet
        .getTotalAdvances()));
    outString += String.format("%22s%s%n", "Total Gas Advances", ": -" + nf.format(employeeSheet
        .getTotalGasAdvances()));
    outString += banner(100);
    outString += String.format("%22s%s%-25s%10s%s%n", "Total Check", ": " + nf.format(employeeSheet
        .getTotalCheck()), "", "Average Cost/Hour", ": " + nf.format(employeeSheet
            .getAverageCostPerHour()));
    final String formattedTotalHours = getTimeString(employeeSheet.getTotalHours());
    final int spacing = (String.format("%s", ": $" + df2.format(employeeSheet.getTotalCheck()))
        .length()) - String.format("%s", ": " + formattedTotalHours).length();
    outString += String.format("%22s%s%-" + (25 + spacing) + "s%10s%s%n", "Total Hours", ": "
        + formattedTotalHours, "", "Average Paid/Hour", ": " + nf.format(employeeSheet
            .getAveragePaidPerHour()));
    return outString;
  }

  private static String generateTimeSheetHeader(int numOfWages, int offset) {
    final String regOverDoubleTimeHeaderString = String.format("%-7s%-7s%-7s", "Reg", "OT", "DT");
    String totalregOverDoubleTimeHeaderStrings = "";
    for (int i = 0; i < numOfWages; i++) {
      totalregOverDoubleTimeHeaderStrings += regOverDoubleTimeHeaderString;
    }
    return String.format("%-" + (offset + 5) + "s%s%8s%10s", "",
        totalregOverDoubleTimeHeaderStrings, "Travel Mileage", "Advances");
  }

  private static String employeeTotalString(EmployeeSheet employeeSheet, List<Wage> wageOrder,
      int offset) {
    String outString = "";
    for (Wage wage : wageOrder) {
      String formattedRegularTime = getTimeString(employeeSheet.getRegularTimeOn(wage));
      String formattedOverTime = getTimeString(employeeSheet.getOverTimeOn(wage));
      String formattedDoubleTime = getTimeString(employeeSheet.getDoubleTimeOn(wage));
      outString += String.format("%-7s%-7s%-7s", formattedRegularTime, formattedOverTime,
          formattedDoubleTime);
    }
    return String.format("%-" + (offset + 5) + "s%s%9s%11s%n", employeeSheet.getEmployee()
        .getName(), outString, employeeSheet.getMileagePay().toString(), employeeSheet.getAdvances()
            .toString());
  }

  private static String timeSheetString(TimeSheet tSheet) {
    // set up
    final NumberFormat nf = TimeSheetConstants.currencyFormatter;
    DecimalFormat df = new DecimalFormat();
    df.setMaximumFractionDigits(1);
    df.setMinimumFractionDigits(1);
    df.setGroupingUsed(true);
    for (final EmployeeSheet employeeSheet : tSheet.getEmployeeSheets()) {
      if (employeeSheet.getEmployee().getName().toString().length() > longestEmployeeName) {
        longestEmployeeName = employeeSheet.getEmployee().getName().toString().length();
      }
    }
    List<Wage> timeSheetWages = tSheet.getWagesUsed();
    // create outstring
    String outString = "";
    outString += tSheet.getName() + " Cover Page\n";
    outString += banner(100);

    outString += generateTimeSheetHeader(timeSheetWages.size(), longestEmployeeName) + "\n";
    Collections.sort(timeSheetWages);

    for (final EmployeeSheet employeeSheet : tSheet.getEmployeeSheets()) {
      outString += employeeTotalString(employeeSheet, timeSheetWages, longestEmployeeName);
    }
    outString += banner(100);
    outString += String.format("%-" + (longestEmployeeName + 5) + "s%s%9s%11s%n", "Totals:",
        displayWageTotals(tSheet), tSheet.getMileagePay().toString(), tSheet.getTotalAdvances()
            .toString());
    outString += banner(100);
    outString += displayWageTotalsString(tSheet);
    outString += banner(100);
    outString += String.format("%22s%s%n", "Total Wage Pay", ": " + nf.format(tSheet.getWagePay()));
    outString += String.format("%22s%s%n", "Employer Cost", ": " + nf.format(tSheet
        .getEmployerCost()));
    outString += String.format("%22s%s%n", "Advancements", ": " + nf.format(tSheet
        .getTotalAdvances()));
    outString += String.format("%22s%s%n", "Total Mileage", ": " + nf.format(tSheet
        .getMileagePay()));
    outString += banner(100);
    outString += String.format("%22s%s%n", "Grand Total", ": " + nf.format(tSheet.getGrandTotal()));
    outString += banner(100);
    outString += String.format("%22s%s%n", "Total Hours", ": " + getTimeString(tSheet
        .getTotalHours()));
    outString += String.format("%22s%s%n", "Average Wage/Hour", ": $" + df.format(tSheet
        .getAveragePaidPerHour()));
    outString += String.format("%22s%s%n", "Average Cost/Hour", ": $" + df.format(tSheet
        .getAverageCostPerHour()));
    outString += "\n";
    return outString;
  }

  private static String generateEmployeeSheetHeader(int numOfWages) {
    final String regOverDoubleTimeHeaderString = String.format("%-7s%-7s%-7s", "Reg", "OT", "DT");
    String totalregOverDoubleTimeHeaderStrings = "  ";
    for (int i = 0; i < numOfWages; i++) {
      totalregOverDoubleTimeHeaderStrings += regOverDoubleTimeHeaderString;
    }
    return String.format("%-11s%-18s%-10s%-10s%s%-10s", "Date", "Job Code", "Time In", "Time Out",
        totalregOverDoubleTimeHeaderStrings, "Travel");
  }

  private static String getTimeString(BigDecimal hours) {
    return String.format("%.0f", Double.parseDouble(hours.toString()));
  }

  public static String getHoursString(BigDecimal hours) {
    if (hours.compareTo(BigDecimal.ZERO) == 0) {
      return "";
    } else {
      return String.format("%.0f", Double.parseDouble(hours.toString()));
    }
  }

  private static String banner(int length) {
    String returnString = "";
    for (int i = 0; i < length; i++) {
      returnString = returnString + "-";
    }
    return returnString + "\n";
  }

  private static List<Wage> getUniqueNamedWages(List<JobEntry> jobEntries) {
    List<Wage> allWages = new ArrayList<>();
    jobSearch : for (JobEntry jobEntry : jobEntries) {
      if (allWages.isEmpty()) {
        allWages.add(jobEntry.getWage());
      } else {
        for (int i = 0; i < allWages.size(); i++) {
          if (allWages.get(i).getName().equals(jobEntry.getWage().getName())) {
            continue jobSearch;
          }
        }
        allWages.add(jobEntry.getWage());
      }
    }
    Collections.sort(allWages);
    return allWages;
  }

  private static String generateJobEntryStrings(List<JobEntry> jobEntries) {
    String outString = "";
    List<Wage> uniqueWages = getUniqueNamedWages(jobEntries);
    int numWages = uniqueWages.size();
    // chose because 21 = 7 x 7 x 7 which is the default separator length
    // for wage headers
    final int wageOffset = 21;
    for (JobEntry jobEntry : jobEntries) {
      outString += String.format("%-4s", jobEntries.indexOf(jobEntry) + 1 + ") ");
      int varyingWagesOffset = 0;
      int varyingTravelOffset = 0;
      // offset of job hours output varies by how many wages there are
      for (int i = 1; i <= numWages; i++) {
        if (jobEntry.getWage().hasSameNameAs(uniqueWages.get(i - 1))) {
          varyingWagesOffset = (i - 1) * wageOffset;
          varyingTravelOffset = (numWages * wageOffset) - (i * wageOffset);
        }
      }
      final String formattedRegularTime = getHoursString(jobEntry.getRegularTime());
      final String formattedOverTime = getHoursString(jobEntry.getOverTime());
      final String formattedDoubleTime = getHoursString(jobEntry.getDoubleTime());
      final String dateComponent = jobEntry.getDateString();
      final String travelComponent = jobEntry.getTravel();
      if (jobEntry.isMultiLineEntry() && !(jobEntry.isFlatRate())) {
        final List<JobTime> jobTimes = jobEntry.getTimes();
        String timeInHours = String.format("%02d", jobTimes.get(0).getTimeIn().getHourOfDay());
        String timeInMinutes = String.format("%02d", jobTimes.get(0).getTimeIn().getMinuteOfHour());
        String timeOutHours = String.format("%02d", jobTimes.get(0).getTimeOutHour());
        String timeOutMinutes = String.format("%02d", jobTimes.get(0).getTimeOutMinute());
        outString += String.format("%-11s%-18s%-10s%-10s%n", dateComponent, jobEntry.getCode(),
            timeInHours + timeInMinutes, timeOutHours + timeOutMinutes);
        for (int i = 1; i < (jobEntry.getTimes().size() - 1); i++) {
          timeInHours = String.format("%02d", jobTimes.get(i).getTimeIn().getHourOfDay());
          timeInMinutes = String.format("%02d", jobTimes.get(i).getTimeIn().getMinuteOfHour());
          timeOutHours = String.format("%02d", jobTimes.get(i).getTimeOutHour());
          timeOutMinutes = String.format("%02d", jobTimes.get(i).getTimeOutMinute());
          outString += String.format("%-29s%-10s%-10s%n", "", timeInHours + timeInMinutes,
              timeOutHours + timeOutMinutes);
        }
        timeInHours = String.format("%02d", jobTimes.get(jobTimes.size() - 1).getTimeIn()
            .getHourOfDay());
        timeInMinutes = String.format("%02d", jobTimes.get(jobTimes.size() - 1).getTimeIn()
            .getMinuteOfHour());
        timeOutHours = String.format("%02d", jobTimes.get(jobTimes.size() - 1).getTimeOutHour());
        timeOutMinutes = String.format("%02d", jobTimes.get(jobTimes.size() - 1)
            .getTimeOutMinute());
        outString += "    ";
        outString += String.format("%-29s%-10s%-10s%-" + (varyingWagesOffset + 1)
            + "s%-7s%-7s%-7s%-" + (varyingTravelOffset + 1) + "s%-7s%n", "", timeInHours
                + timeInMinutes, timeOutHours + timeOutMinutes, "", formattedRegularTime,
            formattedOverTime, formattedDoubleTime, "", travelComponent);
      } else {
        String timeInHours = Integer.toString(jobEntry.getTimes().get(0).getTimeIn()
            .getHourOfDay());
        if (timeInHours.length() != 2) {
          timeInHours = "0" + timeInHours;
        }
        String timeOutHours = Integer.toString(jobEntry.getTimes().get(0).getTimeOutHour());
        if (timeOutHours.length() != 2) {
          timeOutHours = "0" + timeOutHours;
        }
        String timeOutMinutes = Integer.toString(jobEntry.getTimes().get(0).getTimeOutMinute());
        if (timeOutMinutes.length() != 2) {
          timeOutMinutes = timeOutMinutes + "0";
        }
        String timeInMinutes = Integer.toString(jobEntry.getTimes().get(0).getTimeIn()
            .getMinuteOfHour());
        if (timeInMinutes.length() != 2) {
          timeInMinutes = timeInMinutes + "0";
        }
        String timeIn = timeInHours + timeInMinutes;
        String timeOut = timeOutHours + timeOutMinutes;
        if (jobEntry.isFlatRate()) {
          timeIn = "FLAT";
          timeOut = "RATE";
        }
        // add a formatted line for reg ot and dt
        outString += String.format("%-11s%-18s%-10s%-10s%-" + (varyingWagesOffset + 1)
            + "s%-7s%-7s%-7s%-" + (varyingTravelOffset + 1) + "s%-7s%n", dateComponent, jobEntry
                .getCode(), timeIn, timeOut, "", formattedRegularTime, formattedOverTime,
            formattedDoubleTime, "", travelComponent);
      }
    }
    return outString;
  }
}
