package edu.brown.cs.student.TestCensusAPI;

import com.google.common.util.concurrent.UncheckedExecutionException;
import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.FactoryFailureException;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPISources.CensusAPISource;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPISources.CensusDataSourceCache;
import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/** Tests the cache */
public class TestCensusDataSourceCache {
  private CensusAPISource source = new CensusAPISource();
  /**
   * Tests when the cache functions are successful (e.g. loading, hitting, and evicting)
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testCheckLoaded_SuccessfullyLoaded() throws IOException, FactoryFailureException {
    CensusDataSourceCache sourceCache = new CensusDataSourceCache(source, 2, 1, TimeUnit.MINUTES);
    broadbandData kingsData =
        sourceCache.getCountyData(new stateCounty("california", "kings_county"));
    Assert.assertEquals("83.5", kingsData.data().get(1));
    Assert.assertEquals(1, sourceCache.getStats().loadSuccessCount());

    // try to access the data again
    broadbandData kingsData1 =
        sourceCache.getCountyData(new stateCounty("california", "kings_county"));
    Assert.assertEquals(1, sourceCache.getStats().hitCount());

    // eviction when cache is full
    broadbandData middlesexData =
        sourceCache.getCountyData(new stateCounty("new_jersey", "middlesex_county"));
    broadbandData miamiDadeData =
        sourceCache.getCountyData(new stateCounty("florida", "miami-dade_county"));
    Assert.assertEquals(1, sourceCache.getStats().evictionCount());

    // eviction on time
    CensusDataSourceCache sourceCacheTimed =
        new CensusDataSourceCache(source, 0, 1, TimeUnit.MILLISECONDS);
    broadbandData middlesex =
        sourceCacheTimed.getCountyData(new stateCounty("new_jersey", "middlesex_county"));
    broadbandData miamiDade =
        sourceCacheTimed.getCountyData(new stateCounty("florida", "miami-dade_county"));
    Assert.assertEquals(sourceCacheTimed.getStats().evictionCount(), 1);
  }
/**Tests that an UncheckedExecutionException is thrown (UncheckedExecutionException wraps IllegalArgumentException)
 * **/
      @Test
      public void testUncheckedExecutionExceptionError() {
        CensusDataSourceCache sourceCache = new CensusDataSourceCache(source, 2, 1,
     TimeUnit.MINUTES);
        Assert.assertThrows(
            UncheckedExecutionException.class,
            () -> {
              sourceCache.getCountyData(new stateCounty(null, null));
              ;
            });
      }
}
