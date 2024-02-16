package edu.brown.cs.student.main.server.CensusAPI.CensusAPISources;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.StateCountyCodeFetcher;
import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import okio.Buffer;

/**
 * Class that sends and receives requests and responses from the Census API and converts them into a
 * data structure
 */
public class CensusAPISource implements BroadbandDataSource {
  private Map<String, String> stateCodes;

  /**
   * Constructor for the CensusAPISource that, when called, creates an object that houses a hashmap
   * of all available states in the US and their corresponding code as according to the Census API
   *
   * <p>This constructor will throw an error if it cannot connect to the Census API for some reason
   */
  public CensusAPISource() {
    try {
      this.stateCodes = new StateCountyCodeFetcher().getStateCodes();
    } catch (IOException e) {
      System.out.println(
          "there was an error trying to contact the Census API in order to get the state codes");
    }
  }

  /**
   * A helper for the main method within this class that essentially takes in a state and county and
   * converts it into their corresponding codes
   *
   * @param state the desired state to convert into a state code
   * @param county the desired county within the specified state to convert into a county code
   * @return a record, stateCountyCode, that houses two strings that are the state code and county
   *     code of the desired state and county
   * @throws IllegalArgumentException if either the state doesn't exist or the county doesn't exist
   *     within the state
   * @throws IOException if it is unable to connect to the census to obtain county codes for some
   *     reason
   */
  private stateCountyCode resolveStateCounty(String state, String county)
      throws IllegalArgumentException, IOException {
    String stateCode = null;
    if(state != null){
      stateCode = this.stateCodes.get(state.toLowerCase());
    }

    if (stateCode == null) {
      throw new IllegalArgumentException("state does not exist");
    }

    String countyCode = new StateCountyCodeFetcher().getCountyCode(stateCode, county);
    if (countyCode.equals("0")) {
      throw new IllegalArgumentException("county does not exist within state");
    }
    return new stateCountyCode(countyCode, stateCode);
  }

  /**
   * gets the desired data for a county
   *
   * @param input a record of stateCounty where both fields within the record are String with a
   *     county name and a state name (spaces should be replaced with _)
   * @return a record, broadbandData, that holds a list of Strings with the state and county,
   *     percentage of people that have broadband access within the county, and the state and county
   *     codes
   * @throws IllegalArgumentException if the state or county doesn't exist
   * @throws IOException if the connection to the Census API cannot be established for some reason
   */
  @Override
  public broadbandData getCountyData(stateCounty input)
      throws IllegalArgumentException, IOException {
    return getCountyDataFunction(input.county(), input.state());
  }

  /**
   * helper for getCountyCode that holds the main functionality of the method
   *
   * @param county the desired county to search data for
   * @param state the state (geological) of the desired county
   * @return a record broadbandData that holds a list of strings with the state and county name,
   *     percentage of people that have broadband access within the desired county, and the state
   *     and county codes
   * @throws IllegalArgumentException if the state or county doesn't exist
   * @throws IOException if the connection to the Census API cannot be established for some reason
   */
  private broadbandData getCountyDataFunction(String county, String state)
      throws IllegalArgumentException, IOException {
    stateCountyCode codes = this.resolveStateCounty(state, county);
    String countyCode = codes.countyCode;
    String stateCode = codes.stateCode;

    // make request
    URL requestURL =
        new URL(
            "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                + countyCode
                + "&in=state:"
                + stateCode);
    URLConnection urlConnection = requestURL.openConnection();
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect();
    //
    Moshi moshi = new Moshi.Builder().build();
    Type listofListOfString =
        Types.newParameterizedType(
            List.class, Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listofListOfString);
    // get response
    List<List<String>> stateData =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    // C
    for (List<String> countyRow : stateData) {
      if (countyRow.get(3).equals(countyCode)) {
        return new broadbandData(countyRow);
      }
    }
    return null;
  }

  /**
   * Record that holds a state and county code combination
   *
   * @param countyCode a code for a specific county within the state of the stateCode
   * @param stateCode a code for a specific state
   */
  public record stateCountyCode(String countyCode, String stateCode) {}
}
