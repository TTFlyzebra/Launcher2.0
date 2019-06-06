package com.flyzebra.flyui.chache;

/**
 * Author: FlyZebra
 * Time: 18-3-29 下午9:07.
 * Discription: This is ICache
 */

public interface ICache<T extends Object> {
    T get(String key);
    void put(String key, T object);
}
