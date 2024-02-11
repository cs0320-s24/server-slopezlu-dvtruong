package edu.brown.cs.student.main.CensusAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CensusAPIHandler implements Route {
    private Map<String, Integer> stateCodes;

    public CensusAPIHandler() throws IOException {
        this.stateCodes = new StateCountyCodeFetcher().getStateCodes();
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Moshi moshi = new Moshi.Builder().build();

        //For the response map
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

        //For the retrieved data
        JsonAdapter<CensusAPIData> CensusDataAdapter = moshi.adapter(CensusAPIData.class);
        //We think we need to use a map to store the response we get from the api server
        //Need to create a record (which should take in the map that we mentioned above)

        //create a response map
        Map<String, Object> responseMap = new HashMap<>();

        String state = request.queryParams("state");
        String county = request.queryParams("county");
        if ((state == null) || (county == null)) {
            responseMap.put("result", "error_bad_request");
            responseMap.put("query_state", state);
            responseMap.put("query_county", county);
            return adapter.toJson(responseMap);
        }

        int code = new StateCountyCodeFetcher().getCountyCode(this.stateCodes.get(state), county);
        if (code == 0) {
            responseMap.put("result", "error_bad_request");
            responseMap.put("query_state", state);
            responseMap.put("query_county", county);
            responseMap.put("message", "county doesn't exist within specified state");
            return adapter.toJson(responseMap);
        }


        return null;
    }

}
