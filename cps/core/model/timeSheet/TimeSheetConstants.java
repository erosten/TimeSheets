
package cps.core.model.timeSheet;

import java.math.BigDecimal;
import java.text.NumberFormat;

import org.joda.time.DateTimeConstants;

public class TimeSheetConstants {

  public static String ADVANCE = "Advance";

  public static String GAS_ADVANCE = "Gas Advance";

  public static String BONUS = "Bonus";

  public static String RETRO_PAY = "Retro Pay";

  public static int WORK_WEEK_START = DateTimeConstants.MONDAY;

  public static int WORK_WEEK_END = DateTimeConstants.SUNDAY;

  public static BigDecimal EMPLYR_COST_MULT = new BigDecimal("0.325");

  public static BigDecimal EMPLYR_EMPLYEE_COST_MULT = new BigDecimal("1.345");

  public static BigDecimal PREMIUM_WAGE_ADDITION = new BigDecimal("5");

  public static String REGULAR_WAGE = "Regular";

  public static String PREMIUM_WAGE = "Premium";

  public static String WOOD_WAGE = "Wood";

  public static String TRAVEL_WAGE = "Travel";

  public static int STD_DEDUC_TIME = 30;

  public static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

}
