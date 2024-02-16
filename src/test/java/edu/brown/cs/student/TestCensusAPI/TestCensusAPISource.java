<<<<<<< HEAD
package edu.brown.cs.student.TestCensusAPI;

import edu.brown.cs.student.main.server.CensusAPI.CensusAPISources.CensusAPISource;
import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/** Class responsible for testing the CensusAPISource class * */
public class TestCensusAPISource {
  /** Tests that the correct output is produced * */
  @Test
  public void testGetCountyData_ValidInputs() throws IOException {
    CensusAPISource source = new CensusAPISource();
    stateCounty input = new stateCounty("new_jersey", "middlesex_county");
    broadbandData data = source.getCountyData(input);
    Assert.assertEquals("90.6", data.data().get(1));
  }
  /** Tests that an IllegalArgumentException is thrown if given an invalid state input * */
  @Test
  public void testGetCountyData_InvalidStateInput() {
    CensusAPISource source = new CensusAPISource();
    stateCounty input = new stateCounty("roblox", "middlesex_county");
    Assert.assertThrows(
        IllegalArgumentException.class,
        () -> {
          broadbandData data = source.getCountyData(input);
        });
  }
  /** Tests that an IllegalArgumentException is thrown if given an invalid county input * */
  @Test
  public void testGetCountyData_InvalidCountyInput() {
    CensusAPISource source = new CensusAPISource();
    stateCounty input = new stateCounty("new_jersey", "roblox");
    Assert.assertThrows(
        IllegalArgumentException.class,
        () -> {
          broadbandData data = source.getCountyData(input);
        });
  }
  /** Tests that an IllegalArgumentException is thrown if not given inputs for both parameters * */
  @Test
  public void testGetCountyData_MissingBothInputs() {
    CensusAPISource source = new CensusAPISource();
    stateCounty input = new stateCounty("", "");
    Assert.assertThrows(
        IllegalArgumentException.class,
        () -> {
          broadbandData data = source.getCountyData(input);
        });
  }
}
