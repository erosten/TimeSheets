
package cps.excel;

public enum WorkBookType {
  XLS , XLSX;

  public String getExtension() {
    return this.toString();
  }
}