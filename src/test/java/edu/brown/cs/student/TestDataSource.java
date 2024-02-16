package edu.brown.cs.student;
import static org.junit.jupiter.api.Assertions.assertEquals;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPISources.CensusAPISource;
import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class TestDataSource {
  @Test
  public void test_DataSource() throws IOException {
    broadbandData kings_cali =
        new CensusAPISource().getCountyData(new stateCounty("California", "Kings_County"));
    assertEquals("83.5", kings_cali.data().get(1));
  }
}
