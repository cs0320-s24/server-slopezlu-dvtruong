package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.CensusAPI.CensusAPIHandler;
import edu.brown.cs.student.main.server.CensusAPI.StateCountyCodeFetcher;
import java.io.IOException;
import spark.Spark;

public class Server {
  static final int port = 3232;

  public Server() throws IOException {
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Method", "*");
        });
    Spark.get("/broadband", new CensusAPIHandler(new StateCountyCodeFetcher().getStateCodes()));
    Spark.init();
    Spark.awaitInitialization();
  }

  public static void main(String[] args) {
    try {
      Server server = new Server();
    } catch (IOException e) {
      System.out.println("error occurred when trying to fetch state codes");
    }
    System.out.print("starting server...");
  }
}
