package edu.brown.cs.student.TestCensusAPI;

import edu.brown.cs.student.main.server.CSVAPI.CSVDataSource;
import edu.brown.cs.student.main.server.CSVAPI.Parser.Creator.FactoryFailureException;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPISources.CensusAPISource;
import edu.brown.cs.student.main.server.CensusAPI.CensusAPISources.CensusDataSourceCache;
import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Tests the cache
 */
public class TestCensusDataSourceCache {
    /**
     * Tests when the cache functions are successful (e.g. loading, hitting, and evicting)
     * @throws IOException
     * @throws FactoryFailureException
     */
    @Test
    public void testCheckLoaded_SuccessfullyLoaded() throws IOException, FactoryFailureException {
        CensusAPISource  source = new CensusAPISource();
        CensusDataSourceCache sourceCache = new CensusDataSourceCache(source,2, 1 , TimeUnit.MINUTES);
        broadbandData kingsData = sourceCache.getCountyData(new stateCounty("california", "kings_county"));
        Assert.assertEquals("83.5", kingsData.data().get(1));
        Assert.assertEquals(1, sourceCache.getStats().loadSuccessCount());

        //try to access the data again
        broadbandData kingsData1 = sourceCache.getCountyData(new stateCounty("california", "kings_county"));
        Assert.assertEquals(1, sourceCache.getStats().hitCount());

        //eviction when cache is full
        broadbandData middlesexData = sourceCache.getCountyData(new stateCounty("new_jersey", "middlesex_county"));
        broadbandData miamiDadeData = sourceCache.getCountyData(new stateCounty("florida", "miami-dade_county"));
        Assert.assertEquals(1, sourceCache.getStats().evictionCount());

        //eviction on time
        CensusDataSourceCache sourceCacheTimed = new CensusDataSourceCache(source,0, 1, TimeUnit.SECONDS);
        broadbandData middlesex = sourceCacheTimed.getCountyData(new stateCounty("new_jersey", "middlesex_county"));
        broadbandData miamiDade= sourceCacheTimed.getCountyData(new stateCounty("florida", "miami-dade_county"));
        Assert.assertEquals(1, sourceCacheTimed.getStats().evictionCount());
    }
}
