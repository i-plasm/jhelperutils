package io.github.iplasm.library.jhelperutils;

import java.util.regex.Pattern;

public class TextUtils {

  public static String extractExtension(String urlOrPath) {
    String extension = "";
    int dotIndex = urlOrPath.lastIndexOf(".");
    if (dotIndex != -1) {
      extension = urlOrPath.substring(dotIndex + 1, urlOrPath.length());
    }
    return extension;
  }

  public static boolean containsLineBreaks(String string) {
    return Pattern.compile("\\v+").matcher(string).find();
  }

  public static boolean containsWhiteSpaces(String string) {
    return Pattern.compile("\\s+").matcher(string).find();
  }

  public static String removeLineBreaks(String string) {
    return string.replaceAll("\\v+", " ");
  }
}
