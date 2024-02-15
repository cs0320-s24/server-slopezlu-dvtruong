package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.CSVAPI.CSVDataSource;
import edu.brown.cs.student.main.server.CSVAPI.LoadCSVHandler;
import edu.brown.cs.student.main.server.CSVAPI.SearchCSVHandler;
import edu.brown.cs.student.main.server.CSVAPI.ViewCSVHandler;
import edu.brown.cs.student.main.server.CensusAPI.BroadbandDataSource;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPIHandler;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPISource;
import edu.brown.cs.student.main.server.CensusAPI.CensusDataSourceCache;
import java.io.IOException;
import spark.Spark;

public class Server {
  static final int port = 3232;

  private CSVDataSource csvDataSource;
  private BroadbandDataSource broadbandDataSource;

  public Server(CSVDataSource csvDataSource, BroadbandDataSource broadbandDataSource)
      throws IOException {
    this.csvDataSource = csvDataSource;
    this.broadbandDataSource = broadbandDataSource;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Method", "*");
        });
    Spark.get("/broadband", new CensusAPIHandler(this.broadbandDataSource));
    Spark.get("/loadcsv", new LoadCSVHandler(this.csvDataSource));
    Spark.get("/searchcsv", new SearchCSVHandler(this.csvDataSource));
    Spark.get("/viewcsv", new ViewCSVHandler(this.csvDataSource));
    Spark.init();
    Spark.awaitInitialization();
  }

  public static void main(String[] args) {
    try {
      Server server =
          new Server(new CSVDataSource(), new CensusDataSourceCache(new CensusAPISource(), 5, 1));
    } catch (IOException e) {
      System.out.println("error occurred when trying to fetch state codes");
    }
    System.out.print("starting server...");
  }
}
