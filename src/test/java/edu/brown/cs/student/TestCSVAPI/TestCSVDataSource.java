package edu.brown.cs.student.TestCSVAPI;

import edu.brown.cs.student.main.server.CSVAPI.CSVDataSource;
import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.FactoryFailureException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
/**Class responsible for testing the CSVDataSource class and its helper methods
 * **/
public class TestCSVDataSource {
  private String filepath = "data/dol_ri_earnings_disparity.csv";
  private String filepathNoHeaders = "data/no_headers.csv";
  /**Tests that a file is successfully loaded if given valid inputs for file and headersOrNot
   * **/
    @Test
    public void testCheckLoaded_SuccessfullyLoaded() throws IOException, FactoryFailureException {
        CSVDataSource source = new CSVDataSource();
        source.load(filepath, true);
        Assert.assertEquals(true, source.checkLoaded());
    }
    /**Tests that a FileNotFoundException was thrown if file does not exist
     * **/
  @Test
  public void testLoad_InvalidFileInput() {
    CSVDataSource source = new CSVDataSource();
    Assert.assertThrows(
        FileNotFoundException.class,
        () -> {
          source.load("roblox", true);
        });
  }
    /**Tests that the proxy form of the CSV data is unmodifiable
     * **/
  @Test
  public void testProxy_CheckUnmodifiable() throws IOException, FactoryFailureException {
    CSVDataSource source = new CSVDataSource();
    source.load(filepath, true);
    Assert.assertEquals(true, source.checkLoaded());
    Assert.assertThrows(
        UnsupportedOperationException.class,
        () -> {
          source.proxy().remove(0);
          ;
        });
  }
    /**Tests that the proxy form of the headers map is unmodifiable
     * **/
  @Test
  public void testHeaderProxy_CheckUnmodifiable() throws IOException, FactoryFailureException {
    CSVDataSource source = new CSVDataSource();
    source.load(filepath, true);
    Assert.assertEquals(true, source.checkLoaded());
    Assert.assertThrows(
        UnsupportedOperationException.class,
        () -> {
          source.headersProxy().remove(0);
          ;
        });
  }
}
