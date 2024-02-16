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

/**
 * Class responsible for handling HTTP requests to CSV API server to search for a specified value in
 * the loaded CSV file *
 */
public class SearchCSVHandler implements Route {
  /**
   * Processes HTTP requests to search in a loaded CSV file and produces a response in JSON format
   * with the result of the request, indicating whether it was successful or not, the query
   * parameters, such as searchFor value, columnIdentifier, and useColumnHeaders, and a message
   * providing more info on errors. Handler ensures that a CSV file is first loaded before
   * attempting to perform a search operation and makes sure that the inputs for the query are
   * valid, otherwise returns appropriate response
   *
   * @param CSVDataSource Data source for searchcsv functionality *
   */
  private CSVDataSource data;

  public SearchCSVHandler(CSVDataSource data) {
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

    if (!data.checkLoaded()) {
      responseMap.put("result", "error_datasource");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put("query_useColumnHeaders", useColumnHeaders);
      responseMap.put("message", "please load in a file before searching");
      return adapter.toJson(responseMap);
    } else if ((searchFor.isEmpty())) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put("query_useColumnHeaders", useColumnHeaders);
      responseMap.put("message", "please specify a value to search for");
      return adapter.toJson(responseMap);
    } else if ((!searchFor.isEmpty())
        && (useColumnHeaders.isEmpty())
        && (!columnIdentifier.isEmpty())) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put("query_useColumnHeaders", useColumnHeaders);
      responseMap.put("message", "please specify whether to use column headers or not");
      return adapter.toJson(responseMap);
    } else if (!(useColumnHeaders.equals("true") || useColumnHeaders.equals("false"))) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_searchFor", searchFor);
      responseMap.put("query_columnIdentifier", columnIdentifier);
      responseMap.put("query_useColumnHeaders", useColumnHeaders);
      responseMap.put(
          "message", "please enter either 'true' or 'false' for the useColumnHeader parameter");
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
