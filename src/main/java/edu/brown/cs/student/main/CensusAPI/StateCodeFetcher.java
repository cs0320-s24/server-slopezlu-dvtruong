package edu.brown.cs.student.main.CensusAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.*;
import okio.Buffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateCodeFetcher {
    public StateCodeFetcher(){

    }
    public Map<String, Integer> getStateCodes() throws IOException{
        URL requestURL = new URL("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*");
        URLConnection urlConnection = requestURL.openConnection();
        HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
        clientConnection.connect();

        Moshi moshi = new Moshi.Builder().build();
        Type listListOfString = Types.newParameterizedType(List.class, Types.newParameterizedType(List.class, String.class));
        JsonAdapter<List<List<String>>> adapter = moshi.adapter(listListOfString);
        List<List<String>> responseData = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        clientConnection.disconnect();

        Map<String, Integer> stateCodeMap = new HashMap<>();
        for (int i=1; i<responseData.size(); i++){
            List<String> stateData = responseData.get(i);
            String stateName = stateData.get(0);
            String stateCode = stateData.get(1);
            stateCodeMap.put(stateName, Integer.parseInt(stateCode));
        }
        return stateCodeMap;
    }
//    public static void main(String[] args) throws IOException {
//        StateCodeFetcher fetcher = new StateCodeFetcher();
//        Map<String, Integer> stateCodes = fetcher.getStateCodes();
//        for (String stateName : stateCodes.keySet()) {
//            int stateCode = stateCodes.get(stateName);
//                System.out.println("state: " + stateName + ", code: " + stateCode);
//            }
//    }
}
