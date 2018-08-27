
package cps.core.model.test.timeSheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import cps.core.ProgramPortal;
import cps.core.db.frame.DerbyDatabase;
import cps.core.model.timeSheet.TimeSheet;
import cps.core.model.timeSheet.TimeSheetWage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTimeSheetStartEnd {

  private static ProgramPortal pp;
  private static DerbyDatabase testDB;

  private TimeSheet ts1;
  private TimeSheet ts2;
  private TimeSheet ts3;

  @BeforeClass
  public static void loadDBConnection() throws SQLException {
    testDB = DerbyDatabase.TestDB;
    pp = new ProgramPortal(testDB);
  }

  private LocalDate createDate(String dateString) throws ParseException {
    Calendar calendarDate = Calendar.getInstance();
    final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    java.util.Date date = formatter.parse(dateString);
    calendarDate.setTime(date);
    return new LocalDate(date.getTime());
  }

  private TimeSheet createTimeSheet1() throws ParseException {
    TimeSheet.Builder tb = null;
    // create timeSheet
    tb = new TimeSheet.Builder(this.createDate("01/01/2017"));
    tb.nextSheet(null);
    tb.previousSheet(null);
    tb.stdMileageRate(new BigDecimal("0.535"));
    tb.travelWage(new TimeSheetWage.WageBuilder(new BigDecimal("10.00")).build());
    return tb.build();
  }

  private TimeSheet createTimeSheet2() throws ParseException {
    TimeSheet.Builder tb = null;
    // create timeSheet
    tb = new TimeSheet.Builder(this.createDate("01/15/2017"));
    tb.nextSheet(null);
    tb.previousSheet(null);
    tb.stdMileageRate(new BigDecimal("0.535"));
    tb.travelWage(new TimeSheetWage.WageBuilder(new BigDecimal("10.00")).build());
    return tb.build();
  }

  private TimeSheet createTimeSheet3() throws ParseException {
    TimeSheet.Builder tb = null;
    // create timeSheet
    tb = new TimeSheet.Builder(this.createDate("01/16/2017"));
    tb.nextSheet(null);
    tb.previousSheet(null);
    tb.stdMileageRate(new BigDecimal("0.535"));
    tb.travelWage(new TimeSheetWage.WageBuilder(new BigDecimal("10.00")).build());
    return tb.build();
  }

  @Test
  public void testDates() throws ClassNotFoundException, ParseException, SQLException {
    ts1 = createTimeSheet1();
    ts2 = createTimeSheet2();
    ts3 = createTimeSheet3();
    test();
    test2();
    test3();
  }

  /**
   * Tests the timeSheet start and end dates.
   * 
   * @throws ParseException
   *           if date cannot be parsed
   * @throws ClassNotFoundException
   *           if db restart fails
   * @throws SQLException
   *           if db restart fails
   */
  public void test() throws ParseException, ClassNotFoundException, SQLException {
    pp.addTimeSheet(ts1);
    LocalDate startDate = this.createDate("01/1/2017");
    LocalDate endDate = this.createDate("01/15/2017");
    TimeSheet tsheet = pp.findTimeSheetByDate(startDate);
    assertEquals(ts1, tsheet);
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    pp = pp.restart();
    assertEquals(startDate, pp.findAllTimeSheets().get(0).getStartDate());
    assertEquals(endDate, pp.findAllTimeSheets().get(0).getEndDate());
    assertTrue(pp.removeTimeSheet(tsheet.getId()));
  }

  /**
   * Tests the timeSheet start and end dates.
   * 
   * @throws ParseException
   *           if date cannot be parsed
   * @throws ClassNotFoundException
   *           if db restart fails
   * @throws SQLException
   *           if db restart fails
   */
  public void test2() throws ParseException, ClassNotFoundException, SQLException {
    pp.addTimeSheet(ts2);
    final LocalDate startDate = this.createDate("01/01/2017");
    final LocalDate endDate = this.createDate("01/15/2017");
    TimeSheet tsheet = pp.findTimeSheetById(ts2.getId());
    assertEquals(ts2, tsheet);
    pp = pp.restart();
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertTrue(pp.removeTimeSheet(tsheet.getId()));
  }

  /**
   * Tests the timeSheet start and end dates.
   * 
   * @throws ParseException
   *           if date cannot be parsed
   * @throws ClassNotFoundException
   *           if db restart fails
   * @throws SQLException
   *           if db restart fails
   */
  public void test3() throws ParseException, ClassNotFoundException, SQLException {
    pp.addTimeSheet(ts3);
    LocalDate startDate = this.createDate("01/16/2017");
    TimeSheet tsheet = pp.findTimeSheetById(ts3.getId());
    pp = pp.restart();
    LocalDate endDate = this.createDate("01/31/2017");
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertTrue(pp.removeTimeSheet(tsheet.getId()));
  }

  @AfterClass
  public static void wipeDB() throws SQLException {
    testDB.reset();
    testDB.shutdown();
  }

}
