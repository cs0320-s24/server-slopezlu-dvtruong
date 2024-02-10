package edu.brown.cs.student.main.CensusAPI;

import spark.Access;
import spark.Spark;

import static spark.Spark.after;

public class Server {
    static final int port = 3232;
    public final CensusAPISource state;
    public Server(CensusAPISource state){
        this.state = state;
        Spark.port(port);

        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Method", "*");
        });
        Spark.get("/census", new CensusAPIHandler(state));
        Spark.init();
        Spark.awaitInitialization();
    }

}
