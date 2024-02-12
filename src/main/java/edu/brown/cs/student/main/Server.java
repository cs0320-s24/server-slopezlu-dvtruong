package edu.brown.cs.student.main;

import edu.brown.cs.student.main.CensusAPI.CensusAPIHandler;
import edu.brown.cs.student.main.CensusAPI.CensusAPIDataSource;
import spark.Spark;
import static spark.Spark.after;

public class Server {
    static final int port = 3232;
    public final CensusAPIDataSource state;

    public Server(CensusAPIDataSource state){
        this.state = state;
        Spark.port(port);
        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Method", "*");
        });
        Spark.get("/broadband", new CensusAPIHandler(state));
        Spark.init();
        Spark.awaitInitialization();
    }
    public static void main(String[] args) {
        Server server = new Server(new CensusAPIDataSource());
        System.out.println("Server started; exiting main...");
    }
}

