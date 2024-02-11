package edu.brown.cs.student.main.CensusAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CensusAPIHandler implements Route {
    private final CensusDataSource state;
    public CensusAPIHandler(CensusDataSource state) {
        this.state = state;
    }

    @Override
    public Object handle(Request request, Response response) {
        Moshi moshi = new Moshi.Builder().build();
        Type listOfStringArrays = Types.newParameterizedType(List.class, Types.newParameterizedType(List.class, String.class));
        JsonAdapter<List<List<String>>> adapter = moshi.adapter(listOfStringArrays);
        List<List<String>> responseList = new ArrayList<>();
//        JsonAdapter<CensusAPIData> censusAPIDataJsonAdapter = moshi.adapter(CensusAPIData.class);
//        String state = request.params("state");
//        String county = request.params("county");
//        if(state == null || county == null) {
//            responseList.put("query_state", state);
//            responseList.put("query_county", county);
//            responseList.put("type", "error");
//            responseList.put("error_type", "missing_parameter");
//            responseList.put("error_arg", county == null ? "state" : "county");
//            return adapter.toJson(responseList);
//        }
        return null;
    }
}
