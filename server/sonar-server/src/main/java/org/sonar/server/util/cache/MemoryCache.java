/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.util.cache;

import org.sonar.server.exceptions.NotFoundException;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This in-memory cache relies on {@link org.sonar.server.util.cache.CacheLoader} to
 * load missing elements.
 * Warning - all searches are kept in memory, even when elements are not found.
 */
public class MemoryCache<K, V> {

  private final CacheLoader<K, V> loader;
  private final Map<K, V> map = new HashMap<>();

  public MemoryCache(CacheLoader<K, V> loader) {
    this.loader = loader;
  }

  @CheckForNull
  public V getNullable(K key) {
    V value = map.get(key);
    if (value == null) {
      if (!map.containsKey(key)) {
        value = loader.load(key);
        map.put(key, value);
      }
    }
    return value;
  }

  public V get(K key) {
    V value = getNullable(key);
    if (value == null) {
      throw new NotFoundException("Not found: " + key);
    }
    return value;
  }

  /**
   * Get values associated with keys. All the requested keys are included
   * in the Map result. Value is null if the key is not found in cache.
   */
  public Map<K, V> getAllNullable(Iterable<K> keys) {
    List<K> missingKeys = new ArrayList<>();
    Map<K, V> result = new HashMap<>();
    for (K key : keys) {
      V value = map.get(key);
      if (value == null && !map.containsKey(key)) {
        missingKeys.add(key);
      } else {
        result.put(key, value);
      }
    }
    if (!missingKeys.isEmpty()) {
      Map<K, V> missingValues = loader.loadAll(missingKeys);
      map.putAll(missingValues);
      result.putAll(missingValues);
      for (K missingKey : missingKeys) {
        if (!map.containsKey(missingKey)) {
          map.put(missingKey, null);
          result.put(missingKey, null);
        }
      }
    }
    return result;
  }

  public void clear() {
    map.clear();
  }
}
