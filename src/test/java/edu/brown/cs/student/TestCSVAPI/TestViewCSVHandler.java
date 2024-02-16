package edu.brown.cs.student.TestCSVAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.CSVAPI.CSVDataSource;
import edu.brown.cs.student.main.server.CSVAPI.LoadCSVHandler;
import edu.brown.cs.student.main.server.CSVAPI.ViewCSVHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestViewCSVHandler {
  @BeforeAll
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
    Spark.get("/viewcsv", new ViewCSVHandler(csvDataSource));
    Spark.awaitInitialization();
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/loadcsv");
    Spark.unmap("/viewcsv");
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

  @Test
  public void testViewCSVRequestSuccess() throws IOException {
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=" + filepath + "&headers=true");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseLoadBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseLoadBody.get("result"));
    loadConnection.disconnect();

    HttpURLConnection viewConnection = tryRequest("viewcsv");
    assertEquals(200, viewConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(viewConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));
    viewConnection.disconnect();
  }

  @Test
  public void testViewCSVRequestFail_LoadNotPerformed() throws IOException {
    HttpURLConnection viewConnection = tryRequest("viewcsv");
    assertEquals(200, viewConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(viewConnection.getInputStream()));
    assertEquals("error_datasource", responseBody.get("result"));
    viewConnection.disconnect();
  }
}
