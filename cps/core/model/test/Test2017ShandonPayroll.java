
package cps.core.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import cps.core.ProgramPortal;
import cps.core.db.frame.DerbyDatabase;
import cps.core.model.employee.Employee;
import cps.core.model.employee.EmployeeWage;
import cps.core.model.employee.PersonName;
import cps.core.model.timeSheet.Deduction;
import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.Extra;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.JobWage;
import cps.core.model.timeSheet.TimeSheet;
import cps.core.model.timeSheet.TimeSheetWage;
import cps.excel.TimeSheetExporter;
import cps.excel.WorkBookType;
import cps.gui.tools.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test2017ShandonPayroll {

  private static ProgramPortal pp;
  private static DerbyDatabase testDB;
  private int runTimes = 1;
  private int currentRun = 0;

  @BeforeClass
  public static void loadDBConnection() throws SQLException {
    testDB = DerbyDatabase.TestDB;
    pp = new ProgramPortal(testDB);
  }

  @Test
  public void testAddPyrl17031KMsheetInOrder() throws ParseException, ClassNotFoundException,
      SQLException {
    // build KM employee
    Employee km = this.createKm();
    assertTrue(pp.addNewEmployee(km));
    Employee Tyl = this.createTyl();
    assertTrue(pp.addNewEmployee(Tyl));
    Employee REk = this.createREk();
    assertTrue(pp.addNewEmployee(REk));
    Employee RS = this.createRS();
    assertTrue(pp.addNewEmployee(RS));
    Employee ApJ = this.createApJ();
    assertTrue(pp.addNewEmployee(ApJ));
    // Employee PC = this.createPC();
    final String before = testDB.getSnapshot(false);
    TimeSheet tsheet = this.create170731TimeSheet();
    pp.addTimeSheet(tsheet);
    // check objects JobEntry,EmployeeSheet,TimeSheet are correct and findable
    // in DB
    // test job fields
    addKrausMichaelJobs(km, tsheet);
    addTylJobs(Tyl, tsheet);
    addREkJobs(REk, tsheet);
    addRodriguezSantaJobs(RS, tsheet);
    addApJJobs(ApJ, tsheet);
    if (currentRun == 0) {
      TimeSheetExporter tseXLSX = new TimeSheetExporter();
      File directoryXLSX = new FileChooser().getFile();
      tseXLSX.export(tsheet, directoryXLSX, WorkBookType.XLSX, pp);
    }
    assertTrue(pp.removeTimeSheet(tsheet));
    // assert db is same as before any jobs were added
    assertEquals(before, testDB.getSnapshot(false));
    currentRun = currentRun + 1;
    if (currentRun <= runTimes) {
      testDB.reset();
      testDB.restart();
      testAddPyrl17031KMsheetInOrder();
    }
  }

  private Employee createKm() {
    Employee.Builder eb = null;
    eb = new Employee.Builder(new PersonName.NameBuilder("Michael", "Kraus").build(), "KM");
    eb.langBonus(BigDecimal.ZERO).wage(new EmployeeWage.Builder(
        "Regular",
        new BigDecimal("13.50")));
    eb.wage(new EmployeeWage.Builder("Wood", new BigDecimal("13.50")));
    eb.wage(new EmployeeWage.Builder("Premium", new BigDecimal("18.50")));
    return eb.build();
  }

  private Employee createTyl() {
    Employee.Builder eb = null;
    eb = new Employee.Builder(new PersonName.NameBuilder("Tylan", "Ahmed").build(), "Tyl");
    eb.langBonus(BigDecimal.ZERO).wage(new EmployeeWage.Builder(
        "Regular",
        new BigDecimal("11.00")));
    eb.wage(new EmployeeWage.Builder("Wood", new BigDecimal("11.00")));
    eb.wage(new EmployeeWage.Builder("Premium", new BigDecimal("16.00")));
    return eb.build();
  }

  private Employee createREk() {
    Employee.Builder eb = null;
    eb = new Employee.Builder(new PersonName.NameBuilder("Erik", "Rosten").build(), "REk");
    eb.langBonus(BigDecimal.ZERO).wage(new EmployeeWage.Builder(
        "Regular",
        new BigDecimal("13.00")));
    eb.wage(new EmployeeWage.Builder("Wood", new BigDecimal("11.00")));
    eb.wage(new EmployeeWage.Builder("Premium", new BigDecimal("18.00")));
    return eb.build();
  }

  private Employee createRS() {
    Employee.Builder eb = null;
    eb = new Employee.Builder(
        new PersonName.NameBuilder("Maria (Santa)", "Rodriguez").build(),
        "RS");
    eb.langBonus(new BigDecimal("0.50")).wage(new EmployeeWage.Builder(
        "Regular",
        new BigDecimal("13.50")));
    eb.wage(new EmployeeWage.Builder("Wood", new BigDecimal("11.00")));
    eb.wage(new EmployeeWage.Builder("Premium", new BigDecimal("18.50")));
    return eb.build();
  }

  private Employee createApJ() {
    Employee.Builder eb = null;
    eb = new Employee.Builder(new PersonName.NameBuilder("Aparisio", "Jose").build(), "ApJ");
    eb.langBonus(new BigDecimal("0.50")).wage(new EmployeeWage.Builder(
        "Regular",
        new BigDecimal("12.50")));
    eb.wage(new EmployeeWage.Builder("Wood", new BigDecimal("13.75")));
    eb.wage(new EmployeeWage.Builder("Premium", new BigDecimal("17.50")));
    return eb.build();
  }

  // private Employee createPC() {
  // Employee.Builder eb = null;
  // eb = new Employee.Builder(new PersonName.NameBuilder("Cipriano", "Paredes").build(), "PC");
  // eb.langBonus(new BigDecimal("0.50")).wage(new EmployeeWage.Builder(
  // "Regular",
  // new BigDecimal("16.50")));
  // eb.wage(new EmployeeWage.Builder("Wood", new BigDecimal("17.50")));
  // eb.wage(new EmployeeWage.Builder("Premium", new BigDecimal("21.50")));
  // return eb.build();
  // }

  private LocalDate createDate(String dateString) throws ParseException {
    Calendar calendarDate = Calendar.getInstance();
    final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    java.util.Date date = formatter.parse(dateString);
    calendarDate.setTime(date);
    return new LocalDate(date.getTime());
  }

  private TimeSheet create170731TimeSheet() throws ParseException {
    TimeSheet.Builder tb = null;
    // create timeSheet
    tb = new TimeSheet.Builder(this.createDate("07/17/2017"));
    tb.nextSheet(null);
    tb.previousSheet(null);
    tb.stdMileageRate(new BigDecimal("0.535"));
    tb.travelWage(new TimeSheetWage.WageBuilder(new BigDecimal("10.00")).build());
    return tb.build();
  }

  private JobEntry createKMJob1(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #1 - SNS Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS Travel");
    jb.date(this.createDate("07/17/2017"));
    jb.travelString("Ride");
    jb.addTime(new LocalTime(06, 22), new LocalTime(8, 00));
    jb.addTime(new LocalTime(8, 30), new LocalTime(8, 50));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.stdDeduction(true);
    return jb.build();
  }

  private JobEntry createKMJob2(Employee emp) throws ParseException {
    // create JobEntry #2 - SNS1701a
    JobEntry.Builder jb2 = new JobEntry.Builder(emp);
    jb2.code("SNS1701a");
    jb2.date(this.createDate("07/17/2017"));
    jb2.addTime(new LocalTime(8, 50), new LocalTime(12, 40));
    jb2.addTime(new LocalTime(14, 25), new LocalTime(17, 25));
    jb2.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb2.build();
  }

  private JobEntry createKMJob3(Employee emp) throws ParseException {
    // create JobEntry #3 - SNS1701a
    JobEntry.Builder jb2 = new JobEntry.Builder(emp);
    jb2.code("SNS1701a");
    jb2.date(this.createDate("07/18/2017"));
    jb2.addTime(new LocalTime(8, 15), new LocalTime(12, 15));
    jb2.addTime(new LocalTime(13, 10), new LocalTime(17, 35));
    jb2.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb2.build();
  }

  private JobEntry createKMJob4(Employee emp) throws ParseException {
    JobEntry.Builder jb4 = new JobEntry.Builder(emp);
    jb4.code("SNS1701a");
    jb4.date(this.createDate("07/19/2017"));
    jb4.addTime(new LocalTime(8, 06), new LocalTime(12, 45));
    jb4.addTime(new LocalTime(14, 30), new LocalTime(18, 20));
    jb4.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb4.build();
  }

  private JobEntry createKMJob5(Employee emp) throws ParseException {
    JobEntry.Builder jb5 = new JobEntry.Builder(emp);
    jb5.code("SNS1701a");
    jb5.date(this.createDate("07/20/2017"));
    jb5.addTime(new LocalTime(8, 10), new LocalTime(12, 30));
    jb5.addTime(new LocalTime(13, 50), new LocalTime(18, 20));
    jb5.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb5.build();
  }

  private JobEntry createKMJob6(Employee emp) throws ParseException {
    JobEntry.Builder jb6 = new JobEntry.Builder(emp);
    jb6.code("SNS1701a");
    jb6.date(this.createDate("07/21/2017"));
    jb6.addTime(new LocalTime(8, 10), new LocalTime(12, 45));
    jb6.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb6.build();
  }

  private JobEntry createKMJob7(Employee emp) throws ParseException {
    JobEntry.Builder jb7 = new JobEntry.Builder(emp);
    jb7.code("SNS1701b");
    jb7.date(this.createDate("07/21/2017"));
    jb7.addTime(new LocalTime(14, 30), new LocalTime(18, 20));
    jb7.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    return jb7.build();
  }

  private JobEntry createKMJob8(Employee emp) throws ParseException {
    JobEntry.Builder jb8 = new JobEntry.Builder(emp);
    jb8.code("SNS1701b");
    jb8.date(this.createDate("07/22/2017"));
    jb8.addTime(new LocalTime(8, 10), new LocalTime(11, 30));
    jb8.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    return jb8.build();
  }

  private JobEntry createKMJob9(Employee emp) throws ParseException {
    JobEntry.Builder jb9 = new JobEntry.Builder(emp);
    jb9.code("SNS1701a");
    jb9.date(this.createDate("07/22/2017"));
    jb9.addTime(new LocalTime(11, 30), new LocalTime(12, 30));
    jb9.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb9.build();
  }

  private JobEntry createKMJob10(Employee emp, TimeSheet tsheet) throws ParseException {
    JobEntry.Builder jb10 = new JobEntry.Builder(emp);
    jb10.code("Travel SNS>Anx");
    jb10.date(this.createDate("07/22/2017"));
    jb10.addTime(new LocalTime(12, 30), new LocalTime(14, 05));
    jb10.addTime(new LocalTime(15, 05), new LocalTime(16, 05));
    jb10.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    return jb10.build();
  }

  private JobEntry createKMJob11(Employee emp) throws ParseException {
    JobEntry.Builder jb11 = new JobEntry.Builder(emp);
    jb11.code("SNS Unload/Van Return");
    jb11.date(this.createDate("07/22/2017"));
    jb11.addTime(new LocalTime(16, 05), new LocalTime(16, 50));
    jb11.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb11.travelString("Ride");
    return jb11.build();
  }

  private JobEntry createKMJob12(Employee emp) throws ParseException {
    JobEntry.Builder jb12 = new JobEntry.Builder(emp);
    jb12.code("DPH1704");
    jb12.date(this.createDate("07/25/2017"));
    jb12.addTime(new LocalTime(10, 02), new LocalTime(10, 30));
    jb12.addTime(new LocalTime(10, 50), new LocalTime(13, 05));
    jb12.addTime(new LocalTime(14, 10), new LocalTime(16, 05));
    jb12.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb12.build();
  }

  private JobEntry createKMJob13(Employee emp) throws ParseException {
    JobEntry.Builder jb13 = new JobEntry.Builder(emp);
    jb13.code("HHC1705a");
    jb13.date(this.createDate("07/25/2017"));
    jb13.addTime(new LocalTime(10, 30), new LocalTime(10, 50));
    jb13.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb13.build();
  }

  private JobEntry createKMJob14(Employee emp) throws ParseException {
    JobEntry.Builder jb14 = new JobEntry.Builder(emp);
    jb14.code("DPH1704");
    jb14.date(this.createDate("07/26/2017"));
    jb14.addTime(new LocalTime(8, 15), new LocalTime(9, 25));
    jb14.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb14.build();
  }

  private JobEntry createKMJob15(Employee emp) throws ParseException {
    JobEntry.Builder jb15 = new JobEntry.Builder(emp);
    jb15.code("HHC1705b");
    jb15.date(this.createDate("07/26/2017"));
    jb15.addTime(new LocalTime(9, 25), new LocalTime(13, 32));
    jb15.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb15.build();
  }

  private JobEntry createKMJob16(Employee emp, TimeSheet tsheet) throws ParseException {
    JobEntry.Builder jb16 = new JobEntry.Builder(emp);
    jb16.code("ORC Travel");
    jb16.date(this.createDate("07/29/2017"));
    jb16.addTime(new LocalTime(7, 19), new LocalTime(8, 00));
    jb16.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb16.travelString("Ride");
    jb16.stdDeduction(true);
    return jb16.build();
  }

  private JobEntry createKMJob17(Employee emp) throws ParseException {
    JobEntry.Builder jb17 = new JobEntry.Builder(emp);
    jb17.code("ORC1701");
    jb17.date(this.createDate("07/29/2017"));
    jb17.addTime(new LocalTime(8, 00), new LocalTime(12, 40));
    jb17.addTime(new LocalTime(13, 45), new LocalTime(15, 10));
    jb17.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    return jb17.build();
  }

  private JobEntry createKMJob18(Employee emp) throws ParseException {
    JobEntry.Builder jb18 = new JobEntry.Builder(emp);
    jb18.code("ORC1702 incl T");
    jb18.date(this.createDate("07/29/2017"));
    jb18.addTime(new LocalTime(15, 10), new LocalTime(19, 05));
    jb18.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb18.travelString("Ride");
    return jb18.build();
  }

  private JobEntry createKMJob19(Employee emp) throws ParseException {
    JobEntry.Builder jb19 = new JobEntry.Builder(emp);
    jb19.code("ORC1703 incl T");
    jb19.date(this.createDate("07/29/2017"));
    jb19.addTime(new LocalTime(19, 05), new LocalTime(21, 40));
    jb19.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb19.travelString("Ride");
    return jb19.build();
  }

  private JobEntry createKMJob20(Employee emp, TimeSheet tsheet) throws ParseException {
    JobEntry.Builder jb20 = new JobEntry.Builder(emp);
    jb20.code("ORC Travel");
    jb20.date(this.createDate("07/29/2017"));
    jb20.addTime(new LocalTime(21, 40), new LocalTime(22, 11));
    jb20.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb20.travelString("Ride");
    jb20.stdDeduction(false);
    return jb20.build();
  }

  private JobEntry createKMJob21(Employee emp, TimeSheet tsheet) throws ParseException {
    JobEntry.Builder jb21 = new JobEntry.Builder(emp);
    jb21.code("ORC Travel");
    jb21.date(this.createDate("07/30/2017"));
    jb21.addTime(new LocalTime(7, 39), new LocalTime(8, 10));
    jb21.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb21.travelString("Ride");
    jb21.stdDeduction(true);
    return jb21.build();
  }

  private JobEntry createKMJob22(Employee emp) throws ParseException {
    JobEntry.Builder jb22 = new JobEntry.Builder(emp);
    jb22.code("ORC1701 incl T");
    jb22.date(this.createDate("07/30/2017"));
    jb22.addTime(new LocalTime(8, 10), new LocalTime(10, 50));
    jb22.addTime(new LocalTime(13, 00), new LocalTime(13, 19));
    jb22.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb22.travelString("Ride");
    return jb22.build();
  }

  private JobEntry createKMJob23(Employee emp) throws ParseException {
    JobEntry.Builder jb23 = new JobEntry.Builder(emp);
    jb23.code("ORC1702 incl T");
    jb23.date(this.createDate("07/30/2017"));
    jb23.addTime(new LocalTime(10, 50), new LocalTime(13, 00));
    jb23.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb23.travelString("Ride");
    return jb23.build();
  }

  private JobEntry createKMJob24(Employee emp) throws ParseException {
    JobEntry.Builder jb24 = new JobEntry.Builder(emp);
    jb24.code("Travel LV>LA incl L");
    jb24.date(this.createDate("07/30/2017"));
    jb24.addTime(new LocalTime(13, 19), new LocalTime(13, 26));
    jb24.addTime(new LocalTime(14, 9), new LocalTime(14, 25));
    jb24.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb24.travelString("Ride");
    return jb24.build();
  }

  private JobEntry createKMJob25(Employee emp) throws ParseException {
    JobEntry.Builder jb25 = new JobEntry.Builder(emp);
    jb25.code("ORC1703");
    jb25.date(this.createDate("07/30/2017"));
    jb25.addTime(new LocalTime(14, 25), new LocalTime(16, 19));
    jb25.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    return jb25.build();
  }

  private JobEntry createKMJob26(Employee emp, TimeSheet tsheet) throws ParseException {
    JobEntry.Builder jb26 = new JobEntry.Builder(emp);
    jb26.code("ORC Travel");
    jb26.date(this.createDate("07/30/2017"));
    jb26.addTime(new LocalTime(16, 19), new LocalTime(16, 50));
    jb26.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb26.travelString("Ride");
    jb26.stdDeduction(false);
    return jb26.build();
  }

  private JobEntry createKMJob27(Employee emp, TimeSheet tsheet) throws ParseException {
    JobEntry.Builder jb27 = new JobEntry.Builder(emp);
    jb27.code("ORC Travel");
    jb27.date(this.createDate("07/31/2017"));
    jb27.addTime(new LocalTime(8, 37), new LocalTime(9, 11));
    jb27.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb27.travelString("Ride");
    jb27.stdDeduction(true);
    return jb27.build();
  }

  private JobEntry createKMJob28(Employee emp) throws ParseException {
    JobEntry.Builder jb28 = new JobEntry.Builder(emp);
    jb28.code("ORC1701 incl T");
    jb28.date(this.createDate("07/31/2017"));
    jb28.addTime(new LocalTime(9, 11), new LocalTime(12, 15));
    jb28.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb28.travelString("Ride");
    return jb28.build();
  }

  private JobEntry createKMJob29(Employee emp) throws ParseException {
    JobEntry.Builder jb29 = new JobEntry.Builder(emp);
    jb29.code("Travel LV>OR incl L");
    jb29.date(this.createDate("07/31/2017"));
    jb29.addTime(new LocalTime(12, 15), new LocalTime(12, 26));
    jb29.addTime(new LocalTime(13, 4), new LocalTime(13, 10));
    jb29.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb29.travelString("Ride");
    return jb29.build();
  }

  private JobEntry createKMJob30(Employee emp) throws ParseException {
    JobEntry.Builder jb30 = new JobEntry.Builder(emp);
    jb30.code("ORC1702");
    jb30.date(this.createDate("07/31/2017"));
    jb30.addTime(new LocalTime(13, 10), new LocalTime(15, 40));
    jb30.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    return jb30.build();
  }

  private JobEntry createKMJob31(Employee emp) throws ParseException {
    JobEntry.Builder jb31 = new JobEntry.Builder(emp);
    jb31.code("ORC1703 incl T");
    jb31.date(this.createDate("07/31/2017"));
    jb31.addTime(new LocalTime(15, 40), new LocalTime(18, 00));
    jb31.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb31.travelString("Ride");
    return jb31.build();
  }

  private JobEntry createKMJob32(Employee emp, TimeSheet tsheet) throws ParseException {
    JobEntry.Builder jb32 = new JobEntry.Builder(emp);
    jb32.code("ORC Travel");
    jb32.date(this.createDate("07/31/2017"));
    jb32.addTime(new LocalTime(18, 00), new LocalTime(18, 15));
    jb32.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb32.travelString("Ride");
    jb32.stdDeduction(false);
    return jb32.build();
  }

  private JobEntry createTylJob1(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #1 - SNS Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS Travel");
    jb.date(this.createDate("07/17/2017"));
    jb.travelString("Ride");
    jb.addTime(new LocalTime(07, 00), new LocalTime(8, 50));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.stdDeduction(true);
    JobEntry job = jb.build();
    assertEquals(2, tsheet.getEmployeeSheets().size());
    return job;
  }

  private JobEntry createTylJob2(Employee emp) throws ParseException {
    // create JobEntry #1 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/17/2017"));
    jb.addTime(new LocalTime(8, 50), new LocalTime(12, 40));
    jb.addTime(new LocalTime(14, 25), new LocalTime(17, 25));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createTylJob3(Employee emp) throws ParseException {
    // create JobEntry #3 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/18/2017"));
    jb.addTime(new LocalTime(8, 10), new LocalTime(12, 15));
    jb.addTime(new LocalTime(13, 55), new LocalTime(17, 35));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createTylJob4(Employee emp) throws ParseException {
    // create JobEntry #3 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/19/2017"));
    jb.addTime(new LocalTime(8, 06), new LocalTime(12, 45));
    jb.addTime(new LocalTime(14, 30), new LocalTime(18, 20));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createTylJob5(Employee emp) throws ParseException {
    // create JobEntry #5 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/20/2017"));
    jb.addTime(new LocalTime(8, 10), new LocalTime(12, 30));
    jb.addTime(new LocalTime(13, 50), new LocalTime(18, 25));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createTylJob6(Employee emp) throws ParseException {
    // create JobEntry #6 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/21/2017"));
    jb.addTime(new LocalTime(8, 10), new LocalTime(12, 45));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createTylJob7(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #7 - SNS Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS Travel");
    jb.date(this.createDate("07/21/2017"));
    jb.travelString("Ride");
    jb.addTime(new LocalTime(13, 55), new LocalTime(15, 26));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.stdDeduction(false);
    return jb.build();
  }

  private Deduction createTylAdv(Employee emp) throws ParseException {
    // create $100 advance
    Deduction advance = new Deduction(
        emp,
        "Advance",
        new BigDecimal("100"),
        this.createDate("07/16/2017"));
    return advance;
  }

  private JobEntry createREkJob1(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #1 - SNS Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS Travel");
    jb.date(this.createDate("07/17/2017"));
    jb.addTime(new LocalTime(7, 00), new LocalTime(8, 50));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.stdDeduction(true);
    jb.travelString("145.0");
    return jb.build();
  }

  private JobEntry createREkJob2(Employee emp) throws ParseException {
    // create JobEntry #2 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/17/2017"));
    jb.addTime(new LocalTime(8, 50), new LocalTime(12, 40));
    jb.addTime(new LocalTime(14, 25), new LocalTime(17, 25));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createREkJob3(Employee emp) throws ParseException {
    // create JobEntry #3 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/18/2017"));
    jb.addTime(new LocalTime(8, 10), new LocalTime(12, 15));
    jb.addTime(new LocalTime(13, 55), new LocalTime(17, 35));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createREkJob4(Employee emp) throws ParseException {
    // create JobEntry #4 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/19/2017"));
    jb.addTime(new LocalTime(8, 06), new LocalTime(12, 45));
    jb.addTime(new LocalTime(14, 30), new LocalTime(18, 20));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createREkJob5(Employee emp) throws ParseException {
    // create JobEntry #5 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/20/2017"));
    jb.addTime(new LocalTime(8, 10), new LocalTime(12, 30));
    jb.addTime(new LocalTime(13, 50), new LocalTime(18, 25));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createREkJob6(Employee emp) throws ParseException {
    // create JobEntry #6 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/21/2017"));
    jb.addTime(new LocalTime(8, 10), new LocalTime(12, 45));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createREkJob7(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #7 - SNS Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/21/2017"));
    jb.addTime(new LocalTime(13, 55), new LocalTime(15, 26));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.stdDeduction(false);
    jb.travelString("145.0");
    return jb.build();
  }

  private JobEntry createREkJob8(Employee emp) throws ParseException {
    // create JobEntry #8 - Admin
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("Admin");
    jb.date(this.createDate("07/21/2017"));
    jb.addTime(new LocalTime(17, 10), new LocalTime(18, 32));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createREkJob9(Employee emp) throws ParseException {
    // create JobEntry #9 - Admin
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("Admin");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(11, 50), new LocalTime(17, 20));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private Deduction createREkAdv(Employee emp) throws ParseException {
    // create $100 advance
    Deduction advance = new Deduction(
        emp,
        "Advance",
        new BigDecimal("100"),
        this.createDate("07/16/2017"));
    return advance;
  }

  private JobEntry createRSJob1(Employee emp) throws ParseException {
    // create JobEntry #1 - EFF Survey
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("EFF Survey");
    jb.date(this.createDate("07/16/2017"));
    jb.flatRateHours(new BigDecimal("100"));
    jb.addTime(new LocalTime(14, 20), new LocalTime(15, 10));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob2(Employee emp) throws ParseException {
    // create JobEntry #2 - SMK1717
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SMK1717");
    jb.date(this.createDate("07/17/2017"));
    jb.addTime(new LocalTime(9, 00), new LocalTime(13, 00));
    jb.addTime(new LocalTime(14, 50), new LocalTime(18, 40));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob3(Employee emp) throws ParseException {
    // create JobEntry #3 - UTS1701
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("UTS1701");
    jb.date(this.createDate("07/18/2017"));
    jb.addTime(new LocalTime(12, 45), true, new LocalTime(17, 58), true);
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob4(Employee emp) throws ParseException {
    // create JobEntry #4 - MDV1707b
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("MDV1707b");
    jb.date(this.createDate("07/19/2017"));
    jb.addTime(new LocalTime(6, 00), new LocalTime(12, 00));
    jb.addTime(new LocalTime(12, 30), new LocalTime(16, 30));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob5(Employee emp) throws ParseException {
    // create JobEntry #5 - Travel MDV>GEN
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("Travel MDV>GEN");
    jb.date(this.createDate("07/19/2017"));
    jb.addTime(new LocalTime(16, 30), new LocalTime(16, 45));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb.travelString("4.5");
    return jb.build();
  }

  private JobEntry createRSJob6(Employee emp) throws ParseException {
    // create JobEntry #1 - GEN1707c
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("GEN1707c");
    jb.date(this.createDate("07/19/2017"));
    jb.flatRateHours(new BigDecimal("400"));
    jb.addTime(new LocalTime(16, 45), new LocalTime(20, 45));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob7(Employee emp) throws ParseException {
    // create JobEntry #7 - UTS1701
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("UTS1701");
    jb.date(this.createDate("07/20/2017"));
    jb.addTime(new LocalTime(8, 00), false, new LocalTime(12, 00), true);
    jb.addTime(new LocalTime(12, 30), true, new LocalTime(16, 30), false);
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob8(Employee emp) throws ParseException {
    // create JobEntry #8 - UTS1701
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("UTS1701");
    jb.date(this.createDate("07/22/2017"));
    jb.addTime(new LocalTime(9, 00), new LocalTime(13, 31));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob9(Employee emp) throws ParseException {
    // create JobEntry #9 - SYP1707c
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SYP1707c");
    jb.date(this.createDate("07/23/2017"));
    jb.addTime(new LocalTime(13, 55), new LocalTime(16, 55));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb.flatRateHours(new BigDecimal("300"));
    return jb.build();
  }

  private JobEntry createRSJob10(Employee emp) throws ParseException {
    // create JobEntry #10 - EFF1702
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("EFF1702");
    jb.date(this.createDate("07/25/2017"));
    jb.addTime(new LocalTime(9, 00), new LocalTime(13, 10));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob11(Employee emp) throws ParseException {
    // create JobEntry #11 - Travel EFF>DPH
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("Travel EFF>DPH");
    jb.date(this.createDate("07/25/2017"));
    jb.addTime(new LocalTime(13, 10), new LocalTime(13, 28));
    jb.addTime(new LocalTime(14, 01), new LocalTime(14, 10));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb.travelString("10.9");
    return jb.build();
  }

  private JobEntry createRSJob12(Employee emp) throws ParseException {
    // create JobEntry #12 - DPH1704
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("DPH1704");
    jb.date(this.createDate("07/25/2017"));
    jb.addTime(new LocalTime(14, 10), new LocalTime(16, 05));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob13(Employee emp) throws ParseException {
    // create JobEntry #13 - DPH1704
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("DPH1704");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(8, 00), new LocalTime(9, 48));
    jb.addTime(new LocalTime(10, 05), new LocalTime(12, 19));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob14(Employee emp) throws ParseException {
    // create JobEntry #14 - HHC1705a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("HHC1705a");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(9, 48), new LocalTime(10, 05));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob15(Employee emp) throws ParseException {
    // create JobEntry #15 - Travel DPH>UTS
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("Travel DPH>UTS");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(12, 49), new LocalTime(13, 00));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb.travelString("5.3");
    return jb.build();
  }

  private JobEntry createRSJob16(Employee emp) throws ParseException {
    // create JobEntry #16 - UTS1701
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("UTS1701");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(13, 00), new LocalTime(16, 9));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob17(Employee emp) throws ParseException {
    // create JobEntry #17 - Travel UTS>GEN
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("Travel UTS>GEN");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(17, 20), new LocalTime(17, 30));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb.travelString("Ride");
    return jb.build();
  }

  private JobEntry createRSJob18(Employee emp) throws ParseException {
    // create JobEntry #18 - GEN1707d
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("GEN1707d");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(17, 30), new LocalTime(19, 30));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb.flatRateHours(new BigDecimal("200"));
    return jb.build();
  }

  private JobEntry createRSJob19(Employee emp) throws ParseException {
    // create JobEntry #19 - UTS1701
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("UTS1701");
    jb.date(this.createDate("07/27/2017"));
    jb.addTime(new LocalTime(8, 30), new LocalTime(13, 21));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob20(Employee emp) throws ParseException {
    // create JobEntry #20 - UTS1701
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("UTS1701");
    jb.date(this.createDate("07/28/2017"));
    jb.addTime(new LocalTime(8, 00), new LocalTime(12, 00));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob21(Employee emp) throws ParseException {
    // create JobEntry #21 - TCS1704
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("TCS1704");
    jb.date(this.createDate("07/28/2017"));
    jb.addTime(new LocalTime(15, 00), new LocalTime(16, 30));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb.flatRateHours(new BigDecimal("150"));
    return jb.build();
  }

  private JobEntry createRSJob22(Employee emp) throws ParseException {
    // create JobEntry #22 - SYP1707d
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SYP1707d");
    jb.date(this.createDate("07/28/2017"));
    jb.addTime(new LocalTime(17, 55), new LocalTime(20, 55));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb.flatRateHours(new BigDecimal("300"));
    return jb.build();
  }

  private JobEntry createRSJob23(Employee emp) throws ParseException {
    // create JobEntry #23 - SYP1707e
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/28/2017"));
    jb.addTime(new LocalTime(21, 00), new LocalTime(22, 30));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb.flatRateHours(new BigDecimal("150"));
    return jb.build();
  }

  private JobEntry createRSJob24(Employee emp) throws ParseException {
    // create JobEntry #24 - UTS1701
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("UTS1701");
    jb.date(this.createDate("07/29/2017"));
    jb.addTime(new LocalTime(8, 00), new LocalTime(11, 00));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createRSJob25(Employee emp) throws ParseException {
    // create JobEntry #25 - UTS1701
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("UTS1701");
    jb.date(this.createDate("07/31/2017"));
    jb.addTime(new LocalTime(8, 00), new LocalTime(13, 00));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob1(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #1 - SNS Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS Travel");
    jb.date(this.createDate("07/17/2017"));
    jb.addTime(new LocalTime(5, 44), new LocalTime(8, 00));
    jb.addTime(new LocalTime(8, 30), new LocalTime(8, 50));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob2(Employee emp) throws ParseException {
    // create JobEntry #2 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/17/2017"));
    jb.addTime(new LocalTime(8, 50), new LocalTime(12, 40));
    jb.addTime(new LocalTime(14, 20), new LocalTime(17, 25));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob3(Employee emp) throws ParseException {
    // create JobEntry #3 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/18/2017"));
    jb.addTime(new LocalTime(8, 15), new LocalTime(12, 15));
    jb.addTime(new LocalTime(13, 10), new LocalTime(17, 35));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob4(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #4 - SNS Travel & Refueling
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS Travel & Refueling");
    jb.date(this.createDate("07/18/2017"));
    jb.addTime(new LocalTime(17, 35), new LocalTime(18, 15));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.stdDeduction(false);
    return jb.build();
  }

  private JobEntry createApJJob5(Employee emp) throws ParseException {
    // create JobEntry #5 - SN1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/19/2017"));
    jb.addTime(new LocalTime(8, 06), new LocalTime(12, 45));
    jb.addTime(new LocalTime(13, 46), new LocalTime(18, 20));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob6(Employee emp) throws ParseException {
    // create JobEntry #6 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/20/2017"));
    jb.addTime(new LocalTime(8, 10), new LocalTime(13, 00));
    jb.addTime(new LocalTime(13, 40), new LocalTime(20, 00));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob7(Employee emp) throws ParseException {
    // create JobEntry #7 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/21/2017"));
    jb.addTime(new LocalTime(8, 10), new LocalTime(9, 00));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob8(Employee emp) throws ParseException {
    // create JobEntry #8 - SNS1701b
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701b");
    jb.date(this.createDate("07/21/2017"));
    jb.addTime(new LocalTime(9, 00), new LocalTime(12, 45));
    jb.addTime(new LocalTime(14, 30), new LocalTime(18, 20));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob9(Employee emp) throws ParseException {
    // create JobEntry #9 - SNS1701b
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701b");
    jb.date(this.createDate("07/22/2017"));
    jb.addTime(new LocalTime(8, 10), new LocalTime(11, 30));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob10(Employee emp) throws ParseException {
    // create JobEntry #10 - SNS1701a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS1701a");
    jb.date(this.createDate("07/22/2017"));
    jb.addTime(new LocalTime(11, 30), new LocalTime(12, 30));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob11(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #11 - Travel SNS>Anx
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("Travel SNS>Anx");
    jb.date(this.createDate("07/22/2017"));
    jb.addTime(new LocalTime(12, 30), new LocalTime(14, 05));
    jb.addTime(new LocalTime(15, 05), new LocalTime(16, 05));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob12(Employee emp) throws ParseException {
    // create JobEntry #12 - SNS Unload/Van Return
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("SNS Unload/Van Return");
    jb.date(this.createDate("07/22/2017"));
    jb.addTime(new LocalTime(16, 05), new LocalTime(17, 15));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob13(Employee emp) throws ParseException {
    // create JobEntry #13 - HHC1705a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("HHC1705a");
    jb.date(this.createDate("07/25/2017"));
    jb.addTime(new LocalTime(10, 02), new LocalTime(13, 05));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob14(Employee emp) throws ParseException {
    // create JobEntry #14 - DPH1704
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("DPH1704");
    jb.date(this.createDate("07/25/2017"));
    jb.addTime(new LocalTime(14, 10), new LocalTime(15, 31));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob15(Employee emp) throws ParseException {
    // create JobEntry #15 - Crew Pick Up
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("Crew Pick Up");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(7, 48), new LocalTime(8, 15));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb.travelString("Truck");
    return jb.build();
  }

  private JobEntry createApJJob16(Employee emp) throws ParseException {
    // create JobEntry #16 - DPH1704
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("DPH1704");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(8, 15), new LocalTime(10, 17));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob17(Employee emp) throws ParseException {
    // create JobEntry #17 - HHC1705a
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("HHC1705a");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(10, 17), new LocalTime(10, 58));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob18(Employee emp) throws ParseException {
    // create JobEntry #18 - HHC1705b
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("HHC1705b");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(10, 58), new LocalTime(13, 32));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob19(Employee emp) throws ParseException {
    // create JobEntry #19 - Crew Take Hm
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("Crew Take Hm");
    jb.date(this.createDate("07/26/2017"));
    jb.addTime(new LocalTime(13, 32), new LocalTime(13, 55));
    jb.addTime(new LocalTime(14, 25), new LocalTime(14, 45));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Regular").getName(),
        pp.findWageByName(emp, "Regular").getRate()));
    jb.travelString("Truck");
    return jb.build();
  }

  private JobEntry createApJJob20(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #20 - ORC Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC Travel");
    jb.date(this.createDate("07/29/2017"));
    jb.addTime(new LocalTime(6, 41), new LocalTime(8, 00));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob21(Employee emp) throws ParseException {
    // create JobEntry #21 - ORC1701
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC1701");
    jb.date(this.createDate("07/29/2017"));
    jb.addTime(new LocalTime(8, 00), new LocalTime(12, 40));
    jb.addTime(new LocalTime(13, 45), new LocalTime(15, 10));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob22(Employee emp) throws ParseException {
    // create JobEntry #22 - ORC1702 incl T
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC1702 incl T");
    jb.date(this.createDate("07/29/2017"));
    jb.addTime(new LocalTime(15, 10), new LocalTime(19, 05));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob23(Employee emp) throws ParseException {
    // create JobEntry #23 - ORC1703 incl T
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC1703 incl T");
    jb.date(this.createDate("07/29/2017"));
    jb.addTime(new LocalTime(19, 05), new LocalTime(21, 40));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob24(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #24 - ORC Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC Travel");
    jb.date(this.createDate("07/29/2017"));
    jb.addTime(new LocalTime(21, 40), new LocalTime(22, 41));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob25(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #25 - ORC Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC Travel");
    jb.date(this.createDate("07/30/2017"));
    jb.addTime(new LocalTime(6, 45), true, new LocalTime(8, 10), true);
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob26(Employee emp) throws ParseException {
    // create JobEntry #26 - ORC1701 incl T
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC1701 incl T");
    jb.date(this.createDate("07/30/2017"));
    jb.addTime(new LocalTime(8, 10), new LocalTime(10, 50));
    jb.addTime(new LocalTime(13, 00), new LocalTime(13, 19));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob27(Employee emp) throws ParseException {
    // create JobEntry #27 - ORC1702 incl T
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC1702 incl T");
    jb.date(this.createDate("07/30/2017"));
    jb.addTime(new LocalTime(10, 50), new LocalTime(13, 00));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob28(Employee emp) throws ParseException {
    // create JobEntry #28 - Travel LV>LA incl L
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("Travel LV>LA incl L");
    jb.date(this.createDate("07/30/2017"));
    jb.addTime(new LocalTime(13, 19), new LocalTime(13, 26));
    jb.addTime(new LocalTime(14, 9), new LocalTime(14, 25));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob29(Employee emp) throws ParseException {
    // create JobEntry #29 - ORC1703
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC1703");
    jb.date(this.createDate("07/30/2017"));
    jb.addTime(new LocalTime(14, 25), new LocalTime(16, 19));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob30(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #30 - ORC Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC Travel");
    jb.date(this.createDate("07/30/2017"));
    jb.addTime(new LocalTime(16, 19), new LocalTime(17, 20));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob31(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #31 - ORC Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC Travel");
    jb.date(this.createDate("07/31/2017"));
    jb.addTime(new LocalTime(8, 07), new LocalTime(9, 11));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob32(Employee emp) throws ParseException {
    // create JobEntry #32 - ORC1701 incl T
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC1701 incl T");
    jb.date(this.createDate("07/31/2017"));
    jb.addTime(new LocalTime(9, 11), new LocalTime(12, 15));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb.travelString("Van");
    return jb.build();

  }

  private JobEntry createApJJob33(Employee emp) throws ParseException {
    // create JobEntry #33 - Travel LV>OR incl L
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("Travel LV>OR incl L");
    jb.date(this.createDate("07/31/2017"));
    jb.addTime(new LocalTime(12, 15), new LocalTime(12, 26));
    jb.addTime(new LocalTime(13, 4), new LocalTime(13, 10));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob34(Employee emp) throws ParseException {
    // create JobEntry #34 - ORC1702
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC1702");
    jb.date(this.createDate("07/31/2017"));
    jb.addTime(new LocalTime(13, 10), new LocalTime(15, 40));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    return jb.build();
  }

  private JobEntry createApJJob35(Employee emp) throws ParseException {
    // create JobEntry #35 - ORC1703 incl T
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC1703 incl T");
    jb.date(this.createDate("07/31/2017"));
    jb.addTime(new LocalTime(15, 40), new LocalTime(18, 00));
    jb.wage(new JobWage(
        pp.findWageByName(emp, "Wood").getName(),
        pp.findWageByName(emp, "Wood").getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private JobEntry createApJJob36(Employee emp, TimeSheet tsheet) throws ParseException {
    // create JobEntry #36 - ORC Travel
    JobEntry.Builder jb = new JobEntry.Builder(emp);
    jb.code("ORC Travel");
    jb.date(this.createDate("07/31/2017"));
    jb.addTime(new LocalTime(18, 00), new LocalTime(18, 58));
    jb.wage(new JobWage(tsheet.getTravelWage().getName(), tsheet.getTravelWage().getRate()));
    jb.travelString("Van");
    return jb.build();
  }

  private void addKrausMichaelJobs(Employee emp, TimeSheet tsheet) throws ParseException {
    JobEntry job = this.createKMJob1(emp, tsheet);
    final EmployeeSheet es = tsheet.getEmployeeSheetFor(job.getEmployee());
    pp.addJobEntry(job);
    assertEquals(job, pp.findJobById(job.getId()));
    job = pp.findJobById(job.getId());
    assertTrue(job.hasStdDeduction());
    assertEquals(new BigDecimal("197"), pp.findJobById(job.getId()).getTotalHours());
    assertEquals(new BigDecimal("197"), pp.findJobById(job.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("19.70"), pp.findJobById(job.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), pp.findJobById(job.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job.getId()).isFlatRate());
    assertEquals(this.createDate("07/17/2017"), job.getDate());
    assertEquals("7/17/17", job.getDateString());
    assertEquals("Ride", pp.findJobById(job.getId()).getTravel());
    assertEquals("SNS Travel", pp.findJobById(job.getId()).getCode());
    assertFalse(pp.findJobById(job.getId()).isFlatRate());
    assertTrue(pp.findJobById(job.getId()).isMultiLineEntry());
    // employeeSheet field
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(1, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job, tsheet.getEmployeeSheets().get(0).getJobs().get(0));
    assertEquals(emp, job.getEmployee());
    assertEquals(new BigDecimal("10.00"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("13.45"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("19.70"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("197"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(1, tsheet.getJobs().size());
    assertEquals(job, tsheet.getJobs().get(0));
    assertEquals("Pyrl 170731", tsheet.getName());
    // create start date
    final LocalDate startDate = this.createDate("07/16/2017");
    assertEquals(startDate, tsheet.getStartDate());
    // create end date
    final LocalDate endDate = this.createDate("07/31/2017");
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("19.70"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("6.40"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("26.10"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("197"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("10.00"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("13.25"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job, pp.findJobsOn(job.getDate()).get(0));
    // job 2
    JobEntry job2 = this.createKMJob2(emp);
    pp.addJobEntry(job2);
    // check objects JobEntry,EmployeeSheet,TimeSheet are correct and findable
    // in DB
    // test job fields
    assertEquals(job2, pp.findJobById(job2.getId()));
    job2 = pp.findJobById(job2.getId());
    assertEquals(new BigDecimal("683"), pp.findJobById(job2.getId()).getTotalHours());
    assertEquals(new BigDecimal("603"), pp.findJobById(job2.getId()).getRegularTime());
    assertEquals(new BigDecimal("80"), pp.findJobById(job2.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("97.61"), pp.findJobById(job2.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("81.41"), pp.findJobById(job2.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("16.20"), pp.findJobById(job2.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job2.getId()).isFlatRate());
    assertEquals(this.createDate("07/17/2017"), job2.getDate());
    assertEquals("7/17/17", job2.getDateString());
    assertEquals("", pp.findJobById(job2.getId()).getTravel());
    assertEquals("SNS1701a", pp.findJobById(job2.getId()).getCode());
    assertFalse(pp.findJobById(job2.getId()).isFlatRate());
    assertTrue(pp.findJobById(job2.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(2, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job2, tsheet.getEmployeeSheets().get(0).getJobs().get(1));
    assertEquals(emp, job2.getEmployee());
    assertEquals(new BigDecimal("13.33"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17.93"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("117.31"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("117.31"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("117.31"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("880"), es.getTotalHours());
    assertEquals(new BigDecimal("97.61"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("81.41"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("16.20"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(2, tsheet.getJobs().size());
    assertEquals(job2, tsheet.getJobs().get(1));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("117.31"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.12"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("155.43"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("880"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.33"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17.66"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job2, pp.findJobsOn(job.getDate()).get(1));
    // create JobEntry #3 - SNS1701a
    JobEntry job3 = this.createKMJob3(emp);
    pp.addJobEntry(job3);
    assertEquals(job3, pp.findJobById(job3.getId()));
    // test job3
    job3 = pp.findJobById(job3.getId());
    assertEquals(new BigDecimal("842"), pp.findJobById(job3.getId()).getTotalHours());
    assertEquals(new BigDecimal("800"), pp.findJobById(job3.getId()).getRegularTime());
    assertEquals(new BigDecimal("42"), pp.findJobById(job3.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("116.51"), pp.findJobById(job3.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("108.00"), pp.findJobById(job3.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("8.51"), pp.findJobById(job3.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job3.getId()).isFlatRate());
    assertEquals(this.createDate("07/18/2017"), job3.getDate());
    assertEquals("7/18/17", job3.getDateString());
    assertEquals("", pp.findJobById(job3.getId()).getTravel());
    assertEquals("SNS1701a", pp.findJobById(job3.getId()).getCode());
    assertFalse(pp.findJobById(job3.getId()).isFlatRate());
    assertTrue(pp.findJobById(job3.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(3, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job3, tsheet.getEmployeeSheets().get(0).getJobs().get(2));
    assertEquals(emp, job3.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("13.58"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.26"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("233.81"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("233.81"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("233.81"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("214.11"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("189.41"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("122"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("1722"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(3, tsheet.getJobs().size());
    assertEquals(job3, tsheet.getJobs().get(2));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("233.81"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.99"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("309.80"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1722"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.58"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17.99"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job3, pp.findJobsOn(job3.getDate()).get(0));
    // create JobEntry #4 - SNS1701a
    JobEntry job4 = this.createKMJob4(emp);
    pp.addJobEntry(job4);
    assertEquals(job4, pp.findJobById(job4.getId()));
    // test job4
    job4 = pp.findJobById(job4.getId());
    assertEquals(new BigDecimal("848"), pp.findJobById(job4.getId()).getTotalHours());
    assertEquals(new BigDecimal("800"), pp.findJobById(job4.getId()).getRegularTime());
    assertEquals(new BigDecimal("48"), pp.findJobById(job4.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("117.72"), pp.findJobById(job4.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("108.00"), pp.findJobById(job4.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("9.72"), pp.findJobById(job4.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job4.getId()).isFlatRate());
    assertEquals(this.createDate("07/19/2017"), job4.getDate());
    assertEquals("7/19/17", job4.getDateString());
    assertEquals("", pp.findJobById(job4.getId()).getTravel());
    assertEquals("SNS1701a", pp.findJobById(job4.getId()).getCode());
    assertFalse(pp.findJobById(job4.getId()).isFlatRate());
    assertTrue(pp.findJobById(job4.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(4, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job4, tsheet.getEmployeeSheets().get(0).getJobs().get(3));
    assertEquals(emp, job4.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("13.68"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.40"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("351.53"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("351.53"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("351.53"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("331.83"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("297.41"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2203"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("170"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("2570"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(4, tsheet.getJobs().size());
    assertEquals(job4, tsheet.getJobs().get(3));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("351.53"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("114.25"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("465.78"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2570"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.68"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.12"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job4, pp.findJobsOn(job4.getDate()).get(0));
    // create JobEntry #5 - SNS1701a
    JobEntry job5 = this.createKMJob5(emp);
    pp.addJobEntry(job5);
    assertEquals(job5, pp.findJobById(job5.getId()));
    // test job5
    job5 = pp.findJobById(job5.getId());
    assertEquals(new BigDecimal("883"), pp.findJobById(job5.getId()).getTotalHours());
    assertEquals(new BigDecimal("800"), pp.findJobById(job5.getId()).getRegularTime());
    assertEquals(new BigDecimal("83"), pp.findJobById(job5.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("124.81"), pp.findJobById(job5.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("108.00"), pp.findJobById(job5.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("16.81"), pp.findJobById(job5.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job5.getId()).isFlatRate());
    assertEquals(this.createDate("07/20/2017"), job5.getDate());
    assertEquals("7/20/17", job5.getDateString());
    assertEquals("", pp.findJobById(job5.getId()).getTravel());
    assertEquals("SNS1701a", pp.findJobById(job5.getId()).getCode());
    assertFalse(pp.findJobById(job5.getId()).isFlatRate());
    assertTrue(pp.findJobById(job5.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(5, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job5, tsheet.getEmployeeSheets().get(0).getJobs().get(4));
    assertEquals(emp, job5.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("13.79"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.55"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("476.34"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("476.34"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("476.34"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("456.64"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("405.41"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("51.23"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3003"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("253"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("3453"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(5, tsheet.getJobs().size());
    assertEquals(job5, tsheet.getJobs().get(4));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("476.34"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("154.81"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("631.15"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3453"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.79"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.28"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job5, pp.findJobsOn(job5.getDate()).get(0));
    // create JobEntry #6 - SNS1701a
    JobEntry job6 = this.createKMJob6(emp);
    pp.addJobEntry(job6);
    assertEquals(job6, pp.findJobById(job6.getId()));
    // test job6
    job6 = pp.findJobById(job6.getId());
    assertEquals(new BigDecimal("458"), pp.findJobById(job6.getId()).getTotalHours());
    assertEquals(new BigDecimal("458"), pp.findJobById(job6.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("61.83"), pp.findJobById(job6.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("61.83"), pp.findJobById(job6.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job6.getId()).isFlatRate());
    assertEquals(this.createDate("07/21/2017"), job6.getDate());
    assertEquals("7/21/17", job6.getDateString());
    assertEquals("", pp.findJobById(job6.getId()).getTravel());
    assertEquals("SNS1701a", pp.findJobById(job6.getId()).getCode());
    assertFalse(pp.findJobById(job6.getId()).isFlatRate());
    assertFalse(pp.findJobById(job6.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(6, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job6, tsheet.getEmployeeSheets().get(0).getJobs().get(5));
    assertEquals(emp, job6.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("13.76"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.51"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("538.17"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("538.17"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("538.17"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("518.47"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("467.24"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("51.23"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3461"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("253"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("3911"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(6, tsheet.getJobs().size());
    assertEquals(job6, tsheet.getJobs().get(5));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("538.17"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("174.90"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("713.07"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3911"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.76"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.23"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job6, pp.findJobsOn(job6.getDate()).get(0));
    assertEquals(1, pp.findJobsOn(job6.getDate()).size());
    // create JobEntry #7 - SNS1701b
    JobEntry job7 = this.createKMJob7(emp);
    pp.addJobEntry(job7);
    assertEquals(job7, pp.findJobById(job7.getId()));
    // test job7
    job7 = pp.findJobById(job7.getId());
    assertEquals(new BigDecimal("383"), pp.findJobById(job7.getId()).getTotalHours());
    assertEquals(new BigDecimal("342"), pp.findJobById(job7.getId()).getRegularTime());
    assertEquals(new BigDecimal("41"), pp.findJobById(job7.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("54.47"), pp.findJobById(job7.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("46.17"), pp.findJobById(job7.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("8.30"), pp.findJobById(job7.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job7.getId()).isFlatRate());
    assertEquals(this.createDate("07/21/2017"), job7.getDate());
    assertEquals("7/21/17", job7.getDateString());
    assertEquals("", pp.findJobById(job7.getId()).getTravel());
    assertEquals("SNS1701b", pp.findJobById(job7.getId()).getCode());
    assertFalse(pp.findJobById(job7.getId()).isFlatRate());
    assertFalse(pp.findJobById(job7.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(7, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job7, tsheet.getEmployeeSheets().get(0).getJobs().get(6));
    assertEquals(emp, job7.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("13.80"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.56"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("592.64"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("592.64"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("592.64"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("518.47"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("467.24"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("51.23"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3461"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("54.47"), es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("46.17"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("8.30"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("342"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("41"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("4294"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(7, tsheet.getJobs().size());
    assertEquals(job7, tsheet.getJobs().get(6));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("592.64"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("192.61"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("785.25"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4294"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.80"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.29"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job7, pp.findJobsOn(job7.getDate()).get(1));
    assertEquals(2, pp.findJobsOn(job7.getDate()).size());
    // create JobEntry #8 - SNS1701b
    JobEntry job8 = this.createKMJob8(emp);
    pp.addJobEntry(job8);
    assertEquals(job8, pp.findJobById(job8.getId()));
    // test job8
    job8 = pp.findJobById(job8.getId());
    assertEquals(new BigDecimal("333"), pp.findJobById(job8.getId()).getTotalHours());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job8.getId()).getRegularTime());
    assertEquals(new BigDecimal("333"), pp.findJobById(job8.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job8.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job8.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job8.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job8.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job8.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("67.43"), pp.findJobById(job8.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job8.getId()).getRegularPay().setScale(0));
    assertEquals(new BigDecimal("67.43"), pp.findJobById(job8.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job8.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job8.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job8.getId()).isFlatRate());
    assertEquals(this.createDate("07/22/2017"), job8.getDate());
    assertEquals("7/22/17", job8.getDateString());
    assertEquals("", pp.findJobById(job8.getId()).getTravel());
    assertEquals("SNS1701b", pp.findJobById(job8.getId()).getCode());
    assertFalse(pp.findJobById(job8.getId()).isFlatRate());
    assertFalse(pp.findJobById(job8.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(8, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job8, tsheet.getEmployeeSheets().get(0).getJobs().get(7));
    assertEquals(emp, job8.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.27"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.19"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("660.07"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("660.07"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("660.07"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("518.47"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("467.24"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("51.23"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3461"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("253"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("121.91"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("46.17"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("342"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("374"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("4627"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(8, tsheet.getJobs().size());
    assertEquals(job8, tsheet.getJobs().get(7));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("660.07"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("214.52"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("874.60"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4627"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.27"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.90"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job8, pp.findJobsOn(job8.getDate()).get(0));
    assertEquals(1, pp.findJobsOn(job8.getDate()).size());
    // create JobEntry #9 - SNS1701a
    JobEntry job9 = this.createKMJob9(emp);
    pp.addJobEntry(job9);
    assertEquals(job9, pp.findJobById(job9.getId()));
    // test job9
    job9 = pp.findJobById(job9.getId());

    assertEquals(new BigDecimal("100"), pp.findJobById(job9.getId()).getTotalHours());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getRegularTime());
    assertEquals(new BigDecimal("100"), pp.findJobById(job9.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("20.25"), pp.findJobById(job9.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getRegularPay().setScale(0));
    assertEquals(new BigDecimal("20.25"), pp.findJobById(job9.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job9.getId()).isFlatRate());
    assertEquals(this.createDate("07/22/2017"), job9.getDate());
    assertEquals("7/22/17", job9.getDateString());
    assertEquals("", pp.findJobById(job9.getId()).getTravel());
    assertEquals("SNS1701a", pp.findJobById(job9.getId()).getCode());
    assertFalse(pp.findJobById(job9.getId()).isFlatRate());
    assertFalse(pp.findJobById(job9.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(9, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job9, tsheet.getEmployeeSheets().get(0).getJobs().get(8));
    assertEquals(emp, job9.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.39"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.36"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("680.32"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("680.32"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("680.32"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("538.72"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("467.24"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("71.48"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3461"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("353"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("121.91"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("46.17"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("342"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("374"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("4727"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(9, tsheet.getJobs().size());
    assertEquals(job9, tsheet.getJobs().get(8));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("680.32"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("221.10"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("901.43"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4727"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.39"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.07"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job9, pp.findJobsOn(job9.getDate()).get(1));
    assertEquals(2, pp.findJobsOn(job9.getDate()).size());
    // create JobEntry #10 - Travel SNS>Anx
    JobEntry job10 = this.createKMJob10(emp, tsheet);
    pp.addJobEntry(job10);
    assertEquals(job10, pp.findJobById(job10.getId()));
    // test job10
    job10 = pp.findJobById(job10.getId());

    assertEquals(new BigDecimal("258"), pp.findJobById(job10.getId()).getTotalHours());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job10.getId()).getRegularTime());
    assertEquals(new BigDecimal("258"), pp.findJobById(job10.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job10.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job10.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job10.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job10.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job10.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("38.70"), pp.findJobById(job10.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job10.getId()).getRegularPay().setScale(0));
    assertEquals(new BigDecimal("38.70"), pp.findJobById(job10.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job10.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job10.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job10.getId()).isFlatRate());
    assertEquals(this.createDate("07/22/2017"), job10.getDate());
    assertEquals("7/22/17", job10.getDateString());
    assertEquals("", pp.findJobById(job10.getId()).getTravel());
    assertEquals("Travel SNS>Anx", pp.findJobById(job10.getId()).getCode());
    assertFalse(pp.findJobById(job10.getId()).isFlatRate());
    assertTrue(pp.findJobById(job10.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(10, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job10, tsheet.getEmployeeSheets().get(0).getJobs().get(9));
    assertEquals(emp, job10.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.42"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.40"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("719.02"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("719.02"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("719.02"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("58.40"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("538.72"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("467.24"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("71.48"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3461"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("353"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("121.91"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("46.17"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("342"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("374"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("4985"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(10, tsheet.getJobs().size());
    assertEquals(job10, tsheet.getJobs().get(9));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("719.02"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("233.68"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("952.70"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4985"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.42"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.11"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job10, pp.findJobsOn(job10.getDate()).get(2));
    assertEquals(3, pp.findJobsOn(job10.getDate()).size());
    // create JobEntry #11 - SNS Unload/Van Return
    JobEntry job11 = this.createKMJob11(emp);
    pp.addJobEntry(job11);
    assertEquals(job11, pp.findJobById(job11.getId()));
    // test job11
    job11 = pp.findJobById(job11.getId());

    assertEquals(new BigDecimal("75"), pp.findJobById(job11.getId()).getTotalHours());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job11.getId()).getRegularTime());
    assertEquals(new BigDecimal("75"), pp.findJobById(job11.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job11.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job11.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job11.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job11.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job11.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("15.19"), pp.findJobById(job11.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job11.getId()).getRegularPay().setScale(0));
    assertEquals(new BigDecimal("15.19"), pp.findJobById(job11.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job11.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job11.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job11.getId()).isFlatRate());
    assertEquals(this.createDate("07/22/2017"), job11.getDate());
    assertEquals("7/22/17", job11.getDateString());
    assertEquals("Ride", pp.findJobById(job11.getId()).getTravel());
    assertEquals("SNS Unload/Van Return", pp.findJobById(job11.getId()).getCode());
    assertFalse(pp.findJobById(job11.getId()).isFlatRate());
    assertFalse(pp.findJobById(job11.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(11, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job11, tsheet.getEmployeeSheets().get(0).getJobs().get(10));
    assertEquals(emp, job11.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.51"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.52"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("734.21"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("734.21"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("734.21"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("58.40"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("553.91"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("467.24"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3461"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("121.91"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("46.17"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("342"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("374"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("5060"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job11));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(11, tsheet.getJobs().size());
    assertEquals(job11, tsheet.getJobs().get(10));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("734.21"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("238.62"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("972.83"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("5060"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.51"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.23"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job11, pp.findJobsOn(job11.getDate()).get(3));
    assertEquals(4, pp.findJobsOn(job11.getDate()).size());
    // create JobEntry #12 - DPH1704
    JobEntry job12 = this.createKMJob12(emp);
    pp.addJobEntry(job12);
    assertEquals(job12, pp.findJobById(job12.getId()));
    // test job12
    job12 = pp.findJobById(job12.getId());
    assertEquals(new BigDecimal("463"), pp.findJobById(job12.getId()).getTotalHours());
    assertEquals(new BigDecimal("463"), pp.findJobById(job12.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job12.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job12.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job12.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job12.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job12.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job12.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("62.51"), pp.findJobById(job12.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("62.51"), pp.findJobById(job12.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job12.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job12.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job12.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job12.getId()).isFlatRate());
    assertEquals(this.createDate("07/25/2017"), job12.getDate());
    assertEquals("7/25/17", job12.getDateString());
    assertEquals("", pp.findJobById(job12.getId()).getTravel());
    assertEquals("DPH1704", pp.findJobById(job12.getId()).getCode());
    assertFalse(pp.findJobById(job12.getId()).isFlatRate());
    assertTrue(pp.findJobById(job12.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(12, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(job12, tsheet.getEmployeeSheets().get(0).getJobs().get(11));
    assertEquals(emp, job12.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.43"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.40"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("796.72"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("796.72"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("796.72"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("58.40"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("616.41"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("529.74"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3924"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("121.91"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("46.17"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("342"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("374"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("5523"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job12));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(12, tsheet.getJobs().size());
    assertEquals(job12, tsheet.getJobs().get(11));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("796.72"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("258.93"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1055.65"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("5523"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.43"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.11"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job12, pp.findJobsOn(job12.getDate()).get(0));
    assertEquals(1, pp.findJobsOn(job12.getDate()).size());
    // create JobEntry #13 - HHC1705a
    JobEntry job13 = this.createKMJob13(emp);
    pp.addJobEntry(job13);
    assertEquals(job13, pp.findJobById(job13.getId()));
    // test job13
    job13 = pp.findJobById(job13.getId());
    assertEquals(new BigDecimal("33"), pp.findJobById(job13.getId()).getTotalHours());
    assertEquals(new BigDecimal("33"), pp.findJobById(job13.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job13.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job13.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job13.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job13.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job13.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job13.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("4.46"), pp.findJobById(job13.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4.46"), pp.findJobById(job13.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job13.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job13.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job13.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job13.getId()).isFlatRate());
    assertEquals(this.createDate("07/25/2017"), job13.getDate());
    assertEquals("7/25/17", job13.getDateString());
    assertEquals("", pp.findJobById(job13.getId()).getTravel());
    assertEquals("HHC1705a", pp.findJobById(job13.getId()).getCode());
    assertFalse(pp.findJobById(job13.getId()).isFlatRate());
    assertFalse(pp.findJobById(job13.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(13, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(12, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job13));
    assertEquals(job13, tsheet.getEmployeeSheets().get(0).getJobs().get(12));
    assertEquals(emp, job13.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.42"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.39"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("801.17"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("801.17"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("801.17"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("58.40"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("620.87"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("534.20"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3957"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("121.91"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("46.17"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("342"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("374"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("5556"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job13));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(13, tsheet.getJobs().size());
    assertEquals(job13, tsheet.getJobs().get(12));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("801.17"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("260.38"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1061.55"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("5556"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.42"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.11"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job13, pp.findJobsOn(job13.getDate()).get(1));
    assertEquals(2, pp.findJobsOn(job13.getDate()).size());
    // create JobEntry #14 - DPH1704
    JobEntry job14 = this.createKMJob14(emp);
    pp.addJobEntry(job14);
    assertEquals(job14, pp.findJobById(job14.getId()));
    // test job14
    job14 = pp.findJobById(job14.getId());

    assertEquals(new BigDecimal("117"), pp.findJobById(job14.getId()).getTotalHours());
    assertEquals(new BigDecimal("117"), pp.findJobById(job14.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job14.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job14.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job14.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job14.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job14.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job14.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("15.80"), pp.findJobById(job14.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("15.80"), pp.findJobById(job14.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job14.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job14.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job14.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job14.getId()).isFlatRate());
    assertEquals(this.createDate("07/26/2017"), job14.getDate());
    assertEquals("7/26/17", job14.getDateString());
    assertEquals("", pp.findJobById(job14.getId()).getTravel());
    assertEquals("DPH1704", pp.findJobById(job14.getId()).getCode());
    assertFalse(pp.findJobById(job14.getId()).isFlatRate());
    assertFalse(pp.findJobById(job14.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(14, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(13, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job14));
    assertEquals(job14, tsheet.getEmployeeSheets().get(0).getJobs().get(13));
    assertEquals(emp, job14.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.40"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.37"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("816.97"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("816.97"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("816.97"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("58.40"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("636.66"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("549.99"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4074"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("121.91"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("46.17"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("342"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("374"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("5673"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job14));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(14, tsheet.getJobs().size());
    assertEquals(job14, tsheet.getJobs().get(13));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("816.97"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("265.51"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1082.48"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("5673"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.40"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.08"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job14, pp.findJobsOn(job14.getDate()).get(0));
    assertEquals(1, pp.findJobsOn(job14.getDate()).size());
    // create JobEntry #15 - HHC1705b
    JobEntry job15 = this.createKMJob15(emp);
    pp.addJobEntry(job15);
    assertEquals(job15, pp.findJobById(job15.getId()));
    // test job15
    job15 = pp.findJobById(job15.getId());
    assertEquals(new BigDecimal("412"), pp.findJobById(job15.getId()).getTotalHours());
    assertEquals(new BigDecimal("412"), pp.findJobById(job15.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job15.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job15.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job15.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job15.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job15.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job15.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("55.62"), pp.findJobById(job15.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("55.62"), pp.findJobById(job15.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job15.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job15.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job15.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job15.getId()).isFlatRate());
    assertEquals(this.createDate("07/26/2017"), job15.getDate());
    assertEquals("7/26/17", job15.getDateString());
    assertEquals("", pp.findJobById(job15.getId()).getTravel());
    assertEquals("HHC1705b", pp.findJobById(job15.getId()).getCode());
    assertFalse(pp.findJobById(job15.getId()).isFlatRate());
    assertFalse(pp.findJobById(job15.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(15, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(14, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job15));
    assertEquals(job15, tsheet.getEmployeeSheets().get(0).getJobs().get(14));
    assertEquals(emp, job15.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.34"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.29"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("872.59"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("872.59"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("872.59"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("58.40"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("121.91"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("46.17"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("342"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("374"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("6085"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job15));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(15, tsheet.getJobs().size());
    assertEquals(job15, tsheet.getJobs().get(14));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("872.59"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("283.59"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1156.18"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("6085"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.34"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.00"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job15, pp.findJobsOn(job15.getDate()).get(1));
    assertEquals(2, pp.findJobsOn(job15.getDate()).size());
    // create JobEntry #16 - ORC Travel
    JobEntry job16 = this.createKMJob16(emp, tsheet);
    pp.addJobEntry(job16);
    assertEquals(job16, pp.findJobById(job16.getId()));
    // test job16
    job16 = pp.findJobById(job16.getId());
    assertTrue(job16.hasStdDeduction());
    assertEquals(new BigDecimal("68"), pp.findJobById(job16.getId()).getTotalHours());
    assertEquals(new BigDecimal("68"), pp.findJobById(job16.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job16.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job16.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job16.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job16.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job16.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job16.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("6.80"), pp.findJobById(job16.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("6.80"), pp.findJobById(job16.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job16.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job16.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job16.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job16.getId()).isFlatRate());
    assertEquals(this.createDate("07/29/2017"), job16.getDate());
    assertEquals("7/29/17", job16.getDateString());
    assertEquals("Ride", pp.findJobById(job16.getId()).getTravel());
    assertEquals("ORC Travel", pp.findJobById(job16.getId()).getCode());
    assertFalse(pp.findJobById(job16.getId()).isFlatRate());
    assertFalse(pp.findJobById(job16.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(16, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(15, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job16));
    assertEquals(job16, tsheet.getEmployeeSheets().get(0).getJobs().get(15));
    assertEquals(emp, job16.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.29"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.22"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("879.39"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("879.39"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("879.39"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("65.20"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("26.50"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("265"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("258"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("121.91"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("46.17"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("342"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("374"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("6153"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job16));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(16, tsheet.getJobs().size());
    assertEquals(job16, tsheet.getJobs().get(15));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("879.39"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("285.80"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1165.19"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("6153"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.29"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.94"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job16, pp.findJobsOn(job16.getDate()).get(0));
    assertEquals(1, pp.findJobsOn(job16.getDate()).size());
    // create JobEntry #17 - ORC1701
    JobEntry job17 = this.createKMJob17(emp);
    pp.addJobEntry(job17);
    assertEquals(job17, pp.findJobById(job17.getId()));
    // test job17
    job17 = pp.findJobById(job17.getId());

    assertEquals(new BigDecimal("608"), pp.findJobById(job17.getId()).getTotalHours());
    assertEquals(new BigDecimal("608"), pp.findJobById(job17.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job17.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job17.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job17.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job17.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job17.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job17.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("82.08"), pp.findJobById(job17.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("82.08"), pp.findJobById(job17.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job17.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job17.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job17.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job17.getId()).isFlatRate());
    assertEquals(this.createDate("07/29/2017"), job17.getDate());
    assertEquals("7/29/17", job17.getDateString());
    assertEquals("", pp.findJobById(job17.getId()).getTravel());
    assertEquals("ORC1701", pp.findJobById(job17.getId()).getCode());
    assertFalse(pp.findJobById(job17.getId()).isFlatRate());
    assertTrue(pp.findJobById(job17.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(17, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(16, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job17));
    assertEquals(job17, tsheet.getEmployeeSheets().get(0).getJobs().get(16));
    assertEquals(emp, job17.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.22"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.13"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("961.47"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("961.47"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("961.47"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("65.20"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("26.50"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("265"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("258"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("203.99"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("128.25"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("950"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("374"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("6761"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job17));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(17, tsheet.getJobs().size());
    assertEquals(job17, tsheet.getJobs().get(16));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("961.47"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("312.48"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1273.94"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("6761"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.22"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.84"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job17, pp.findJobsOn(job17.getDate()).get(1));
    assertEquals(2, pp.findJobsOn(job17.getDate()).size());
    // create JobEntry #18 - ORC1702 incl T
    JobEntry job18 = this.createKMJob18(emp);
    pp.addJobEntry(job18);
    assertEquals(job18, pp.findJobById(job18.getId()));
    // test job18
    job18 = pp.findJobById(job18.getId());
    assertEquals(new BigDecimal("392"), pp.findJobById(job18.getId()).getTotalHours());
    assertEquals(new BigDecimal("124"), pp.findJobById(job18.getId()).getRegularTime());
    assertEquals(new BigDecimal("268"), pp.findJobById(job18.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job18.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job18.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job18.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job18.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job18.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("71.01"), pp.findJobById(job18.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("16.74"), pp.findJobById(job18.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("54.27"), pp.findJobById(job18.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job18.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job18.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job18.getId()).isFlatRate());
    assertEquals(this.createDate("07/29/2017"), job18.getDate());
    assertEquals("7/29/17", job18.getDateString());
    assertEquals("Ride", pp.findJobById(job18.getId()).getTravel());
    assertEquals("ORC1702 incl T", pp.findJobById(job18.getId()).getCode());
    assertFalse(pp.findJobById(job18.getId()).isFlatRate());
    assertFalse(pp.findJobById(job18.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(18, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(17, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job18));
    assertEquals(job18, tsheet.getEmployeeSheets().get(0).getJobs().get(17));
    assertEquals(emp, job18.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.43"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.41"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1032.48"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1032.48"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1032.48"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("65.20"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("26.50"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("265"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("258"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("275.00"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("144.99"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("130.01"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1074"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("642"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("7153"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job18));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(18, tsheet.getJobs().size());
    assertEquals(job18, tsheet.getJobs().get(17));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1032.48"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("335.55"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1368.03"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("7153"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.43"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.13"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job18, pp.findJobsOn(job18.getDate()).get(2));
    assertEquals(3, pp.findJobsOn(job18.getDate()).size());
    // create JobEntry #19 - ORC1703 incl T
    JobEntry job19 = this.createKMJob19(emp);
    pp.addJobEntry(job19);
    assertEquals(job19, pp.findJobById(job19.getId()));
    // test job19
    job19 = pp.findJobById(job19.getId());
    assertEquals(new BigDecimal("258"), pp.findJobById(job19.getId()).getTotalHours());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job19.getId()).getRegularTime());
    assertEquals(new BigDecimal("132"), pp.findJobById(job19.getId()).getOverTime());
    assertEquals(new BigDecimal("126"), pp.findJobById(job19.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job19.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job19.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job19.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job19.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("60.75"), pp.findJobById(job19.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job19.getId()).getRegularPay().setScale(0));
    assertEquals(new BigDecimal("26.73"), pp.findJobById(job19.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), pp.findJobById(job19.getId()).getDoublePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job19.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job19.getId()).isFlatRate());
    assertEquals(this.createDate("07/29/2017"), job19.getDate());
    assertEquals("7/29/17", job19.getDateString());
    assertEquals("Ride", pp.findJobById(job19.getId()).getTravel());
    assertEquals("ORC1703 incl T", pp.findJobById(job19.getId()).getCode());
    assertFalse(pp.findJobById(job19.getId()).isFlatRate());
    assertFalse(pp.findJobById(job19.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(19, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(18, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job19));
    assertEquals(job19, tsheet.getEmployeeSheets().get(0).getJobs().get(18));
    assertEquals(emp, job19.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.75"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.84"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1093.23"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1093.23"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1093.23"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("65.20"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("26.50"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("265"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("258"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("335.75"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("144.99"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1074"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("7411"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job19));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(19, tsheet.getJobs().size());
    assertEquals(job19, tsheet.getJobs().get(18));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1093.23"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("355.30"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1448.52"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("7411"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.75"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.55"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job19, pp.findJobsOn(job19.getDate()).get(3));
    assertEquals(4, pp.findJobsOn(job19.getDate()).size());
    // create JobEntry #20 - ORC Travel
    JobEntry job20 = this.createKMJob20(emp, tsheet);
    pp.addJobEntry(job20);
    assertEquals(job20, pp.findJobById(job20.getId()));
    // test job20
    job20 = pp.findJobById(job20.getId());
    assertTrue(job20.hasStdDeduction());

    assertEquals(new BigDecimal("52"), pp.findJobById(job20.getId()).getTotalHours());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job20.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job20.getId()).getOverTime());
    assertEquals(new BigDecimal("52"), pp.findJobById(job20.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job20.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job20.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job20.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job20.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("10.40"), pp.findJobById(job20.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job20.getId()).getRegularPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job20.getId()).getOverPay().setScale(0));
    assertEquals(new BigDecimal("10.40"), pp.findJobById(job20.getId()).getDoublePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job20.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job20.getId()).isFlatRate());
    assertEquals(this.createDate("07/29/2017"), job20.getDate());
    assertEquals("7/29/17", job20.getDateString());
    assertEquals("Ride", pp.findJobById(job20.getId()).getTravel());
    assertEquals("ORC Travel", pp.findJobById(job20.getId()).getCode());
    assertFalse(pp.findJobById(job20.getId()).isFlatRate());
    assertFalse(pp.findJobById(job20.getId()).isMultiLineEntry());
    // employeeSheet fields

    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(20, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(19, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job20));
    assertEquals(job20, tsheet.getEmployeeSheets().get(0).getJobs().get(19));
    assertEquals(emp, job20.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.79"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.89"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1103.63"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1103.63"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1103.63"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("75.60"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("26.50"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("265"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("258"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("335.75"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("144.99"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1074"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("7463"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job20));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(20, tsheet.getJobs().size());
    assertEquals(job20, tsheet.getJobs().get(19));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1103.63"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("358.68"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1462.30"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("7463"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.79"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.59"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job20, pp.findJobsOn(job20.getDate()).get(4));
    assertEquals(5, pp.findJobsOn(job20.getDate()).size());
    // create JobEntry #21 - ORC Travel
    JobEntry job21 = this.createKMJob21(emp, tsheet);
    pp.addJobEntry(job21);
    assertEquals(job21, pp.findJobById(job21.getId()));
    // test job21
    job21 = pp.findJobById(job21.getId());
    assertTrue(job21.hasStdDeduction());
    assertEquals(new BigDecimal("52"), pp.findJobById(job21.getId()).getTotalHours());
    assertEquals(new BigDecimal("52"), pp.findJobById(job21.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job21.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job21.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job21.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job21.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job21.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job21.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("5.20"), pp.findJobById(job21.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("5.20"), pp.findJobById(job21.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job21.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job21.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job21.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job21.getId()).isFlatRate());
    assertEquals(this.createDate("07/30/2017"), job21.getDate());
    assertEquals("7/30/17", job21.getDateString());
    assertEquals("Ride", pp.findJobById(job21.getId()).getTravel());
    assertEquals("ORC Travel", pp.findJobById(job21.getId()).getCode());
    assertFalse(pp.findJobById(job21.getId()).isFlatRate());
    assertFalse(pp.findJobById(job21.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(21, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(20, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job21));
    assertEquals(job21, tsheet.getEmployeeSheets().get(0).getJobs().get(20));
    assertEquals(emp, job21.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.75"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.85"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1108.83"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1108.83"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1108.83"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("80.80"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("31.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("317"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("258"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("335.75"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("144.99"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1074"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("7515"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job21));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(21, tsheet.getJobs().size());
    assertEquals(job21, tsheet.getJobs().get(20));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1108.83"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("360.37"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1469.19"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("7515"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.75"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.55"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job21, pp.findJobsOn(job21.getDate()).get(0));
    assertEquals(1, pp.findJobsOn(job21.getDate()).size());
    // create JobEntry #22 - ORC1701 incl T
    JobEntry job22 = this.createKMJob22(emp);
    pp.addJobEntry(job22);
    assertEquals(job22, pp.findJobById(job22.getId()));
    // test job22
    job22 = pp.findJobById(job22.getId());
    assertEquals(new BigDecimal("298"), pp.findJobById(job22.getId()).getTotalHours());
    assertEquals(new BigDecimal("298"), pp.findJobById(job22.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job22.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job22.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job22.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job22.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job22.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job22.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("40.23"), pp.findJobById(job22.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("40.23"), pp.findJobById(job22.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job22.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job22.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job22.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job22.getId()).isFlatRate());
    assertEquals(this.createDate("07/30/2017"), job22.getDate());
    assertEquals("7/30/17", job22.getDateString());
    assertEquals("Ride", pp.findJobById(job22.getId()).getTravel());
    assertEquals("ORC1701 incl T", pp.findJobById(job22.getId()).getCode());
    assertFalse(pp.findJobById(job22.getId()).isFlatRate());
    assertTrue(pp.findJobById(job22.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(22, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(21, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job22));
    assertEquals(job22, tsheet.getEmployeeSheets().get(0).getJobs().get(21));
    assertEquals(emp, job22.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.71"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.78"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1149.06"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1149.06"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1149.06"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("80.80"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("31.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("317"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("258"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("375.98"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("185.22"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1372"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("7813"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job22));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(22, tsheet.getJobs().size());
    assertEquals(job22, tsheet.getJobs().get(21));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1149.06"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("373.44"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1522.50"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("7813"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.71"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.49"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job22, pp.findJobsOn(job22.getDate()).get(1));
    assertEquals(2, pp.findJobsOn(job22.getDate()).size());
    // create JobEntry #23 - ORC1702 incl T
    JobEntry job23 = this.createKMJob23(emp);
    pp.addJobEntry(job23);
    assertEquals(job23, pp.findJobById(job23.getId()));
    // test job23
    job23 = pp.findJobById(job23.getId());
    assertEquals(new BigDecimal("217"), pp.findJobById(job23.getId()).getTotalHours());
    assertEquals(new BigDecimal("217"), pp.findJobById(job23.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job23.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job23.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job23.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job23.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job23.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job23.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("29.30"), pp.findJobById(job23.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("29.30"), pp.findJobById(job23.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job23.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job23.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job23.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job23.getId()).isFlatRate());
    assertEquals(this.createDate("07/30/2017"), job23.getDate());
    assertEquals("7/30/17", job23.getDateString());
    assertEquals("Ride", pp.findJobById(job23.getId()).getTravel());
    assertEquals("ORC1702 incl T", pp.findJobById(job23.getId()).getCode());
    assertFalse(pp.findJobById(job23.getId()).isFlatRate());
    assertFalse(pp.findJobById(job23.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(23, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(22, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job23));
    assertEquals(job23, tsheet.getEmployeeSheets().get(0).getJobs().get(22));
    assertEquals(emp, job23.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.67"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.74"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1178.35"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1178.35"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1178.35"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("80.80"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("31.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("317"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("258"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("405.27"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("214.52"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1589"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("8030"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job23));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(23, tsheet.getJobs().size());
    assertEquals(job23, tsheet.getJobs().get(22));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1178.35"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("382.96"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1561.31"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("8030"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.67"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.44"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job23, pp.findJobsOn(job23.getDate()).get(2));
    assertEquals(3, pp.findJobsOn(job23.getDate()).size());
    // create JobEntry #24 - Travel LV>LA incl L
    JobEntry job24 = this.createKMJob24(emp);
    pp.addJobEntry(job24);
    assertEquals(job24, pp.findJobById(job24.getId()));
    // test job24
    job24 = pp.findJobById(job24.getId());
    assertEquals(new BigDecimal("38"), pp.findJobById(job24.getId()).getTotalHours());
    assertEquals(new BigDecimal("38"), pp.findJobById(job24.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job24.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job24.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job24.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job24.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job24.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job24.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("5.13"), pp.findJobById(job24.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("5.13"), pp.findJobById(job24.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job24.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job24.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job24.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job24.getId()).isFlatRate());
    assertEquals(this.createDate("07/30/2017"), job24.getDate());
    assertEquals("7/30/17", job24.getDateString());
    assertEquals("Ride", pp.findJobById(job24.getId()).getTravel());
    assertEquals("Travel LV>LA incl L", pp.findJobById(job24.getId()).getCode());
    assertFalse(pp.findJobById(job24.getId()).isFlatRate());
    assertTrue(pp.findJobById(job24.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(24, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(23, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job24));
    assertEquals(job24, tsheet.getEmployeeSheets().get(0).getJobs().get(23));
    assertEquals(emp, job24.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.67"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.73"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1183.48"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1183.48"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1183.48"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("80.80"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("31.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("317"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("258"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("410.40"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("219.65"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1627"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("8068"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job24));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(24, tsheet.getJobs().size());
    assertEquals(job24, tsheet.getJobs().get(23));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1183.48"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("384.63"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1568.11"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("8068"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.67"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.44"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job24, pp.findJobsOn(job24.getDate()).get(3));
    assertEquals(4, pp.findJobsOn(job24.getDate()).size());
    // create JobEntry #25 - ORC1703
    JobEntry job25 = this.createKMJob25(emp);
    pp.addJobEntry(job25);
    assertEquals(job25, pp.findJobById(job25.getId()));
    // test job25
    job25 = pp.findJobById(job25.getId());
    assertEquals(new BigDecimal("190"), pp.findJobById(job25.getId()).getTotalHours());
    assertEquals(new BigDecimal("190"), pp.findJobById(job25.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job25.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job25.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job25.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job25.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job25.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job25.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("25.65"), pp.findJobById(job25.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("25.65"), pp.findJobById(job25.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job25.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job25.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job25.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job25.getId()).isFlatRate());
    assertEquals(this.createDate("07/30/2017"), job25.getDate());
    assertEquals("7/30/17", job25.getDateString());
    assertEquals("", pp.findJobById(job25.getId()).getTravel());
    assertEquals("ORC1703", pp.findJobById(job25.getId()).getCode());
    assertFalse(pp.findJobById(job25.getId()).isFlatRate());
    assertFalse(pp.findJobById(job25.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(25, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(24, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job25));
    assertEquals(job25, tsheet.getEmployeeSheets().get(0).getJobs().get(24));
    assertEquals(emp, job25.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.64"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.69"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1209.13"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1209.13"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1209.13"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("80.80"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("31.70"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("38.70"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("317"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("258"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("436.05"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("245.30"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1817"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("8258"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job25));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(25, tsheet.getJobs().size());
    assertEquals(job25, tsheet.getJobs().get(24));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1209.13"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("392.97"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1602.10"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("8258"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.64"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.40"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job25, pp.findJobsOn(job25.getDate()).get(4));
    assertEquals(5, pp.findJobsOn(job25.getDate()).size());
    // create JobEntry #26 - ORC Travel
    JobEntry job26 = this.createKMJob26(emp, tsheet);
    pp.addJobEntry(job26);
    assertEquals(job26, pp.findJobById(job26.getId()));
    // test job26
    job26 = pp.findJobById(job26.getId());
    assertTrue(job26.hasStdDeduction());

    assertEquals(new BigDecimal("52"), pp.findJobById(job26.getId()).getTotalHours());
    assertEquals(new BigDecimal("5"), pp.findJobById(job26.getId()).getRegularTime());
    assertEquals(new BigDecimal("47"), pp.findJobById(job26.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job26.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job26.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job26.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job26.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job26.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("7.55"), pp.findJobById(job26.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("0.50"), pp.findJobById(job26.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("7.05"), pp.findJobById(job26.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job26.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job26.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job26.getId()).isFlatRate());
    assertEquals(this.createDate("07/30/2017"), job26.getDate());
    assertEquals("7/30/17", job26.getDateString());
    assertEquals("Ride", pp.findJobById(job26.getId()).getTravel());
    assertEquals("ORC Travel", pp.findJobById(job26.getId()).getCode());
    assertFalse(pp.findJobById(job26.getId()).isFlatRate());
    assertFalse(pp.findJobById(job26.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(26, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(25, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job26));
    assertEquals(job26, tsheet.getEmployeeSheets().get(0).getJobs().get(25));
    assertEquals(emp, job26.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.64"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.69"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1216.68"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1216.68"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1216.68"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("88.35"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("32.20"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("45.75"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("679"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("322"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("305"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4914"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("436.05"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("245.30"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2717"), es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("1817"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("8310"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job26));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(26, tsheet.getJobs().size());
    assertEquals(job26, tsheet.getJobs().get(25));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1216.68"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("395.42"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1612.10"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("8310"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.64"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.40"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job26, pp.findJobsOn(job26.getDate()).get(5));
    assertEquals(6, pp.findJobsOn(job26.getDate()).size());
    // create JobEntry #27 - ORC Travel
    JobEntry job27 = this.createKMJob27(emp, tsheet);
    pp.addJobEntry(job27);
    assertEquals(job27, pp.findJobById(job27.getId()));
    // test job27
    job27 = pp.findJobById(job27.getId());
    assertTrue(job27.hasStdDeduction());
    assertEquals(new BigDecimal("57"), pp.findJobById(job27.getId()).getTotalHours());
    assertEquals(new BigDecimal("57"), pp.findJobById(job27.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job27.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job27.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job27.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job27.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job27.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job27.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("5.70"), pp.findJobById(job27.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("5.70"), pp.findJobById(job27.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job27.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job27.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job27.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job27.getId()).isFlatRate());
    assertEquals(this.createDate("07/31/2017"), job27.getDate());
    assertEquals("7/31/17", job27.getDateString());
    assertEquals("Ride", pp.findJobById(job27.getId()).getTravel());
    assertEquals("ORC Travel", pp.findJobById(job27.getId()).getCode());
    assertFalse(pp.findJobById(job27.getId()).isFlatRate());
    assertFalse(pp.findJobById(job27.getId()).isMultiLineEntry());
    // employeeSheet fields

    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(27, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(26, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job27));
    assertEquals(job27, tsheet.getEmployeeSheets().get(0).getJobs().get(26));
    assertEquals(emp, job27.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.61"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.65"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1222.38"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1222.38"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1222.38"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("94.05"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("37.90"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("45.75"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("736"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("379"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("305"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4914"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("436.05"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("245.30"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2717"), es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("1817"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("8367"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job27));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(27, tsheet.getJobs().size());
    assertEquals(job27, tsheet.getJobs().get(26));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1222.38"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("397.27"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1619.65"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("8367"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.61"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.36"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job27, pp.findJobsOn(job27.getDate()).get(0));
    assertEquals(1, pp.findJobsOn(job27.getDate()).size());
    // create JobEntry #28 - ORC1701 incl T
    JobEntry job28 = this.createKMJob28(emp);
    pp.addJobEntry(job28);
    assertEquals(job28, pp.findJobById(job28.getId()));
    // test job28
    job28 = pp.findJobById(job28.getId());
    assertEquals(new BigDecimal("307"), pp.findJobById(job28.getId()).getTotalHours());
    assertEquals(new BigDecimal("307"), pp.findJobById(job28.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job28.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job28.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job28.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job28.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job28.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job28.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("41.45"), pp.findJobById(job28.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("41.45"), pp.findJobById(job28.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job28.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job28.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job28.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job28.getId()).isFlatRate());
    assertEquals(this.createDate("07/31/2017"), job28.getDate());
    assertEquals("7/31/17", job28.getDateString());
    assertEquals("Ride", pp.findJobById(job28.getId()).getTravel());
    assertEquals("ORC1701 incl T", pp.findJobById(job28.getId()).getCode());
    assertFalse(pp.findJobById(job28.getId()).isFlatRate());
    assertFalse(pp.findJobById(job28.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(28, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(27, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job28));
    assertEquals(job28, tsheet.getEmployeeSheets().get(0).getJobs().get(27));
    assertEquals(emp, job28.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.57"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.60"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1263.83"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1263.83"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1263.83"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("94.05"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("37.90"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("45.75"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("736"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("379"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("305"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4914"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("477.50"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("286.74"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3024"), es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("2124"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("8674"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job28));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(28, tsheet.getJobs().size());
    assertEquals(job28, tsheet.getJobs().get(27));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1263.83"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("410.74"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1674.57"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("8674"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.57"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.31"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job28, pp.findJobsOn(job28.getDate()).get(1));
    assertEquals(2, pp.findJobsOn(job28.getDate()).size());
    // create JobEntry #29 - Travel LV>OR incl L
    JobEntry job29 = this.createKMJob29(emp);
    pp.addJobEntry(job29);
    assertEquals(job29, pp.findJobById(job29.getId()));
    // test job29
    job29 = pp.findJobById(job29.getId());
    assertEquals(new BigDecimal("28"), pp.findJobById(job29.getId()).getTotalHours());
    assertEquals(new BigDecimal("28"), pp.findJobById(job29.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job29.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job29.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job29.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job29.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job29.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job29.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("3.78"), pp.findJobById(job29.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3.78"), pp.findJobById(job29.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job29.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job29.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job29.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job29.getId()).isFlatRate());
    assertEquals(this.createDate("07/31/2017"), job29.getDate());
    assertEquals("7/31/17", job29.getDateString());
    assertEquals("Ride", pp.findJobById(job29.getId()).getTravel());
    assertEquals("Travel LV>OR incl L", pp.findJobById(job29.getId()).getCode());
    assertFalse(pp.findJobById(job29.getId()).isFlatRate());
    assertTrue(pp.findJobById(job29.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(29, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(28, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job29));
    assertEquals(job29, tsheet.getEmployeeSheets().get(0).getJobs().get(28));
    assertEquals(emp, job29.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.57"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.59"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1267.61"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1267.61"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1267.61"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("94.05"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("37.90"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("45.75"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("736"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("379"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("305"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4914"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("481.28"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("290.52"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3052"), es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("2152"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("8702"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job29));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(29, tsheet.getJobs().size());
    assertEquals(job29, tsheet.getJobs().get(28));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1267.61"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("411.97"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1679.58"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("8702"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.57"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.30"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job29, pp.findJobsOn(job29.getDate()).get(2));
    assertEquals(3, pp.findJobsOn(job29.getDate()).size());
    // create JobEntry #30 - ORC1702
    JobEntry job30 = this.createKMJob30(emp);
    pp.addJobEntry(job30);
    assertEquals(job30, pp.findJobById(job30.getId()));
    // test job30
    job30 = pp.findJobById(job30.getId());
    assertEquals(new BigDecimal("250"), pp.findJobById(job30.getId()).getTotalHours());
    assertEquals(new BigDecimal("250"), pp.findJobById(job30.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job30.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job30.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job30.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job30.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job30.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job30.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("33.75"), pp.findJobById(job30.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.75"), pp.findJobById(job30.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job30.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job30.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job30.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job30.getId()).isFlatRate());
    assertEquals(this.createDate("07/31/2017"), job30.getDate());
    assertEquals("7/31/17", job30.getDateString());
    assertEquals("", pp.findJobById(job30.getId()).getTravel());
    assertEquals("ORC1702", pp.findJobById(job30.getId()).getCode());
    assertFalse(pp.findJobById(job30.getId()).isFlatRate());
    assertFalse(pp.findJobById(job30.getId()).isMultiLineEntry());
    // employeeSheet fields

    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(30, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(29, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job30));
    assertEquals(job30, tsheet.getEmployeeSheets().get(0).getJobs().get(29));
    assertEquals(emp, job30.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.54"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.55"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1301.36"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1301.36"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1301.36"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("94.05"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("37.90"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("45.75"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("736"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("379"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("305"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4914"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("515.03"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("324.27"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("156.74"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3302"), es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("2402"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("774"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("8952"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job30));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(30, tsheet.getJobs().size());
    assertEquals(job30, tsheet.getJobs().get(29));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1301.36"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("422.94"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1724.30"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("8952"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.54"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.26"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job30, pp.findJobsOn(job30.getDate()).get(3));
    assertEquals(4, pp.findJobsOn(job30.getDate()).size());
    // create JobEntry #31 - ORC1703 incl T
    JobEntry job31 = this.createKMJob31(emp);
    pp.addJobEntry(job31);
    assertEquals(job31, pp.findJobById(job31.getId()));
    // test job31
    job31 = pp.findJobById(job31.getId());
    assertEquals(new BigDecimal("233"), pp.findJobById(job31.getId()).getTotalHours());
    assertEquals(new BigDecimal("158"), pp.findJobById(job31.getId()).getRegularTime());
    assertEquals(new BigDecimal("75"), pp.findJobById(job31.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job31.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job31.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job31.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job31.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job31.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("36.52"), pp.findJobById(job31.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("21.33"), pp.findJobById(job31.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("15.19"), pp.findJobById(job31.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job31.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job31.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job31.getId()).isFlatRate());
    assertEquals(this.createDate("07/31/2017"), job31.getDate());
    assertEquals("7/31/17", job31.getDateString());
    assertEquals("Ride", pp.findJobById(job31.getId()).getTravel());
    assertEquals("ORC1703 incl T", pp.findJobById(job31.getId()).getCode());
    assertFalse(pp.findJobById(job31.getId()).isFlatRate());
    assertFalse(pp.findJobById(job31.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(31, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(30, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job31));
    assertEquals(job31, tsheet.getEmployeeSheets().get(0).getJobs().get(30));
    assertEquals(emp, job31.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.57"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.59"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1337.87"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1337.87"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1337.87"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("94.05"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("37.90"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("45.75"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("736"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("379"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("305"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4914"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("551.54"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("345.60"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("171.92"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3535"), es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("2560"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("849"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("9185"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job31));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(31, tsheet.getJobs().size());
    assertEquals(job31, tsheet.getJobs().get(30));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1337.87"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("434.81"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1772.68"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("9185"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.57"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.30"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job31, pp.findJobsOn(job31.getDate()).get(4));
    assertEquals(5, pp.findJobsOn(job31.getDate()).size());
    // create JobEntry #32 - ORC Travel
    JobEntry job32 = this.createKMJob32(emp, tsheet);
    pp.addJobEntry(job32);
    assertEquals(job32, pp.findJobById(job32.getId()));
    // test job32
    job32 = pp.findJobById(job32.getId());

    assertTrue(job32.hasStdDeduction());
    assertEquals(new BigDecimal("25"), pp.findJobById(job32.getId()).getTotalHours());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job32.getId()).getRegularTime());
    assertEquals(new BigDecimal("25"), pp.findJobById(job32.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job32.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job32.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job32.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job32.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job32.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("3.75"), pp.findJobById(job32.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job32.getId()).getRegularPay().setScale(0));
    assertEquals(new BigDecimal("3.75"), pp.findJobById(job32.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job32.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job32.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job32.getId()).isFlatRate());
    assertEquals(this.createDate("07/31/2017"), job32.getDate());
    assertEquals("7/31/17", job32.getDateString());
    assertEquals("Ride", pp.findJobById(job32.getId()).getTravel());
    assertEquals("ORC Travel", pp.findJobById(job32.getId()).getCode());
    assertFalse(pp.findJobById(job32.getId()).isFlatRate());
    assertFalse(pp.findJobById(job32.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(1, tsheet.getEmployeeSheets().size());
    assertEquals(32, tsheet.getEmployeeSheets().get(0).getJobs().size());
    assertEquals(31, tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job32));
    assertEquals(job32, tsheet.getEmployeeSheets().get(0).getJobs().get(31));
    assertEquals(emp, job32.getEmployee());
    assertEquals(new BigDecimal("14.57"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.59"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1341.62"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("1341.62"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1341.62"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("97.80"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("37.90"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("49.50"), es.getOverPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.40"), es.getDoublePayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("761"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("379"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("330"), es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("52"), es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("692.28"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("605.61"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("86.67"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4914"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("4486"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("428"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("551.54"), es.getTotalPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("345.60"), es.getRegularPayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("171.92"), es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("34.02"), es.getDoublePayOn(pp.findWageByName(emp, "Wood"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3535"), es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("2560"), es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("849"), es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("126"), es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("9210"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job32));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(32, tsheet.getJobs().size());
    assertEquals(job32, tsheet.getJobs().get(31));
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(startDate, tsheet.getStartDate());
    assertEquals(endDate, tsheet.getEndDate());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1341.62"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("436.03"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1777.65"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("9210"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.57"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.30"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(job32, pp.findJobsOn(job32.getDate()).get(5));
    assertEquals(6, pp.findJobsOn(job32.getDate()).size());
  }

  // @Test
  // public void testAddPyrl17031KMsheetOutOfOrder() throws ParseException {
  // TimeSheet tsheet = this.create170731TimeSheet();
  // Employee emp = pp.findEmployeeByAbbreviation("KM");
  // JobEntry job32 = this.createKMJob32(emp, tsheet);
  // pp.addJobEntry(job32);
  // pp.addJobEntry(this.createKMJob2(emp, tsheet));
  // pp.addJobEntry(this.createKMJob23(emp, tsheet));
  // pp.addJobEntry(this.createKMJob4(emp, tsheet));
  // pp.addJobEntry(this.createKMJob28(emp, tsheet));
  // pp.addJobEntry(this.createKMJob13(emp, tsheet));
  // pp.addJobEntry(this.createKMJob1(emp, tsheet));
  // pp.addJobEntry(this.createKMJob9(emp, tsheet));
  // pp.addJobEntry(this.createKMJob5(emp, tsheet));
  // pp.addJobEntry(this.createKMJob8(emp, tsheet));
  // pp.addJobEntry(this.createKMJob18(emp, tsheet));
  // pp.addJobEntry(this.createKMJob3(emp, tsheet));
  // pp.addJobEntry(this.createKMJob10(emp, tsheet));
  // pp.addJobEntry(this.createKMJob22(emp, tsheet));
  // pp.addJobEntry(this.createKMJob12(emp, tsheet));
  // pp.addJobEntry(this.createKMJob17(emp, tsheet));
  // pp.addJobEntry(this.createKMJob25(emp, tsheet));
  // pp.addJobEntry(this.createKMJob11(emp, tsheet));
  // pp.addJobEntry(this.createKMJob14(emp, tsheet));
  // pp.addJobEntry(this.createKMJob16(emp, tsheet));
  // pp.addJobEntry(this.createKMJob19(emp, tsheet));
  // pp.addJobEntry(this.createKMJob20(emp, tsheet));
  // pp.addJobEntry(this.createKMJob30(emp, tsheet));
  // pp.addJobEntry(this.createKMJob15(emp, tsheet));
  // pp.addJobEntry(this.createKMJob21(emp, tsheet));
  // pp.addJobEntry(this.createKMJob24(emp, tsheet));
  // pp.addJobEntry(this.createKMJob29(emp, tsheet));
  // pp.addJobEntry(this.createKMJob26(emp, tsheet));
  // pp.addJobEntry(this.createKMJob6(emp, tsheet));
  // pp.addJobEntry(this.createKMJob27(emp, tsheet));
  // pp.addJobEntry(this.createKMJob31(emp, tsheet));
  // pp.addJobEntry(this.createKMJob7(emp, tsheet));
  // // test job32
  // job32 = pp.findJobById(job32.getId());
  // assertEquals(new Time(LocalTime(18, 00), job32.getTimesIn().get(0));
  // assertEquals(new Time(LocalTime(18, 15), job32.getTimesOut().get(0));
  // assertEquals(new BigDecimal("25"),
  // pp.findJobById(job32.getId()).getTotalHours());
  // assertEquals(BigDecimal.ZERO,
  // pp.findJobById(job32.getId()).getRegularTime());
  // assertEquals(new BigDecimal("25"),
  // pp.findJobById(job32.getId()).getOverTime());
  // assertEquals(BigDecimal.ZERO,
  // pp.findJobById(job32.getId()).getDoubleTime());
  // assertEquals(BigDecimal.ZERO,
  // pp.findJobById(job32.getId()).getDoubleHalfTime());
  // assertEquals(BigDecimal.ZERO, pp.findJobById(job32.getId()).getMileage());
  // assertEquals(BigDecimal.ZERO,
  // pp.findJobById(job32.getId()).getMileagePay().setScale(0));
  // assertEquals(BigDecimal.ZERO,
  // pp.findJobById(job32.getId()).getFlatRatePay().setScale(0));
  // assertEquals(new BigDecimal("3.75"),
  // pp.findJobById(job32.getId()).getTotalWagePay().setScale(2,
  // RoundingMode.HALF_UP));
  // assertEquals(BigDecimal.ZERO,
  // pp.findJobById(job32.getId()).getRegularPay().setScale(0));
  // assertEquals(new BigDecimal("3.75"),
  // pp.findJobById(job32.getId()).getOverPay().setScale(2,
  // RoundingMode.HALF_UP));
  // assertEquals(BigDecimal.ZERO,
  // pp.findJobById(job32.getId()).getDoublePay().setScale(0));
  // assertEquals(BigDecimal.ZERO,
  // pp.findJobById(job32.getId()).getDoubleHalfPay().setScale(0));
  // assertFalse(pp.findJobById(job32.getId()).isFlatRate());
  // assertEquals(this.createDate("07/31/2017"), job32.getDate());
  // assertEquals("7/31/17", job32.getDateString());
  // assertEquals("Ride", pp.findJobById(job32.getId()).getTravel());
  // assertEquals("ORC Travel", pp.findJobById(job32.getId()).getCode());
  // assertFalse(pp.findJobById(job32.getId()).isFlatRate());
  // assertFalse(pp.findJobById(job32.getId()).isMultiLineEntry());
  // // employeeSheet fields
  // assertEquals(job32.getEmployeeSheet(),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()));
  // assertEquals(1, tsheet.getEmployeeSheets().size());
  // assertEquals(32, tsheet.getEmployeeSheets().get(0).getJobs().size());
  // assertEquals(31,
  // tsheet.getEmployeeSheets().get(0).getJobs().indexOf(job32));
  // assertEquals(job32, tsheet.getEmployeeSheets().get(0).getJobs().get(31));
  // assertEquals(emp, job32.getEmployee());
  // assertEquals(emp, job32.getEmployeeSheet().getEmployee());
  // assertEquals(new BigDecimal("14.57"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getAveragePaidPerHour()
  // .setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("19.59"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getAverageCostPerHour()
  // .setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("1341.62"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getTotalCheck());
  // assertEquals(BigDecimal.ZERO,
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getMileagePay().setScale(0));
  // assertEquals(BigDecimal.ZERO,
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getMileage().setScale(0));
  // assertEquals(new BigDecimal("1341.62"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getTotalPay().setScale(2,
  // RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("1341.62"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getWagePay().setScale(2,
  // RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("97.80"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getTotalPayOn(tsheet
  // .getTravelWage()).setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("37.90"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getRegularPayOn(tsheet
  // .getTravelWage()).setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("49.50"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getOverPayOn(tsheet
  // .getTravelWage()).setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("10.40"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getDoublePayOn(tsheet
  // .getTravelWage()).setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("761"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getTotalTimeOn(tsheet
  // .getTravelWage()));
  // assertEquals(new BigDecimal("379"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getRegularTimeOn(tsheet
  // .getTravelWage()));
  // assertEquals(new BigDecimal("330"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getOverTimeOn(tsheet
  // .getTravelWage()));
  // assertEquals(new BigDecimal("52"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getDoubleTimeOn(tsheet
  // .getTravelWage()));
  // assertEquals(new BigDecimal("692.28"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getTotalPayOn(pp
  // .findWageByName(emp, "Regular")).setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("605.61"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getRegularPayOn(pp
  // .findWageByName(emp, "Regular")).setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("86.67"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getOverPayOn(pp
  // .findWageByName(emp, "Regular")).setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("4914"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getTotalTimeOn(pp
  // .findWageByName(emp, "Regular")));
  // assertEquals(new BigDecimal("4486"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getRegularTimeOn(pp
  // .findWageByName(emp, "Regular")));
  // assertEquals(new BigDecimal("428"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getOverTimeOn(pp
  // .findWageByName(emp, "Regular")));
  // assertEquals(new BigDecimal("551.54"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getTotalPayOn(pp
  // .findWageByName(emp, "Wood")).setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("345.60"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getRegularPayOn(pp
  // .findWageByName(emp, "Wood")).setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("171.92"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getOverPayOn(pp
  // .findWageByName(emp, "Wood")).setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("34.02"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getDoublePayOn(pp
  // .findWageByName(emp, "Wood")).setScale(2, RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("3535"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getTotalTimeOn(pp
  // .findWageByName(emp, "Wood")));
  // assertEquals(new BigDecimal("2560"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getRegularTimeOn(pp
  // .findWageByName(emp, "Wood")));
  // assertEquals(new BigDecimal("849"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getOverTimeOn(pp
  // .findWageByName(emp, "Wood")));
  // assertEquals(new BigDecimal("126"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getDoubleTimeOn(pp
  // .findWageByName(emp, "Wood")));
  // assertEquals(new BigDecimal("9210"),
  // pp.findEmployeeSheetById(job32.getEmployeeSheet().getId()).getTotalHours());
  // // timeSheet fields
  // assertEquals(1, pp.findAllTimeSheets().size());
  // assertEquals(tsheet, pp.findAllTimeSheets().get(0));
  // assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
  // assertEquals(tsheet, pp.findTimeSheetFor(job32.getId()).getTimeSheet());
  // tsheet = pp.findAllTimeSheets().get(0);
  // assertEquals(32, tsheet.getJobs().size());
  // assertEquals(job32, tsheet.getJobs().get(31));
  // assertEquals("Pyrl 170731", tsheet.getName());
  // assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
  // assertEquals(job32.getWage().getName().getWage().getName(),
  // pp.findTimeSheetById(tsheet.getId()).getTravelWage());
  // assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
  // assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
  // assertEquals(new BigDecimal("1341.62"), tsheet.getWagePay().setScale(2,
  // RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("436.03"),
  // pp.findTimeSheetById(tsheet.getId()).getEmployerCost().setScale(2,
  // RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("1777.65"),
  // pp.findTimeSheetById(tsheet.getId()).getGrandTotal().setScale(2,
  // RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("9210"), tsheet.getTotalHours());
  // assertEquals(new BigDecimal("14.57"),
  // pp.findTimeSheetById(tsheet.getId()).getAveragePaidPerHour().setScale(2,
  // RoundingMode.HALF_UP));
  // assertEquals(new BigDecimal("19.30"),
  // pp.findTimeSheetById(tsheet.getId()).getAverageCostPerHour().setScale(2,
  // RoundingMode.HALF_UP));
  // assertNull(tsheet.getPrevious());
  // assertNull(tsheet.getNext());
  // assertEquals(job32, pp.findJobsOn(job32.getDate()).get(5));
  // assertEquals(6, pp.findJobsOn(job32.getDate()).size());
  // }
  //
  //

  /**
   * Adds Tylan jobs to the timeSheet.
   * 
   * @param emp
   *          employee representing tyl
   * @param tsheet
   *          timesheet to add to
   * @throws ParseException
   *           if date cannot be parsed
   */
  public void addTylJobs(Employee emp, TimeSheet tsheet) throws ParseException {
    JobEntry job6 = this.createTylJob6(emp);
    final EmployeeSheet es = tsheet.getEmployeeSheetFor(emp);
    pp.addJobEntry(job6);
    // test job6
    job6 = pp.findJobById(job6.getId());
    assertEquals(new BigDecimal("458"), pp.findJobById(job6.getId()).getTotalHours());
    assertEquals(new BigDecimal("458"), pp.findJobById(job6.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("50.38"), pp.findJobById(job6.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getOverPay().setScale(0));
    assertEquals(new BigDecimal("50.38"), pp.findJobById(job6.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job6.getId()).isFlatRate());
    assertEquals(this.createDate("07/21/2017"), job6.getDate());
    assertEquals("7/21/17", job6.getDateString());
    assertEquals("SNS1701a", pp.findJobById(job6.getId()).getCode());
    assertFalse(pp.findJobById(job6.getId()).isFlatRate());
    assertFalse(pp.findJobById(job6.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(2, tsheet.getEmployeeSheets().size());
    assertEquals(1, tsheet.getEmployeeSheets().get(1).getJobs().size());
    assertEquals(0, tsheet.getEmployeeSheets().get(1).getJobs().indexOf(job6));
    assertEquals(job6, tsheet.getEmployeeSheets().get(1).getJobs().get(0));
    assertEquals(emp, job6.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("11.00"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("14.80"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("50.38"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("50.38"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("50.38"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("50.38"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("50.38"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Regular")).setScale(0));
    assertEquals(new BigDecimal("458"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("458"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("458"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job6));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(33, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job6.getWage().getName(), pp.findWageByName(emp, "Regular").getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1392.00"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("452.40"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1844.40"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("9668"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.40"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.08"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(3, pp.findJobsOn(job6.getDate()).size());
    JobEntry job2 = this.createTylJob2(emp);
    pp.addJobEntry(job2);
    // test job2
    job2 = pp.findJobById(job2.getId());
    assertEquals(new BigDecimal("683"), pp.findJobById(job2.getId()).getTotalHours());
    assertEquals(new BigDecimal("683"), pp.findJobById(job2.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("75.13"), pp.findJobById(job2.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getOverPay().setScale(0));
    assertEquals(new BigDecimal("75.13"), pp.findJobById(job2.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job2.getId()).isFlatRate());
    assertEquals(this.createDate("07/17/2017"), job2.getDate());
    assertEquals("7/17/17", job2.getDateString());
    assertEquals("SNS1701a", pp.findJobById(job2.getId()).getCode());
    assertFalse(pp.findJobById(job2.getId()).isFlatRate());
    assertTrue(pp.findJobById(job2.getId()).isMultiLineEntry());
    // employeeSheet fields

    assertEquals(2, tsheet.getEmployeeSheets().size());
    assertEquals(2, tsheet.getEmployeeSheets().get(1).getJobs().size());
    assertEquals(0, tsheet.getEmployeeSheets().get(1).getJobs().indexOf(job2));
    assertEquals(job2, tsheet.getEmployeeSheets().get(1).getJobs().get(0));
    assertEquals(emp, job2.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("11.00"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("14.80"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("125.51"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("125.51"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("125.51"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("125.51"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("125.51"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Regular")).setScale(0));
    assertEquals(new BigDecimal("1141"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("1141"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("1141"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job2));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(34, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job2.getWage().getName(), pp.findWageByName(emp, "Regular").getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1467.13"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("476.82"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1943.95"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10351"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("14.17"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.78"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(3, pp.findJobsOn(job2.getDate()).size());
    JobEntry job4 = this.createTylJob4(emp);
    pp.addJobEntry(job4);
    // test job4
    job4 = pp.findJobById(job4.getId());
    assertEquals(new BigDecimal("848"), pp.findJobById(job4.getId()).getTotalHours());
    assertEquals(new BigDecimal("800"), pp.findJobById(job4.getId()).getRegularTime());
    assertEquals(new BigDecimal("48"), pp.findJobById(job4.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("95.92"), pp.findJobById(job4.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("88.00"), pp.findJobById(job4.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("7.92"), pp.findJobById(job4.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job4.getId()).isFlatRate());
    assertEquals(this.createDate("07/19/2017"), job4.getDate());
    assertEquals("7/19/17", job4.getDateString());
    assertEquals("SNS1701a", pp.findJobById(job4.getId()).getCode());
    assertFalse(pp.findJobById(job4.getId()).isFlatRate());
    assertTrue(pp.findJobById(job4.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(2, tsheet.getEmployeeSheets().size());
    assertEquals(3, tsheet.getEmployeeSheets().get(1).getJobs().size());
    assertEquals(1, tsheet.getEmployeeSheets().get(1).getJobs().indexOf(job4));
    assertEquals(job4, tsheet.getEmployeeSheets().get(1).getJobs().get(1));
    assertEquals(emp, job4.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("11.13"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("14.97"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("221.43"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("221.43"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("221.43"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("221.43"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("213.51"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("7.92"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1989"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("1941"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("48"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("1989"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job4));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(35, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job4.getWage().getName(), pp.findWageByName(emp, "Regular").getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1563.05"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("507.99"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2071.04"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("11199"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.96"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.49"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(2, pp.findJobsOn(job4.getDate()).size());
    JobEntry job1 = this.createTylJob1(emp, tsheet);
    pp.addJobEntry(job1);
    // test job1
    job1 = pp.findJobById(job1.getId());
    assertEquals(new BigDecimal("183"), pp.findJobById(job1.getId()).getTotalHours());
    assertEquals(new BigDecimal("183"), pp.findJobById(job1.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("18.30"), pp.findJobById(job1.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.30"), pp.findJobById(job1.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job1.getId()).isFlatRate());
    assertEquals(this.createDate("07/17/2017"), job1.getDate());
    assertEquals("7/17/17", job1.getDateString());
    assertEquals("SNS Travel", pp.findJobById(job1.getId()).getCode());
    assertFalse(pp.findJobById(job1.getId()).isFlatRate());
    assertFalse(pp.findJobById(job1.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(2, tsheet.getEmployeeSheets().size());
    assertEquals(4, tsheet.getEmployeeSheets().get(1).getJobs().size());
    assertEquals(0, tsheet.getEmployeeSheets().get(1).getJobs().indexOf(job1));
    assertEquals(job1, tsheet.getEmployeeSheets().get(1).getJobs().get(0));
    assertEquals(emp, job1.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("11.20"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("15.07"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("243.36"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("243.36"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("243.36"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.30"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.30"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(new BigDecimal("183"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("183"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("225.06"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("206.25"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.81"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("1989"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("1875"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("114"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("2172"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job1));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(36, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job1.getWage().getName(), tsheet.getTravelWage().getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1584.98"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("515.12"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2100.10"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("11382"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.93"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.45"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(4, pp.findJobsOn(job1.getDate()).size());
    assertTrue(job1.hasStdDeduction());
    // test job2 values again
    job2 = pp.findJobById(job2.getId());
    assertEquals(new BigDecimal("683"), pp.findJobById(job2.getId()).getTotalHours());
    assertEquals(new BigDecimal("617"), pp.findJobById(job2.getId()).getRegularTime());
    assertEquals(new BigDecimal("66"), pp.findJobById(job2.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("78.76"), pp.findJobById(job2.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("10.89"), pp.findJobById(job2.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("67.87"), pp.findJobById(job2.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job2.getId()).isFlatRate());
    assertEquals(this.createDate("07/17/2017"), job2.getDate());
    assertEquals("7/17/17", job2.getDateString());
    assertEquals("SNS1701a", pp.findJobById(job2.getId()).getCode());
    assertFalse(pp.findJobById(job2.getId()).isFlatRate());
    assertTrue(pp.findJobById(job2.getId()).isMultiLineEntry());
    JobEntry job5 = this.createTylJob5(emp);
    pp.addJobEntry(job5);
    // test job5
    job5 = pp.findJobById(job5.getId());
    assertEquals(new BigDecimal("892"), pp.findJobById(job5.getId()).getTotalHours());
    assertEquals(new BigDecimal("800"), pp.findJobById(job5.getId()).getRegularTime());
    assertEquals(new BigDecimal("92"), pp.findJobById(job5.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("103.18"), pp.findJobById(job5.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("88.00"), pp.findJobById(job5.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("15.18"), pp.findJobById(job5.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job5.getId()).isFlatRate());
    assertEquals(this.createDate("07/20/2017"), job5.getDate());
    assertEquals("7/20/17", job5.getDateString());
    assertEquals("SNS1701a", pp.findJobById(job5.getId()).getCode());
    assertFalse(pp.findJobById(job5.getId()).isFlatRate());
    assertTrue(pp.findJobById(job5.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(2, tsheet.getEmployeeSheets().size());
    assertEquals(5, tsheet.getEmployeeSheets().get(1).getJobs().size());
    assertEquals(3, tsheet.getEmployeeSheets().get(1).getJobs().indexOf(job5));
    assertEquals(job5, tsheet.getEmployeeSheets().get(1).getJobs().get(3));
    assertEquals(emp, job5.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("11.31"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("15.21"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("346.54"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("346.54"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("346.54"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.30"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.30"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(new BigDecimal("183"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("183"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("328.24"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("294.25"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.99"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2881"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("2675"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("206"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("3064"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job5));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(37, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job5.getWage().getName(), pp.findWageByName(emp, "Regular").getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1688.16"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("548.65"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2236.82"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("12274"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.75"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.22"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(2, pp.findJobsOn(job5.getDate()).size());
    assertFalse(job5.hasStdDeduction());
    JobEntry job3 = this.createTylJob3(emp);
    pp.addJobEntry(job3);
    // test job3
    job3 = pp.findJobById(job3.getId());
    assertEquals(new BigDecimal("775"), pp.findJobById(job3.getId()).getTotalHours());
    assertEquals(new BigDecimal("775"), pp.findJobById(job3.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("85.25"), pp.findJobById(job3.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("85.25"), pp.findJobById(job3.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getOverPay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job3.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job3.getId()).isFlatRate());
    assertEquals(this.createDate("07/18/2017"), job3.getDate());
    assertEquals("7/18/17", job3.getDateString());
    assertEquals("SNS1701a", pp.findJobById(job3.getId()).getCode());
    assertFalse(pp.findJobById(job3.getId()).isFlatRate());
    assertTrue(pp.findJobById(job3.getId()).isMultiLineEntry());
    // employeeSheet fields

    assertEquals(2, tsheet.getEmployeeSheets().size());
    assertEquals(6, tsheet.getEmployeeSheets().get(1).getJobs().size());
    assertEquals(2, tsheet.getEmployeeSheets().get(1).getJobs().indexOf(job3));
    assertEquals(job3, tsheet.getEmployeeSheets().get(1).getJobs().get(2));
    assertEquals(emp, job3.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("11.25"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("15.13"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("431.79"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("431.79"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("431.79"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.30"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.30"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(new BigDecimal("183"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("183"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("413.49"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("379.50"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.99"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3656"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("3450"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("206"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("3839"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job3));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(38, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job3.getWage().getName(), pp.findWageByName(emp, "Regular").getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1773.41"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("576.36"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2349.77"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("13049"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.59"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.01"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(2, pp.findJobsOn(job3.getDate()).size());
    assertFalse(job3.hasStdDeduction());
    JobEntry job7 = this.createTylJob7(emp, tsheet);
    pp.addJobEntry(job7);
    // test job7
    job7 = pp.findJobById(job7.getId());
    assertEquals(new BigDecimal("152"), pp.findJobById(job7.getId()).getTotalHours());
    assertEquals(new BigDecimal("152"), pp.findJobById(job7.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("15.20"), pp.findJobById(job7.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getOverPay().setScale(0));
    assertEquals(new BigDecimal("15.20"), pp.findJobById(job7.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job7.getId()).isFlatRate());
    assertEquals(this.createDate("07/21/2017"), job7.getDate());
    assertEquals("7/21/17", job7.getDateString());
    assertEquals("Ride", pp.findJobById(job7.getId()).getTravel());
    assertEquals("SNS Travel", pp.findJobById(job7.getId()).getCode());
    assertFalse(pp.findJobById(job7.getId()).isFlatRate());
    assertFalse(pp.findJobById(job7.getId()).isMultiLineEntry());
    // employeeSheet fields
    assertEquals(2, tsheet.getEmployeeSheets().size());
    assertEquals(7, tsheet.getEmployeeSheets().get(1).getJobs().size());
    assertEquals(6, tsheet.getEmployeeSheets().get(1).getJobs().indexOf(job7));
    assertEquals(job7, tsheet.getEmployeeSheets().get(1).getJobs().get(6));
    assertEquals(emp, job7.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("11.20"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("15.06"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("446.99"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("446.99"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("446.99"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.50"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.50"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(new BigDecimal("335"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("335"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("413.49"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("379.50"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.99"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3656"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("3450"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("206"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("3991"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job7));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(39, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job7.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1788.61"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("581.30"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2369.91"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("13201"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.55"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17.95"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(4, pp.findJobsOn(job7.getDate()).size());
    // test advance
    Extra advance = this.createTylAdv(emp);
    pp.addExtra(advance);
    advance = es.getAdvances().get(0);
    assertNotNull(advance);
    assertEquals(new BigDecimal("100"), advance.getAmount());
    assertEquals("Advance", advance.getName());
    assertEquals(this.createDate("07/16/2017"), advance.getDate());
    // employeeSheet fields
    assertEquals(2, tsheet.getEmployeeSheets().size());
    assertEquals(7, tsheet.getEmployeeSheets().get(1).getJobs().size());
    assertEquals(6, tsheet.getEmployeeSheets().get(1).getJobs().indexOf(job7));
    assertEquals(job7, tsheet.getEmployeeSheets().get(1).getJobs().get(6));
    assertEquals(emp, job7.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("11.20"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("15.06"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("346.99"), es.getTotalCheck());
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("446.99"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("446.99"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.50"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.50"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(new BigDecimal("335"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("335"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("413.49"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("379.50"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.99"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3656"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("3450"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("206"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("3991"), es.getTotalHours());
    assertEquals(advance, es.getAdvances().get(0));
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job7));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(39, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job7.getWage().getName(), pp.findTimeSheetById(tsheet.getId()).getTravelWage()
        .getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(BigDecimal.ZERO, tsheet.getMileagePay().setScale(0));
    assertEquals(new BigDecimal("1788.61"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("581.30"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2269.91"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("13201"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.55"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17.95"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(4, pp.findJobsOn(job7.getDate()).size());
    assertEquals(advance, pp.findTimeSheetById(tsheet.getId()).getAdvances().get(0));
  }

  private void addREkJobs(Employee emp, TimeSheet tsheet) throws ParseException {
    JobEntry job1 = this.createREkJob1(emp, tsheet);
    JobEntry job2 = this.createREkJob2(emp);
    JobEntry job3 = this.createREkJob3(emp);
    JobEntry job4 = this.createREkJob4(emp);
    final EmployeeSheet es = tsheet.getEmployeeSheetFor(emp);
    pp.addJobEntry(job1);
    pp.addJobEntry(job2);
    pp.addJobEntry(job3);
    pp.addJobEntry(job4);
    job4 = pp.findJobById(job4.getId());
    assertEquals(new BigDecimal("848"), pp.findJobById(job4.getId()).getTotalHours());
    assertEquals(new BigDecimal("800"), pp.findJobById(job4.getId()).getRegularTime());
    assertEquals(new BigDecimal("48"), pp.findJobById(job4.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("113.36"), pp.findJobById(job4.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("9.36"), pp.findJobById(job4.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("104.00"), pp.findJobById(job4.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job4.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job4.getId()).isFlatRate());
    assertEquals(this.createDate("07/19/2017"), job4.getDate());
    assertEquals("7/19/17", job4.getDateString());
    assertEquals("", pp.findJobById(job4.getId()).getTravel());
    assertFalse(job4.hasStdDeduction());
    // employeeSheet fields
    assertEquals(3, tsheet.getEmployeeSheets().size());
    assertEquals(4, tsheet.getEmployeeSheets().get(2).getJobs().size());
    assertEquals(3, tsheet.getEmployeeSheets().get(2).getJobs().indexOf(job4));
    assertEquals(job4, tsheet.getEmployeeSheets().get(2).getJobs().get(3));
    assertEquals(emp, job4.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("13.08"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17.59"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("390.76"), es.getTotalCheck().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("65.27"), es.getMileagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("122.0"), es.getMileage().setScale(1, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("390.76"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("325.49"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("65.27"), es.getMileagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("122.0"), es.getMileage().setScale(1, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.30"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.30"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(new BigDecimal("183"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("183"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("307.19"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("284.96"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("22.23"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2306"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("2192"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("114"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("2489"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job4));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(43, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job4.getWage().getName(), pp.findWageByName(emp, "Regular").getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(new BigDecimal("65.27"), tsheet.getMileagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2114.10"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("687.08"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2766.46"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("15690"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.47"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.27"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(3, pp.findJobsOn(job4.getDate()).size());
    JobEntry job5 = this.createREkJob5(emp);
    pp.addJobEntry(job5);
    job5 = pp.findJobById(job5.getId());
    assertEquals(new BigDecimal("892"), pp.findJobById(job5.getId()).getTotalHours());
    assertEquals(new BigDecimal("800"), pp.findJobById(job5.getId()).getRegularTime());
    assertEquals(new BigDecimal("92"), pp.findJobById(job5.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("121.94"), pp.findJobById(job5.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17.94"), pp.findJobById(job5.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("104.00"), pp.findJobById(job5.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job5.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job5.getId()).isFlatRate());
    assertEquals(this.createDate("07/20/2017"), job5.getDate());
    assertEquals("7/20/17", job5.getDateString());
    assertEquals("", pp.findJobById(job5.getId()).getTravel());
    assertFalse(job5.hasStdDeduction());
    // employeeSheet fields
    assertEquals(3, tsheet.getEmployeeSheets().size());
    assertEquals(5, tsheet.getEmployeeSheets().get(2).getJobs().size());
    assertEquals(4, tsheet.getEmployeeSheets().get(2).getJobs().indexOf(job5));
    assertEquals(job5, tsheet.getEmployeeSheets().get(2).getJobs().get(4));
    assertEquals(emp, job5.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("13.23"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17.80"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("512.70"), es.getTotalCheck().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("65.27"), es.getMileagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("122.0"), es.getMileage().setScale(1, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("512.70"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("447.43"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.30"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.30"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(new BigDecimal("183"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("183"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("429.13"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("388.96"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("40.17"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3198"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("2992"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("206"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("3381"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job5));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(44, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job5.getWage().getName(), pp.findWageByName(emp, "Regular").getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(new BigDecimal("65.27"), tsheet.getMileagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2236.04"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("726.71"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2928.03"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("16582"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.48"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.26"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(3, pp.findJobsOn(job5.getDate()).size());
    JobEntry job6 = this.createREkJob6(emp);
    JobEntry job7 = this.createREkJob7(emp, tsheet);
    pp.addJobEntry(job6);
    pp.addJobEntry(job7);
    job7 = pp.findJobById(job7.getId());
    assertEquals(new BigDecimal("152"), pp.findJobById(job7.getId()).getTotalHours());
    assertEquals(new BigDecimal("152"), pp.findJobById(job7.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoubleHalfTime());
    assertEquals(new BigDecimal("122.0"), pp.findJobById(job7.getId()).getMileage().setScale(1,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("65.27"), pp.findJobById(job7.getId()).getMileagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("15.20"), pp.findJobById(job7.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getOverPay().setScale(0));
    assertEquals(new BigDecimal("15.20"), pp.findJobById(job7.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job7.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job7.getId()).isFlatRate());
    assertEquals(this.createDate("07/21/2017"), job7.getDate());
    assertEquals("7/21/17", job7.getDateString());
    assertEquals("145.0", pp.findJobById(job7.getId()).getTravel());
    assertTrue(job7.hasStdDeduction());
    // employeeSheet fields
    assertEquals(3, tsheet.getEmployeeSheets().size());
    assertEquals(7, tsheet.getEmployeeSheets().get(2).getJobs().size());
    assertEquals(6, tsheet.getEmployeeSheets().get(2).getJobs().indexOf(job7));
    assertEquals(job7, tsheet.getEmployeeSheets().get(2).getJobs().get(6));
    assertEquals(emp, job7.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("13.08"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17.60"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("652.71"), es.getTotalCheck().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("130.54"), es.getMileagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("244.0"), es.getMileage().setScale(1, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("652.71"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("522.17"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.50"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.50"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(new BigDecimal("335"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("335"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("488.67"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("448.50"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("40.17"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3656"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("3450"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("206"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("3991"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job7));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(46, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job7.getWage().getName(), tsheet.getTravelWage().getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(new BigDecimal("130.54"), tsheet.getMileagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2310.78"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("751.00"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3092.33"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17192"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.44"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.57"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(6, pp.findJobsOn(job7.getDate()).size());
    JobEntry job8 = this.createREkJob8(emp);
    JobEntry job9 = this.createREkJob9(emp);
    pp.addJobEntry(job8);
    pp.addJobEntry(job9);
    job9 = pp.findJobById(job9.getId());
    assertEquals(new BigDecimal("550"), pp.findJobById(job9.getId()).getTotalHours());
    assertEquals(new BigDecimal("550"), pp.findJobById(job9.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getMileage().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("71.50"), pp.findJobById(job9.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getOverPay().setScale(0));
    assertEquals(new BigDecimal("71.50"), pp.findJobById(job9.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job9.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job9.getId()).isFlatRate());
    assertEquals(this.createDate("07/26/2017"), job9.getDate());
    assertEquals("7/26/17", job9.getDateString());
    assertEquals("", pp.findJobById(job9.getId()).getTravel());
    assertFalse(job9.hasStdDeduction());
    // employeeSheet fields
    assertEquals(3, tsheet.getEmployeeSheets().size());
    assertEquals(9, tsheet.getEmployeeSheets().get(2).getJobs().size());
    assertEquals(8, tsheet.getEmployeeSheets().get(2).getJobs().indexOf(job9));
    assertEquals(job9, tsheet.getEmployeeSheets().get(2).getJobs().get(8));
    assertEquals(emp, job9.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("13.07"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17.58"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("742.02"), es.getTotalCheck().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("130.54"), es.getMileagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("244.0"), es.getMileage().setScale(1, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("742.02"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("611.48"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.50"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.50"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(new BigDecimal("335"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("335"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("577.98"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("537.81"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("40.17"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4343"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("4137"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("206"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("4678"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job9));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(48, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job9.getWage().getName(), pp.findWageByName(emp, "Regular").getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(new BigDecimal("130.54"), tsheet.getMileagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2400.09"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("780.03"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3210.66"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17879"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.42"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.52"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(3, pp.findJobsOn(job9.getDate()).size());
    // test advance
    Deduction advance = this.createREkAdv(emp);
    pp.addExtra(advance);
    EmployeeSheet REkSheet = pp.findEmployeeSheetById(tsheet.getEmployeeSheets().get(2).getId());
    Extra advance2 = REkSheet.getAdvances().get(0);
    assertNotNull(advance2);
    assertEquals(new BigDecimal("100"), advance2.getAmount());
    assertEquals("Advance", advance2.getName());
    assertEquals(this.createDate("07/16/2017"), advance2.getDate());
    // employeeSheet fields
    assertEquals(new BigDecimal("13.07"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17.58"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("642.02"), es.getTotalCheck());
    assertEquals(new BigDecimal("130.54"), es.getMileagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("244.0"), es.getMileage().setScale(1, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("742.02"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("611.48"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.50"), es.getTotalPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("33.50"), es.getRegularPayOn(tsheet.getTravelWage()).setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(new BigDecimal("335"), es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("335"), es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("577.98"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("537.81"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("40.17"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4343"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("4137"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("206"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("4678"), es.getTotalHours());
    assertEquals(advance, es.getAdvances().get(0));
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job7));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(48, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(new BigDecimal("130.54"), tsheet.getMileagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("244.0"), tsheet.getMileage().setScale(1, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2400.09"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("780.03"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("200.00"), tsheet.getTotalAdvances().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3110.66"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17879"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.42"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.52"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(advance, pp.findTimeSheetById(tsheet.getId()).getAdvances().get(1));
  }

  private void addRodriguezSantaJobs(Employee emp, TimeSheet tsheet) throws ParseException {
    JobEntry job1 = this.createRSJob1(emp);
    pp.addJobEntry(job1);
    final EmployeeSheet es = tsheet.getEmployeeSheetFor(emp);
    job1 = pp.findJobById(job1.getId());
    assertEquals(new BigDecimal("100"), pp.findJobById(job1.getId()).getTotalHours());
    assertEquals(new BigDecimal("100"), pp.findJobById(job1.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getMileagePay().setScale(0));
    assertEquals(new BigDecimal("14.00"), pp.findJobById(job1.getId()).getFlatRatePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("14.00"), pp.findJobById(job1.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getOverPay().setScale(0));
    assertEquals(new BigDecimal("14.00"), pp.findJobById(job1.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job1.getId()).getDoubleHalfPay().setScale(0));
    assertEquals(this.createDate("07/16/2017"), job1.getDate());
    assertEquals("7/16/17", job1.getDateString());
    assertEquals("", pp.findJobById(job1.getId()).getTravel());
    assertFalse(job1.hasStdDeduction());
    assertTrue(job1.isFlatRate());
    // employeeSheet fields
    assertEquals(4, tsheet.getEmployeeSheets().size());
    assertEquals(1, tsheet.getEmployeeSheets().get(3).getJobs().size());
    assertEquals(0, tsheet.getEmployeeSheets().get(3).getJobs().indexOf(job1));
    assertEquals(job1, tsheet.getEmployeeSheets().get(3).getJobs().get(0));
    assertEquals(emp, job1.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.00"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.83"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("14.00"), es.getTotalCheck().setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("14.00"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("14.00"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("14.00"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("14.00"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Regular")).setScale(0));
    assertEquals(new BigDecimal("100"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("100"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("100"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job1));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(49, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job1.getWage().getName(), pp.findWageByName(emp, "Regular").getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(new BigDecimal("130.54"), tsheet.getMileagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2414.09"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("784.58"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("200.00"), tsheet.getTotalAdvances().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3129.21"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("17979"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.43"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.52"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(1, pp.findJobsOn(job1.getDate()).size());
    JobEntry job2 = this.createRSJob2(emp);
    pp.addJobEntry(job2);
    job2 = pp.findJobById(job2.getId());
    assertEquals(new BigDecimal("783"), pp.findJobById(job2.getId()).getTotalHours());
    assertEquals(new BigDecimal("783"), pp.findJobById(job2.getId()).getRegularTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getOverTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getFlatRatePay().setScale(0));
    assertEquals(new BigDecimal("109.62"), pp.findJobById(job2.getId()).getTotalWagePay().setScale(
        2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getOverPay().setScale(0));
    assertEquals(new BigDecimal("109.62"), pp.findJobById(job2.getId()).getRegularPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoublePay().setScale(0));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job2.getId()).getDoubleHalfPay().setScale(0));
    assertFalse(pp.findJobById(job2.getId()).isFlatRate());
    assertEquals(this.createDate("07/17/2017"), job2.getDate());
    assertEquals("7/17/17", job2.getDateString());
    assertEquals("", pp.findJobById(job2.getId()).getTravel());
    assertFalse(job2.hasStdDeduction());
    // employeeSheet fields
    assertEquals(4, tsheet.getEmployeeSheets().size());
    assertEquals(2, tsheet.getEmployeeSheets().get(3).getJobs().size());
    assertEquals(1, tsheet.getEmployeeSheets().get(3).getJobs().indexOf(job2));
    assertEquals(job2, tsheet.getEmployeeSheets().get(3).getJobs().get(1));
    assertEquals(emp, job2.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("14.00"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.83"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("123.62"), es.getTotalCheck().setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getMileagePay().setScale(0));
    assertEquals(BigDecimal.ZERO, es.getMileage().setScale(0));
    assertEquals(new BigDecimal("123.62"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("123.62"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("123.62"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("123.62"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Regular")).setScale(0));
    assertEquals(new BigDecimal("883"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("883"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("883"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job2));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(50, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job2.getWage().getName(), pp.findWageByName(emp, "Regular").getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(new BigDecimal("130.54"), tsheet.getMileagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2523.71"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("820.21"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("200.00"), tsheet.getTotalAdvances().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3274.46"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18762"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.45"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.52"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(7, pp.findJobsOn(job2.getDate()).size());
    JobEntry job3 = this.createRSJob3(emp);
    JobEntry job4 = this.createRSJob4(emp);
    JobEntry job5 = this.createRSJob5(emp);
    JobEntry job6 = this.createRSJob6(emp);
    pp.addJobEntry(job3);
    pp.addJobEntry(job4);
    pp.addJobEntry(job5);
    pp.addJobEntry(job6);
    job6 = pp.findJobById(job6.getId());
    assertEquals(new BigDecimal("400"), pp.findJobById(job6.getId()).getTotalHours());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getRegularTime());
    assertEquals(new BigDecimal("175"), pp.findJobById(job6.getId()).getOverTime());
    assertEquals(new BigDecimal("225"), pp.findJobById(job6.getId()).getDoubleTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getDoubleHalfTime());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getMileage());
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getMileagePay().setScale(0));
    assertEquals(new BigDecimal("99.75"), pp.findJobById(job6.getId()).getFlatRatePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("99.75"), pp.findJobById(job6.getId()).getTotalWagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("36.75"), pp.findJobById(job6.getId()).getOverPay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getRegularPay().setScale(0));
    assertEquals(new BigDecimal("63.00"), pp.findJobById(job6.getId()).getDoublePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, pp.findJobById(job6.getId()).getDoubleHalfPay().setScale(0));
    assertTrue(pp.findJobById(job6.getId()).isFlatRate());
    assertEquals(this.createDate("07/19/2017"), job6.getDate());
    assertEquals("7/19/17", job6.getDateString());
    assertEquals("", pp.findJobById(job6.getId()).getTravel());
    assertFalse(job6.hasStdDeduction());
    // employeeSheet fields
    assertEquals(4, tsheet.getEmployeeSheets().size());
    assertEquals(6, tsheet.getEmployeeSheets().get(3).getJobs().size());
    assertEquals(5, tsheet.getEmployeeSheets().get(3).getJobs().indexOf(job6));
    assertEquals(job6, tsheet.getEmployeeSheets().get(3).getJobs().get(5));
    assertEquals(emp, job6.getEmployee());
    assertEquals(emp, es.getEmployee());
    assertEquals(new BigDecimal("16.10"), es.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("21.66"), es.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("458.11"), es.getTotalCheck().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2.41"), es.getMileagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4.5"), es.getMileage().setScale(1, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("458.11"), es.getTotalPay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("455.70"), es.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(tsheet.getTravelWage()).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(tsheet.getTravelWage()));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(tsheet.getTravelWage()));
    assertEquals(new BigDecimal("455.70"), es.getTotalPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("308.70"), es.getRegularPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("84.00"), es.getOverPayOn(pp.findWageByName(emp, "Regular"))
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2830"), es.getTotalTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("2205"), es.getRegularTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("400"), es.getOverTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(new BigDecimal("225"), es.getDoubleTimeOn(pp.findWageByName(emp, "Regular")));
    assertEquals(BigDecimal.ZERO, es.getTotalPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getRegularPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getOverPayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getDoublePayOn(pp.findWageByName(emp, "Wood")).setScale(0));
    assertEquals(BigDecimal.ZERO, es.getTotalTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getRegularTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getOverTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(BigDecimal.ZERO, es.getDoubleTimeOn(pp.findWageByName(emp, "Wood")));
    assertEquals(new BigDecimal("2830"), es.getTotalHours());
    // timeSheet fields
    assertEquals(1, pp.findAllTimeSheets().size());
    assertEquals(tsheet, pp.findAllTimeSheets().get(0));
    assertEquals(tsheet, pp.findTimeSheetById(tsheet.getId()));
    assertEquals(tsheet, pp.findTimeSheetFor(job6));
    tsheet = pp.findAllTimeSheets().get(0);
    assertEquals(54, tsheet.getJobs().size());
    assertEquals("Pyrl 170731", tsheet.getName());
    assertEquals(new BigDecimal("0.535"), tsheet.getStandardMileageRate());
    assertEquals(job6.getWage().getName(), pp.findWageByName(emp, "Regular").getName());
    assertEquals(new BigDecimal("10.00"), tsheet.getTravelWageRate());
    assertEquals(new BigDecimal("132.95"), tsheet.getMileagePay().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("2855.79"), tsheet.getWagePay().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("928.13"), pp.findTimeSheetById(tsheet.getId()).getEmployerCost()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("200.00"), tsheet.getTotalAdvances().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("3716.87"), pp.findTimeSheetById(tsheet.getId()).getGrandTotal()
        .setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("20709"), tsheet.getTotalHours());
    assertEquals(new BigDecimal("13.79"), pp.findTimeSheetById(tsheet.getId())
        .getAveragePaidPerHour().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("18.91"), pp.findTimeSheetById(tsheet.getId())
        .getAverageCostPerHour().setScale(2, RoundingMode.HALF_UP));
    assertNull(tsheet.getPrevious());
    assertNull(tsheet.getNext());
    assertEquals(6, pp.findJobsOn(job6.getDate()).size());
    pp.addJobEntry(this.createRSJob7(emp));
    pp.addJobEntry(this.createRSJob8(emp));
    pp.addJobEntry(this.createRSJob9(emp));
    pp.addJobEntry(this.createRSJob10(emp));
    pp.addJobEntry(this.createRSJob11(emp));
    pp.addJobEntry(this.createRSJob12(emp));
    pp.addJobEntry(this.createRSJob13(emp));
    pp.addJobEntry(this.createRSJob14(emp));
    pp.addJobEntry(this.createRSJob15(emp));
    pp.addJobEntry(this.createRSJob16(emp));
    pp.addJobEntry(this.createRSJob17(emp));
    pp.addJobEntry(this.createRSJob18(emp));
    pp.addJobEntry(this.createRSJob19(emp));
    pp.addJobEntry(this.createRSJob20(emp));
    pp.addJobEntry(this.createRSJob21(emp));
    pp.addJobEntry(this.createRSJob22(emp));
    pp.addJobEntry(this.createRSJob23(emp));
    pp.addJobEntry(this.createRSJob24(emp));
    JobEntry job25 = this.createRSJob25(emp);
    pp.addJobEntry(job25);
    assertEquals(new BigDecimal("8302"), es.getTotalHours());
    assertEquals(new BigDecimal("1259.52"), es.getTotalCheck().setScale(2, RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("4775.93"), tsheet.getGrandTotal().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("13.94"), tsheet.getAveragePaidPerHour().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("19.01"), tsheet.getAverageCostPerHour().setScale(2,
        RoundingMode.HALF_UP));
  }

  /**
   * Add Apj jobs to timesheet.
   * 
   * @param emp
   *          apj emp object
   * @param tsheet
   *          timesheet to add to
   * @throws ParseException
   *           if date cannot be parsed
   */
  public void addApJJobs(Employee emp, TimeSheet tsheet) throws ParseException {
    pp.addJobEntry(this.createApJJob1(emp, tsheet));
    pp.addJobEntry(this.createApJJob2(emp));
    pp.addJobEntry(this.createApJJob3(emp));
    pp.addJobEntry(this.createApJJob4(emp, tsheet));
    pp.addJobEntry(this.createApJJob5(emp));
    pp.addJobEntry(this.createApJJob6(emp));
    pp.addJobEntry(this.createApJJob7(emp));
    pp.addJobEntry(this.createApJJob8(emp));
    pp.addJobEntry(this.createApJJob9(emp));
    EmployeeSheet es = tsheet.getEmployeeSheetFor(emp);
    JobEntry job10 = this.createApJJob10(emp);
    pp.addJobEntry(job10);
    assertEquals(new BigDecimal("5174"), es.getTotalHours());
    pp.addJobEntry(this.createApJJob11(emp, tsheet));
    pp.addJobEntry(this.createApJJob12(emp));
    pp.addJobEntry(this.createApJJob13(emp));
    pp.addJobEntry(this.createApJJob14(emp));
    JobEntry job15 = this.createApJJob15(emp);
    pp.addJobEntry(job15);
    assertEquals(new BigDecimal("6034"), es.getTotalHours());
    pp.addJobEntry(this.createApJJob16(emp));
    pp.addJobEntry(this.createApJJob17(emp));
    pp.addJobEntry(this.createApJJob18(emp));
    pp.addJobEntry(this.createApJJob19(emp));
    pp.addJobEntry(this.createApJJob20(emp, tsheet));
    JobEntry job21 = this.createApJJob21(emp);
    pp.addJobEntry(job21);
    assertEquals(new BigDecimal("7374"), es.getTotalHours());
    pp.addJobEntry(this.createApJJob22(emp));
    pp.addJobEntry(this.createApJJob23(emp));
    pp.addJobEntry(this.createApJJob24(emp, tsheet));
    pp.addJobEntry(this.createApJJob25(emp, tsheet));
    pp.addJobEntry(this.createApJJob26(emp));
    pp.addJobEntry(this.createApJJob27(emp));
    pp.addJobEntry(this.createApJJob28(emp));
    pp.addJobEntry(this.createApJJob29(emp));
    pp.addJobEntry(this.createApJJob30(emp, tsheet));
    pp.addJobEntry(this.createApJJob31(emp, tsheet));
    pp.addJobEntry(this.createApJJob32(emp));
    pp.addJobEntry(this.createApJJob33(emp));
    pp.addJobEntry(this.createApJJob34(emp));
    pp.addJobEntry(this.createApJJob35(emp));
    JobEntry job36 = this.createApJJob36(emp, tsheet);
    pp.addJobEntry(job36);
    assertEquals(new BigDecimal("10135"), es.getTotalHours());
    assertEquals(new BigDecimal("1494.29"), es.getTotalCheck());
    assertEquals(new BigDecimal("6755.87"), tsheet.getGrandTotal().setScale(2,
        RoundingMode.HALF_UP));
    assertEquals(new BigDecimal("36316"), tsheet.getTotalHours());
  }

  @AfterClass
  public static void wipeDB() throws SQLException {
    testDB.reset();
    testDB.shutdown();
  }

}
