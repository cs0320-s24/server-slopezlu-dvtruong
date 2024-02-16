package edu.brown.cs.student.main.server.CensusAPI.CensusAPISources;

import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import java.io.IOException;

/** Interface for the data source for the handler of "/broadband" */
public interface BroadbandDataSource {
  /**
   * Obtains the data for a specified state/county combination
   *
   * @param input a record of stateCounty where both fields within the record are String with a
   *     county name and a state name (spaces should be replaced with _)
   * @return a record, broadbandData, that houses a list of the county name and state, percentage of
   *     people who have broadband access, and state and county code
   * @throws IllegalArgumentException if the state/county combination doesn't exist within the
   *     dataset
   * @throws IOException if something went wrong when trying to connect to the API if there is one
   *     to connect to
   */
  public broadbandData getCountyData(stateCounty input)
      throws IllegalArgumentException, IOException;
}
