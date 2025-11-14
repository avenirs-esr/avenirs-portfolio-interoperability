package fr.avenirsesr.portfolio.interoperability.shared.infrastructure.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FileReader {
  public static <T> List<T> readCSV(
      String resourcePath, String separator, Function<String[], T> mapper) {
    List<T> results = new ArrayList<>();

    try (InputStream inputStream = FileReader.class.getResourceAsStream(resourcePath);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

      String line;
      boolean isFirstLine = true;
      while ((line = reader.readLine()) != null) {
        if (isFirstLine) {
          isFirstLine = false;
          continue;
        }

        String[] tokens = line.split(separator);
        results.add(mapper.apply(tokens));
      }
    } catch (IOException e) {
      throw new RuntimeException("Error reading CSV file: " + resourcePath, e);
    }

    return results;
  }
}
