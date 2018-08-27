
package cps.excel.TimeSheet;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class testCellStyles {

  /**
   * test class.
   */
  // test class, resource warning @ end is not thrown..
  @SuppressWarnings("resource")
  public static void testStyles() throws Exception {
    Map<Date, List<Object>> data = new TreeMap<Date, List<Object>>();

    data.put(new GregorianCalendar(2017, 9, 29, 6, 0).getTime(), Arrays.asList("user 1", 1234.56));
    data.put(new GregorianCalendar(2017, 9, 30, 6, 0).getTime(), Arrays.asList("user 2", 789.12));
    data.put(new GregorianCalendar(2017, 9, 31, 6, 0).getTime(), Arrays.asList("user 3",
        131415.16));
    data.put(new GregorianCalendar(2017, 9, 29, 15, 45).getTime(), Arrays.asList("user 4",
        1234567.89));
    data.put(new GregorianCalendar(2017, 9, 30, 9, 45).getTime(), Arrays.asList("user 5", 123.45));

    Workbook wb = new XSSFWorkbook();
    CreationHelper creationHelper = wb.getCreationHelper();

    // on workbook level we are creating as much cell styles as needed:
    CellStyle datestyle = wb.createCellStyle();
    datestyle.setDataFormat(creationHelper.createDataFormat().getFormat("mm/dd/yyyy"));
    CellStyle currencystyle = wb.createCellStyle();
    currencystyle.setDataFormat(creationHelper.createDataFormat().getFormat("$#,##0.00"));

    // now we are creating the sheet and the cells and are putting the data into
    // the cells
    Sheet sheet = wb.createSheet();
    Row row = sheet.createRow(0);
    Cell cell = row.createCell(0);
    cell.setCellValue("Date");
    cell = row.createCell(1);
    cell.setCellValue("Logged in User");
    cell = row.createCell(2);
    cell.setCellValue("Amount");

    int rowNum = 1;

    for (Map.Entry<Date, List<Object>> entry : data.entrySet()) {

      row = sheet.createRow(rowNum++);
      cell = row.createCell(0);
      Date loginDate = entry.getKey();
      cell.setCellValue(loginDate);
      // if the cell needs a special cell style, then we are using one of the
      // ones we have previous created
      cell.setCellStyle(datestyle);

      List<Object> userdatas = entry.getValue();

      int cellNum = 1;
      for (Object userdata : userdatas) {
        cell = row.createCell(cellNum);
        if (cellNum == 1) {
          cell.setCellValue((String) userdata);
        } else if (cellNum == 2) {
          cell.setCellValue((Double) userdata);
          // if the cell needs a special cell style, then we are using one of
          // the ones we have previous created
          cell.setCellStyle(currencystyle);
        }
        cellNum++;
      }

    }

    wb.write(new FileOutputStream("CreateExcelNumberFormats.xlsx"));
    wb.close();

  }

}