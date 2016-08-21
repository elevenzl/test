package com.cache.interfaces;

public interface Cache<K, V> {
	/**
	 * 返回当前缓存的大小
	 * @return
	 */
	int size();
	/**
	 * 返回默认存活时间
	 * @return
	 */
	long getDefaultExpire();
	/**
	 * 向缓存中添加value对象,生存时间为默认时间
	 * @param key
	 * @param value
	 */
	void put(K key,V value);
	/**
	 * 向缓存中添加value对象，并指定存活时间
	 * @param key
	 * @param value
	 * @param expire
	 */
	void put(K key,V value,long expire);
	/**
	 * 查找缓存对象
	 * @param key
	 * @return
	 */
	V get(K key);
	/**
	 * 淘汰对象
	 * @return
	 */
	int eliminate();
	/**
	 * 删除缓存对象
	 * @param key
	 */
	void remove(K key);
	/**
	 * 缓存是否已满
	 * @return
	 */
	boolean isFull();
	/**
	 * 清除所有对象
	 */
	void clear();
	/**
	 * 返回缓存大小
	 * @return
	 */
	int getCacheSize();
	/**
	 * 缓存是否为空
	 * @return
	 */
	boolean isEmpty();
}
