package edu.brown.cs.student.main.server.CensusAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import okio.Buffer;

public class CensusAPISource {
  public broadbandData getCountyData(String countyCode, String stateCode) throws IOException {
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
    for (List<String> county : stateData) {
      if (county.get(3).equals(countyCode)) {
        return new broadbandData(Float.parseFloat(county.get(1)));
      }
    }
    return null;
  }
}
