package edu.brown.cs.student.MockSources;

import edu.brown.cs.student.main.server.CensusAPI.CensusAPISources.BroadbandDataSource;
import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import java.io.IOException;
import java.util.List;

public class MockCensusAPISource implements BroadbandDataSource {
  private final List<broadbandData> data;

  public MockCensusAPISource(List<broadbandData> data) {
    this.data = data;
  }

  @Override
  public broadbandData getCountyData(stateCounty input)
      throws IllegalArgumentException, IOException {
    for (broadbandData county : this.data) {
      if (input.county().equals(county.data().get(0))
          && (input.state().equals(county.data().get(2)))) {
        return county;
      }
    }
    throw new IllegalArgumentException("state/county combination does not exist");
  }
}
