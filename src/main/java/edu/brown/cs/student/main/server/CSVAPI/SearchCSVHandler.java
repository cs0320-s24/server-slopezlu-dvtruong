package edu.brown.cs.student.main.server.CSVAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSVSearch;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {
  private CSVDataSource data;

  public SearchCSVHandler(CSVDataSource data) {
    this.data = data;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    HashMap<String, Object> responseMap = new HashMap<>();
    String searchFor = request.queryParams("searchFor");
    String columnIdentifier = request.queryParams("columnIdentifier");
    if (!data.checkLoaded()) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put("message", "please load in a file before searching");
      return adapter.toJson(responseMap);
    } else if ((searchFor == null) && (columnIdentifier != null)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put("message", "please specify a value to search for");
      return adapter.toJson(responseMap);
    }
    try {
      CSVSearch searcher = new CSVSearch();
      responseMap.put("result", "success");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put(
          "data", searcher.search(data.proxy(), data.headerProxy(), searchFor, columnIdentifier));
      responseMap.put("message", "successfully performed a search");
      return adapter.toJson(responseMap);
    } catch (IllegalArgumentException e) {
      responseMap.put("result", "error_no_such_column_name");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      if (data.headersOrNot() == true) {
        List<String> availableHeaders = new ArrayList<>();
        for (String key : data.headerProxy().keySet()) {
          availableHeaders.add(key);
        }
        responseMap.put(
            "message",
            "column "
                + columnIdentifier
                + "was not found. The following column headers are available: "
                + availableHeaders);
      } else {
        List<String> indices = new ArrayList<>();
        for (String key : data.headerProxy().keySet()) {
          indices.add(key);
        }
        responseMap.put(
            "message",
            "column "
                + columnIdentifier
                + "was not found. The following column indices are available: "
                + indices);
      }
      return adapter.toJson(responseMap);
    }
  }
}
