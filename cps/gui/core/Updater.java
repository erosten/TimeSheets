
package cps.gui.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Updater {

  private final static String versionURL = "https://github.com/erosten/TimeSheets/blob/master/version";

  private final static String historyURL = "http://custodialplus.000webhostapp.com/history";

  //
  // public static String getLatestVersion() throws Exception {
  //
  // String s;
  // Process p;
  // try {
  // p = Runtime.getRuntime().exec("ls -aF");
  // BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
  // while ((s = br.readLine()) != null)
  // System.out.println("line: " + s);
  // p.waitFor();
  // System.out.println("exit: " + p.exitValue());
  // p.destroy();
  // } catch (Exception e) {
  // }
  // return "";
  // }

  public static String getLatestVersion() throws Exception {
    String data = getData(versionURL);
    return data.substring(data.indexOf("[version]") + 9, data.indexOf("[/version]"));
  }

  public static String getCurrentVersion() {
    File file = new File("src/version.txt");
    // This will reference one line at a time
    String line = null;
    try {
      // FileReader reads text files in the default encoding.
      FileReader fileReader = new FileReader(file);

      // Always wrap FileReader in BufferedReader.
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      line = bufferedReader.readLine();

      // Always close files.
      bufferedReader.close();
    } catch (FileNotFoundException ex) {
      System.out.println("Unable to open file '" + file.getAbsolutePath() + "'");
    } catch (IOException ex) {
      System.out.println("Error reading file '" + file.getAbsolutePath() + "'");
      // Or we could just do this:
      // ex.printStackTrace();
    }
    return line.substring(line.indexOf("[version]") + 9, line.indexOf("[/version]"));

  }

  public static String getWhatsNew() throws Exception

  {

    String data = getData(historyURL);

    return data.substring(data.indexOf("[history]") + 9, data.indexOf("[/history]"));

  }

  private static String getData(String address) throws Exception

  {

    URL url = new URL(address);

    InputStream html = null;

    html = url.openStream();

    int c = 0;

    StringBuffer buffer = new StringBuffer("");

    while (c != -1) {

      c = html.read();

      buffer.append((char) c);

    }

    return buffer.toString();

  }
}
