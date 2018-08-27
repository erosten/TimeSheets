
package cps.gui.tools;
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

//automatically opens a dialogue box where you can choose a file and it is brought into the program
public class FileChooser extends JPanel {

  File chosenFile = null;

  private static final long serialVersionUID = -2039017963853534543L;

  /**
   * Returns a directory File object chosen by the user. This class will open a file directory
   * window and prompt the user to choose a directory File
   */
  public FileChooser() {
    super(new BorderLayout());
    // Create a file chooser
    final JFileChooser fc = new JFileChooser();
    // Uncomment one of the following lines to try a different
    // file selection mode. The first allows just directories
    // to be selected (and, at least in the Java look and feel,
    // shown). The second allows both files and directories
    // to be selected. If you leave these lines commented out,
    // then the default mode (FILES_ONLY) will be used.
    //
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    // fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    final int returnVal = fc.showOpenDialog(FileChooser.this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      // do anything with file chosen
      this.chosenFile = fc.getSelectedFile();
      // String prog_dir_path = program_directory.getAbsolutePath();
      // Employees.WORKING_DIR = program_directory.getAbsolutePath();
      // String program_file_path = System.getProperty("java.class.path")
      // + System.getProperty("file.separator") + "Properties.properties";
      // Properties prop = new Properties();
      // prop.setProperty("WORKING_DIR", prog_dir_path);
      // FileOutputStream out = new FileOutputStream(program_file_path);
      // prop.store(out, "WORKING DIR = " + prog_dir_path);
      // System.out.println(program_file_path);
      // out.close();
    } else if (returnVal == JFileChooser.CANCEL_OPTION) {
      System.out.println("User cancelled..");
    } else {
      System.out.println("Something went wrong [FileChooser]..");
    }
  }

  public File getFile() {
    return this.chosenFile;
  }
}