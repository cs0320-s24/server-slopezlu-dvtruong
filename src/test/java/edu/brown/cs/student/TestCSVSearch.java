package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.CSVSearch;
import edu.brown.cs.student.main.Parser.Creator.FactoryFailureException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestCSVSearch {

  /**
   * Testing for when the value exists with the parameters provided
   *
   * @throws IOException if something went wrong when trying to read or parse the CSV file
   * @throws FactoryFailureException if there is an inconsistent number of columns
   */
  @Test
  public void testSearchExists() throws IOException, FactoryFailureException {
    // searching throughout the whole CSV
    CSVSearch csvSearch = new CSVSearch();
    List<List<String>> actualSearch =
        csvSearch.search(
            "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testfile1.csv",
            "Derek",
            null,
            true,
            false);

    List<List<String>> expectedSearch = new ArrayList<>();
    List<String> expectedRow1 = new ArrayList<>(List.of("Derek", "19", "Miami"));
    expectedSearch.add(expectedRow1);

    assertEquals(expectedSearch, actualSearch);

    // search through one column header
    actualSearch =
        csvSearch.search(
            "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testfile1.csv",
            "Derek",
            "Name",
            true,
            true);
    assertEquals(expectedSearch, actualSearch);

    // search through one column index
    actualSearch =
        csvSearch.search(
            "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testfile1.csv",
            "Derek",
            "0",
            true,
            false);
    assertEquals(expectedSearch, actualSearch);
  }

  /**
   * Test search when the item to search for doesn't exist within the paramters provided
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testSearchNotExist() throws IOException, FactoryFailureException {
    // doesn't exist at all within the file
    CSVSearch csvSearch = new CSVSearch();
    List<List<String>> actualSearch =
        csvSearch.search(
            "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testfile1.csv",
            "Derke",
            null,
            true,
            false);

    List<List<String>> expectedSearch = new ArrayList<>();
    assertEquals(expectedSearch, actualSearch);

    // with a header and doesn't exist at all within the file
    actualSearch =
        csvSearch.search(
            "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testfile1.csv",
            "Derke",
            "Name",
            true,
            true);
    assertEquals(expectedSearch, actualSearch);

    // Exists within the CSV file but doesn't exist within the desired column header
    actualSearch =
        csvSearch.search(
            "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testfile1.csv",
            "Derek",
            "Age",
            true,
            true);
    assertEquals(expectedSearch, actualSearch);

    // Exists within the CSV file but doesn't exist within the desired column index
    actualSearch =
        csvSearch.search(
            "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testfile1.csv",
            "Derke",
            "1",
            true,
            false);
    assertEquals(expectedSearch, actualSearch);
  }

  /** Testing search when there are an inconsistent number of columns in the CSV */
  @Test
  public void testInconsistentColumns() {
    CSVSearch csvSearch = new CSVSearch();
    boolean thrown = false;
    try {
      csvSearch.search(
          "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testInconsistentColumns",
          "Derek",
          "Name",
          true,
          true);
    } catch (IOException e) {
      System.out.println("This test has failed.");
    } catch (FactoryFailureException e) {
      thrown = true;
    }
    assertTrue(thrown);
  }

  /** Testing search when desired column to search doesn't exist */
  @Test
  public void testColumnNonexistent() {
    CSVSearch csvSearch = new CSVSearch();
    boolean thrown = false;
    try {
      csvSearch.search(
          "/Users/derektruong/CS0320/csv-dvtruong-dot/src/test/java/edu/brown/cs/student/test_CSV_files/testfile1.csv",
          "Derek",
          "yo",
          true,
          true);
    } catch (IllegalArgumentException e) {
      thrown = true;
    } catch (IOException e) {
      System.out.println("This test has failed.");
    } catch (FactoryFailureException e) {
      System.out.print("This test has failed.");
    }
    assertTrue(thrown);
  }
}
