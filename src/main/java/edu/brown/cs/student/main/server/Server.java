package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.CSVAPI.CSVDataSource;
import edu.brown.cs.student.main.server.CSVAPI.LoadCSVHandler;
import edu.brown.cs.student.main.server.CSVAPI.SearchCSVHandler;
import edu.brown.cs.student.main.server.CSVAPI.ViewCSVHandler;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPIHandler;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPISources.BroadbandDataSource;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPISources.CensusAPISource;
import java.io.IOException;
import spark.Spark;

/**
 * Class that constructs and runs a web server by initializing the routes for several API endpoints
 * to allow end-users to make requests and receive responses from the CSV API server, or from the
 * ACS Census API server. Constructed using the Spark framework *
 */
public class Server {
  /**
   * Initializes the server with the specified data sources and routes
   *
   * @param csvDataSource Data source for loadcsv, viewcsv, and searchcsv functionality
   * @param broadbandDataSource Data source for broadband data from the ACS Census API server
   * @throws IOException If an I/O error occurs during server initialization.
   */
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
  /**
   * Main method used to initialize the server with CSDataSource and CensusAPISource as parameters.
   * Produces a message in the terminal to indicate a successful start up of server *
   */
  public static void main(String[] args) {
    try {
      Server server = new Server(new CSVDataSource(), new CensusAPISource());
    } catch (IOException e) {
      System.out.println("Error occurred when trying to fetch state codes");
    }
    System.out.print("Starting server...");
  }
}
