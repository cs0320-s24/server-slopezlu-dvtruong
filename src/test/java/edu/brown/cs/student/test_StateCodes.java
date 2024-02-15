package edu.brown.cs.student;

import edu.brown.cs.student.main.server.CensusAPI.StateCountyCodeFetcher;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class test_StateCodes {
  @Test
  public void test_StateCodes() throws IOException {
    Map<String, String> stateCodes = new StateCountyCodeFetcher().getStateCodes();
    Assert.assertEquals(stateCodes.get("alaska"), "02");
    Assert.assertEquals(stateCodes.get("new_jersey"), "34");
    Assert.assertEquals(stateCodes.get("dsnfsdjfndskf"), null);
  }

  @Test
  public void test_CountyCodes() throws IOException {
    String countyCode = new StateCountyCodeFetcher().getCountyCode("06", "glenn_county");
    Assert.assertEquals(countyCode, "021");
  }
}
