package edu.brown.cs.student.TestCensusAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.MockSources.MockCensusAPISource;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPIHandler;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPISources.BroadbandDataSource;
import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.annotations.BeforeClass;
import spark.Spark;

/** Tests for the CensusAPIHandler on a mock data source */
public class TestCensusAPIHandlerMock {
  /** sets up the server on a port */
  @BeforeClass
  public static void setupBeforeAll() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  /**
   * sets up the server to accept "/broadband" and sets up the adapter to convert response maps to
   * use assertequals
   */
  @BeforeEach
  public void setup() {
    List<broadbandData> mockData = new ArrayList<>();
    broadbandData miamiData =
        new broadbandData(new ArrayList<>(List.of("miami", "90.2", "florida")));
    broadbandData middlesexData =
        new broadbandData(new ArrayList<>(List.of("middlesex", "80.2", "new_jersey")));
    mockData.add(miamiData);
    mockData.add(middlesexData);

    BroadbandDataSource mockedSource = new MockCensusAPISource(mockData);
    Spark.get("/broadband", new CensusAPIHandler(mockedSource));
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  /** shuts down the server gracefully */
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
   * Tests when there is a successful request
   *
   * @throws IOException if connection to server cannot be established for some reason
   */
  @Test
  public void testBroadbandRequestSuccessful() throws IOException {
    HttpURLConnection miamiConnection = tryRequest(new stateCounty("florida", "miami"));
    assertEquals(200, miamiConnection.getResponseCode());
    Map<String, Object> miamiResponseBody =
        adapter.fromJson(new Buffer().readFrom(miamiConnection.getInputStream()));
    assertEquals("success", miamiResponseBody.get("result"));
    assertEquals("90.2", miamiResponseBody.get("percentage of people that have broadband access"));
    miamiConnection.disconnect();

    // looking for the other item in the list for the data
    HttpURLConnection middlesexConnection = tryRequest(new stateCounty("new_jersey", "middlesex"));
    Map<String, Object> middlesexResponseBody =
        adapter.fromJson(new Buffer().readFrom(middlesexConnection.getInputStream()));
    assertEquals(200, middlesexConnection.getResponseCode());
    assertEquals(
        "80.2", middlesexResponseBody.get("percentage of people that have broadband access"));
    middlesexConnection.disconnect();
  }

  /**
   * Tests when there are missing fields
   *
   * @throws IOException if the connection to the server cannot be established for some reason
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
   * Tests for when the state/county combination doesn't exist within the dataset
   *
   * @throws IOException if the connection to the server cannot be established for some reason
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
