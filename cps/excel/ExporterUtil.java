
package cps.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

public class ExporterUtil {

  /**
   * Creates a cell reference to a specified cell on a specified sheet.
   * 
   * @param cell
   *          the cell to create a reference to
   * @param sheet
   *          the sheet the cell is on
   * @return a CellReference object representing the cell given
   */
  public static CellReference getCellReference(Cell cell, Sheet sheet) {
    return new CellReference(
        sheet.getSheetName(),
        cell.getRowIndex(),
        cell.getColumnIndex(),
        false,
        false);
  }

  /**
   * Sets a merged region on a sheet from the given parameters.
   * 
   * @param rowA
   *          first Row to merge from
   * @param rowB
   *          last Row to merge to
   * @param colA
   *          first Column to merge from
   * @param colB
   *          last Column to merge to
   * @param sheet
   *          the sheet to set the merged region on
   */
  public static void setMergedRegions(int rowA, int rowB, int colA, int colB, Sheet sheet) {
    sheet.addMergedRegion(new CellRangeAddress(
        rowA, // first row (0-based)
        rowB, // second row (0-based)
        colA, // first column (0-based)
        colB // second column (0-based)
    ));
  }

  /**
   * Fills a sheet with the specified number of cells.
   * 
   * @param numRows
   *          amount of rows to add
   * @param numCols
   *          amount of columns to add
   * @param sheet
   *          the sheet to add cells to
   */
  public static void fillWithCells(int numRows, int numCols, Sheet sheet) {
    // TODO what happens when the sheet already has cells?
    for (int i = 0; i < numRows; i++) {
      sheet.createRow(i);
    }
    for (Row row : sheet) {
      for (int i = 0; i < numCols; i++) {
        row.createCell(i);
      }
    }
  }

}
