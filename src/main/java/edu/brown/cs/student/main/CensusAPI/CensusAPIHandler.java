package edu.brown.cs.student.main.CensusAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CensusAPIHandler implements Route {
    private final CensusAPIDataSource state;
    private Map<String, Integer> stateCodes;

    public CensusAPIHandler(CensusAPIDataSource state) {
        this.state = state;
        try {
            this.stateCodes = new StateCountyCodeFetcher().getStateCodes();
    } catch (IOException e) {
            //idk what to put here
        }
    }

        @Override
    public Object handle(Request request, Response response) throws Exception {
        Moshi moshi = new Moshi.Builder().build();

        //For the response map
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

        //For the retrieved data
        JsonAdapter<broadbandData> CensusDataAdapter = moshi.adapter(broadbandData.class);
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

        int stateCode = this.stateCodes.get(state);
        int countyCode = new StateCountyCodeFetcher().getCountyCode(stateCode, county);
        if (countyCode == 0) {
            responseMap.put("result", "error_bad_request");
            responseMap.put("query_state", state);
            responseMap.put("query_county", county);
            responseMap.put("message", "county doesn't exist within specified state");
            return adapter.toJson(responseMap);
        }

        try {
            broadbandData data = new CensusAPIDataSource().getCountyData(countyCode, stateCode);
        }
        catch(){}
        return null;
    }

}
