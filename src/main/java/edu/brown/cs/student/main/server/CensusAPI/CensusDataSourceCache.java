 package edu.brown.cs.student.main.server.CensusAPI;

 import java.util.Collection;
 import java.util.Map;

 import com.google.common.cache.CacheBuilder;
 import com.google.common.cache.CacheLoader;
 import com.google.common.cache.LoadingCache;
 public class CensusDataSourceCache {
    private CensusAPISource census;
    private LoadingCache<String, Map<String, Object>> cache;
    public CensusDataSourceCache(CensusAPISource census){
        this.census = census;

    }
 }
