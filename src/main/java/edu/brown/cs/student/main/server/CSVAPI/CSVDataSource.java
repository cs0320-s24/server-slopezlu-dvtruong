package edu.brown.cs.student.main.server.CSVAPI;

import edu.brown.cs.student.main.server.CSVAPI.Parser.CSVParser;
import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.ExampleCreators.ListofStringCreator;
import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.FactoryFailureException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CSVDataSource {
  private List<List<String>> csv;
  private List<List<String>> publicCSV;
  private Map<String, Integer> headers;
  private Map<String, Integer> publicHeaders;
  /**
   * Class that loads and stores CSV data from a file and gives users access to a read-only form of
   * it *
   */
  public CSVDataSource() {
    /**
     * Loads data from a file, checks that it was successfully loaded, and provides a read-only form
     * of the CSV data and headers for added security in the form of unmodifiable objects
     */
    this.csv = null;
  }
  // loads a file using filename and headersOrNot provided
  public void load(String filename, boolean headersOrNot)
      throws IOException, FactoryFailureException {
    final Pattern regexSplitCSVRow =
        Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String firstRow = reader.readLine();
    String[] rowArray = regexSplitCSVRow.split(firstRow);

    this.headers = new HashMap<>();
    if (headersOrNot) {
      int i = 0;
      for (String item : rowArray) {
        this.headers.put(item.replace(" ", "_"), i);
        i++;
      }
    }
    this.csv =
        new CSVParser<List<String>>(
                new FileReader(filename), new ListofStringCreator(rowArray.length), headersOrNot)
            .parse();
  }
  // checks if a file was successfully loaded
  public boolean checkLoaded() {
    return this.csv != null;
  }
  // creates a proxy of the CSV data
  public List<List<String>> proxy() {
    if (publicCSV == null) {
      this.publicCSV = Collections.unmodifiableList(this.csv);
    }
    return publicCSV;
  }
  // creates a proxy of the headers map
  public Map<String, Integer> headersProxy() {
    if (publicHeaders == null) {
      this.publicHeaders = Collections.unmodifiableMap(this.headers);
    }
    return publicHeaders;
  }
}
