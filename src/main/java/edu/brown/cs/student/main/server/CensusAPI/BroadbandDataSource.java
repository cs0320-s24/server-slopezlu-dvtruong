package edu.brown.cs.student.main.server.CensusAPI;

import java.io.IOException;

public interface BroadbandDataSource {
  public broadbandData getCountyData(stateCounty input) throws IOException;
}
