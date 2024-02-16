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

public class CensusAPISource implements BroadbandDataSource {
  private Map<String, String> stateCodes;

  public CensusAPISource() {
    try {
      this.stateCodes = new StateCountyCodeFetcher().getStateCodes();
    } catch (IOException e) {
      System.out.println(
          "there was an error trying to contact the Census API in order to get the state codes");
    }
  }

  private stateCountyCode resolveStateCounty(String state, String county)
      throws IllegalArgumentException, IOException {
    String stateCode = this.stateCodes.get(state.toLowerCase());
    if (stateCode == null) {
      throw new IllegalArgumentException("state does not exist");
    }

    String countyCode = new StateCountyCodeFetcher().getCountyCode(stateCode, county);
    if (countyCode.equals("0")) {
      throw new IllegalArgumentException("county does not exist within state");
    }
    return new stateCountyCode(countyCode, stateCode);
  }

  @Override
  public broadbandData getCountyData(stateCounty input)
      throws IllegalArgumentException, IOException {
    return getCountyDataFunction(input.county(), input.state());
  }

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

  public record stateCountyCode(String countyCode, String stateCode) {}
}
