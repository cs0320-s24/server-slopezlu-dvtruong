package edu.brown.cs.student;

import edu.brown.cs.student.main.server.CensusAPI.CensusAPISource;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class test_DataSource {
  @Test
  public void test_DataSource() throws IOException {
    broadbandData kings_cali = new CensusAPISource().getCountyData("031", "06");
    Assert.assertEquals(kings_cali.S2802_C03_022E(), 83.5);
  }
}
