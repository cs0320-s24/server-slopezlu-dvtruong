package edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.ExampleCreators;

import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.CreatorFromRow;
import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.FactoryFailureException;

import java.util.List;

/**
 * PersonCreator is a class that creates a Person from a row that contains 3 elements in this
 * specific order: [name, age, hometown].
 */
public class PersonCreator implements CreatorFromRow<Person> {

  /**
   * create is the main method for this class and creates a Person from a row that is inputted
   *
   * @param row the row that we want to make into a person
   * @return a person object that represents the row
   * @throws FactoryFailureException if there are not enough or too many elements in the row to
   *     satisfy the fields of a Person object
   */
  @Override
  public Person create(List<String> row) throws FactoryFailureException {
    if (row.size() != 3) {
      throw new FactoryFailureException("Cannot create object", row);
    }
    return new Person(row.get(0), Integer.parseInt(row.get(1)), row.get(2));
  }
}
