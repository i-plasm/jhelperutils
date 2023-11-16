package io.github.iplasm.library.java.commons;

public class SysUtils {
  public static boolean isNeitherWindowsNorMac() {
    String os = System.getProperty("os.name").toLowerCase();
    return !isWindows() && !isMacOs();
  }

  public static boolean isMacOs() {
    String os = System.getProperty("os.name").toLowerCase();
    return os.startsWith("mac");
  }

  public static boolean isWindows() {
    String os = System.getProperty("os.name").toLowerCase();
    return os.startsWith("windows");
  }
}
