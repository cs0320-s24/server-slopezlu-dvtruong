package edu.brown.cs.student.main.server.CSVAPI.Parser;

import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.CreatorFromRow;
import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.FactoryFailureException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class allows one to parse a CSV and convert each row into their desired object.
 *
 * @param <T> The Object type that each row is converted to.
 */
public class CSVParser<T> {
  private Reader reader;
  private CreatorFromRow<T> creator;
  private boolean headersOrNot;

  /**
   * Constructor for CSVParser: constructs a CSVParser with the ability to parse through a CSV
   * passed through the desired reader and convert the rows into a desirable object
   *
   * @param reader the desired reader to pass into the parser. This should read the desired CSV to
   *     parse.
   * @param creator an Object that extends the CreatorFromRow interface that should be
   * @param headersOrNot a Boolean indicating whether there are headers in the CSV
   */
  public CSVParser(Reader reader, CreatorFromRow<T> creator, boolean headersOrNot) {
    if (reader == null || creator == null) {
      throw new IllegalArgumentException("Reader/Creator cannot be null");
    }
    this.reader = reader;
    this.creator = creator;
    this.headersOrNot = headersOrNot;
  }

  /**
   * Method that parses through a CSV, converts each row into the desired object type as specified
   * by the constructor, and adds it to a resulting list to return. As well, if the headersOrNot
   * boolean is true, then the function will skip the headers line (which is assumed to be the first
   * line of the CSV) and continue to parse the rest of the rows of data. If the headersOrNot
   * boolean is not true, then we simply read the whole CSV.
   *
   * @return a list of the rows in the CSV (where each row is represented as the desired object
   *     type)
   */
  public List<T> parse() throws IOException, FactoryFailureException {
    BufferedReader bufferedReader = new BufferedReader(this.reader);
    final Pattern regexSplitCSVRow =
        Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
    List<T> result = new ArrayList<>();

    if (this.headersOrNot) {
      bufferedReader.readLine();
    }
    String line = bufferedReader.readLine();
    while (line != null) {
      String[] rowArray = regexSplitCSVRow.split(line);
      ArrayList<String> rowList = new ArrayList<String>();
      for (String item : rowArray) {
        rowList.add(item);
      }
      result.add(creator.create(rowList));
      line = bufferedReader.readLine();
    }

    return result;
  }
}
