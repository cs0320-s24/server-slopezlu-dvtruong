package edu.brown.cs.student.main.CensusAPI;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.List;

public class CensusAPIHandler implements Route {
    private final CensusAPISource state;

    public CensusAPIHandler(CensusAPISource state) {
        this.state = state;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        Type listType = Types.newParameterizedType(List.class, String.class);
        JsonAdapter<CensusAPIResponse> adapter = moshi.adapter(listType);
        //We think we need to use a map to store the response we get from the api server
        //Need to create a record (which should take in the map that we mentioned above)
        return null;
    }
}
