package edu.brown.cs.student.main.server.CensusAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import okio.Buffer;

public class CensusAPISource {
  public broadbandData getCountyData(String countyCode, String stateCode) throws IOException {
    URL requestURL =
        new URL(
            "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                + countyCode
                + "&in=state:"
                + stateCode);
    URLConnection urlConnection = requestURL.openConnection();
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect();

    Moshi moshi = new Moshi.Builder().build();
    // Type listofListOfString = Types.newParameterizedType(List.class,
    // Types.newParameterizedType(List.class, String.class));
    // JsonAdapter<List<List<String>>> adapter = moshi.adapter(listofListOfString);

    JsonAdapter<broadbandData> adapter = moshi.adapter(broadbandData.class);
    broadbandData countyData =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    return countyData;
  }
}
