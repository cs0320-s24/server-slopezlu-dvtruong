package edu.brown.cs.student.main.server.CensusAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CensusAPIHandler implements Route {
  private Map<String, String> stateCodes;

  public CensusAPIHandler(Map<String, String> stateCodes) {
    this.stateCodes = stateCodes;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Moshi moshi = new Moshi.Builder().build();

    // For the response map
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    // For the retrieved data
    // JsonAdapter<broadbandData> CensusDataAdapter = moshi.adapter(broadbandData.class);
    // We think we need to use a map to store the response we get from the api server
    // Need to create a record (which should take in the map that we mentioned above)

    // create a response map
    Map<String, Object> responseMap = new HashMap<>();

    String state = request.queryParams("state");
    String county = request.queryParams("county");
    if ((state == null) || (county == null)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_state", state);
      responseMap.put("query_county", county);
      return adapter.toJson(responseMap);
    }

    String stateCode = this.stateCodes.get(state);
    String countyCode = new StateCountyCodeFetcher().getCountyCode(stateCode, county);
    if (countyCode.equals("0")) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_state", state);
      responseMap.put("query_county", county);
      responseMap.put("message", "county doesn't exist within specified state");
      return adapter.toJson(responseMap);
    }

    try {
      broadbandData data = new CensusAPISource().getCountyData(countyCode, stateCode);
      responseMap.put("result", "success");
      responseMap.put("state", state);
      responseMap.put("county", county);
      responseMap.put("percentage of people that have broadband access", data.S2802_C03_022E());
      return adapter.toJson(responseMap);
    } catch (IOException e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("query_state", state);
      responseMap.put("query_county", county);
      return adapter.toJson(responseMap);
    }
  }
}
