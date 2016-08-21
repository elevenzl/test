package com.cache.lrucache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.cache.abstracts.AbstractCacheMap;

public class LRUCache<K,V> extends AbstractCacheMap<K, V> {

	public LRUCache(int cacheSize, long defaultExpire) {
		super(cacheSize, defaultExpire);
		AtomicInteger i = new AtomicInteger(2);
		this.cacheMap = new LinkedHashMap<K,CachedObject<K,V>>(cacheSize+1,1f,true){

			@Override
			protected boolean removeEldestEntry(
					Entry<K, AbstractCacheMap<K, V>.CachedObject<K, V>> eldest) {
				return LRUCache.this.removeEldestEntry(eldest);
			}
		};
	}
	
	private boolean removeEldestEntry(Map.Entry<K,CachedObject<K,V>> eldest){
		if(cacheSize==0){
			return false;
		}
		return size()>cacheSize;
	}

	@Override
	protected int eliminateCache() {
		if(!isNeedClearExpiredObject()){return 0;}
		
		Iterator<CachedObject<K,V>> iterator = cacheMap.values().iterator();
		int count=0;
		while(iterator.hasNext()){
			CachedObject<K,V> cacheObject = iterator.next();
			if(cacheObject.isExpired()){
				iterator.remove();
				count++;
			}
		}
		return count;
	}
	
}
