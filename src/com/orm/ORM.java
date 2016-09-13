/**
 * Copyright (c) 2011-2015, Mobangjack 莫帮杰 (mobangjack@foxmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Object relationship mapping.
 * @author 帮杰
 *
 */
public class ORM {

	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object... objects) {
		if (objects==null) {
			return true;
		}
		for (Object object : objects) {
			if (objects==null) {
				return true;
			}
			if (object instanceof Collection) {
				return ((Collection)object).isEmpty();
			}
			if (object instanceof Map) {
				return ((Map)object).isEmpty();
			}
		}
		return false;
	}
	
	@SafeVarargs
	public static <K,V> Map<K, V> smallest(Map<K, V>...maps) {
		if (isEmpty((Object[])maps)) {
			return null;
		}
		if (maps.length==1) {
			return maps[0];
		}
		Map<K, V> dest = maps[0];
		for (int i = 1; i < maps.length; i++) {
			if (maps[i].size()<dest.size()) {
				dest = maps[i];
			}
		}
		return dest;
	}
	
	@SafeVarargs
	public static <K,V> Map<K, V> largest(Map<K, V>...maps) {
		if (isEmpty((Object[])maps)) {
			return null;
		}
		if (maps.length==1) {
			return maps[0];
		}
		Map<K, V> dest = maps[0];
		for (int i = 1; i < maps.length; i++) {
			if (maps[i].size()>dest.size()) {
				dest = maps[i];
			}
		}
		return dest;
	}
	
	@SafeVarargs
	public static <K,V> boolean containsKey(K key,Map<K, V>...maps) {
		if (isEmpty((Object[])maps)) {
			return false;
		}
		for (Map<K, V> map : maps) {
			if (!map.containsKey(key)) {
				return false;
			}
		}
		return true;
	}
	
	@SafeVarargs
	public static <K,V> boolean containsValue(V value,Map<K, V>...maps) {
		if (isEmpty((Object[])maps)) {
			return false;
		}
		for (Map<K, V> map : maps) {
			if (!map.containsValue(value)) {
				return false;
			}
		}
		return true;
	}
	
	@SafeVarargs
	public static <K,V> boolean contain(K key,V value,Map<K, V>...maps) {
		if (isEmpty((Object[])maps)) {
			return false;
		}
		for (Map<K, V> map : maps) {
			if (!map.containsKey(key)) {
				return false;
			}else if (!map.containsValue(value)) {
				return false;
			}
		}
		return true;
	}
	
	@SafeVarargs
	public static <K,V> Map<K, V> intersectByKey(Map<K, V>...maps) {
		Map<K, V> smallest = smallest(maps);
		if (isEmpty(smallest)) {
			return null;
		}
		Map<K, V> opt = new HashMap<K, V>();
		for (Entry<K, V> e:smallest.entrySet()) {
			if (containsKey(e.getKey(), maps)) {
				opt.put(e.getKey(), e.getValue());
			}
		}
		return opt;
	}
	
	@SafeVarargs
	public static <K,V> Map<K, V> intersectByValue(Map<K, V>...maps) {
		Map<K, V> smallest = smallest(maps);
		if (isEmpty(smallest)) {
			return null;
		}
		Map<K, V> opt = new HashMap<K, V>();
		for (Entry<K, V> e:smallest.entrySet()) {
			if (containsValue(e.getValue(), maps)) {
				opt.put(e.getKey(), e.getValue());
			}
		}
		return opt;
	}

	@SafeVarargs
	public static <K,V> Map<K, V> intersect(Map<K, V>...maps) {
		Map<K, V> smallest = smallest(maps);
		if (isEmpty(smallest)) {
			return null;
		}
		Map<K, V> opt = new HashMap<K, V>();
		for (Entry<K, V> e:smallest.entrySet()) {
			if (contain(e.getKey(),e.getValue(), maps)) {
				opt.put(e.getKey(), e.getValue());
			}
		}
		return opt;
	}
	
	@SafeVarargs
	public static <K,V> Map<K, V> unite(Map<K, V>...maps) {
		Map<K, V> largest = largest(maps); 
		if (isEmpty(largest)) {
			return null;
		}
		Map<K, V> opt = new HashMap<K, V>();
		opt.putAll(largest);
		for (Map<K, V> map : maps) {
			for (Entry<K, V> e:map.entrySet()) {
				if (!largest.containsKey(e.getKey())) {
					opt.put(e.getKey(), e.getValue());
				}
			}
		}
		return opt;
	}
	
	public static void copy(Object fromBean,Object toBean) {
		mapToBean(toBean, beanToMap(fromBean));
	}
	
	@SuppressWarnings("rawtypes")
	public static Map beanToMap(Object bean) {
		if (bean==null) {
			return null;
		}
		Field[] fields = Ref.getBeanFields(bean.getClass());
		Map<String, Object> map = null;
		if(fields!=null){
			map = new HashMap<String, Object>(fields.length);
			for(Field field:fields){
				map.put(field.getName(), Ref.getFieldVal(bean, field));
			}
		}
		return map;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static List<Map> beansToMaps(List<Object> beans) {
		if (isEmpty(beans)) {
			return null;
		}
		List<Map> maps = new ArrayList<Map>();
		for (Object bean : beans) {
			maps.add(beanToMap(bean));
		}
		return maps;
	}
	
	@SuppressWarnings("rawtypes")
	public static Map beanToMap(Object bean,Map mapping) {
		return new CascadingMapping(2).addLayer(mapping).addLayer(beanToMap(bean)).doForwardMapping();
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static List<Map> beansToMaps(List<Object> beans,Map mapping) {
		if (isEmpty(beans)) {
			return null;
		}
		List<Map> maps = new ArrayList<Map>();
		for (Object bean : beans) {
			maps.add(beanToMap(bean,mapping));
		}
		return maps;
	}
	
	@SuppressWarnings("rawtypes")
	public static void mapToBean(Object bean,Map map) {
		Field[] fields = Ref.getBeanFields(bean.getClass());
		if (fields!=null) {
			for (Field field : fields) {
				if (map.containsKey(field.getName())) {
					Ref.setFieldVal(bean, field, map.get(field.getName()));
				}
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static void mapToBean(Object bean,Map map,Map mapping) {
		mapToBean(bean, new CascadingMapping(2).addLayer(mapping).addLayer(map).doForwardMapping());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> mapsToBeans(Class<?> beanClass,List<Map> maps) {
		if (isEmpty(beanClass,maps)) {
			return null;
		}
		List<T> list = new ArrayList<T>();
		T bean = null;
		for(Map<String, Object> map:maps) {
			bean = Ref.newInstance(beanClass);
			mapToBean(bean, map);
			list.add(bean);
		}
		return list.isEmpty()?null:list;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> mapsToBeans(Class<?> beanClass,List<Map> maps,Map mapping) {
		if (isEmpty(beanClass,maps,mapping)) {
			return null;
		}
		List<T> list = new ArrayList<T>();
		T bean = null;
		for(Map<String, Object> map:maps){
			bean = Ref.newInstance(beanClass);
			mapToBean(bean, map, mapping);
			list.add(bean);
		}
		return list.isEmpty()?null:list;
	}
	
}
