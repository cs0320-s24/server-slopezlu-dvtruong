package edu.brown.cs.student.main.server.CSVAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Parser.Creator.FactoryFailureException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {
  private final CSVDataSource data;

  public LoadCSVHandler(CSVDataSource data) {
    this.data = data;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Moshi moshi = new Moshi.Builder().build();

    // For the response map
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    HashMap<String, Object> responseMap = new HashMap<>();

    String filepath = request.queryParams("filepath");
    String headers = request.queryParams("headers");

    // handle it as usual and make sure to put the responseMaps as according to the exceptions that
    // might be thrown from CSVParser

    // errors that may occur when a parameter as specified above is empty or when both of them are
    // empty
    if ((filepath == null) && (headers != null)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_filepath", filepath);
      responseMap.put("query_headers", headers);
      responseMap.put("message", "please input a filepath");
      return adapter.toJson(responseMap);
    } else if ((filepath != null) && (headers == null)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_filepath", filepath);
      responseMap.put("query_headers", headers);
      responseMap.put(
          "message",
          "please indicate whether the CSV has headers by typing/inputting true or false into the headers param");
      return adapter.toJson(responseMap);
    } else if ((filepath == null) && (headers == null)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_filepath", filepath);
      responseMap.put("query_headers", headers);
      responseMap.put(
          "message",
          "please input a filepath and indicate whether the CSV has headers by typing/inputting true or false into the headers param");
      return adapter.toJson(responseMap);
    }

    Boolean headersOrNot = null;
    if (headers.toLowerCase().equals("true")) {
      headersOrNot = true;
    } else if (headers.toLowerCase().equals("false")) {
      headersOrNot = false;
    }
    // this is for what happens when having headers in the csv is not indicated within the request
    if (headersOrNot == null) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_filepath", filepath);
      responseMap.put("query_headers", headers);
      responseMap.put(
          "message",
          "please indicate whether the CSV has headers by typing/inputting true or false into the headers param");
      return adapter.toJson(responseMap);
    }

    try {
      data.load(filepath, headersOrNot);
      responseMap.put("result", "success");
      responseMap.put("query_filepath", filepath);
      responseMap.put("query_headers", headers);
      responseMap.put("message", "file successfully loaded");
      return adapter.toJson(responseMap);
    } catch (FileNotFoundException e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("query_filepath", filepath);
      responseMap.put("query_headers", headers);
      responseMap.put("message", "the file corresponding to the inputted filepath cannot be found");
      return adapter.toJson(responseMap);
    } catch (IOException e) {
      responseMap.put("result", "error_cannot_parse");
      responseMap.put("query_filepath", filepath);
      responseMap.put("query_headers", headers);
      responseMap.put(
          "message", "there was an error that occurred while trying to parse the CSV file");
      return adapter.toJson(responseMap);
    } catch (FactoryFailureException e) {
      responseMap.put("result", "error_cannot_parse");
      responseMap.put("query_filepath", filepath);
      responseMap.put("query_headers", headers);
      responseMap.put(
          "message",
          "there are rows where the numbers of columns are not consistent with the rest of the file");
      return adapter.toJson(responseMap);
    }

    // also look on how you can make the Parser more robust
  }
}
