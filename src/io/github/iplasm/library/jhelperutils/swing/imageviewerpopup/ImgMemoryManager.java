package io.github.iplasm.library.jhelperutils.swing.imageviewerpopup;

import java.lang.management.ManagementFactory;

public class ImgMemoryManager {

  public static long getAvailableMemory() {
    long freeMem = Runtime.getRuntime().freeMemory();
    long totalMem = Runtime.getRuntime().totalMemory();
    long maxMem = Runtime.getRuntime().maxMemory();
    return freeMem + (maxMem - totalMem);
  }

  public static boolean determineJVMUsesLowMemory() {
    for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
      if ((arg.startsWith("-Xmx") || arg.contains("-XX:MaxRAM"))) {
        boolean hasFourOrMoreDigits = arg.length() - arg.replaceAll("\\d", "").length() >= 4;
        if ((arg.toLowerCase().endsWith("g") ||
            (arg.toLowerCase().endsWith("m") && hasFourOrMoreDigits))) {
          return false;

        }
      }
    }
    return true;
  }

  public static boolean normalQualityCondition() {
    return getAvailableMemory() > 150000000;
  }

  public static boolean highQualityCondition(int width, int height) {
    return getAvailableMemory() > 400000000 ||
        (getAvailableMemory() > 300000000 && width * height < 1000 * 1000);
  }

  public static boolean canUseIntermediateImages(Integer w, Integer h) {
    return getAvailableMemory() > 400000000 ||
        (getAvailableMemory() > 300000000 && w * h < 1000 * 1000) ||
        (getAvailableMemory() > 200000000 && w * h < 500 * 500);
  }
}
