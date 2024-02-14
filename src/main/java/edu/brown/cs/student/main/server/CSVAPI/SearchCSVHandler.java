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
  private boolean checkLoadedCSV;

  public SearchCSVHandler(CSVDataSource data) {
    this.checkLoadedCSV = checkLoadedCSV;
    this.data = data;
  }

  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    HashMap<String, Object> responseMap = new HashMap<>();
    String searchFor = request.queryParams("searchFor");
    String columnIdentifier = request.queryParams("columnIdentifier");
    String useColumnHeaders = request.queryParams("useColumnHeaders");
    // If file was not loaded, return the appropriate response
    if (!data.checkLoaded()) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put("query_useColumnHeaders", useColumnHeaders);
      responseMap.put("message", "please load in a file before searching");
      return adapter.toJson(responseMap);
    }
    // If no searchFor is provided, return appropriate response
    else if ((searchFor.isEmpty())
        && (!useColumnHeaders.isEmpty())
        && (!columnIdentifier.isEmpty())) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put("query_useColumnHeaders", useColumnHeaders);
      responseMap.put("message", "please specify a value to search for");
      return adapter.toJson(responseMap);
    }
    // If no useColumnHeader is provided, return appropriate response
    else if ((!searchFor.isEmpty())
        && (useColumnHeaders.isEmpty())
        && (!columnIdentifier.isEmpty())) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put("query_useColumnHeaders", useColumnHeaders);
      responseMap.put("message", "please specify whether to use column headers or not");
      return adapter.toJson(responseMap);
    }
    try {
      CSVSearch searcher = new CSVSearch();
      responseMap.put("result", "success");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put("query_useColumnHeaders", useColumnHeaders);
      Type listListString =
          Types.newParameterizedType(
              List.class, Types.newParameterizedType(List.class, String.class));
      JsonAdapter<List<List<String>>> searchAdapter = moshi.adapter(listListString);
      responseMap.put(
          "data",
          searchAdapter.toJson(
              searcher.search(
                  data.proxy(),
                  data.headersProxy(),
                  useColumnHeaders,
                  searchFor,
                  columnIdentifier)));
      responseMap.put("message", "successfully performed a search");
      return adapter.toJson(responseMap);

    } catch (IllegalArgumentException e) {
      responseMap.put("result", "error_no_such_column");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put("query_useColumnHeaders", useColumnHeaders);
      if (useColumnHeaders.equals("true")) {
        List<String> availableHeaders = new ArrayList<>();
        for (String key : data.headersProxy().keySet()) {
          availableHeaders.add(key);
        }
        responseMap.put(
            "message",
            "column "
                + columnIdentifier
                + " was not found. Because useColumnHeaders=true, the following column headers are available: "
                + availableHeaders);
      } else if (useColumnHeaders.equals("false")) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < data.headersProxy().keySet().size(); i++) {
          indices.add(i);
        }

        responseMap.put(
            "message",
            "column "
                + columnIdentifier
                + " was not found. Because useColumnHeaders=false, the following column indices are available: "
                + indices);
      }
      return adapter.toJson(responseMap);
    }
  }
}
