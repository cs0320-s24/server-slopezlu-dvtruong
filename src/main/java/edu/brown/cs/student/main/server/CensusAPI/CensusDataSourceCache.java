package edu.brown.cs.student.main.server.CensusAPI;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CensusDataSourceCache implements BroadbandDataSource {
  private final LoadingCache<stateCounty, broadbandData> cache;
  private final BroadbandDataSource wrappedSource;

  public CensusDataSourceCache(BroadbandDataSource sourceToWrap) {
    this.wrappedSource = sourceToWrap;
    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .recordStats()
            .build(
                new CacheLoader<stateCounty, broadbandData>() {
                  @Override
                  public broadbandData load(stateCounty stateCounty) throws IOException {
                    System.out.println("hello");
                    System.out.println(cache.stats());
                    return wrappedSource.getCountyData(stateCounty);
                  }
                });
  }

  @Override
  public broadbandData getCountyData(stateCounty input) throws IOException {
    broadbandData result = cache.getUnchecked(input);
    System.out.println(cache.stats());
    return result;
  }
}
