package edu.brown.cs.student.main.CensusAPI;
import spark.Spark;
import static spark.Spark.after;

public class Server {
    static final int port = 3232;
    //broadBandDataSource might need to be an interface, not a class (WeatherDataSource is an interface in class livecode)
    //If you have multiple endpoints, then use an interface. Otherwise, use just a class (determine how many endpoints you have!)
    private final CensusDataSource state;
    public Server(CensusDataSource state){
        this.state = state;
        Spark.port(port);
        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Method", "*");
        });
    //endpoint for Spark.get() to be determined-waiting for response from TA
        Spark.get("https://api.census.gov/data/2021/acs/acs1/subject/variables", new CensusAPIHandler(state));
        Spark.awaitInitialization();
    }
    public static void main(String[] args) {
        Server server = new Server(new CensusDataSource());
        System.out.println("Server started; exiting main...");
    }
}
