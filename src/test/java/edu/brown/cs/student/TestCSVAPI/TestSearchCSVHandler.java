package edu.brown.cs.student.TestCSVAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.CSVAPI.CSVDataSource;
import edu.brown.cs.student.main.server.CSVAPI.LoadCSVHandler;
import edu.brown.cs.student.main.server.CSVAPI.SearchCSVHandler;
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

/** Class responsible for testing the SearchCSVHandler class * */
public class TestSearchCSVHandler {
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
    Spark.get("/searchcsv", new SearchCSVHandler(csvDataSource));
    Spark.awaitInitialization();
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/loadcsv");
    Spark.unmap("/searchcsv");
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
  /**
   * Tests that a successful searchcsv request can be made with a loaded CSV file and the
   * appropriate parameters *
   */
  @Test
  public void testSearchCSVRequestSuccess() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + filepath + "&headers=true");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseLoadBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseLoadBody.get("result"));
    loadConnection.disconnect();

    HttpURLConnection searchConnection =
        tryRequest("searchcsv?searchFor=White&columnIdentifier=Data_Type&useColumnHeaders=true");
    assertEquals(200, searchConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(searchConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));
    searchConnection.disconnect();
  }

  /** Tests that an error_bad_request is produced if no query parameters are provided * */
  @Test
  public void testSearchCSVRequestError_MissingAllParameters() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + filepath + "&headers=true");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseLoadBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseLoadBody.get("result"));
    loadConnection.disconnect();

    HttpURLConnection searchConnection =
        tryRequest("searchcsv?searchFor=&columnIdentifier=&useColumnHeaders=");
    assertEquals(200, searchConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(searchConnection.getInputStream()));
    assertEquals("error_bad_request", responseBody.get("result"));
    searchConnection.disconnect();
  }

  /**
   * Tests that an error_bad_request is produced if the searchFor query parameter is not provided *
   */
  @Test
  public void testSearchCSVRequestError_MissingSearchParameter() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + filepath + "&headers=true");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseLoadBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseLoadBody.get("result"));
    loadConnection.disconnect();

    HttpURLConnection searchConnection =
        tryRequest("searchcsv?searchFor=&columnIdentifier=Data_Type&useColumnHeaders=true");
    assertEquals(200, searchConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(searchConnection.getInputStream()));
    assertEquals("error_bad_request", responseBody.get("result"));
    searchConnection.disconnect();
  }
  /**
   * Tests that an error_bad_request is produced if the useColumnHeaders query parameter is not
   * provided *
   */
  @Test
  public void testSearchCSVRequestError_MissingUseColumnHeadersParameter() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + filepath + "&headers=true");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseLoadBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseLoadBody.get("result"));
    loadConnection.disconnect();

    HttpURLConnection searchConnection =
        tryRequest("searchcsv?searchFor=White&columnIdentifier=Data_Type&useColumnHeaders=");
    assertEquals(200, searchConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(searchConnection.getInputStream()));
    assertEquals("error_bad_request", responseBody.get("result"));
    searchConnection.disconnect();
  }
  /** Tests that a searchcsv request fails if a file is not loaded in first * */
  @Test
  public void testSearchCSVRequestError_LoadNotPerformed() throws IOException {
    HttpURLConnection viewConnection =
        tryRequest("searchcsv?searchFor=White&columnIdentifier=Data_Type&useColumnHeaders=true");
    assertEquals(200, viewConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(viewConnection.getInputStream()));
    assertEquals("error_datasource", responseBody.get("result"));
    viewConnection.disconnect();
  }

  /**
   * Tests that an error_no_such_column is produced if the columnIdentifier does not exist in the
   * file *
   */
  @Test
  public void testSearchCSVRequestError_InvalidColumnIdentifier() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + filepath + "&headers=true");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseLoadBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseLoadBody.get("result"));
    loadConnection.disconnect();

    HttpURLConnection searchConnection =
        tryRequest("searchcsv?searchFor=White&columnIdentifier=roblox&useColumnHeaders=true");
    assertEquals(200, searchConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(searchConnection.getInputStream()));
    assertEquals("error_no_such_column", responseBody.get("result"));
    searchConnection.disconnect();
  }

  /** Tests that an error_bad_request is produced if an invalid input for useColumnHeaders */
  @Test
  public void testSearchCSVRequestFail_InvalidUseColumnHeadersInput() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + filepath + "&headers=true");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseLoadBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseLoadBody.get("result"));
    loadConnection.disconnect();

    HttpURLConnection searchConnection =
        tryRequest("searchcsv?searchFor=White&columnIdentifier=Data_Type&useColumnHeaders=roblox");
    assertEquals(200, searchConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(searchConnection.getInputStream()));
    assertEquals("error_bad_request", responseBody.get("result"));
    searchConnection.disconnect();
  }
}
