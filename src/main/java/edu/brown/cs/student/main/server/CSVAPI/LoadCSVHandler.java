package edu.brown.cs.student.main.server.CSVAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.FactoryFailureException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class responsible for handling HTTP requests to CSV API server to load CSV files * */
public class LoadCSVHandler implements Route {
  /**
   * Processes HTTP requests to load CSV data and produces a response in JSON format with the result
   * of the request, indicating whether it was successful or not, the query parameters, and a
   * message providing more info on errors. Handler ensures that the query parameters are valid,
   * otherwise returns appropriate response
   *
   * @param CSVDataSource Data source for loadcsv functionality *
   */
  private CSVDataSource data;

  public LoadCSVHandler(CSVDataSource data) {
    this.data = data;
  }

  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();

    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    HashMap<String, Object> responseMap = new HashMap<>();

    String filepath = request.queryParams("filepath");
    String filePathRestricted = "data/" + filepath;
    String headers = request.queryParams("headers");

    if ((filepath == null) && (headers != null)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("query_filepath", filepath);
      responseMap.put("query_headers", headers);
      responseMap.put("message", "please input a filepath starting from the data directory");
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
      data.load(filePathRestricted, headersOrNot);
      responseMap.put("result", "success");
      responseMap.put("query_filepath", filepath);
      responseMap.put("query_headers", headers);
      responseMap.put("message", "file successfully loaded");
      return adapter.toJson(responseMap);
    } catch (FileNotFoundException e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("query_filepath", filepath);
      responseMap.put("query_headers", headers);
      responseMap.put("message", "the file does not exist in data directory");
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
  }
}
