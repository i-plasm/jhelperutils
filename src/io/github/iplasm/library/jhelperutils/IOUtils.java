package io.github.iplasm.library.jhelperutils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

public class IOUtils {

  public static void openViaOSCommand(String uriString, String command, boolean shouldWaitFor)
      throws IOException {
    String[] cmd = new String[] {command, uriString};
    Process process = Runtime.getRuntime().exec(cmd);
    if (shouldWaitFor) {
      try {
        process.waitFor();
      } catch (InterruptedException e) {
        throw new IOException(e);
      }
    }
  }

  public static void browseURLOrPathViaDesktop(URI uri) throws IOException {
    Desktop desktop = Desktop.getDesktop();
    if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
      desktop.browse(uri);
    }
  }

}
