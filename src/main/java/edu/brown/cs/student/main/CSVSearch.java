package edu.brown.cs.student.main;

import edu.brown.cs.student.main.Parser.CSVParser;
import edu.brown.cs.student.main.Parser.Creator.ExampleCreators.ListofStringCreator;
import edu.brown.cs.student.main.Parser.Creator.FactoryFailureException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/** Class that contains the search method that is used for user-end search capabilities */
public class CSVSearch {
  /**
   * Searches for rows within a CSV that contain a string (not case-sensitive and as long as the row
   * contains the sequence of letters within a specified column or within the entire row (which is
   * specified)
   *
   * @param filename the filepath of the CSV file to search in. For example, if we have a directory
   *     that the file exists in, the format would be the following: "directory1/filename"
   * @param searchFor the desired String to look for
   * @param columnIdentifier the desired column to look at (in the format of a String) and can be
   *     null if there is no column identifier
   * @param columnHeader a boolean indicating whether there are headers in the CSV file
   * @param useColumnHeader a boolean indicating whether to use the headers or to use column index
   *     or none
   * @return a List of List of Strings that contain the appropriate rows for the search
   * @throws FileNotFoundException if the file doesn't exist
   * @throws IOException if there was another error when trying to read/parse the CSV file
   * @throws FactoryFailureException if the number of rows in consistent
   */
  public List<List<String>> search(
      String filename,
      String searchFor,
      String columnIdentifier,
      boolean columnHeader,
      boolean useColumnHeader)
      throws FileNotFoundException, IOException, FactoryFailureException, IllegalArgumentException {

    // for headers
    HashMap<String, Integer> headers = new HashMap<>();
    final Pattern regexSplitCSVRow =
        Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String firstRow = reader.readLine();
    String[] rowArray = regexSplitCSVRow.split(firstRow);
    int i = 0;
    if (useColumnHeader) {
      for (String item : rowArray) {
        headers.put(item, i);
        i++;
      }
    } else {
      for (String item : rowArray) {
        headers.put(Integer.toString(i), i);
        i++;
      }
    }
    if (!headers.containsKey(columnIdentifier) && useColumnHeader) {
      throw new IllegalArgumentException("Column identifier doesn't exist within this CSV");
    }
    // search
    CSVParser<List<String>> fileParser =
        new CSVParser<List<String>>(
            new FileReader(filename), new ListofStringCreator(headers.size()), columnHeader);
    List<List<String>> listOfRows = fileParser.parse();
    List<List<String>> result = new ArrayList<>();

    if (columnIdentifier != null) {
      int columnToLookAt = headers.get(columnIdentifier);
      for (List<String> row : listOfRows) {
        if (row.get(columnToLookAt).toLowerCase().contains(searchFor.toLowerCase())) {
          result.add(row);
        }
      }
      return result;
    } else {
      for (List<String> row : listOfRows) {
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
