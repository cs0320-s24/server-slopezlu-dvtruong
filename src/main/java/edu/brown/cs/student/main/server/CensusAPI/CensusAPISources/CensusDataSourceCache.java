package edu.brown.cs.student.main.server.CensusAPI.CensusAPISources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes.stateCounty;
import edu.brown.cs.student.main.server.CensusAPI.broadbandData;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Class that caches responses from the Census API and uses some of CensusAPISource functionality to
 * load in new data in the cache
 */
public class CensusDataSourceCache implements BroadbandDataSource {
  private final LoadingCache<stateCounty, broadbandData> cache;
  private final BroadbandDataSource wrappedSource;

  /**
   * Constructor for the cache data source in which removal can be modified based on how long the
   * cache has been occupied and how full the cache can be
   *
   * @param sourceToWrap the data source that the cache loads from if the data doesn't already exist
   *     within the cache
   * @param cacheSize the desired number of items for the cache to hold before removal
   * @param timeDuration the desired number of minutes the cache should hold data for before removal
   */
  public CensusDataSourceCache(
      BroadbandDataSource sourceToWrap, Integer cacheSize, Integer timeDuration) {
    this.wrappedSource = sourceToWrap;
    if (cacheSize == 0 && timeDuration == 0) {
      this.cache =
          CacheBuilder.newBuilder()
              .recordStats()
              .build(
                  new CacheLoader<stateCounty, broadbandData>() {
                    @Override
                    public broadbandData load(stateCounty stateCounty) throws IOException {
                      return wrappedSource.getCountyData(stateCounty);
                    }
                  });

    } else if (cacheSize == 0) {
      this.cache =
          CacheBuilder.newBuilder()
              .expireAfterWrite(timeDuration, TimeUnit.MINUTES)
              .recordStats()
              .build(
                  new CacheLoader<stateCounty, broadbandData>() {
                    @Override
                    public broadbandData load(stateCounty stateCounty) throws IOException {
                      return wrappedSource.getCountyData(stateCounty);
                    }
                  });
    } else if (timeDuration == 0) {
      this.cache =
          CacheBuilder.newBuilder()
              .maximumSize(cacheSize)
              .recordStats()
              .build(
                  new CacheLoader<stateCounty, broadbandData>() {
                    @Override
                    public broadbandData load(stateCounty stateCounty) throws IOException {
                      return wrappedSource.getCountyData(stateCounty);
                    }
                  });
    } else {
      this.cache =
          CacheBuilder.newBuilder()
              .maximumSize(cacheSize)
              .expireAfterWrite(timeDuration, TimeUnit.MINUTES)
              .recordStats()
              .build(
                  new CacheLoader<stateCounty, broadbandData>() {
                    @Override
                    public broadbandData load(stateCounty stateCounty) throws IOException {
                      return wrappedSource.getCountyData(stateCounty);
                    }
                  });
    }
    if (cacheSize < 0 || timeDuration < 0) {
      throw new IllegalArgumentException("Please enter a positive value");
    }
  }

  /**
   * Retrieves data from the cache based on a desired state and county
   *
   * <p>If the data doesn't already exist within the cache, it will send a request to the Census API
   * and load the data into the cache
   *
   * @param input a record of stateCounty where both fields within the record are String with a
   *     county name and a state name (spaces should be replaced with _)
   * @return a record, broadbandData, that holds a list of Strings with the county name and state,
   *     percentage of people with broadband access, and the state and county codes
   */
  @Override
  public broadbandData getCountyData(stateCounty input) {
    broadbandData result = cache.getUnchecked(input);
    System.out.println(cache.stats());
    return result;
  }
}
