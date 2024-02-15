package edu.brown.cs.student.main.server.CSVAPI;

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
   * @throws IllegalArgumentException if the column identifier does not exist in the map
   */
  public List<List<String>> search(
      List<List<String>> csvFile,
      Map<String, Integer> headers,
      String useColumnHeader,
      String searchFor,
      String columnIdentifier)
      throws IllegalArgumentException {
    // search
    List<List<String>> result = new ArrayList<>();
    if (useColumnHeader.equals("true") && !columnIdentifier.isEmpty()) {
      if (!headers.containsKey(columnIdentifier)) {
        throw new IllegalArgumentException("Column identifier does not exist in headers");
      }
      int columnToLookAt = headers.get(columnIdentifier);
      for (List<String> row : csvFile) {
        if (row.get(columnToLookAt).toLowerCase().contains(searchFor.toLowerCase())) {
          result.add(row);
        }
      }
    } else if (useColumnHeader.equals("false") && !columnIdentifier.isEmpty()) {
      int columnToLookAt = Integer.parseInt(columnIdentifier);
      if (columnToLookAt < 0 || columnToLookAt >= csvFile.get(0).size()) {
        throw new IllegalArgumentException("Column index is out of bounds");
      }
      for (List<String> row : csvFile) {
        if (row.get(columnToLookAt).toLowerCase().contains(searchFor.toLowerCase())) {
          result.add(row);
        }
      }
    } else {
      for (List<String> row : csvFile) {
        for (String item : row) {
          if (item.toLowerCase().contains(searchFor.toLowerCase())) {
            result.add(row);
          }
        }
      }
    }
    return result;
  }
}
