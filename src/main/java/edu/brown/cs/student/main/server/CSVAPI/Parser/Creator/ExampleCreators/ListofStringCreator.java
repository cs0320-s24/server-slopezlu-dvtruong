package edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.ExampleCreators;

import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.CreatorFromRow;
import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.FactoryFailureException;

import java.util.List;

/**
 * The ListofStringCreator is a class that implements the interface CreatorFromRow and transforms an
 * inputted row, which is a List of strings, into a List of strings. This class is primarily used in
 * the CSVSearch class.
 */
public class ListofStringCreator implements CreatorFromRow<List<String>> {
  private final int numberOfColumns;

  /**
   * Constructor for the ListofStringCreator class
   *
   * @param numberOfColumns the number of desired columns for the CSV
   */
  public ListofStringCreator(int numberOfColumns) {
    this.numberOfColumns = numberOfColumns;
  }
  /**
   * returns a list of strings out of a provided row, where the row provided is a list of strings
   *
   * @param row a row in the CSV file that is in the form of a list of strings
   * @return a List of string that is the new representation of the row
   * @throws FactoryFailureException if there is an inconsistent number of columns from what is
   *     desired
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    if (row.size() != this.numberOfColumns) {
      throw new FactoryFailureException("Inconsistent number of columns", row);
    }
    return row;
  }
}
