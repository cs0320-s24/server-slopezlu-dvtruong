package edu.brown.cs.student.main.server.CSVAPI;

import edu.brown.cs.student.main.Parser.CSVParser;
import edu.brown.cs.student.main.Parser.Creator.ExampleCreators.ListofStringCreator;
import edu.brown.cs.student.main.Parser.Creator.FactoryFailureException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class CSVDataSource {
  private List<List<String>> csv;
  private List<List<String>> publicCSV;
  private HashMap<String, Integer> headers;

  public CSVDataSource() {
    this.csv = null; // not sure if using null in this case is smart
  }

  public void load(String filename, boolean headersOrNot)
      throws IOException, FactoryFailureException {
    // use parser to parse the desired file and fill csv with it
    // also figure out how to make it safer (they can only access a certain directory or something
    // like that)
    // ^^^this may have to be done in the handler
    final Pattern regexSplitCSVRow =
        Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String firstRow = reader.readLine();
    String[] rowArray = regexSplitCSVRow.split(firstRow);

    //for the headers
    this.headers = new HashMap<>();
    if (headersOrNot) {
      int i = 0;
      for (String item: rowArray) {
        this.headers.put(item, i);
        i++;
      }
    } else {
      int i = 0;
      for (String item: rowArray) {
        this.headers.put(Integer.toString(i), i);
        i++;
      }
    }

    this.csv =
        new CSVParser<List<String>>(
                new FileReader(filename), new ListofStringCreator(rowArray.length), headersOrNot)
            .parse();
  }

  public boolean checkLoaded() {
    return this.csv != null;
  }

  public List<List<String>> proxy() {
    if (publicCSV == null) {
      this.publicCSV = Collections.unmodifiableList(this.csv);
    }
    return publicCSV;
  }
}
