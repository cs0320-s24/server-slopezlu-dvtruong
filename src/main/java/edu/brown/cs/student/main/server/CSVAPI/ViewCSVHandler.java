package edu.brown.cs.student.main.server.CSVAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewCSVHandler implements Route {
  private CSVDataSource data;

  public ViewCSVHandler(CSVDataSource data) {
    this.data = data;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Moshi moshi = new Moshi.Builder().build();

    // For the response map
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    HashMap<String, Object> responseMap = new HashMap<>();

    if (!data.checkLoaded()) {
      responseMap.put("result", "error_datasource");
      responseMap.put(
          "message",
          "NO CSV has been loaded. Please use /loadcsv with the proper params to load one.");
      return adapter.toJson(responseMap);
    }

    // for returning the CSV data
    List<List<String>> returnData = new ArrayList<>();
    if (!data.headersProxy().isEmpty()) {
      List<String> row = new ArrayList<>();
      for (int i = 0; i < data.headersProxy().size(); i++) {
        for (String key : data.headersProxy().keySet()) {
          if (data.headersProxy().get(key) == i) {
            row.add(key);
          }
        }
      }
      returnData.add(row);
    }
    returnData.addAll(data.proxy());

    // TODO: remember to fix this later
    responseMap.put("result", "success");
    responseMap.put("data", returnData);
    return adapter.toJson(responseMap);
  }
}
