package edu.brown.cs.student.TestCSVAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.CSVAPI.CSVDataSource;
import edu.brown.cs.student.main.server.CSVAPI.LoadCSVHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.annotations.BeforeClass;
import spark.Spark;

/** Class responsible for testing the LoadCSVHandler * */
public class TestLoadCSVHandler {
  @BeforeClass
  public static void setupOnce() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  @BeforeEach
  public void setup() {
    CSVDataSource csvDataSource = new CSVDataSource();
    Spark.get("/loadcsv", new LoadCSVHandler(csvDataSource));
    Spark.awaitInitialization();
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/loadcsv");
    Spark.awaitStop();
  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  private String filepath = "dol_ri_earnings_disparity.csv";
  /** Tests that a successful loadcsv request is made with the appropriate query parameters* */
  @Test
  public void testLoadCSVRequestSuccess() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + filepath + "&headers=true");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));
    loadConnection.disconnect();
  }
  /**
   * Tests that an error_bad_request is produced if both filepath and headers query parameters are
   * missing *
   */
  @Test
  public void testLoadCSVRequestFail_MissingBothParameters() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("error_bad_request", responseBody.get("result"));
    loadConnection.disconnect();
  }
  /** Tests that an error_datasource error is produced if filepath parameter is missing * */
  @Test
  public void testLoadCSVRequestFail_MissingFilePathParameter() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + "&headers=true");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("error_datasource", responseBody.get("result"));
    loadConnection.disconnect();
  }
  /** Tests that an error_bad_request is produced if the headers query parameter is missing * */
  @Test
  public void testLoadCSVRequestFail_MissingHeadersParameter() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + filepath + "&headers=");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("error_bad_request", responseBody.get("result"));
    loadConnection.disconnect();
  }
  /**
   * Tests that an error_bad_request is produced if an headers is an invalid input that is not
   * "true" or "false" *
   */
  @Test
  public void testLoadCSVRequestFail_InvalidHeaderInput() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + "&headers=roblox");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("error_bad_request", responseBody.get("result"));
    loadConnection.disconnect();
  }
  /**
   * Tests that an error_datasource is produced if the file does not exist in the data directory *
   */
  @Test
  public void testLoadCSVRequestFail_FileNotInDirectory() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + "isfun" + "&headers=true");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("error_datasource", responseBody.get("result"));
    loadConnection.disconnect();
  }
  /** Tests that an error_cannot_parse is produced if the file cannot be parsed * */
  @Test
  public void testLoadCSVRequestFail_CannotParse() throws IOException {
    HttpURLConnection loadConnection =
        tryRequest("loadcsv?filepath=" + "malformed_signs.csv" + "&headers=true");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("error_cannot_parse", responseBody.get("result"));
    loadConnection.disconnect();
  }
}
