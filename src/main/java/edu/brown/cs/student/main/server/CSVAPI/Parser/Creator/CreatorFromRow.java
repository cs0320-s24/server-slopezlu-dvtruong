package edu.brown.cs.student.main.server.CSVAPI.Parser.Creator;

import java.util.List;

/**
 * This interface defines a method that allows your CSV parser to convert each row into an object of
 * some arbitrary passed type.
 *
 * <p>It is also recommended that, in the create function, having a desired number of columns within
 * each row and evaluating that boolean. If it turns out that the actual row's size and the desired
 * size aren't the same, throw the FactoryFailureException as you would have issues creating your
 * desired object in this case.
 *
 * <p>Your parser class constructor should take a second parameter of this generic interface type.
 */
public interface CreatorFromRow<T> {
  T create(List<String> row) throws FactoryFailureException;
}
