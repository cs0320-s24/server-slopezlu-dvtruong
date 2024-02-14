package edu.brown.cs.student.main;

import edu.brown.cs.student.main.Parser.Creator.FactoryFailureException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Class that contains the search method that is used for user-end search capabilities */
public class CSVSearch {
  /**
   * Searches for rows within a CSV that contain a string (not case-sensitive and as long as the row
   * contains the sequence of letters within a specified column or within the entire row (which is
   * specified)
   *
   * @param searchFor the desired String to look for
   * @param columnIdentifier the desired column to look at (in the format of a String) and can be
   *     null if there is no column identifier
   * @param useColumnHeader a boolean indicating whether to use the headers or to use column index
   *     or none
   * @return a List of List of Strings that contain the appropriate rows for the search
   * @throws FileNotFoundException if the file doesn't exist
   * @throws IOException if there was another error when trying to read/parse the CSV file
   * @throws FactoryFailureException if the number of rows in consistent
   * //TODO: add useColumnIdentifier
   */
  public List<List<String>> search(
      List<List<String>> csvFile,
      Map<String, Integer> headers,
      String searchFor,
      String columnIdentifier)
      throws IllegalArgumentException {
    // search
    List<List<String>> result = new ArrayList<>();

    if (columnIdentifier != null) {
      int columnToLookAt = headers.get(columnIdentifier);
      for (List<String> row : csvFile) {
        if (row.get(columnToLookAt).toLowerCase().contains(searchFor.toLowerCase())) {
          result.add(row);
        }
      }
      return result;
    } else {
      for (List<String> row : csvFile) {
        for (String item : row) {
          if (item.toLowerCase().contains(searchFor.toLowerCase())) {
            result.add(row);
          }
        }
      }
      return result;
    }
  }
}
