package edu.brown.cs.student;

import edu.brown.cs.student.main.server.CensusAPI.CensusAPISource;
import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class test_DataSource {
  @Test
  public void test_DataSource() throws IOException {
    broadbandData kings_cali =
        new CensusAPISource().getCountyData(new stateCounty("California", "Kings_County"));
    Assert.assertEquals(kings_cali.data(), 83.5);
  }
}
