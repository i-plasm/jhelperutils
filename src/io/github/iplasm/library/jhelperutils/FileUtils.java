package io.github.iplasm.library.jhelperutils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileUtils {
  public static List<Path> findByFileExtension(Path path, String fileExtension) throws IOException {
    if (!Files.isDirectory(path) || path.toString().trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Invalid path: it either does not exist or is not a directory.");
    }

    if (!Files.isReadable(path)) {
      throw new IllegalArgumentException("Provided directory must have read permissions");
    }
    List<Path> result = new ArrayList<>();

    Iterator<Path> itr = Files.walk(path).iterator();
    while (true) {
      try {
        if (itr.hasNext()) {
          Path next = itr.next();
          if (next.toString().endsWith(fileExtension) &&
              !next.getFileName().toString().startsWith(".")) {
            result.add(next);
          }
        } else {
          break;
        }
      } catch (Exception e) {
        System.err.print(e.getLocalizedMessage());
      }
    }
    return result;
  }
}
