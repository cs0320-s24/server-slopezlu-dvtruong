package edu.brown.cs.student.main.server.CSVAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class responsible for handling HTTP requests to CSV API server to view the loaded CSV file * */
public class ViewCSVHandler implements Route {
  /**
   * Processes HTTP requests to view a loaded CSV file and produces a response in JSON format with
   * the result of the request, indicating whether it was successful or not, the CSV data itself,
   * and, in the case of an error, an appropriate response Handler ensures that a CSV file is first
   * loaded before attempting to perform a view operation, otherwise returns appropriate response
   *
   * @param CSVDataSource Data source for searchcsv functionality *
   */
  private CSVDataSource data;

  public ViewCSVHandler(CSVDataSource data) {
    this.data = data;
  }

  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();

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

    responseMap.put("result", "success");
    responseMap.put("data", data.proxy());
    return adapter.toJson(responseMap);
  }
}
