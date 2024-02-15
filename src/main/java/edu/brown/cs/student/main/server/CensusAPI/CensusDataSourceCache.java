package edu.brown.cs.student.main.server.CensusAPI;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CensusDataSourceCache implements BroadbandDataSource {
  private final LoadingCache<stateCounty, broadbandData> cache;
  private final BroadbandDataSource wrappedSource;

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
    if(cacheSize < 0 || timeDuration < 0){
        throw new IllegalArgumentException("Please enter a positive value");
    }
  }

  @Override
  public broadbandData getCountyData(stateCounty input) throws IOException {
    broadbandData result = cache.getUnchecked(input);
    System.out.println(cache.stats());
    return result;
  }
}
