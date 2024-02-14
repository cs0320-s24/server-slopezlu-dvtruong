package edu.brown.cs.student.main.server.CensusAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CensusAPIHandler implements Route {
  private BroadbandDataSource dataSource;

  public CensusAPIHandler(BroadbandDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Moshi moshi = new Moshi.Builder().build();

    // For the response map
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    // create a response map
    Map<String, Object> responseMap = new HashMap<>();
    LocalDateTime requestTime = LocalDateTime.now();

    String state = request.queryParams("state");
    String county = request.queryParams("county");

    // both fields are empty
    if ((state == null) && (county == null)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_state", state);
      responseMap.put("query_county", county);
      responseMap.put(
          "date & time of request",
          requestTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      responseMap.put(
          "message",
          "Please input a state and county to get data for. If there are spaces in the names of either, please replace them with _");
      return adapter.toJson(responseMap);
      // if county is empty
    } else if ((state != null) && (county == null)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_state", state);
      responseMap.put("query_county", county);
      responseMap.put(
          "date & time of request",
          requestTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      responseMap.put(
          "message",
          "Please input a county to get data for. If there are spaces in the name, please replace them with _");
      return adapter.toJson(responseMap);
      // if state is empty
    } else if ((state == null) && (county != null)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_state", state);
      responseMap.put("query_county", county);
      responseMap.put(
          "date & time of request",
          requestTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      responseMap.put(
          "message",
          "Please input a state to get data for. If there are spaces in the name, please replace them with _");
      return adapter.toJson(responseMap);
    }
    try {
      broadbandData data = this.dataSource.getCountyData(new stateCounty(state, county));
      responseMap.put("result", "success");
      responseMap.put("state", state);
      responseMap.put("county", county);
      responseMap.put("percentage of people that have broadband access", data.data().get(1));
      responseMap.put(
          "date & time of request",
          requestTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      return adapter.toJson(responseMap);
    } catch (IllegalArgumentException e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("query_state", state);
      responseMap.put("query_county", county);
      responseMap.put(
          "date & time of request",
          requestTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      responseMap.put("message", "state/county combination doesn't exist");
      return adapter.toJson(responseMap);
    } catch (IOException e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("query_state", state);
      responseMap.put("query_county", county);
      responseMap.put(
          "date & time of request",
          requestTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      return adapter.toJson(responseMap);
    }
  }
}
