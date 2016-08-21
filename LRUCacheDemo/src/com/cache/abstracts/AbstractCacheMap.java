package com.cache.abstracts;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.cache.interfaces.Cache;

public abstract class AbstractCacheMap<K, V> implements Cache<K, V> {

	public class CachedObject<K2,V2>{
		final K2 key;
		final V2 cachedObject;
		long lastAccess;	//最后访问次数
		long accessCount;//访问次数
		long ttl;					//对象存活时间
		
		CachedObject(K2 key,V2 value,long ttl){
			this.key=key;
			this.cachedObject = value;
			this.ttl=ttl;
			this.lastAccess=System.currentTimeMillis();
		}
		public boolean isExpired(){
			if(ttl==0){
				return false;
			}
			return lastAccess+ttl <System.currentTimeMillis();
		}
		
		V2 getObject(){
			lastAccess = System.currentTimeMillis();
			accessCount++;
			return cachedObject;
		}
	}
	
	protected Map<K,CachedObject<K,V>> cacheMap;
	private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
	private final Lock readLock = cacheLock.readLock();
	private final Lock writeLock = cacheLock.writeLock();
	
	protected int cacheSize;			//缓存大小　0->无限制
	protected boolean existCustomExpire; //是否设置默认时间
	
	protected long defaultExpire; //默认过期时间

	public AbstractCacheMap(int cacheSize, long defaultExpire) {
		this.cacheSize = cacheSize;
		this.defaultExpire = defaultExpire;
	}

	@Override
	public int size() {
		return cacheMap.size();
	}

	@Override
	public long getDefaultExpire() {
		return defaultExpire;
	}
	
	protected boolean isNeedClearExpiredObject(){
		return defaultExpire>0||existCustomExpire;
	}

	@Override
	public void put(K key, V value) {
		put(key,value,defaultExpire);
	}

	@Override
	public void put(K key, V value, long expire) {
		writeLock.lock();
		try {
			CachedObject<K,V> co = new CachedObject<K,V>(key,value,expire);
			if(expire!=0){
				existCustomExpire=true;
			}
			if(isFull()){
				eliminate();
			}
			cacheMap.put(key, co);
		} finally{
			writeLock.unlock();
		}
	}

	@Override
	public V get(K key) {
		readLock.lock();
		try {
			CachedObject<K,V> co=cacheMap.get(key);
			if(co==null){
				return null;
			}
			if(co.isExpired()==true){
				cacheMap.remove(key);
				return null;
			}
			return co.getObject();
		} finally{
			readLock.unlock();
		}
	}

	@Override
	public int eliminate() {
		writeLock.lock();
		try {
			return eliminateCache();
		} finally{
			writeLock.unlock();
		}
	}
	/**
	 * 淘汰具体实现
	 * @return
	 */
	protected abstract int eliminateCache(); 
	
	@Override
	public void remove(K key) {
		writeLock.lock();
		try {
			cacheMap.remove(key);
		} finally{
			writeLock.unlock();
		}
	}

	@Override
	public boolean isFull() {
		if(cacheSize==0){
			return false;
		}
		return cacheMap.size() >= cacheSize;
	}

	@Override
	public void clear() {
		writeLock.lock();
		try {
			cacheMap.clear();
		} finally{
			writeLock.unlock();
		}
	}

	@Override
	public int getCacheSize() {
		return cacheSize;
	}

	@Override
	public boolean isEmpty() {
		return size()==0;
	}

}
