package edu.brown.cs.student.main.CensusAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okio.Buffer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountyCodeFetcher {
    public CountyCodeFetcher(){
    }
    public Map<String, Integer> getCountyCodes(String stateCode) throws IOException {
        URL requestURL = new URL("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:&in=state:" + stateCode);
        URLConnection urlConnection = requestURL.openConnection();
        HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
        clientConnection.connect();

        Moshi moshi = new Moshi.Builder().build();
        Type listListOfString = Types.newParameterizedType(List.class, Types.newParameterizedType(List.class, String.class));
        JsonAdapter<List<List<String>>> adapter = moshi.adapter(listListOfString);
        List<List<String>> responseData = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        clientConnection.disconnect();

        Map<String, Integer> countyCodeMap = new HashMap<>();
        for (int i=1; i<responseData.size(); i++){
            List<String> countyData = responseData.get(i);
            String countyName = countyData.get(0);
            String countyCode = countyData.get(2);
            countyCodeMap.put(countyName, Integer.parseInt(countyCode));
        }
        return countyCodeMap;
    }
    public static void main(String[] args) throws IOException {
        CountyCodeFetcher fetcher= new CountyCodeFetcher();
        Map<String, Integer> countyCodes = fetcher.getCountyCodes("02");
        for (String countyName : countyCodes.keySet()) {
            int countyCode = countyCodes.get(countyName);
            System.out.println("county: " + countyName + ", code: " + countyCode);
        }
    }
    }
