package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.Parser.CSVParser;
import edu.brown.cs.student.main.Parser.Creator.ExampleCreators.ListofStringCreator;
import edu.brown.cs.student.main.Parser.Creator.ExampleCreators.Person;
import edu.brown.cs.student.main.Parser.Creator.ExampleCreators.PersonCreator;
import edu.brown.cs.student.main.Parser.Creator.FactoryFailureException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestCSVParser {

  /**
   * Testing the CSVParser with a string reader on a very basic CSV and converting it into a List of
   * Strings
   */
  @Test
  public void testStringReaderSimple() throws IOException, FactoryFailureException {
    String CSVToParse =
        """
                    Name,Phone Number,Hometown
                    Derek,4010,Miami
                    Danny,3021,Miami
                    David,2010,Chicago
                    Adriel,3040,Newark""";

    CSVParser<List<String>> stringParser =
        new CSVParser<List<String>>(new StringReader(CSVToParse), new ListofStringCreator(3), true);
    List<List<String>> actualParse = stringParser.parse();

    List<List<String>> expectedParse = new ArrayList<>();
    List<String> expectedRow2 = new ArrayList<>();
    expectedRow2.add("Derek");
    expectedRow2.add("4010");
    expectedRow2.add("Miami");
    List<String> expectedRow3 = new ArrayList<>();
    expectedRow3.add("Danny");
    expectedRow3.add("3021");
    expectedRow3.add("Miami");
    List<String> expectedRow4 = new ArrayList<>();
    expectedRow4.add("David");
    expectedRow4.add("2010");
    expectedRow4.add("Chicago");
    List<String> expectedRow5 = new ArrayList<>();
    expectedRow5.add("Adriel");
    expectedRow5.add("3040");
    expectedRow5.add("Newark");

    expectedParse.add(expectedRow2);
    expectedParse.add(expectedRow3);
    expectedParse.add(expectedRow4);
    expectedParse.add(expectedRow5);

    assertEquals(expectedParse, actualParse);
  }

  /**
   * Testing the CSVParser on a simple CSV file where each row is converted into a list of strings
   */
  @Test
  public void testFileReaderSimple() {
    try {
      CSVParser<List<String>> fileParser =
          new CSVParser<List<String>>(
              new FileReader(
                  "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testfile1.csv"),
              new ListofStringCreator(3),
              true);
      List<List<String>> actualParse = fileParser.parse();

      List<String> expectedRow1 = new ArrayList<>(List.of("Derek", "19", "Miami"));
      List<String> expectedRow2 = new ArrayList<>(List.of("Danny", "18", "Miami"));
      List<String> expectedRow3 = new ArrayList<>(List.of("David", "20", "Chicago"));
      List<String> expectedRow4 = new ArrayList<>(List.of("Adriel", "20", "Newark"));
      List<List<String>> expectedParse =
          new ArrayList<>(List.of(expectedRow1, expectedRow2, expectedRow3, expectedRow4));

      assertEquals(expectedParse, actualParse);
    } catch (IOException | FactoryFailureException e) {
      System.out.println("File not found");
    }
  }

  /** Test the CSVParser when indicating no headers are present */
  @Test
  public void testCSVNoHeaders() throws IOException, FactoryFailureException {
    String CSVToParse =
        """
                        Derek,4010,Miami
                        Danny,3021,Miami
                        David,2010,Chicago
                        Adriel,3040,Newark""";

    CSVParser<List<String>> stringParserNoHeaders =
        new CSVParser<List<String>>(
            new StringReader(CSVToParse), new ListofStringCreator(3), false);
    List<List<String>> actualParse = stringParserNoHeaders.parse();
    List<String> expectedRow1 = new ArrayList<>(List.of("Derek", "4010", "Miami"));
    List<String> expectedRow2 = new ArrayList<>(List.of("Danny", "3021", "Miami"));
    List<String> expectedRow3 = new ArrayList<>(List.of("David", "2010", "Chicago"));
    List<String> expectedRow4 = new ArrayList<>(List.of("Adriel", "3040", "Newark"));
    List<List<String>> expectedParse =
        new ArrayList<>(List.of(expectedRow1, expectedRow2, expectedRow3, expectedRow4));
    assertEquals(expectedParse, actualParse);
  }

  /**
   * Testing the parser when turning rows into persons on a specific CSV (as the developer will have
   * the option to choose how they want their own specific CSV parsed)
   */
  @Test
  public void testParseIntoPersonFormat() throws IOException, FactoryFailureException {
    CSVParser<Person> fileParser =
        new CSVParser<Person>(
            new FileReader(
                "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testfile1.csv"),
            new PersonCreator(),
            true);
    List<Person> actualParse = fileParser.parse();
    System.out.println(actualParse);

    List<Person> expectedParse =
        new ArrayList<>(
            List.of(
                new Person("Derek", 19, "Miami"),
                new Person("Danny", 18, "Miami"),
                new Person("David", 20, "Chicago"),
                new Person("Adriel", 20, "Newark")));
    boolean equals = true;
    for (int i = 0; i < actualParse.size(); i++) {
      if (!actualParse.get(i).toString().equals(expectedParse.get(i).toString())) {
        equals = false;
      }
    }
    assertTrue(equals);
  }

  /**
   * Testing parse when there are inconsistent columns. However, I am under the assumption that, if
   * there are inconsistent columns, then the CreatorFromRow object wouldn't be able to make an
   * object out of the row, so a FactoryFailureException would be thrown.
   */
  @Test
  public void testParseInconsistentColumns() {
    CSVParser<Person> fileParser = null;
    boolean thrown = false;
    try {
      fileParser =
          new CSVParser<Person>(
              new FileReader(
                  "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testInconsistentColumns"),
              new PersonCreator(),
              true);
    } catch (FileNotFoundException e) {
      System.out.println("This test has failed.");
    }
    try {
      List<Person> actualParse = fileParser.parse();
    } catch (IOException e) {
      System.out.println("This test has failed.");
    } catch (FactoryFailureException e) {
      thrown = true;
    }
    assertTrue(thrown);
  }
}
