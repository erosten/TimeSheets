
package cps.excel;

import cps.core.ProgramPortal;
import cps.core.model.employee.Employee;
import cps.core.model.frame.Wage;
import cps.core.model.timeSheet.EmployeeSheet;
import cps.core.model.timeSheet.JobEntry;
import cps.core.model.timeSheet.TimeSheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TimeSheetExporter {

  private CreationHelper creationHelper;
  private RichTextString companyTitleRichString;

  private Font cambria16ptNoU;
  private Font cambria16ptU;
  private Font timesNewRoman16pt;
  private Font timesNewRoman12pt;
  private Font timesNewRoman11pt;
  private Font timesNewRoman11ptRed;
  private Font timesNewRoman10ptRedItalicsU;
  private Font timesNewRoman10ptBlue;
  private Font timesNewRoman10ptU;
  private Font timesNewRoman10pt;
  private Font timesNewRoman9ptWhite;
  private Font timesNewRoman9ptItalics;
  private Font timesNewRoman9ptBlueItalics;
  private Font timesNewRoman9pt;
  private Font timesNewRoman8ptBlueItalicized;
  private Font timesNewRoman8ptBlue;
  private Font calibri11pt;

  // global styles
  private CellStyle wageNameStyle;
  private CellStyle companyNameStyle;
  private CellStyle timeSheePeriodStyle;
  private CellStyle regOTDTStyle;
  private CellStyle timesNewRoman10ptRightJustified;
  private CellStyle borderedCurrencyStyle;
  private CellStyle totalHoursStyle;
  private CellStyle timesNewRoman9ptLeftJustifiedItalicized;
  private CellStyle currencyStyleCentered;

  // sheet styles
  private CellStyle wageNameHiddenStyle;
  private CellStyle sheetWageRateHidden;
  private CellStyle sheetWageRateNonHidden;
  private CellStyle jobDateStyle;
  private CellStyle jobCodeStyle;
  private CellStyle jobTimeStyle;
  private CellStyle jobTimeStyleUncertain;
  private CellStyle sheetEmployeeNameStyle;
  private CellStyle sheetEmployeeNameStyleUnderLined;
  private CellStyle sheetPeriodTitleStyle;
  private CellStyle jobHourStyle;
  private CellStyle jobHourStyleUncertain;
  private CellStyle jobMileageStyle;
  private CellStyle sheetWageHourHeaderStyle;
  private CellStyle sheetHourTotalStyle;
  private CellStyle sheetMileageTotalStyle;
  private CellStyle timesNewRoman9ptRightJustifiedItalicized;
  private CellStyle sheetMileageRateStyle;
  private CellStyle sheetAdvanceTotalStyle;
  private CellStyle stdDeductionStyle;

  // cover styles
  private CellStyle coverSheetTitleStyle;
  private CellStyle currencyStyleBottomJustified;
  private CellStyle currencyStyleBlueBottomJustified;
  private CellStyle coverEmployeeNameStyle;
  private CellStyle coverAdvanceTitleStyle;
  private CellStyle coverHourTotalStyle;
  private CellStyle timesNewRoman12ptRightJustified;
  private CellStyle coverMileageTotalStyle;
  private CellStyle timesNewRoman10ptRightJustifiedBlue;
  private CellStyle timesNewRoman8ptRightJustifiedBlue;
  private CellStyle currencyStyleBlueCentered;
  private CellStyle coverEmployeeAdvanceStyle;
  private CellStyle currencyStyleSmallBlueCenteredThreeDecimals;
  private CellStyle currencyStyleSmallBlueCentered;
  private CellStyle coverAdvanceTotalStyle;
  private CellStyle coverEmployeeMileageStyle;
  private CellStyle borderLeftTopBot;
  private CellStyle borderRightTopBot;
  private CellStyle borderTopBot;

  private Workbook wbk;
  private Sheet cover;
  private Sheet divider;

  public TimeSheetExporter() {
  }

  /**
   * Method is denoted below.
   * <p>
   * 1- set necessary globals
   * <p>
   * 2- create necessary sheets and cells
   * <p>
   * 3- set necessary row/column spacing
   * <p>
   * 4- create cover sheet header
   * <p>
   * 5- create cover sheet wage struc & footer
   * <p>
   * 6- create employee sheet headers
   * <p>
   * 7- fill in employee sheet job data & footer, complete cover sheet formulae directory must exist
   * before sending here
   * 
   * @param tsheet
   *          timeSheet to create an excel file for
   * @param directory
   *          directory to create an excel file in
   * @param wbt
   *          the type of workbook to create
   * @param portal
   *          the ProgramPortal to grab all employees from
   */
  public void export(TimeSheet tsheet, File directory, WorkBookType wbt, ProgramPortal portal) {
    try (Workbook wbk = (wbt.equals(WorkBookType.XLS)) ? new HSSFWorkbook() : new XSSFWorkbook()) {
      this.wbk = wbk;
      creationHelper = wbk.getCreationHelper();
      // 1- set necessary globals
      setFonts();
      setGlobalStyles();
      setSheetStyles();
      setCoverStyles();
      // 2- create Cover Sheet as much as possible
      // 2a - create sheet
      cover = wbk.createSheet("Cover");
      cover.setZoom(129);
      // 2b - create cover cells
      List<Employee> employees = portal.findActiveEmployees();
      // 13 for cover header
      // 1 for divider
      // 1 for each employee
      // 8 for cover footer
      int coverRows = Constants.cover_header_rows + 1 + employees.size()
          + Constants.cover_footer_rows;
      ExporterUtil.fillWithCells(coverRows, Constants.cover_sheet_cols, cover);
      // 2c - set cover widths/heights
      setCoverSheetColumnWidths();
      setCoverSheetRowHeights();
      // 3 - create Employee Sheets as much as possible
      // 3a create active employee sheets/cells
      createActiveEmployeeSheetCells(tsheet.getEmployees(), tsheet);
      // 3b create divider
      divider = wbk.createSheet("ACTIVE | INACTIVE");
      // 3c create inactive employee sheets/cells
      List<Employee> inactiveEmployees = portal.findAllEmployees();
      inactiveEmployees.removeAll(tsheet.getEmployees());
      createInactiveEmployeeSheetCells(inactiveEmployees);
      // 3d- set employee sheet row/column spacing
      setEmployeeSheetColumnWidths();
      setEmployeeSheetRowHeights();
      // 4- create headers and cover wage structure
      // 4a create generic headers for cover/eSheets
      createGenericHeaders();
      // 4b finish up cover sheet header
      createCoverSheetHeader(tsheet.getTimeSheetPeriodString());
      // 4c - create cover wage structure
      createCoverWageStructureAndFooter(employees, tsheet);
      // 6- create employee sheet headers
      createEmployeeSheetHeaders(employees);
      // 7- fill in employee sheet job data & footer, complete cover sheet
      // formulae
      createEmployeeSheetData(tsheet, employees.size());
      String filename = tsheet.getName() + "." + wbt.getExtension();
      FileOutputStream out = new FileOutputStream(
          directory.getAbsolutePath() + File.separator + filename);
      wbk.write(out);
      out.close();
    } catch (IOException e) {
      throw new IllegalStateException("IOException closing the generated excel file");
    }
    System.out.println("Exporting Done");
  }

  private void setFonts() {
    timesNewRoman16pt = wbk.createFont();
    timesNewRoman16pt.setFontHeightInPoints((short) 16);
    timesNewRoman16pt.setFontName("Times New Roman");
    timesNewRoman10ptRedItalicsU = wbk.createFont();
    timesNewRoman10ptRedItalicsU.setFontHeightInPoints((short) 10);
    timesNewRoman10ptRedItalicsU.setFontName("Times New Roman");
    timesNewRoman10ptRedItalicsU.setUnderline(Font.U_SINGLE);
    timesNewRoman10ptRedItalicsU.setColor(Font.COLOR_RED);
    timesNewRoman10ptRedItalicsU.setItalic(true);
    timesNewRoman8ptBlue = wbk.createFont();
    timesNewRoman8ptBlue.setFontHeightInPoints((short) 8);
    timesNewRoman8ptBlue.setFontName("Times New Roman");
    timesNewRoman8ptBlue.setColor(IndexedColors.BLUE.index);
    timesNewRoman9ptItalics = wbk.createFont();
    timesNewRoman9ptItalics.setFontName("Times New Roman");
    timesNewRoman9ptItalics.setFontHeightInPoints((short) 9);
    timesNewRoman9ptItalics.setItalic(true);
    timesNewRoman9ptBlueItalics = wbk.createFont();
    timesNewRoman9ptBlueItalics.setFontName("Times New Roman");
    timesNewRoman9ptBlueItalics.setFontHeightInPoints((short) 9);
    timesNewRoman9ptBlueItalics.setItalic(true);
    timesNewRoman9ptBlueItalics.setColor(IndexedColors.BLUE.index);
    timesNewRoman9ptWhite = wbk.createFont();
    timesNewRoman9ptWhite.setFontName("Times New Roman");
    timesNewRoman9ptWhite.setFontHeightInPoints((short) 9);
    timesNewRoman9ptWhite.setColor(IndexedColors.WHITE.index);
    timesNewRoman9pt = wbk.createFont();
    timesNewRoman9pt.setFontName("Times New Roman");
    timesNewRoman9pt.setFontHeightInPoints((short) 9);
    timesNewRoman11pt = wbk.createFont();
    timesNewRoman11pt.setFontHeightInPoints((short) 11);
    timesNewRoman11pt.setFontName("Times New Roman");
    timesNewRoman11ptRed = wbk.createFont();
    timesNewRoman11ptRed.setFontHeightInPoints((short) 11);
    timesNewRoman11ptRed.setFontName("Times New Roman");
    timesNewRoman11ptRed.setColor(IndexedColors.RED.index);
    timesNewRoman8ptBlueItalicized = wbk.createFont();
    timesNewRoman8ptBlueItalicized.setFontHeightInPoints((short) 8);
    timesNewRoman8ptBlueItalicized.setFontName("Times New Roman");
    timesNewRoman8ptBlueItalicized.setColor(IndexedColors.BLUE.index);
    timesNewRoman8ptBlueItalicized.setItalic(true);
    timesNewRoman10ptBlue = wbk.createFont();
    timesNewRoman10ptBlue.setFontHeightInPoints((short) 10);
    timesNewRoman10ptBlue.setFontName("Times New Roman");
    timesNewRoman10ptBlue.setColor(IndexedColors.BLUE.index);
    cambria16ptU = wbk.createFont();
    cambria16ptNoU = wbk.createFont();
    cambria16ptU.setFontHeightInPoints((short) 16);
    cambria16ptU.setFontName("Cambria");
    cambria16ptU.setUnderline(Font.U_SINGLE);
    cambria16ptU.setColor(IndexedColors.BLUE.index);
    cambria16ptNoU.setFontHeightInPoints((short) 16);
    cambria16ptNoU.setFontName("Cambria");
    cambria16ptNoU.setColor(IndexedColors.BLUE.index);
    timesNewRoman12pt = wbk.createFont();
    timesNewRoman12pt.setFontHeightInPoints((short) 12);
    timesNewRoman12pt.setFontName("Times New Roman");
    timesNewRoman10pt = wbk.createFont();
    timesNewRoman10pt.setFontHeightInPoints((short) 10);
    timesNewRoman10pt.setFontName("Times New Roman");
    calibri11pt = wbk.createFont();
    calibri11pt.setFontHeightInPoints((short) 11);
    calibri11pt.setFontName("Calibri");
    timesNewRoman10ptU = wbk.createFont();
    timesNewRoman10ptU.setFontHeightInPoints((short) 10);
    timesNewRoman10ptU.setFontName("Times New Roman");
    timesNewRoman10ptU.setUnderline(Font.U_SINGLE);
  }

  private void setCoverStyles() {
    // style 1
    coverSheetTitleStyle = wbk.createCellStyle();
    coverSheetTitleStyle.setFont(timesNewRoman16pt);
    coverSheetTitleStyle.setAlignment(HorizontalAlignment.CENTER);
    coverSheetTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    // style 2
    currencyStyleBlueBottomJustified = wbk.createCellStyle();
    currencyStyleBlueBottomJustified.setDataFormat(creationHelper.createDataFormat().getFormat(
        "$#,##0.00"));
    currencyStyleBlueBottomJustified.setFont(timesNewRoman10ptBlue);
    currencyStyleBlueBottomJustified.setAlignment(HorizontalAlignment.CENTER);
    currencyStyleBlueBottomJustified.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 3
    coverEmployeeNameStyle = wbk.createCellStyle();
    coverEmployeeNameStyle.setFont(timesNewRoman10pt);
    coverEmployeeNameStyle.setAlignment(HorizontalAlignment.LEFT);
    coverEmployeeNameStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 4
    coverAdvanceTitleStyle = wbk.createCellStyle();
    coverAdvanceTitleStyle.setFont(timesNewRoman10ptRedItalicsU);
    coverAdvanceTitleStyle.setAlignment(HorizontalAlignment.CENTER);
    coverAdvanceTitleStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 5
    coverHourTotalStyle = wbk.createCellStyle();
    coverHourTotalStyle.setFont(timesNewRoman10pt);
    coverHourTotalStyle.setAlignment(HorizontalAlignment.CENTER);
    coverHourTotalStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    coverHourTotalStyle.setBorderLeft(BorderStyle.MEDIUM);
    coverHourTotalStyle.setBorderTop(BorderStyle.MEDIUM);
    coverHourTotalStyle.setBorderRight(BorderStyle.MEDIUM);
    // style 6
    timesNewRoman12ptRightJustified = wbk.createCellStyle();
    timesNewRoman12ptRightJustified.setFont(timesNewRoman12pt);
    timesNewRoman12ptRightJustified.setAlignment(HorizontalAlignment.RIGHT);
    timesNewRoman12ptRightJustified.setVerticalAlignment(VerticalAlignment.CENTER);
    // style 7
    coverMileageTotalStyle = wbk.createCellStyle();
    coverMileageTotalStyle.setDataFormat(creationHelper.createDataFormat().getFormat("$#,##0.00"));
    coverMileageTotalStyle.setFont(timesNewRoman10pt);
    coverMileageTotalStyle.setAlignment(HorizontalAlignment.CENTER);
    coverMileageTotalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    coverMileageTotalStyle.setBorderTop(BorderStyle.THIN);
    coverMileageTotalStyle.setBorderLeft(BorderStyle.THIN);
    coverMileageTotalStyle.setBorderRight(BorderStyle.THIN);
    // style 8
    timesNewRoman10ptRightJustifiedBlue = wbk.createCellStyle();
    timesNewRoman10ptRightJustifiedBlue.setFont(timesNewRoman10ptBlue);
    timesNewRoman10ptRightJustifiedBlue.setAlignment(HorizontalAlignment.RIGHT);
    timesNewRoman10ptRightJustifiedBlue.setVerticalAlignment(VerticalAlignment.CENTER);
    // style 9
    currencyStyleBlueCentered = wbk.createCellStyle();
    currencyStyleBlueCentered.setDataFormat(creationHelper.createDataFormat().getFormat(
        "$#,##0.00"));
    currencyStyleBlueCentered.setFont(timesNewRoman10ptBlue);
    currencyStyleBlueCentered.setAlignment(HorizontalAlignment.CENTER);
    currencyStyleBlueCentered.setVerticalAlignment(VerticalAlignment.CENTER);
    // style 10
    coverEmployeeAdvanceStyle = wbk.createCellStyle();
    coverEmployeeAdvanceStyle.setFont(timesNewRoman10pt);
    coverEmployeeAdvanceStyle.setAlignment(HorizontalAlignment.CENTER);
    coverEmployeeAdvanceStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    coverEmployeeAdvanceStyle.setDataFormat(creationHelper.createDataFormat().getFormat(
        "$#,##0.00_);[Red]($#,##0.00)"));
    // style 11
    timesNewRoman8ptRightJustifiedBlue = wbk.createCellStyle();
    timesNewRoman8ptRightJustifiedBlue.setFont(timesNewRoman8ptBlue);
    timesNewRoman8ptRightJustifiedBlue.setAlignment(HorizontalAlignment.RIGHT);
    timesNewRoman8ptRightJustifiedBlue.setVerticalAlignment(VerticalAlignment.CENTER);
    // style 12
    currencyStyleSmallBlueCenteredThreeDecimals = wbk.createCellStyle();
    currencyStyleSmallBlueCenteredThreeDecimals.setFont(timesNewRoman8ptBlue);
    currencyStyleSmallBlueCenteredThreeDecimals.setAlignment(HorizontalAlignment.CENTER);
    currencyStyleSmallBlueCenteredThreeDecimals.setVerticalAlignment(VerticalAlignment.CENTER);
    currencyStyleSmallBlueCenteredThreeDecimals.setDataFormat(creationHelper.createDataFormat()
        .getFormat("$#,##0.000"));
    // style 13
    currencyStyleSmallBlueCentered = wbk.createCellStyle();
    currencyStyleSmallBlueCentered.setDataFormat(creationHelper.createDataFormat().getFormat(
        "$#,##0.00"));
    currencyStyleSmallBlueCentered.setFont(timesNewRoman8ptBlue);
    currencyStyleSmallBlueCentered.setAlignment(HorizontalAlignment.CENTER);
    currencyStyleSmallBlueCentered.setVerticalAlignment(VerticalAlignment.CENTER);
    // style 14
    coverAdvanceTotalStyle = wbk.createCellStyle();
    coverAdvanceTotalStyle.setFont(timesNewRoman10pt);
    coverAdvanceTotalStyle.setAlignment(HorizontalAlignment.CENTER);
    coverAdvanceTotalStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    coverAdvanceTotalStyle.setDataFormat(creationHelper.createDataFormat().getFormat(
        "$#,##0.00_);[Red]($#,##0.00)"));
    // style 15
    borderLeftTopBot = wbk.createCellStyle();
    borderLeftTopBot.setBorderBottom(BorderStyle.THIN);
    borderLeftTopBot.setBorderTop(BorderStyle.THIN);
    borderLeftTopBot.setBorderLeft(BorderStyle.THIN);
    borderLeftTopBot.setAlignment(HorizontalAlignment.CENTER);
    borderLeftTopBot.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 16
    borderRightTopBot = wbk.createCellStyle();
    borderRightTopBot.setBorderBottom(BorderStyle.THIN);
    borderRightTopBot.setBorderTop(BorderStyle.THIN);
    borderRightTopBot.setBorderRight(BorderStyle.THIN);
    // style 17
    borderTopBot = wbk.createCellStyle();
    borderTopBot.setBorderBottom(BorderStyle.THIN);
    borderTopBot.setBorderTop(BorderStyle.THIN);
  }

  private void setSheetStyles() {
    // style 1
    wageNameHiddenStyle = wbk.createCellStyle();
    wageNameHiddenStyle.setFont(timesNewRoman9ptItalics);
    wageNameHiddenStyle.setAlignment(HorizontalAlignment.RIGHT);
    wageNameHiddenStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 2
    sheetWageRateHidden = wbk.createCellStyle();
    sheetWageRateHidden.setFont(timesNewRoman9ptWhite);
    sheetWageRateHidden.setAlignment(HorizontalAlignment.CENTER);
    sheetWageRateHidden.setVerticalAlignment(VerticalAlignment.BOTTOM);
    sheetWageRateHidden.setDataFormat(creationHelper.createDataFormat().getFormat("0.00"));
    // style 3
    sheetWageRateNonHidden = wbk.createCellStyle();
    sheetWageRateNonHidden.setFont(timesNewRoman9pt);
    sheetWageRateNonHidden.setAlignment(HorizontalAlignment.CENTER);
    sheetWageRateNonHidden.setVerticalAlignment(VerticalAlignment.BOTTOM);
    sheetWageRateNonHidden.setDataFormat(creationHelper.createDataFormat().getFormat("0.00"));
    // style 4
    jobDateStyle = wbk.createCellStyle();
    jobDateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("m/d/yy"));
    jobDateStyle.setFont(timesNewRoman11pt);
    jobDateStyle.setAlignment(HorizontalAlignment.CENTER);
    jobDateStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 5
    jobCodeStyle = wbk.createCellStyle();
    jobCodeStyle.setFont(timesNewRoman11pt);
    jobCodeStyle.setAlignment(HorizontalAlignment.CENTER);
    jobCodeStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 6
    jobTimeStyle = wbk.createCellStyle();
    jobTimeStyle.setFont(timesNewRoman11pt);
    jobTimeStyle.setAlignment(HorizontalAlignment.CENTER);
    jobTimeStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    jobTimeStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0000"));
    // style 7
    jobTimeStyleUncertain = wbk.createCellStyle();
    jobTimeStyleUncertain.setFont(timesNewRoman11ptRed);
    jobTimeStyleUncertain.setAlignment(HorizontalAlignment.CENTER);
    jobTimeStyleUncertain.setVerticalAlignment(VerticalAlignment.BOTTOM);
    jobTimeStyleUncertain.setDataFormat(creationHelper.createDataFormat().getFormat("0000"));
    // style 8
    sheetEmployeeNameStyle = wbk.createCellStyle();
    sheetEmployeeNameStyle.setFont(timesNewRoman11pt);
    sheetEmployeeNameStyle.setAlignment(HorizontalAlignment.RIGHT);
    sheetEmployeeNameStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 9
    sheetEmployeeNameStyleUnderLined = wbk.createCellStyle();
    sheetEmployeeNameStyleUnderLined.setFont(calibri11pt);
    sheetEmployeeNameStyleUnderLined.setBorderBottom(BorderStyle.THIN);
    sheetEmployeeNameStyleUnderLined.setAlignment(HorizontalAlignment.LEFT);
    sheetEmployeeNameStyleUnderLined.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 10
    sheetPeriodTitleStyle = wbk.createCellStyle();
    sheetPeriodTitleStyle.setFont(calibri11pt);
    sheetPeriodTitleStyle.setBorderBottom(BorderStyle.THIN);
    sheetPeriodTitleStyle.setAlignment(HorizontalAlignment.CENTER);
    sheetPeriodTitleStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 11
    jobHourStyle = wbk.createCellStyle();
    jobHourStyle.setFont(timesNewRoman11pt);
    jobHourStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0"));
    jobHourStyle.setAlignment(HorizontalAlignment.CENTER);
    jobHourStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 12
    jobHourStyleUncertain = wbk.createCellStyle();
    jobHourStyleUncertain.setFont(timesNewRoman11ptRed);
    jobHourStyleUncertain.setDataFormat(creationHelper.createDataFormat().getFormat("0"));
    jobHourStyleUncertain.setAlignment(HorizontalAlignment.CENTER);
    jobHourStyleUncertain.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 13
    jobMileageStyle = wbk.createCellStyle();
    jobMileageStyle.setFont(timesNewRoman9ptItalics);
    jobMileageStyle.setAlignment(HorizontalAlignment.CENTER);
    jobMileageStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    jobMileageStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0.0_);[Red](0.0)"));
    // style 14
    sheetWageHourHeaderStyle = wbk.createCellStyle();
    sheetWageHourHeaderStyle.setFont(timesNewRoman8ptBlueItalicized);
    sheetWageHourHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
    sheetWageHourHeaderStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // style 15
    sheetHourTotalStyle = wbk.createCellStyle();
    sheetHourTotalStyle.setFont(timesNewRoman10pt);
    sheetHourTotalStyle.setAlignment(HorizontalAlignment.CENTER);
    sheetHourTotalStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    sheetHourTotalStyle.setBorderLeft(BorderStyle.THIN);
    sheetHourTotalStyle.setBorderTop(BorderStyle.THIN);
    sheetHourTotalStyle.setBorderBottom(BorderStyle.THIN);
    sheetHourTotalStyle.setBorderRight(BorderStyle.THIN);
    // style 16
    sheetMileageTotalStyle = wbk.createCellStyle();
    sheetMileageTotalStyle.setFont(timesNewRoman10pt);
    sheetMileageTotalStyle.setAlignment(HorizontalAlignment.CENTER);
    sheetMileageTotalStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    sheetMileageTotalStyle.setDataFormat(creationHelper.createDataFormat().getFormat(
        "0.0_);[Red](0.0)"));
    sheetMileageTotalStyle.setBorderBottom(BorderStyle.THIN);
    sheetMileageTotalStyle.setBorderTop(BorderStyle.THIN);
    sheetMileageTotalStyle.setBorderRight(BorderStyle.THIN);
    sheetMileageTotalStyle.setBorderLeft(BorderStyle.THIN);
    // style 17
    timesNewRoman9ptRightJustifiedItalicized = wbk.createCellStyle();
    timesNewRoman9ptRightJustifiedItalicized.setFont(timesNewRoman9ptItalics);
    timesNewRoman9ptRightJustifiedItalicized.setAlignment(HorizontalAlignment.RIGHT);
    timesNewRoman9ptRightJustifiedItalicized.setVerticalAlignment(VerticalAlignment.CENTER);
    // style 18
    sheetMileageRateStyle = wbk.createCellStyle();
    sheetMileageRateStyle.setFont(timesNewRoman10pt);
    sheetMileageRateStyle.setAlignment(HorizontalAlignment.CENTER);
    sheetMileageRateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    sheetMileageRateStyle.setBorderBottom(BorderStyle.THIN);
    sheetMileageRateStyle.setBorderTop(BorderStyle.THIN);
    sheetMileageRateStyle.setBorderLeft(BorderStyle.THIN);
    sheetMileageRateStyle.setBorderRight(BorderStyle.THIN);
    sheetMileageRateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("$#,##0.000"));
    // style 19
    sheetAdvanceTotalStyle = wbk.createCellStyle();
    sheetAdvanceTotalStyle.setFont(timesNewRoman10pt);
    sheetAdvanceTotalStyle.setAlignment(HorizontalAlignment.CENTER);
    sheetAdvanceTotalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    sheetAdvanceTotalStyle.setDataFormat(creationHelper.createDataFormat().getFormat(
        "$#,##0.00_);[Red]($#,##0.00)"));
    sheetAdvanceTotalStyle.setBorderBottom(BorderStyle.THIN);
    sheetAdvanceTotalStyle.setBorderTop(BorderStyle.THIN);
    sheetAdvanceTotalStyle.setBorderRight(BorderStyle.THIN);
    sheetAdvanceTotalStyle.setBorderLeft(BorderStyle.THIN);
    // style 20
    stdDeductionStyle = wbk.createCellStyle();
    stdDeductionStyle.setFont(timesNewRoman9ptBlueItalics);
    stdDeductionStyle.setAlignment(HorizontalAlignment.CENTER);
    stdDeductionStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);

  }

  private void setGlobalStyles() {
    // set hour styles
    wageNameStyle = wbk.createCellStyle();
    wageNameStyle.setFont(timesNewRoman10ptU);
    wageNameStyle.setAlignment(HorizontalAlignment.CENTER);
    wageNameStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    // set custodialPlus Font
    companyNameStyle = wbk.createCellStyle();
    companyNameStyle.setFont(cambria16ptU);
    // keeps rich text string workbook independent
    companyTitleRichString = wbk.getCreationHelper().createRichTextString(
        "CUSTODIAL-PLUS SERVICES");
    companyTitleRichString.applyFont(14, 23, cambria16ptNoU);
    companyNameStyle.setAlignment(HorizontalAlignment.CENTER);
    companyNameStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    timeSheePeriodStyle = wbk.createCellStyle();
    timeSheePeriodStyle.setFont(timesNewRoman12pt);
    timeSheePeriodStyle.setAlignment(HorizontalAlignment.CENTER);
    timeSheePeriodStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    timeSheePeriodStyle.setBorderBottom(BorderStyle.THIN);
    currencyStyleBottomJustified = wbk.createCellStyle();
    currencyStyleBottomJustified.setDataFormat(creationHelper.createDataFormat().getFormat(
        "$#,##0.00"));
    currencyStyleBottomJustified.setFont(timesNewRoman10pt);
    currencyStyleBottomJustified.setAlignment(HorizontalAlignment.CENTER);
    currencyStyleBottomJustified.setVerticalAlignment(VerticalAlignment.BOTTOM);
    regOTDTStyle = wbk.createCellStyle();
    regOTDTStyle.setFont(timesNewRoman10pt);
    regOTDTStyle.setAlignment(HorizontalAlignment.CENTER);
    regOTDTStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    timesNewRoman10ptRightJustified = wbk.createCellStyle();
    timesNewRoman10ptRightJustified.setFont(timesNewRoman10pt);
    timesNewRoman10ptRightJustified.setAlignment(HorizontalAlignment.RIGHT);
    timesNewRoman10ptRightJustified.setVerticalAlignment(VerticalAlignment.CENTER);
    borderedCurrencyStyle = wbk.createCellStyle();
    borderedCurrencyStyle.setFont(timesNewRoman10pt);
    borderedCurrencyStyle.setAlignment(HorizontalAlignment.CENTER);
    borderedCurrencyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    borderedCurrencyStyle.setBorderBottom(BorderStyle.THIN);
    borderedCurrencyStyle.setBorderTop(BorderStyle.THIN);
    borderedCurrencyStyle.setBorderLeft(BorderStyle.THIN);
    borderedCurrencyStyle.setBorderRight(BorderStyle.THIN);
    borderedCurrencyStyle.setDataFormat(creationHelper.createDataFormat().getFormat("$#,##0.00"));
    totalHoursStyle = wbk.createCellStyle();
    totalHoursStyle.setFont(timesNewRoman10pt);
    totalHoursStyle.setAlignment(HorizontalAlignment.CENTER);
    totalHoursStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    totalHoursStyle.setBorderBottom(BorderStyle.THIN);
    totalHoursStyle.setBorderTop(BorderStyle.THIN);
    totalHoursStyle.setBorderLeft(BorderStyle.THIN);
    totalHoursStyle.setBorderRight(BorderStyle.THIN);
    totalHoursStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0"));
    timesNewRoman9ptLeftJustifiedItalicized = wbk.createCellStyle();
    timesNewRoman9ptLeftJustifiedItalicized.setFont(timesNewRoman9ptItalics);
    timesNewRoman9ptLeftJustifiedItalicized.setAlignment(HorizontalAlignment.LEFT);
    timesNewRoman9ptLeftJustifiedItalicized.setVerticalAlignment(VerticalAlignment.CENTER);
    // blueCoverTotalHeaderStyleL = wbk.createCellStyle();
    // blueCoverTotalHeaderStyleL.setFont(timesNewRoman10ptBlue);
    // blueCoverTotalHeaderStyleL.setAlignment(HorizontalAlignment.LEFT);
    // blueCoverTotalHeaderStyleL.setVerticalAlignment(VerticalAlignment.CENTER);
    currencyStyleCentered = wbk.createCellStyle();
    currencyStyleCentered.setDataFormat(creationHelper.createDataFormat().getFormat("$#,##0.00"));
    currencyStyleCentered.setFont(timesNewRoman10pt);
    currencyStyleCentered.setAlignment(HorizontalAlignment.CENTER);
    currencyStyleCentered.setVerticalAlignment(VerticalAlignment.CENTER);

    coverEmployeeMileageStyle = wbk.createCellStyle();
    coverEmployeeMileageStyle.setDataFormat(creationHelper.createDataFormat().getFormat(
        "$#,##0.00"));
    coverEmployeeMileageStyle.setFont(timesNewRoman10pt);
    coverEmployeeMileageStyle.setAlignment(HorizontalAlignment.CENTER);
    coverEmployeeMileageStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    if (wbk instanceof HSSFWorkbook) {
      HSSFPalette palette = ((HSSFWorkbook) wbk).getCustomPalette();
      // replacing the standard red with freebsd.org red
      palette.setColorAtIndex(IndexedColors.LIGHT_GREEN.index, (byte) 255, // RGB red
          (byte) 255, // RGB green
          (byte) 153 // RGB blue
      );
      coverEmployeeMileageStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
    } else {
      ((XSSFCellStyle) coverEmployeeMileageStyle).setFillForegroundColor(new XSSFColor(
          new java.awt.Color(255, 255, 153)));
    }
    coverEmployeeMileageStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
  }

  private EmployeeSheet getEmployeeSheetFor(String abbreviation, TimeSheet tsheet) {
    for (EmployeeSheet es : tsheet.getEmployeeSheets()) {
      if (es.getEmployee().getAbbreviation().equals(abbreviation)) {
        return es;
      }
    }
    throw new IllegalStateException("could not find employee sheet for " + abbreviation);
  }

  private void createEmployeeSheetData(TimeSheet tsheet, int numEmployees) {
    for (Sheet sheet : wbk) {
      if (sheet.equals(cover) || sheet.equals(divider)) {
        continue;
      }
      int i = 0;
      if (wbk.getSheetIndex(sheet) < wbk.getSheetIndex(divider)) {
        // grab eSheet
        EmployeeSheet eSheet = getEmployeeSheetFor(sheet.getSheetName(), tsheet);
        int dataRows = 0;
        int linesUsed = 0;
        for (JobEntry je : eSheet.getJobs()) {
          linesUsed = createEmployeeSheetJobData(sheet, je, dataRows);
          dataRows = dataRows + linesUsed;
        }
        i = dataRows;
      } else {
        i = 1;
      }
      // 12 header lines in an eSheet
      i = Constants.eSheet_header_rows + i;
      // divider line
      sheet.getRow(i).setHeightInPoints(6);
      // std hr & mileage headers
      sheet.getRow(i + 1).setHeightInPoints(11);
      sheet.getRow(i + 1).getCell(5).setCellStyle(sheetWageHourHeaderStyle);
      sheet.getRow(i + 1).getCell(5).setCellValue("(Standard Work Hours)");
      sheet.getRow(i + 1).getCell(9).setCellStyle(sheetWageHourHeaderStyle);
      sheet.getRow(i + 1).getCell(9).setCellValue("(Wood Work Hours)");
      sheet.getRow(i + 1).getCell(13).setCellStyle(sheetWageHourHeaderStyle);
      sheet.getRow(i + 1).getCell(13).setCellValue("(Premium Work Hours)");
      sheet.getRow(i + 1).getCell(17).setCellStyle(sheetWageHourHeaderStyle);
      sheet.getRow(i + 1).getCell(17).setCellValue("(Travel Hours)");
      sheet.getRow(i + 1).getCell(20).setCellStyle(sheetWageHourHeaderStyle);
      sheet.getRow(i + 1).getCell(20).setCellValue("(Mileage)");
      // monthly hour and mileage totals
      // set cover sheet references as well
      sheet.getRow(i + 2).setHeightInPoints(12);
      // hour totals
      sheet.getRow(i + 2).getCell(3).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 2).getCell(3).setCellValue("MONTHLY TOTALS:");
      // j< 15 so it will run 4 times (4+16 = 20), will not run a 5th time
      String strFormula = "";
      for (int j = 4; j < 17; j = j + 4) {
        sheet.getRow(i + 2).getCell(j).setCellStyle(sheetHourTotalStyle);
        sheet.getRow(i + 2).getCell(j).setCellType(CellType.FORMULA);
        CellReference topRef = ExporterUtil.getCellReference(sheet.getRow(11).getCell(j), sheet);
        CellReference botRef = ExporterUtil.getCellReference(sheet.getRow(i).getCell(j), sheet);
        strFormula = "SUM(" + topRef.formatAsString() + ":" + botRef.formatAsString() + ")";
        sheet.getRow(i + 2).getCell(j).setCellFormula(strFormula);
        sheet.getRow(i + 2).getCell(j + 1).setCellStyle(sheetHourTotalStyle);
        sheet.getRow(i + 2).getCell(j + 1).setCellType(CellType.FORMULA);
        // set cover sheet reference
        CellReference totalRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(j),
            sheet);
        strFormula = totalRef.formatAsString();
        cover.getRow(12 + wbk.getSheetIndex(sheet)).getCell(j - 2).setCellFormula(strFormula);
        cover.getRow(12 + wbk.getSheetIndex(sheet)).getCell(j - 2).setCellStyle(regOTDTStyle);
        topRef = ExporterUtil.getCellReference(sheet.getRow(11).getCell(j + 1), sheet);
        botRef = ExporterUtil.getCellReference(sheet.getRow(i).getCell(j + 1), sheet);
        strFormula = "SUM(" + topRef.formatAsString() + ":" + botRef.formatAsString() + ")";
        sheet.getRow(i + 2).getCell(j + 1).setCellFormula(strFormula);
        // set cover sheet reference
        totalRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(j + 1), sheet);
        strFormula = totalRef.formatAsString();
        cover.getRow(12 + wbk.getSheetIndex(sheet)).getCell(j - 1).setCellFormula(strFormula);
        cover.getRow(12 + wbk.getSheetIndex(sheet)).getCell(j - 1).setCellStyle(regOTDTStyle);
        sheet.getRow(i + 2).getCell(j + 2).setCellStyle(sheetHourTotalStyle);
        sheet.getRow(i + 2).getCell(j + 2).setCellType(CellType.FORMULA);
        topRef = ExporterUtil.getCellReference(sheet.getRow(11).getCell(j + 2), sheet);
        botRef = ExporterUtil.getCellReference(sheet.getRow(i).getCell(j + 2), sheet);
        strFormula = "SUM(" + topRef.formatAsString() + ":" + botRef.formatAsString() + ")";
        sheet.getRow(i + 2).getCell(j + 2).setCellFormula(strFormula);
        // set cover sheet reference
        totalRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(j + 2), sheet);
        strFormula = totalRef.formatAsString();
        cover.getRow(12 + wbk.getSheetIndex(sheet)).getCell(j).setCellFormula(strFormula);
        cover.getRow(12 + wbk.getSheetIndex(sheet)).getCell(j).setCellStyle(regOTDTStyle);
      }
      // mileage totals
      // cover sheet col is 20
      sheet.getRow(i + 2).getCell(20).setCellStyle(sheetMileageTotalStyle);
      CellReference topRef = ExporterUtil.getCellReference(sheet.getRow(11).getCell(20), sheet);
      CellReference botRef = ExporterUtil.getCellReference(sheet.getRow(i).getCell(20), sheet);
      strFormula = "SUM(" + topRef.formatAsString() + ":" + botRef.formatAsString() + ")";
      sheet.getRow(i + 2).getCell(20).setCellType(CellType.FORMULA);
      sheet.getRow(i + 2).getCell(20).setCellFormula(strFormula);
      // divider line
      sheet.getRow(i + 3).setHeightInPoints(15);
      // std pay, total hrs, avg/hr, std mileage rate - i + 4
      sheet.getRow(i + 4).setHeightInPoints(15);
      // set std pay
      sheet.getRow(i + 4).getCell(3).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 4).getCell(3).setCellValue("Standard Pay:");
      sheet.getRow(i + 4).getCell(4).setCellStyle(currencyStyleCentered);
      sheet.getRow(i + 4).getCell(4).setCellType(CellType.FORMULA);
      CellReference stdWageRef = ExporterUtil.getCellReference(sheet.getRow(8).getCell(6), sheet);
      CellReference stdHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(4),
          sheet);
      CellReference otHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(5), sheet);
      CellReference dtHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(6), sheet);
      strFormula = "((" + stdHrsRef.formatAsString() + "*" + stdWageRef.formatAsString() + ")+("
          + otHrsRef.formatAsString() + "* 1.5 *" + stdWageRef.formatAsString() + ")+(" + dtHrsRef
              .formatAsString() + "* 2 *" + stdWageRef.formatAsString() + "))/100";
      sheet.getRow(i + 4).getCell(4).setCellFormula(strFormula);
      // set total hrs
      sheet.getRow(i + 4).getCell(8).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 4).getCell(8).setCellValue("TOTAL HOURS:");
      sheet.getRow(i + 4).getCell(9).setCellStyle(totalHoursStyle);
      CellReference hourStartRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(4),
          sheet);
      CellReference hourEndRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(18),
          sheet);
      strFormula = "SUM(" + hourStartRef.formatAsString() + ":" + hourEndRef.formatAsString() + ")";
      sheet.getRow(i + 4).getCell(9).setCellType(CellType.FORMULA);
      sheet.getRow(i + 4).getCell(9).setCellFormula(strFormula);
      sheet.getRow(i + 4).getCell(10).setCellStyle(borderedCurrencyStyle);
      sheet.getRow(i + 4).getCell(10).setCellType(CellType.FORMULA);
      // update cover sheet reference
      CellReference totalHoursRef = ExporterUtil.getCellReference(sheet.getRow(i + 4).getCell(9),
          sheet);
      cover.getRow(12 + numEmployees + 5).getCell(8).setCellType(CellType.FORMULA);
      String curFormula = cover.getRow(12 + numEmployees + 5).getCell(8).getCellFormula();
      strFormula = totalHoursRef.formatAsString();
      if (wbk.getSheetIndex(sheet) == 1) {
        curFormula = strFormula;
      } else {
        curFormula = curFormula + "+" + strFormula;
      }
      cover.getRow(12 + numEmployees + 5).getCell(8).setCellFormula(curFormula);
      // set avg/hr
      CellReference totalWagesRef = ExporterUtil.getCellReference(sheet.getRow(i + 11).getCell(9),
          sheet);
      strFormula = "IF(" + totalHoursRef.formatAsString() + "=0,0," + totalWagesRef.formatAsString()
          + "/" + totalHoursRef.formatAsString() + "*100)";
      sheet.getRow(i + 4).getCell(10).setCellFormula(strFormula);
      sheet.getRow(i + 4).getCell(11).setCellStyle(timesNewRoman9ptLeftJustifiedItalicized);
      sheet.getRow(i + 4).getCell(11).setCellValue("ave/hr");
      // set std mileage rate
      sheet.getRow(i + 4).getCell(19).setCellStyle(timesNewRoman9ptRightJustifiedItalicized);
      sheet.getRow(i + 4).getCell(19).setCellValue("Mileage Rate:");
      sheet.getRow(i + 4).getCell(20).setCellStyle(sheetMileageRateStyle);
      sheet.getRow(i + 4).getCell(20).setCellType(CellType.FORMULA);
      CellReference coverSheetStdMilRate = ExporterUtil.getCellReference(cover.getRow(12
          + numEmployees + 8).getCell(18), cover);
      strFormula = coverSheetStdMilRate.formatAsString();
      sheet.getRow(i + 4).getCell(20).setCellFormula(strFormula);
      // wood pay, cost / hr, # trips, set mileage - i+5
      sheet.getRow(i + 5).setHeightInPoints(15);
      // set wood pay
      sheet.getRow(i + 5).getCell(3).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 5).getCell(3).setCellValue("Wood Pay:");
      sheet.getRow(i + 5).getCell(4).setCellStyle(currencyStyleCentered);
      sheet.getRow(i + 5).getCell(4).setCellType(CellType.FORMULA);
      final CellReference woodWageRef = ExporterUtil.getCellReference(sheet.getRow(8).getCell(10),
          sheet);
      stdHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(8), sheet);
      otHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(9), sheet);
      dtHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(10), sheet);
      strFormula = "((" + stdHrsRef.formatAsString() + "*" + woodWageRef.formatAsString() + ")+("
          + otHrsRef.formatAsString() + "* 1.5 *" + woodWageRef.formatAsString() + ")+(" + dtHrsRef
              .formatAsString() + "* 2 *" + woodWageRef.formatAsString() + "))/100";
      sheet.getRow(i + 5).getCell(4).setCellFormula(strFormula);
      // set cost / hr
      sheet.getRow(i + 5).getCell(10).setCellStyle(borderedCurrencyStyle);
      CellReference avgWageperHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 4).getCell(
          10), sheet);
      double empCost = tsheet.getEmployerCostMultiplier().add(new BigDecimal("1.00")).doubleValue();
      strFormula = avgWageperHrsRef.formatAsString() + "*" + Double.toString(empCost);
      sheet.getRow(i + 5).getCell(10).setCellFormula(strFormula);
      sheet.getRow(i + 5).getCell(11).setCellStyle(timesNewRoman9ptLeftJustifiedItalicized);
      sheet.getRow(i + 5).getCell(11).setCellValue("cost/hr");
      // set # trips
      sheet.getRow(i + 5).getCell(16).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 5).getCell(16).setCellValue(0);
      sheet.getRow(i + 5).getCell(17).setCellStyle(timesNewRoman9ptLeftJustifiedItalicized);
      sheet.getRow(i + 5).getCell(17).setCellValue("trips");
      // set mileage
      sheet.getRow(i + 5).getCell(19).setCellStyle(timesNewRoman9ptRightJustifiedItalicized);
      sheet.getRow(i + 5).getCell(19).setCellValue("Set Mileage:");
      sheet.getRow(i + 5).getCell(20).setCellStyle(borderedCurrencyStyle);
      sheet.getRow(i + 5).getCell(20).setCellType(CellType.FORMULA);
      CellReference coverSheetStdSetMil = ExporterUtil.getCellReference(cover.getRow(12
          + numEmployees + 9).getCell(18), cover);
      CellReference numTripsRef = ExporterUtil.getCellReference(sheet.getRow(i + 5).getCell(16),
          sheet);
      strFormula = coverSheetStdSetMil.formatAsString() + "*" + numTripsRef.formatAsString();
      sheet.getRow(i + 5).getCell(20).setCellFormula(strFormula);
      // prem pay, std mileage pay - i + 6
      sheet.getRow(i + 6).setHeightInPoints(15);
      // set prem pay
      sheet.getRow(i + 6).getCell(3).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 6).getCell(3).setCellValue("Premium Pay:");
      sheet.getRow(i + 6).getCell(4).setCellStyle(currencyStyleCentered);
      sheet.getRow(i + 6).getCell(4).setCellType(CellType.FORMULA);
      final CellReference premWageRef = ExporterUtil.getCellReference(sheet.getRow(8).getCell(14),
          sheet);
      stdHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(12), sheet);
      otHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(13), sheet);
      dtHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(14), sheet);
      strFormula = "((" + stdHrsRef.formatAsString() + "*" + premWageRef.formatAsString() + ")+("
          + otHrsRef.formatAsString() + "* 1.5 *" + premWageRef.formatAsString() + ")+(" + dtHrsRef
              .formatAsString() + "* 2 *" + premWageRef.formatAsString() + "))/100";
      sheet.getRow(i + 6).getCell(4).setCellFormula(strFormula);
      // std mileage pay
      sheet.getRow(i + 6).getCell(19).setCellStyle(timesNewRoman9ptRightJustifiedItalicized);
      sheet.getRow(i + 6).getCell(19).setCellValue("Standard Mileage:");
      sheet.getRow(i + 6).getCell(20).setCellStyle(borderedCurrencyStyle);
      sheet.getRow(i + 6).getCell(20).setCellType(CellType.FORMULA);
      CellReference stdMileageRateRef = ExporterUtil.getCellReference(sheet.getRow(i + 4).getCell(
          20), sheet);
      CellReference mileageTotalRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(20),
          sheet);
      strFormula = stdMileageRateRef.formatAsString() + "*" + mileageTotalRef.formatAsString();
      sheet.getRow(i + 6).getCell(20).setCellFormula(strFormula);
      // travel pay - i+7
      sheet.getRow(i + 7).setHeightInPoints(15);
      sheet.getRow(i + 7).getCell(3).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 7).getCell(3).setCellValue("Travel Pay:");
      sheet.getRow(i + 7).getCell(4).setCellStyle(currencyStyleCentered);
      sheet.getRow(i + 7).getCell(4).setCellType(CellType.FORMULA);
      final CellReference travelWageRef = ExporterUtil.getCellReference(sheet.getRow(8).getCell(18),
          sheet);
      stdHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(16), sheet);
      otHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(17), sheet);
      dtHrsRef = ExporterUtil.getCellReference(sheet.getRow(i + 2).getCell(18), sheet);
      strFormula = "((" + stdHrsRef.formatAsString() + "*" + travelWageRef.formatAsString() + ")+("
          + otHrsRef.formatAsString() + "* 1.5 *" + travelWageRef.formatAsString() + ")+("
          + dtHrsRef.formatAsString() + "* 2 *" + travelWageRef.formatAsString() + "))/100";
      sheet.getRow(i + 7).getCell(4).setCellFormula(strFormula);
      // flat rate pay
      sheet.getRow(i + 8).setHeightInPoints(13);
      sheet.getRow(i + 8).getCell(3).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 8).getCell(3).setCellValue("FLAT RATE:");
      sheet.getRow(i + 8).getCell(4).setCellStyle(currencyStyleCentered);
      // TODO ask about flat rate pay
      sheet.getRow(i + 8).getCell(4).setCellFormula("0");
      // bonuses
      sheet.getRow(i + 9).setHeightInPoints(13);
      sheet.getRow(i + 9).getCell(3).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 9).getCell(3).setCellValue("Bonuses:");
      sheet.getRow(i + 9).getCell(4).setCellStyle(currencyStyleCentered);
      for (EmployeeSheet empSheet : tsheet.getEmployeeSheets()) {
        if (empSheet.getEmployee().getAbbreviation().equals(sheet.getSheetName())) {
          sheet.getRow(i + 9).getCell(4).setCellValue(empSheet.getTotalBonuses().doubleValue());
        }
      }
      // divider line
      sheet.getRow(i + 10).setHeightInPoints(5);
      // total pay, total check, mileage check
      sheet.getRow(i + 11).setHeightInPoints(15);
      // set total pay
      sheet.getRow(i + 11).getCell(3).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 11).getCell(3).setCellValue("TOTAL PAY:");
      sheet.getRow(i + 11).getCell(4).setCellStyle(borderedCurrencyStyle);
      // set next cell too since they will be merged
      sheet.getRow(i + 11).getCell(5).setCellStyle(borderedCurrencyStyle);
      sheet.getRow(i + 11).getCell(4).setCellType(CellType.FORMULA);
      CellReference firstPayTotal = ExporterUtil.getCellReference(sheet.getRow(i + 4).getCell(4),
          sheet);
      CellReference lastPayTotal = ExporterUtil.getCellReference(sheet.getRow(i + 9).getCell(4),
          sheet);
      strFormula = "SUM(" + firstPayTotal.formatAsString() + ":" + lastPayTotal.formatAsString()
          + ")";
      sheet.getRow(i + 11).getCell(4).setCellFormula(strFormula);
      // update cover sheet reference
      CellReference totalPayRef = ExporterUtil.getCellReference(sheet.getRow(i + 11).getCell(4),
          sheet);
      curFormula = cover.getRow(12 + numEmployees + 5).getCell(2).getCellFormula();
      strFormula = totalPayRef.formatAsString();
      if (wbk.getSheetIndex(sheet) == 1) {
        curFormula = strFormula;
      } else {
        curFormula = curFormula + "+" + strFormula;
      }
      cover.getRow(12 + numEmployees + 5).getCell(2).setCellFormula(curFormula);
      // set mileage check
      sheet.getRow(i + 11).getCell(19).setCellStyle(timesNewRoman9ptRightJustifiedItalicized);
      sheet.getRow(i + 11).getCell(19).setCellValue("Mileage Check:");
      sheet.getRow(i + 11).getCell(20).setCellStyle(borderedCurrencyStyle);
      sheet.getRow(i + 11).getCell(20).setCellType(CellType.FORMULA);
      CellReference stdMileagePayRef = ExporterUtil.getCellReference(sheet.getRow(i + 6).getCell(
          20), sheet);
      sheet.getRow(i + 11).getCell(20).setCellFormula(stdMileagePayRef.formatAsString());
      // advances, gas advances
      sheet.getRow(i + 12).setHeightInPoints(15);
      sheet.getRow(i + 12).getCell(3).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 12).getCell(3).setCellValue("Advances:");
      sheet.getRow(i + 12).getCell(4).setCellStyle(sheetAdvanceTotalStyle);
      // set next cell as well since it will be merged
      sheet.getRow(i + 12).getCell(5).setCellStyle(sheetAdvanceTotalStyle);
      for (EmployeeSheet empSheet : tsheet.getEmployeeSheets()) {
        if (empSheet.getEmployee().getAbbreviation().equals(sheet.getSheetName())) {
          sheet.getRow(i + 12).getCell(4).setCellValue(empSheet.getTotalAdvances().negate()
              .doubleValue());
        }
      }
      sheet.getRow(i + 12).getCell(19).setCellStyle(timesNewRoman9ptRightJustifiedItalicized);
      sheet.getRow(i + 12).getCell(19).setCellValue("Gas Advances:");
      sheet.getRow(i + 12).getCell(20).setCellStyle(sheetAdvanceTotalStyle);
      for (EmployeeSheet empSheet : tsheet.getEmployeeSheets()) {
        if (empSheet.getEmployee().getAbbreviation().equals(sheet.getSheetName())) {
          sheet.getRow(i + 12).getCell(20).setCellValue(empSheet.getTotalGasAdvances()
              .doubleValue());
        }
      }
      // set cover sheet reference
      CellReference advanceRef = ExporterUtil.getCellReference(sheet.getRow(i + 12).getCell(4),
          sheet);
      CellReference gasAdvanceRef = ExporterUtil.getCellReference(sheet.getRow(i + 12).getCell(20),
          sheet);
      strFormula = advanceRef.formatAsString() + "+" + gasAdvanceRef.formatAsString();
      // employee advances total column on cover sheet is 19
      cover.getRow(12 + wbk.getSheetIndex(sheet)).getCell(19).setCellFormula(strFormula);
      cover.getRow(12 + wbk.getSheetIndex(sheet)).getCell(19).setCellStyle(coverAdvanceTotalStyle);
      // set cover sheet reference
      CellReference mileagePayRef = ExporterUtil.getCellReference(sheet.getRow(i + 11).getCell(20),
          sheet);

      strFormula = mileagePayRef.formatAsString();
      // employee mileage total column on cover sheet is 18
      cover.getRow(12 + wbk.getSheetIndex(sheet)).getCell(18).setCellFormula(strFormula);
      FormulaEvaluator evaluator = wbk.getCreationHelper().createFormulaEvaluator();
      CellValue cellValue = evaluator.evaluate(sheet.getRow(i + 11).getCell(20));
      if (cellValue.getNumberValue() > 0.0) {
        cover.getRow(12 + wbk.getSheetIndex(sheet)).getCell(18).setCellStyle(
            coverEmployeeMileageStyle);
      } else {
        cover.getRow(12 + wbk.getSheetIndex(sheet)).getCell(18).setCellStyle(currencyStyleCentered);
      }
      // set total check
      sheet.getRow(i + 11).getCell(8).setCellStyle(timesNewRoman10ptRightJustified);
      sheet.getRow(i + 11).getCell(8).setCellValue("TOTAL CHECK:");
      sheet.getRow(i + 11).getCell(9).setCellStyle(borderedCurrencyStyle);
      // set next cell too cuz it will be merged
      sheet.getRow(i + 11).getCell(10).setCellStyle(borderedCurrencyStyle);
      sheet.getRow(i + 11).getCell(9).setCellType(CellType.FORMULA);

      strFormula = totalPayRef.formatAsString() + "+" + advanceRef.formatAsString() + "+"
          + mileagePayRef.formatAsString() + "+" + gasAdvanceRef.formatAsString();
      sheet.getRow(i + 11).getCell(9).setCellFormula(strFormula);
      // merge final regions
      ExporterUtil.setMergedRegions(i + 4, i + 4, 4, 5, sheet);
      ExporterUtil.setMergedRegions(i + 5, i + 5, 4, 5, sheet);
      ExporterUtil.setMergedRegions(i + 6, i + 6, 4, 5, sheet);
      ExporterUtil.setMergedRegions(i + 7, i + 7, 4, 5, sheet);
      ExporterUtil.setMergedRegions(i + 8, i + 8, 4, 5, sheet);
      ExporterUtil.setMergedRegions(i + 9, i + 9, 4, 5, sheet);
      ExporterUtil.setMergedRegions(i + 11, i + 11, 4, 5, sheet);
      ExporterUtil.setMergedRegions(i + 11, i + 11, 9, 10, sheet);
      ExporterUtil.setMergedRegions(i + 12, i + 12, 4, 5, sheet);
    }
  }

  // returns # lines used (at least 1)
  // index represents how many rows have been used on job data before this job, thus this jobs final
  // calculated time values are at header_rows + index + i
  private int createEmployeeSheetJobData(Sheet sheet, JobEntry je, int index) {
    // set date
    int header_rows = Constants.eSheet_header_rows;
    final Cell dateCell = sheet.getRow(header_rows + index).getCell(0);
    dateCell.setCellStyle(jobDateStyle);
    dateCell.setCellValue(je.getDate().toDateTimeAtStartOfDay().toDate());
    // set code
    final Cell codeCell = sheet.getRow(header_rows + index).getCell(1);
    codeCell.setCellStyle(jobCodeStyle);
    codeCell.setCellValue(je.getCode());
    Cell timeInCell = null;
    Cell timeOutCell = null;
    int i = 0; // # of extra lines used on times
    int linesNeeded = i;
    if (je.hasStdDeduction()) {
      linesNeeded = je.getTimes().size() + 1;
    } else {
      linesNeeded = je.getTimes().size();
    }
    if (!je.isFlatRate()) {
      for (i = 0; i < linesNeeded; i++) {
        if (je.hasStdDeduction() && i == 0) {
          timeInCell = sheet.getRow(header_rows + index + i).getCell(2);
          timeOutCell = sheet.getRow(header_rows + index + i).getCell(3);
          timeInCell.setCellStyle(jobTimeStyle);
          timeOutCell.setCellStyle(jobTimeStyle);
          timeInCell.setCellValue(je.getStdDeduction().getTimeIn().getTimeString());
          timeOutCell.setCellValue(je.getStdDeduction().getTimeOut().getTimeString());
        } else {
          // set time in
          if (!je.hasStdDeduction()) {

            timeInCell = sheet.getRow(header_rows + index + i).getCell(2);
            timeInCell.setCellStyle(jobTimeStyle);
            timeInCell.setCellValue(je.getTimesIn().get(i).getTimeString());
            // set time out
            timeOutCell = sheet.getRow(header_rows + index + i).getCell(3);
            timeOutCell.setCellStyle(jobTimeStyle);
            timeOutCell.setCellValue(je.getTimesOut().get(i).getTimeString());
            // mark red if uncertain
            if (je.getTimesIn().get(i).isUncertain()) {
              timeInCell.setCellStyle(jobTimeStyleUncertain);
            }
            if (je.getTimesOut().get(i).isUncertain()) {
              timeOutCell.setCellStyle(jobTimeStyleUncertain);
            }
          } else {

            timeInCell = sheet.getRow(header_rows + index + i).getCell(2);
            timeInCell.setCellStyle(jobTimeStyle);
            timeInCell.setCellValue(je.getTimesIn().get(i - 1).getTimeString());
            // set time out
            timeOutCell = sheet.getRow(header_rows + index + i).getCell(3);
            timeOutCell.setCellStyle(jobTimeStyle);
            timeOutCell.setCellValue(je.getTimesOut().get(i - 1).getTimeString());
            // mark red if uncertain
            if (je.getTimesIn().get(i - 1).isUncertain()) {
              timeInCell.setCellStyle(jobTimeStyleUncertain);
            }
            if (je.getTimesOut().get(i - 1).isUncertain()) {
              timeOutCell.setCellStyle(jobTimeStyleUncertain);
            }
          }
          if ((i + 1) == linesNeeded) {
            break;
          }
        }
      }
    } else {
      timeInCell = sheet.getRow(header_rows + index + i).getCell(2);
      timeOutCell = sheet.getRow(header_rows + index + i).getCell(3);
      timeInCell.setCellStyle(jobTimeStyle);
      timeOutCell.setCellStyle(jobTimeStyle);
      timeInCell.setCellValue("FLAT");
      timeOutCell.setCellValue("RATE");
      // increment row counter so the next i will be the next row
      // i = i + 1;
    }
    // set hours
    final Wage jw = je.getWage();
    int offset = 0;
    if (jw.getName().equals("Wood")) {
      offset = 4;
    } else if (jw.getName().equals("Premium")) {
      offset = 8;
    } else if (jw.getName().equals("Travel")) {
      offset = 12;
    }
    final Cell hourCellReg = sheet.getRow(header_rows + index + i).getCell(4 + offset);
    final Cell hourCellOT = sheet.getRow(header_rows + index + i).getCell(5 + offset);
    final Cell hourCellDT = sheet.getRow(header_rows + index + i).getCell(6 + offset);
    if (je.hasUncertainTime()) {
      hourCellReg.setCellStyle(jobHourStyleUncertain);
      hourCellOT.setCellStyle(jobHourStyleUncertain);
      hourCellDT.setCellStyle(jobHourStyleUncertain);
    } else {
      hourCellReg.setCellStyle(jobHourStyle);
      hourCellOT.setCellStyle(jobHourStyle);
      hourCellDT.setCellStyle(jobHourStyle);
    }
    // only put cell values if time is nonzero
    if (!je.getRegularTime().equals(BigDecimal.ZERO)) {
      hourCellReg.setCellValue(je.getRegularTime().intValue());
    }
    if (!je.getOverTime().equals(BigDecimal.ZERO)) {
      hourCellOT.setCellValue(je.getOverTime().intValue());
    }
    if (!je.getDoubleTime().equals(BigDecimal.ZERO)) {
      hourCellDT.setCellValue(je.getDoubleTime().intValue());
    }
    final Cell travelCell = sheet.getRow(header_rows + index + i).getCell(20);
    travelCell.setCellStyle(jobMileageStyle);
    // using equals does not trigger when mileage is 0 (it is 0.00000...)
    // set travel mileage / method
    if (!(je.getMileage().compareTo(BigDecimal.ZERO) == 0) && !je.getMileage().equals(
        BigDecimal.ZERO)) {
      travelCell.setCellValue(je.getMileage().doubleValue());
    } else if (!(je.getTravelMethod().isEmpty())) {
      travelCell.setCellValue(je.getTravel());
    }
    // set std deduction values
    if (je.hasStdDeduction()) {
      // set "std deduction"
      Cell stdDeducCell = null;
      if (!je.getRegularTime().equals(BigDecimal.ZERO)) {
        stdDeducCell = sheet.getRow(header_rows + index).getCell(4 + offset);
      } else if (!je.getOverTime().equals(BigDecimal.ZERO)) {
        stdDeducCell = sheet.getRow(header_rows + index).getCell(5 + offset);
      } else {
        stdDeducCell = sheet.getRow(header_rows + index).getCell(6 + offset);
      }
      stdDeducCell.setCellStyle(stdDeductionStyle);
      stdDeducCell.setCellValue("std deduction");
      // set std deduc mileage
      if (!(je.getMileage().compareTo(BigDecimal.ZERO) == 0) && !je.getMileage().equals(
          BigDecimal.ZERO)) {
        sheet.getRow(header_rows + index + i - 1).getCell(20).setCellStyle(jobMileageStyle);
        sheet.getRow(header_rows + index + i - 1).getCell(20).setCellValue(je.getStdDeduction()
            .getStdDeducMileage().negate().doubleValue());
      }
    }
    return (i + 1);
  }

  private Employee getEmployee(List<Employee> employees, String abb) {
    for (Employee employee : employees) {
      if (employee.getAbbreviation().equals(abb)) {
        return employee;
      }
    }
    throw new IllegalStateException("employee did not exist");
  }

  private void createCoverWageStructureHeader() {
    // wage titles
    final Cell langCell = cover.getRow(11).getCell(23);
    final Cell regCell = cover.getRow(11).getCell(25);
    final Cell woodCell = cover.getRow(11).getCell(26);
    final Cell premCell = cover.getRow(11).getCell(27);
    final Cell travelCell = cover.getRow(11).getCell(28);
    langCell.setCellStyle(wageNameStyle);
    langCell.setCellValue("LANG");
    regCell.setCellStyle(wageNameStyle);
    regCell.setCellValue("REG");
    woodCell.setCellStyle(wageNameStyle);
    woodCell.setCellValue("WOOD");
    premCell.setCellStyle(wageNameStyle);
    premCell.setCellValue("PREM");
    travelCell.setCellStyle(wageNameStyle);
    travelCell.setCellValue("TRAVEL");
    ExporterUtil.setMergedRegions(8, 8, 23, 28, cover);
    // wage struc title
    final Cell wageStrucTitleCell1 = cover.getRow(8).getCell(23);
    final Cell wageStrucTitleCell2 = cover.getRow(8).getCell(24);
    final Cell wageStrucTitleCell3 = cover.getRow(8).getCell(25);
    final Cell wageStrucTitleCell4 = cover.getRow(8).getCell(26);
    final Cell wageStrucTitleCell5 = cover.getRow(8).getCell(27);
    final Cell wageStrucTitleCell6 = cover.getRow(8).getCell(28);
    wageStrucTitleCell1.setCellStyle(borderLeftTopBot);
    wageStrucTitleCell1.setCellValue("WAGE STRUCTURE");
    wageStrucTitleCell2.setCellStyle(borderTopBot);
    wageStrucTitleCell3.setCellStyle(borderTopBot);
    wageStrucTitleCell4.setCellStyle(borderTopBot);
    wageStrucTitleCell5.setCellStyle(borderTopBot);
    wageStrucTitleCell6.setCellStyle(borderRightTopBot);
  }

  private void createCoverSheetFooter(int rowStart, TimeSheet tsheet) {
    int i = rowStart;
    // set gap row
    cover.getRow(i).setHeightInPoints(9);
    // set hour total line
    Cell totalsCell = cover.getRow(i + 1).getCell(1);
    totalsCell.setCellStyle(timesNewRoman10ptRightJustified);
    totalsCell.setCellValue("TOTALS:");
    String strFormula = "";
    // j< 15 so it will run 4 times (2+16 = 18), will not run a 5th time
    for (int j = 2; j < 15; j = j + 4) {
      cover.getRow(i + 1).getCell(j).setCellStyle(coverHourTotalStyle);
      cover.getRow(i + 1).getCell(j).setCellType(CellType.FORMULA);
      CellReference topRef = ExporterUtil.getCellReference(cover.getRow(12).getCell(j), cover);
      CellReference botRef = ExporterUtil.getCellReference(cover.getRow(i).getCell(j), cover);
      strFormula = "SUM(" + topRef.formatAsString() + ":" + botRef.formatAsString() + ")";
      cover.getRow(i + 1).getCell(j).setCellFormula(strFormula);
      cover.getRow(i + 1).getCell(j + 1).setCellStyle(coverHourTotalStyle);
      cover.getRow(i + 1).getCell(j + 1).setCellType(CellType.FORMULA);
      topRef = ExporterUtil.getCellReference(cover.getRow(12).getCell(j + 1), cover);
      botRef = ExporterUtil.getCellReference(cover.getRow(i).getCell(j + 1), cover);
      strFormula = "SUM(" + topRef.formatAsString() + ":" + botRef.formatAsString() + ")";
      cover.getRow(i + 1).getCell(j + 1).setCellFormula(strFormula);
      cover.getRow(i + 1).getCell(j + 2).setCellStyle(coverHourTotalStyle);
      cover.getRow(i + 1).getCell(j + 2).setCellType(CellType.FORMULA);
      topRef = ExporterUtil.getCellReference(cover.getRow(12).getCell(j + 2), cover);
      botRef = ExporterUtil.getCellReference(cover.getRow(i).getCell(j + 2), cover);
      strFormula = "SUM(" + topRef.formatAsString() + ":" + botRef.formatAsString() + ")";
      cover.getRow(i + 1).getCell(j + 2).setCellFormula(strFormula);
    }
    // set travel mileage sum
    cover.getRow(i + 1).getCell(18).setCellStyle(coverMileageTotalStyle);
    CellReference topRef = ExporterUtil.getCellReference(cover.getRow(12).getCell(18), cover);
    CellReference botRef = ExporterUtil.getCellReference(cover.getRow(i).getCell(18), cover);
    strFormula = "SUM(" + topRef.formatAsString() + ":" + botRef.formatAsString() + ")";
    cover.getRow(i + 1).getCell(18).setCellFormula(strFormula);
    cover.getRow(i + 1).setHeightInPoints(15);
    // set gap row
    cover.getRow(i + 2).setHeightInPoints(12);
    // set total wages, total hours
    cover.getRow(i + 3).setHeightInPoints(12);
    cover.getRow(i + 3).getCell(1).setCellStyle(timesNewRoman12ptRightJustified);
    cover.getRow(i + 3).getCell(1).setCellValue("TOTAL WAGES:");
    cover.getRow(i + 3).getCell(2).setCellType(CellType.FORMULA);
    cover.getRow(i + 3).getCell(2).setCellStyle(borderedCurrencyStyle);
    // this formula will be updated later
    cover.getRow(i + 3).getCell(2).setCellFormula("0");
    cover.getRow(i + 3).getCell(3).setCellStyle(borderedCurrencyStyle);
    cover.getRow(i + 3).getCell(6).setCellStyle(timesNewRoman10ptRightJustified);
    cover.getRow(i + 3).getCell(6).setCellValue("TOTAL HOURS:");
    cover.getRow(i + 3).getCell(7).setCellStyle(totalHoursStyle);
    CellReference coverHrStart = ExporterUtil.getCellReference(cover.getRow(i + 1).getCell(2),
        cover);
    CellReference coverHrEnd = ExporterUtil.getCellReference(cover.getRow(i + 1).getCell(16),
        cover);
    strFormula = "SUM(" + coverHrStart.formatAsString() + ":" + coverHrEnd.formatAsString() + ")";
    cover.getRow(i + 3).getCell(7).setCellType(CellType.FORMULA);
    cover.getRow(i + 3).getCell(7).setCellFormula(strFormula);
    cover.getRow(i + 3).getCell(8).setCellStyle(totalHoursStyle);
    cover.getRow(i + 3).getCell(8).setCellType(CellType.FORMULA);
    // this formula will be updated later
    cover.getRow(i + 3).getCell(8).setCellFormula("0");
    cover.getRow(i + 3).getCell(9).setCellValue("(match?)");
    cover.getRow(i + 3).getCell(9).setCellStyle(timesNewRoman9ptLeftJustifiedItalicized);
    // set employer cost, travel wage
    cover.getRow(i + 4).setHeightInPoints(12);
    // set emp cost
    cover.getRow(i + 4).getCell(1).setCellStyle(timesNewRoman10ptRightJustifiedBlue);
    cover.getRow(i + 4).getCell(1).setCellValue("Employer Cost:");
    cover.getRow(i + 4).getCell(2).setCellStyle(currencyStyleBlueCentered);
    cover.getRow(i + 4).getCell(2).setCellType(CellType.FORMULA);
    // set travel wage
    cover.getRow(i + 4).getCell(17).setCellStyle(timesNewRoman10ptRightJustifiedBlue);
    cover.getRow(i + 4).getCell(17).setCellValue("Travel Wage:");
    cover.getRow(i + 4).getCell(18).setCellStyle(currencyStyleBlueCentered);
    cover.getRow(i + 4).getCell(18).setCellValue(tsheet.getTravelWageRate().doubleValue());
    CellReference totalWagesRef = ExporterUtil.getCellReference(cover.getRow(i + 3).getCell(2),
        cover);
    strFormula = totalWagesRef.formatAsString() + "*" + tsheet.getEmployerCostMultiplier();
    cover.getRow(i + 4).getCell(2).setCellType(CellType.FORMULA);
    cover.getRow(i + 4).getCell(2).setCellFormula(strFormula);
    // set advancements, avg wage/hr
    cover.getRow(i + 5).setHeightInPoints(12);
    // set advances
    cover.getRow(i + 5).getCell(1).setCellStyle(timesNewRoman10ptRightJustified);
    cover.getRow(i + 5).getCell(1).setCellValue("Advancements:");
    cover.getRow(i + 5).getCell(2).setCellType(CellType.FORMULA);
    cover.getRow(i + 5).getCell(2).setCellStyle(coverEmployeeAdvanceStyle);
    CellReference advTopRef = ExporterUtil.getCellReference(cover.getRow(12).getCell(19), cover);
    CellReference advBotRef = ExporterUtil.getCellReference(cover.getRow(i - 2).getCell(19), cover);
    strFormula = "SUM(" + advTopRef.formatAsString() + ":" + advBotRef.formatAsString() + ")";
    cover.getRow(i + 5).getCell(2).setCellType(CellType.FORMULA);
    cover.getRow(i + 5).getCell(2).setCellFormula(strFormula);
    // set avg wage/hr
    cover.getRow(i + 5).getCell(6).setCellStyle(timesNewRoman10ptRightJustified);
    cover.getRow(i + 5).getCell(6).setCellValue("Ave Wage / Hr:");
    cover.getRow(i + 5).getCell(7).setCellStyle(currencyStyleCentered);
    CellReference totalHoursRef = ExporterUtil.getCellReference(cover.getRow(i + 3).getCell(7),
        cover);
    strFormula = "IF(" + totalHoursRef.formatAsString() + "=0,0," + totalWagesRef.formatAsString()
        + "/" + totalHoursRef.formatAsString() + "*100)";
    cover.getRow(i + 5).getCell(7).setCellType(CellType.FORMULA);
    cover.getRow(i + 5).getCell(7).setCellFormula(strFormula);
    // set mileage, avg cost/hr, std mileage rate
    cover.getRow(i + 6).setHeightInPoints(12);
    // set mileage
    cover.getRow(i + 6).getCell(1).setCellStyle(timesNewRoman10ptRightJustified);
    cover.getRow(i + 6).getCell(1).setCellValue("Mileage:");
    cover.getRow(i + 6).getCell(2).setCellType(CellType.FORMULA);
    cover.getRow(i + 6).getCell(2).setCellStyle(currencyStyleCentered);
    CellReference mileageTotalRef = ExporterUtil.getCellReference(cover.getRow(i + 1).getCell(18),
        cover);
    strFormula = mileageTotalRef.formatAsString();
    cover.getRow(i + 6).getCell(2).setCellFormula(strFormula);
    // set avg cost/hr
    cover.getRow(i + 6).getCell(6).setCellStyle(timesNewRoman10ptRightJustified);
    // wages + cost + mileage
    cover.getRow(i + 6).getCell(6).setCellValue("Ave Cost / Hr:");
    cover.getRow(i + 6).getCell(7).setCellType(CellType.FORMULA);
    CellReference empCostRef = ExporterUtil.getCellReference(cover.getRow(i + 4).getCell(2), cover);
    CellReference mileageRef = ExporterUtil.getCellReference(cover.getRow(i + 6).getCell(2), cover);
    strFormula = "IF(" + totalHoursRef.formatAsString() + "=0,0," + "(" + totalWagesRef
        .formatAsString() + "+" + empCostRef.formatAsString() + "+" + mileageRef.formatAsString()
        + ")/" + totalHoursRef.formatAsString() + "*100)";
    cover.getRow(i + 6).getCell(7).setCellFormula(strFormula);
    cover.getRow(i + 6).getCell(7).setCellStyle(currencyStyleCentered);
    // set std mileage rate
    cover.getRow(i + 6).getCell(17).setCellStyle(timesNewRoman8ptRightJustifiedBlue);
    cover.getRow(i + 6).getCell(17).setCellValue("Standard Mileage Rate:");
    cover.getRow(i + 6).getCell(18).setCellStyle(currencyStyleSmallBlueCenteredThreeDecimals);
    cover.getRow(i + 6).getCell(18).setCellValue(tsheet.getStandardMileageRate().doubleValue());
    // set grand total, set mileage rate
    cover.getRow(i + 7).setHeightInPoints(12);
    // set grand total
    cover.getRow(i + 7).getCell(1).setCellStyle(timesNewRoman12ptRightJustified);
    cover.getRow(i + 7).getCell(1).setCellValue("GRAND TOTAL:");
    cover.getRow(i + 7).getCell(2).setCellType(CellType.FORMULA);
    cover.getRow(i + 7).getCell(2).setCellStyle(currencyStyleCentered);
    strFormula = "SUM(" + totalWagesRef.formatAsString() + ":" + mileageRef.formatAsString() + ")";
    cover.getRow(i + 7).getCell(2).setCellFormula(strFormula);
    // set the "set mileage rate"
    cover.getRow(i + 7).getCell(17).setCellStyle(timesNewRoman8ptRightJustifiedBlue);
    cover.getRow(i + 7).getCell(17).setCellValue("Set Mileage Rate:");
    cover.getRow(i + 7).getCell(18).setCellStyle(currencyStyleSmallBlueCentered);
    cover.getRow(i + 7).getCell(18).setCellValue(new BigDecimal("10").doubleValue());
    for (int k = i + 3; k < i + 8; k++) {
      // merge total regions
      cover.addMergedRegion(new CellRangeAddress(k, k, 2, 3));
    }

  }

  // also sets employee names in column 1 on cover
  private void createCoverWageStructureAndFooter(List<Employee> employees, TimeSheet tsheet) {
    createCoverWageStructureHeader();
    // start employee totals
    int i = Constants.cover_header_rows; // 14th row is where header ends (so i=13, 0 based)
    // iterate through sheets (skipping cover), finding the employee by sheet name
    for (int j = 1; j < wbk.getNumberOfSheets(); j++) {
      final Sheet sheet = wbk.getSheetAt(j);
      if (sheet.equals(divider)) {
        // set divider row and increment lines used
        cover.getRow(i).setHeightInPoints(6);
        i = i + 1;
        continue;
      }
      // set column 0 name on cover
      final Cell coverNameCell = cover.getRow(i).getCell(0);
      final Cell sheetNameCell = sheet.getRow(5).getCell(1);
      final CellReference sheetCellRef = ExporterUtil.getCellReference(sheetNameCell, sheet);
      final String strFormula1 = "CLEAN(" + sheetCellRef.formatAsString() + ")";
      coverNameCell.setCellStyle(coverEmployeeNameStyle);
      coverNameCell.setCellType(CellType.FORMULA);
      coverNameCell.setCellFormula(strFormula1);
      // set wage struc name on cover
      Cell wageStrucNameCell = cover.getRow(i).getCell(21);
      final CellReference nameCellRef = ExporterUtil.getCellReference(coverNameCell, cover);
      final String strFormula2 = "CLEAN(" + nameCellRef.formatAsString() + ")";
      wageStrucNameCell.setCellStyle(coverEmployeeNameStyle);
      wageStrucNameCell.setCellType(CellType.FORMULA);
      wageStrucNameCell.setCellFormula(strFormula2);
      // find employee for the current sheet
      final Employee emp = getEmployee(employees, sheet.getSheetName());
      final Cell langCell = cover.getRow(i).getCell(23);
      final Cell regCell = cover.getRow(i).getCell(25);
      final Cell woodCell = cover.getRow(i).getCell(26);
      final Cell premCell = cover.getRow(i).getCell(27);
      final Cell travelCell = cover.getRow(i).getCell(28);
      final BigDecimal langValue = emp.getLanguageBonus();
      if (langValue.compareTo(BigDecimal.ZERO) > 0) {
        langCell.setCellStyle(currencyStyleBlueBottomJustified);
      } else {
        langCell.setCellStyle(currencyStyleBottomJustified);
      }
      langCell.setCellValue(emp.getLanguageBonus().doubleValue());
      regCell.setCellStyle(currencyStyleBottomJustified);
      regCell.setCellValue(emp.getWage("Regular").getRate().doubleValue());
      woodCell.setCellStyle(currencyStyleBottomJustified);
      woodCell.setCellValue(emp.getWage("Wood").getRate().doubleValue());
      premCell.setCellStyle(currencyStyleBottomJustified);
      premCell.setCellValue(emp.getWage("Premium").getRate().doubleValue());
      travelCell.setCellStyle(currencyStyleBottomJustified);
      travelCell.setCellValue(tsheet.getTravelWageRate().doubleValue());
      i = i + 1;
    }
    this.createCoverSheetFooter(i, tsheet);
  }

  private void createEmployeeSheetHeaders(List<Employee> employees) {
    final Cell periodCell = cover.getRow(7).getCell(15);
    final CellReference periodCellRef = ExporterUtil.getCellReference(periodCell, cover);
    for (Sheet sheet : wbk) {
      if (sheet.equals(divider) || sheet.equals(cover)) {
        continue;
      }
      final Cell sixthRowCell1 = sheet.getRow(5).getCell(0);
      final Cell sixthRowCell2 = sheet.getRow(5).getCell(1);
      final Cell sixthRowCell3 = sheet.getRow(5).getCell(2);
      final Cell sixthRowCell4 = sheet.getRow(5).getCell(3);
      final Cell sixthRowCell15 = sheet.getRow(5).getCell(14);
      final Cell sixthRowCell16 = sheet.getRow(5).getCell(15);
      final Cell sixthRowCell17 = sheet.getRow(5).getCell(16);
      final Cell sixthRowCell18 = sheet.getRow(5).getCell(17);
      final Cell sixthRowCell19 = sheet.getRow(5).getCell(18);
      final Cell eighthRowCell6 = sheet.getRow(7).getCell(5);
      final Cell eighthRowCell10 = sheet.getRow(7).getCell(9);
      final Cell eighthRowCell14 = sheet.getRow(7).getCell(13);
      final Cell eighthRowCell18 = sheet.getRow(7).getCell(17);
      final Cell ninthRowCell6 = sheet.getRow(8).getCell(5);
      final Cell ninthRowCell10 = sheet.getRow(8).getCell(9);
      final Cell ninthRowCell14 = sheet.getRow(8).getCell(13);
      final Cell ninthRowCell18 = sheet.getRow(8).getCell(17);
      final Cell ninthRowCell7 = sheet.getRow(8).getCell(6);
      final Cell ninthRowCell11 = sheet.getRow(8).getCell(10);
      final Cell ninthRowCell15 = sheet.getRow(8).getCell(14);
      final Cell ninthRowCell19 = sheet.getRow(8).getCell(18);
      final Cell tenthRowCell5 = sheet.getRow(9).getCell(4);
      final Cell tenthRowCell4 = sheet.getRow(9).getCell(3);
      final Cell tenthRowCell3 = sheet.getRow(9).getCell(2);
      final Cell eleventhRowCell1 = sheet.getRow(10).getCell(0);
      final Cell eleventhRowCell2 = sheet.getRow(10).getCell(1);
      final Cell eleventhRowCell3 = sheet.getRow(10).getCell(2);
      final Cell eleventhRowCell4 = sheet.getRow(10).getCell(3);
      // set cell values
      // set employee name
      sixthRowCell1.setCellStyle(sheetEmployeeNameStyle);
      sixthRowCell1.setCellValue("NAME:");
      sixthRowCell2.setCellStyle(sheetEmployeeNameStyleUnderLined);
      sixthRowCell2.setCellValue(getEmployeeNameFor(sheet, employees));
      sixthRowCell3.setCellStyle(sheetEmployeeNameStyleUnderLined);
      sixthRowCell4.setCellStyle(sheetEmployeeNameStyleUnderLined);
      // set timesheet period
      sixthRowCell15.setCellStyle(sheetEmployeeNameStyle);
      sixthRowCell15.setCellValue("PERIOD:");
      sixthRowCell15.setCellStyle(timeSheePeriodStyle);
      final String strFormula1 = "CLEAN(" + periodCellRef.formatAsString() + ")";
      sixthRowCell16.setCellType(CellType.FORMULA);
      sixthRowCell16.setCellFormula(strFormula1);
      sixthRowCell16.setCellStyle(timeSheePeriodStyle);
      sixthRowCell17.setCellStyle(timeSheePeriodStyle);
      sixthRowCell18.setCellStyle(timeSheePeriodStyle);
      sixthRowCell19.setCellStyle(timeSheePeriodStyle);
      // set wage titles
      eighthRowCell6.setCellStyle(wageNameStyle);
      eighthRowCell6.setCellValue("STANDARD WORK HOURS");
      eighthRowCell10.setCellStyle(wageNameStyle);
      eighthRowCell10.setCellValue("GYM FLOOR HOURS");
      eighthRowCell14.setCellStyle(wageNameStyle);
      eighthRowCell14.setCellValue("PREMIUM WAGE HOURS");
      eighthRowCell18.setCellStyle(wageNameStyle);
      eighthRowCell18.setCellValue("TRAVEL HOURS");
      ninthRowCell6.setCellStyle(wageNameHiddenStyle);
      ninthRowCell6.setCellValue("Std Wage (hidden):");
      ninthRowCell10.setCellStyle(wageNameHiddenStyle);
      ninthRowCell10.setCellValue("Wood Wage (hidden):");
      ninthRowCell14.setCellStyle(wageNameHiddenStyle);
      ninthRowCell14.setCellValue("Prem Wage (hidden):");
      ninthRowCell18.setCellStyle(wageNameHiddenStyle);
      ninthRowCell18.setCellValue("Travel Wage:");
      // set wages
      final Cell langCell = cover.getRow(wbk.getSheetIndex(sheet) + 12).getCell(23);
      final Cell regCell = cover.getRow(wbk.getSheetIndex(sheet) + 12).getCell(25);
      final Cell woodCell = cover.getRow(wbk.getSheetIndex(sheet) + 12).getCell(26);
      final Cell premCell = cover.getRow(wbk.getSheetIndex(sheet) + 12).getCell(27);
      final Cell travelCell = cover.getRow(wbk.getSheetIndex(sheet) + 12).getCell(28);
      final CellReference langCellRef = ExporterUtil.getCellReference(langCell, cover);
      final CellReference regCellRef = ExporterUtil.getCellReference(regCell, cover);
      final CellReference woodCellRef = ExporterUtil.getCellReference(woodCell, cover);
      final CellReference premCellRef = ExporterUtil.getCellReference(premCell, cover);
      final CellReference travelCellRef = ExporterUtil.getCellReference(travelCell, cover);
      final String strFormula2 = langCellRef.formatAsString() + "+" + regCellRef.formatAsString();
      ninthRowCell7.setCellType(CellType.FORMULA);
      ninthRowCell7.setCellStyle(sheetWageRateHidden);
      ninthRowCell7.setCellFormula(strFormula2);
      final String strFormula3 = woodCellRef.formatAsString();
      ninthRowCell11.setCellType(CellType.FORMULA);
      ninthRowCell11.setCellStyle(sheetWageRateHidden);
      ninthRowCell11.setCellFormula(strFormula3);
      final String strFormula4 = langCellRef.formatAsString() + "+" + premCellRef.formatAsString();
      ninthRowCell15.setCellType(CellType.FORMULA);
      ninthRowCell15.setCellStyle(sheetWageRateHidden);
      ninthRowCell15.setCellFormula(strFormula4);
      final String strFormula5 = travelCellRef.formatAsString();
      ninthRowCell19.setCellType(CellType.FORMULA);
      ninthRowCell19.setCellStyle(sheetWageRateNonHidden);
      ninthRowCell19.setCellFormula(strFormula5);
      // set reg ot dt headers starting on tenthRowCell5
      setRegOTDTHourHeaders(tenthRowCell5);
      // set time in time out, date and job code headers
      tenthRowCell3.setCellStyle(regOTDTStyle);
      tenthRowCell3.setCellValue("TIME");
      tenthRowCell4.setCellStyle(regOTDTStyle);
      tenthRowCell4.setCellValue("TIME");
      eleventhRowCell1.setCellStyle(wageNameStyle);
      eleventhRowCell1.setCellValue("DATE");
      eleventhRowCell2.setCellStyle(wageNameStyle);
      eleventhRowCell2.setCellValue("JOB CODE");
      eleventhRowCell3.setCellStyle(wageNameStyle);
      eleventhRowCell3.setCellValue("IN");
      eleventhRowCell4.setCellStyle(wageNameStyle);
      eleventhRowCell4.setCellValue("OUT");
      // merge period region
      ExporterUtil.setMergedRegions(5, 5, 15, 18, sheet);
    }
  }

  private String getEmployeeNameFor(Sheet sheet, List<Employee> employees) {
    for (Employee emp : employees) {
      if (emp.getAbbreviation().equals(sheet.getSheetName())) {
        return emp.getLastName().toUpperCase() + ", " + emp.getFirstName();
      }
    }
    throw new IllegalStateException(
        "Employee for " + sheet.getSheetName() + " did not exist in object model");
  }

  private void createGenericHeaders() {
    for (Sheet sheet : wbk) {
      if (sheet.equals(divider)) {
        continue;
      }
      final Cell firstRowCell = sheet.getRow(0).getCell(0);
      final Cell thirdRowCell = sheet.getRow(2).getCell(0);
      firstRowCell.setCellStyle(companyNameStyle);
      thirdRowCell.setCellStyle(coverSheetTitleStyle);
      if (!sheet.equals(cover)) {
        CellReference firstCell = ExporterUtil.getCellReference(firstRowCell, cover);
        CellReference thirdCell = ExporterUtil.getCellReference(thirdRowCell, cover);
        final String strFormula1 = "CLEAN(" + firstCell.formatAsString() + ")";
        firstRowCell.setCellType(CellType.FORMULA);
        firstRowCell.setCellFormula(strFormula1);
        final String strFormula2 = "CLEAN(" + thirdCell.formatAsString() + ")";
        thirdRowCell.setCellType(CellType.FORMULA);
        thirdRowCell.setCellFormula(strFormula2);
      } else {
        firstRowCell.setCellValue(companyTitleRichString);
        thirdRowCell.setCellValue("* INDIVIDUAL PAYROLL SHEET *");
      }
      ExporterUtil.setMergedRegions(0, 0, 0, 18, sheet);
      ExporterUtil.setMergedRegions(2, 2, 0, 18, sheet);
      if (sheet.equals(cover)) {
        final Cell fifthRowCell = sheet.getRow(4).getCell(0);
        fifthRowCell.setCellStyle(coverSheetTitleStyle);
        fifthRowCell.setCellValue("==COVER SHEET==");
        ExporterUtil.setMergedRegions(4, 4, 0, 18, sheet);
      }
    }
  }

  private void createCoverSheetHeader(String timeSheetPeriodString) {
    // set timesheet period
    final Cell periodCell1 = cover.getRow(7).getCell(14);
    final Cell periodCell2 = cover.getRow(7).getCell(15);
    final Cell periodCell3 = cover.getRow(7).getCell(16);
    final Cell periodCell4 = cover.getRow(7).getCell(17);
    final Cell periodCell5 = cover.getRow(7).getCell(18);
    periodCell1.setCellStyle(sheetPeriodTitleStyle);
    periodCell1.setCellValue("PERIOD:");
    periodCell2.setCellStyle(timeSheePeriodStyle);
    periodCell2.setCellValue(timeSheetPeriodString);
    // set border of the time sheet period
    periodCell3.setCellStyle(timeSheePeriodStyle);
    periodCell4.setCellStyle(timeSheePeriodStyle);
    periodCell5.setCellStyle(timeSheePeriodStyle);
    // merge timesheet period region
    ExporterUtil.setMergedRegions(7, 7, 15, 18, cover);
    // set wage titles
    final Cell tenthRowCell4 = cover.getRow(9).getCell(3);
    final Cell tenthRowCell7 = cover.getRow(9).getCell(7);
    final Cell tenthRowCell11 = cover.getRow(9).getCell(11);
    final Cell tenthRowCell15 = cover.getRow(9).getCell(15);
    tenthRowCell4.setCellStyle(wageNameStyle);
    tenthRowCell4.setCellValue("STANDARD WORK HOURS");
    tenthRowCell7.setCellStyle(wageNameStyle);
    tenthRowCell7.setCellValue("GYM FLOOR HOURS");
    tenthRowCell11.setCellStyle(wageNameStyle);
    tenthRowCell11.setCellValue("PREMIUM WAGE HOURS");
    tenthRowCell15.setCellStyle(wageNameStyle);
    tenthRowCell15.setCellValue("TRAVEL HOURS");
    final Cell regOTDTinitializerCell = cover.getRow(10).getCell(2);
    this.setRegOTDTHourHeaders(regOTDTinitializerCell);
    // set Advances header
    final Cell advanceHeaderCell = cover.getRow(11).getCell(19);
    advanceHeaderCell.setCellStyle(coverAdvanceTitleStyle);
    advanceHeaderCell.setCellValue("ADVANCES");
  }

  // initializationCell is the first cell that should say "REG"
  private void setRegOTDTHourHeader(Cell initializationCell) {
    initializationCell.setCellStyle(regOTDTStyle);
    initializationCell.setCellValue("REG");
    Cell nextCellInRow = initializationCell.getRow().getCell(initializationCell.getColumnIndex()
        + 1);
    nextCellInRow.setCellValue("OT");
    nextCellInRow.setCellStyle(regOTDTStyle);
    Cell nextNextCellInRow = nextCellInRow.getRow().getCell(nextCellInRow.getColumnIndex() + 1);
    nextNextCellInRow.setCellValue("DT");
    nextNextCellInRow.setCellStyle(regOTDTStyle);
    Cell nextCellInColumn = initializationCell.getSheet().getRow(initializationCell.getRowIndex()
        + 1).getCell(initializationCell.getColumnIndex());
    Cell nextCellInRowInColumn = nextCellInRow.getSheet().getRow(nextCellInRow.getRowIndex() + 1)
        .getCell(nextCellInRow.getColumnIndex());
    Cell nextnextCellInRowInColumn = nextNextCellInRow.getSheet().getRow(nextNextCellInRow
        .getRowIndex() + 1).getCell(nextNextCellInRow.getColumnIndex());
    nextCellInColumn.setCellStyle(wageNameStyle);
    nextCellInRowInColumn.setCellStyle(wageNameStyle);
    nextnextCellInRowInColumn.setCellStyle(wageNameStyle);
    setAsHourCell(nextCellInColumn);
    setAsHourCell(nextCellInRowInColumn);
    setAsHourCell(nextnextCellInRowInColumn);

  }

  // initializationCell is the first cell that should say "REG"
  // adds travel mil part too
  private void setRegOTDTHourHeaders(Cell initializationCell) {
    // set REG OT DT Headers 4 times, 4 over each time
    this.setRegOTDTHourHeader(initializationCell);
    this.setRegOTDTHourHeader(initializationCell.getRow().getCell(initializationCell
        .getColumnIndex() + 4));
    this.setRegOTDTHourHeader(initializationCell.getRow().getCell(initializationCell
        .getColumnIndex() + 8));
    this.setRegOTDTHourHeader(initializationCell.getRow().getCell(initializationCell
        .getColumnIndex() + 12));
    // set TRAVEL header for TRAVEL MIL.
    Cell travelCell = initializationCell.getRow().getCell(initializationCell.getColumnIndex() + 16);
    travelCell.setCellStyle(regOTDTStyle);
    travelCell.setCellValue("TRAVEL");
    // set MIL.
    Cell milCell = initializationCell.getSheet().getRow(initializationCell.getRowIndex() + 1)
        .getCell(initializationCell.getColumnIndex() + 16);
    milCell.setCellStyle(wageNameStyle);
    milCell.setCellValue("MIL.");
  }

  private Cell setAsHourCell(Cell cell) {
    cell.setCellStyle(wageNameStyle);
    cell.setCellValue("HOURS");
    return cell;
  }

  // Points to estimate from
  // 5100 -> 1.67 inches in excel
  // 4000 -> 1.31
  // 3000 -> 0.97
  // 2800 -> 0.92
  // 2760 -> 0.90
  // 2750 -> 0.89
  // 2500 -> 0.82
  // 2000 -> 0.65
  // 1950 -> 0.64
  // 1900 -> 0.63
  // 1800 -> 0.58
  // 1750 -> 0.57
  // 1500 -> 0.49
  // 1400 -> 0.46
  // 1350 -> 0.44
  // 1300 -> 0.42
  // 450 -> 0.15
  // 420 -> 0.14
  // 400 -> 0.13
  // 300 -> 0.1
  private void setEmployeeSheetColumnWidths() {
    for (Sheet eSheet : wbk) {
      if (eSheet.equals(cover) || eSheet.getSheetName().equals("ACTIVE | INACTIVE")) {
        continue;
      }
      eSheet.setColumnWidth(0, 2250);
      eSheet.setColumnWidth(1, 4200);
      eSheet.setColumnWidth(2, 1500);
      eSheet.setColumnWidth(3, 1500);
      eSheet.setColumnWidth(4, 1750);
      eSheet.setColumnWidth(5, 1750);
      eSheet.setColumnWidth(6, 1750);
      eSheet.setColumnWidth(7, 450);
      eSheet.setColumnWidth(8, 1750);
      eSheet.setColumnWidth(9, 1750);
      eSheet.setColumnWidth(10, 1750);
      eSheet.setColumnWidth(11, 450);
      eSheet.setColumnWidth(12, 1750);
      eSheet.setColumnWidth(13, 1750);
      eSheet.setColumnWidth(14, 1750);
      eSheet.setColumnWidth(15, 450);
      eSheet.setColumnWidth(16, 1750);
      eSheet.setColumnWidth(17, 1750);
      eSheet.setColumnWidth(18, 1750);
      eSheet.setColumnWidth(19, 450);
      eSheet.setColumnWidth(20, 1750);
    }
  }

  private void setCoverSheetColumnWidths() {
    // width amnt decreases faster as inches decrease
    // Successes
    // 5100 -> 1.67
    // 1750 -> 0.57
    // 1400 -> 0.46
    // 450 -> 0.15

    cover.setColumnWidth(0, 5100);
    cover.setColumnWidth(1, 1400);
    cover.setColumnWidth(2, 1750);
    cover.setColumnWidth(3, 1750);
    cover.setColumnWidth(4, 1750);
    cover.setColumnWidth(5, 450);
    cover.setColumnWidth(6, 1750);
    cover.setColumnWidth(7, 1750);
    cover.setColumnWidth(8, 1750);
    cover.setColumnWidth(9, 450);
    cover.setColumnWidth(10, 1750);
    cover.setColumnWidth(11, 1750);
    cover.setColumnWidth(12, 1750);
    cover.setColumnWidth(13, 450);
    cover.setColumnWidth(14, 1750);
    cover.setColumnWidth(15, 1750);
    cover.setColumnWidth(16, 1750);
    cover.setColumnWidth(17, 450);
    cover.setColumnWidth(18, 1750);
    cover.setColumnWidth(19, 2760);
    cover.setColumnWidth(20, 2760);
    cover.setColumnWidth(21, 5900);
    cover.setColumnWidth(22, 1225);
    cover.setColumnWidth(23, 1750);
    cover.setColumnWidth(24, 250);
    cover.setColumnWidth(25, 1750);
    cover.setColumnWidth(26, 1750);
    cover.setColumnWidth(27, 1750);
    cover.setColumnWidth(28, 1750);
    cover.setColumnWidth(29, 1750);
  }

  private void setCoverSheetRowHeights() {
    cover.getRow(0).setHeightInPoints(20);
    cover.getRow(1).setHeightInPoints(9);
    cover.getRow(2).setHeightInPoints(20);
    cover.getRow(3).setHeightInPoints(9);
    cover.getRow(4).setHeightInPoints(20);
    cover.getRow(5).setHeightInPoints(9);
    cover.getRow(6).setHeightInPoints(3);
    cover.getRow(7).setHeightInPoints(15);
    cover.getRow(8).setHeightInPoints(12);
    cover.getRow(9).setHeightInPoints(12);
    cover.getRow(10).setHeightInPoints(12);
    cover.getRow(11).setHeightInPoints(12);
    cover.getRow(12).setHeightInPoints(6);
  }

  private void setEmployeeSheetRowHeights(Sheet eSheet) {
    eSheet.getRow(0).setHeightInPoints(20);
    eSheet.getRow(1).setHeightInPoints(9);
    eSheet.getRow(2).setHeightInPoints(20);
    eSheet.getRow(3).setHeightInPoints(12);
    eSheet.getRow(4).setHeightInPoints(3);
    eSheet.getRow(5).setHeightInPoints(15);
    eSheet.getRow(6).setHeightInPoints(15);
    eSheet.getRow(7).setHeightInPoints(13);
    eSheet.getRow(8).setHeightInPoints(12);
    eSheet.getRow(9).setHeightInPoints(12);
    eSheet.getRow(10).setHeightInPoints(12);
    eSheet.getRow(11).setHeightInPoints(3);
  }

  private void setEmployeeSheetRowHeights() {
    for (int i = 1; i < wbk.getNumberOfSheets(); i++) {
      if (wbk.getSheetAt(i).equals(divider)) {
        continue;
      }
      Sheet eSheet = wbk.getSheetAt(i);
      if (eSheet == null) {
        throw new IllegalStateException("Employee sheet excel tab was null");
      }
      setEmployeeSheetRowHeights(eSheet);
    }
  }

  private void createActiveEmployeeSheetCells(List<Employee> activeEmployees, TimeSheet tsheet) {
    for (Employee e : activeEmployees) {
      Sheet sheet = wbk.createSheet(e.getAbbreviation());
      int jobLines = this.getRequiredJobLines(e, tsheet);
      // 12 for header + 1 gap row + jobLines required + 13 for footer
      int numRows = Constants.eSheet_header_rows + 1 + jobLines + Constants.eSheet_footer_rows;
      ExporterUtil.fillWithCells(numRows, Constants.eSheet_cols, sheet);
    }
  }

  private void createInactiveEmployeeSheetCells(List<Employee> inactiveEmployees) {
    Collections.sort(inactiveEmployees, new Employee.LastNameCompare());
    for (Employee e : inactiveEmployees) {
      wbk.createSheet(e.getAbbreviation());
      // 13 for header + 13 for footer + jobLines required
      int numRows = 13 + 13;
      ExporterUtil.fillWithCells(numRows, Constants.eSheet_cols, wbk.getSheet(e.getAbbreviation()));

    }
  }

  private int getRequiredJobLines(Employee employee, TimeSheet tsheet) {
    int jobLines = 0;
    for (JobEntry je : tsheet.getEmployeeSheetFor(employee).getJobs()) {
      jobLines = jobLines + je.getTimes().size();
      if (je.hasStdDeduction()) {
        jobLines = jobLines + 1;
      }
    }
    return jobLines;
  }
}
