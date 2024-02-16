package edu.brown.cs.student.main.server.CensusAPI.CensusAPISources;

import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import java.io.IOException;

public interface BroadbandDataSource {
  public broadbandData getCountyData(stateCounty input)
      throws IllegalArgumentException, IOException;
}
