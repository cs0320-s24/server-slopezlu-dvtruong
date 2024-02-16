package edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;

public class StateCountyCodeFetcher {

  public Map<String, String> getStateCodes() throws IOException {
    URL getStateCodesRequestURL =
        new URL("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*");
    URLConnection urlConnection = getStateCodesRequestURL.openConnection();
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect();

    Moshi moshi = new Moshi.Builder().build();
    Type listofListOfString =
        Types.newParameterizedType(
            List.class, Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listofListOfString);
    List<List<String>> stateCodeData =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    Map<String, String> stateCodeMap = new HashMap<>();
    for (int i = 1; i < stateCodeData.size(); i++) {
      List<String> state = stateCodeData.get(i);
      stateCodeMap.put(state.get(0).toLowerCase().replace(" ", "_"), state.get(1));
    }
    return stateCodeMap;
  }

  public String getCountyCode(String stateCode, String countyName) throws IOException {
    URL getCountyCodesRequestURL =
        new URL(
            "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode);
    URLConnection urlConnection = getCountyCodesRequestURL.openConnection();
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect();

    Moshi moshi = new Moshi.Builder().build();
    Type listofListOfString =
        Types.newParameterizedType(
            List.class, Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listofListOfString);
    List<List<String>> countyCodeData =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    for (int i = 1; i < countyCodeData.size(); i++) {
      List<String> county = countyCodeData.get(i);
      if (county.get(0).split(",")[0].replace(" ", "_").equalsIgnoreCase(countyName)) {
        return county.get(2);
      }
    }
    return "0";
  }
}