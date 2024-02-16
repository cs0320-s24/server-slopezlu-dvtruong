package edu.brown.cs.student.TestCensusAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPIHandler;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPISources.CensusAPISource;
import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
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

/** Testing the CensusAPIHandler on the actual Census API */
public class TestCensusAPIHandlerActual {
  /** setup the server on a port */
  @BeforeAll
  public static void setupBeforeAll() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  /**
   * sets up the server to accept "/broadband" and builds the adapter to assert tests on the
   * responses
   */
  @BeforeEach
  public void setup() {
    Spark.get("/broadband", new CensusAPIHandler(new CensusAPISource()));
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  /** gracefully shuts down the server */
  @AfterEach
  public void teardown() {
    Spark.unmap("/broadband");
    Spark.awaitStop();
  }

  /**
   * method that constructs the request to the server
   *
   * @param toRequest the state and county to request data for
   * @return a connnection that holds the response from the server
   * @throws IOException if the connection to the server cannot be established for some reason
   */
  private HttpURLConnection tryRequest(stateCounty toRequest) throws IOException {
    URL requestURL =
        new URL(
            "http://localhost:"
                + Spark.port()
                + "/broadband?state="
                + toRequest.state()
                + "&county="
                + toRequest.county());

    if (toRequest.county() == null) {
      if (toRequest.state() == null) {
        requestURL = new URL("http://localhost:" + Spark.port() + "/broadband?");
      } else {
        requestURL =
            new URL("http://localhost:" + Spark.port() + "/broadband?state=" + toRequest.state());
      }
    } else if ((toRequest.county() != null) && (toRequest.state() == null)) {
      requestURL =
          new URL("http://localhost:" + Spark.port() + "/broadband?county=" + toRequest.county());
    }

    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Tests for when there are successful requests
   *
   * @throws IOException if connection to server cannot be established
   */
  @Test
  public void testBroadbandSuccessfulRequest() throws IOException {
    HttpURLConnection kingsConnection = tryRequest(new stateCounty("California", "Kings_County"));
    assertEquals(200, kingsConnection.getResponseCode());
    Map<String, Object> kingsResponseBody =
        adapter.fromJson(new Buffer().readFrom(kingsConnection.getInputStream()));
    assertEquals("success", kingsResponseBody.get("result"));
    assertEquals("83.5", kingsResponseBody.get("percentage of people that have broadband access"));
  }

  /**
   * Tests for when there are missing fields (null) within the request
   *
   * @throws IOException if connection to server cannot be established
   */
  @Test
  public void testBroadbandRequestFailMissingFields() throws IOException {
    // both fields
    HttpURLConnection missingBothConnection = tryRequest(new stateCounty(null, null));
    assertEquals(200, missingBothConnection.getResponseCode());
    Map<String, Object> missingBothResponseBody =
        adapter.fromJson(new Buffer().readFrom(missingBothConnection.getInputStream()));
    assertEquals("error_bad_request", missingBothResponseBody.get("result"));
    assertEquals(
        "Please input a state and county to get data for. If there are spaces in the names of either, please replace them with _",
        missingBothResponseBody.get("message"));
    missingBothConnection.disconnect();

    // missing states fields
    HttpURLConnection missingStatesConnection = tryRequest(new stateCounty(null, "some_county"));
    assertEquals(200, missingStatesConnection.getResponseCode());
    Map<String, Object> missingStatesResponseBody =
        adapter.fromJson(new Buffer().readFrom(missingStatesConnection.getInputStream()));
    assertEquals("error_bad_request", missingStatesResponseBody.get("result"));
    assertEquals(
        "Please input a state to get data for. If there are spaces in the name, please replace them with _",
        missingStatesResponseBody.get("message"));
    missingStatesConnection.disconnect();

    // missing county fields
    HttpURLConnection missingCountyConnection = tryRequest(new stateCounty("some_state", null));
    assertEquals(200, missingCountyConnection.getResponseCode());
    Map<String, Object> missingCountyResponseBody =
        adapter.fromJson(new Buffer().readFrom(missingCountyConnection.getInputStream()));
    assertEquals("error_bad_request", missingCountyResponseBody.get("result"));
    assertEquals(
        "Please input a county to get data for. If there are spaces in the name, please replace them with _",
        missingCountyResponseBody.get("message"));
  }

  /**
   * Tests for when the state/county combination that is requested doesn't exist
   *
   * @throws IOException if connection to server cannot be established for some reason
   */
  @Test
  public void testStateCountyNotExistant() throws IOException {
    HttpURLConnection loadConnection = tryRequest(new stateCounty("some", "some"));
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("error_bad_request", responseBody.get("result"));
    assertEquals("state/county combination doesn't exist", responseBody.get("message"));
  }
}
