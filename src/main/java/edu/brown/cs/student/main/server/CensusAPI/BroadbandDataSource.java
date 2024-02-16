package edu.brown.cs.student.main.server.CensusAPI;

import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import java.io.IOException;

public interface BroadbandDataSource {
  public broadbandData getCountyData(stateCounty input) throws IOException;
}
