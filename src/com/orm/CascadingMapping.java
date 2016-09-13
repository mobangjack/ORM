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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Cascading map.
 * （多级映射）
 * @author 帮杰
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CascadingMapping {

	private List<Map> mapList;
	
	public CascadingMapping() {
		mapList = new ArrayList<Map>();
	}

	public CascadingMapping(int initialCapacity) {
		mapList = new ArrayList<Map>(initialCapacity);
	}
	
	
	public List<Map> getMapList() {
		return mapList;
	}

	public void setMapList(List<Map> mapList) {
		this.mapList = mapList;
	}

	public CascadingMapping addLayer(Map layer) {
		mapList.add(layer);
		return this;
	}
	
	public boolean removeLayer(Map layer) {
		return mapList.remove(layer);
	}
	
	public Map removeLayer(int layerIndex) {
		return mapList.remove(layerIndex);
	}
	
	public void clear() {
		mapList.clear();
	}
	
	public Map getLayer(int layerIndex) {
		return mapList.get(layerIndex);
	}
	
	public Map doForwardMapping(int fromLayer,int toLayer) {
		if (fromLayer>toLayer) {
			return null;
		}
		int size = mapList.size();
		if (size<1) {
			return null;
		}
		if (fromLayer<1) {
			fromLayer = 1;
		}
		if (toLayer>size) {
			toLayer = size;
		}
		Map map = new HashMap();
		map.putAll(mapList.get(fromLayer-1));
		for (int i = fromLayer; i < toLayer; i++) {
			doForwardMapping(map, mapList.get(i));
		}
		return map;
	}
	
	public Map doForwardMapping() {
		if (mapList.isEmpty()) {
			return null;
		}else if (mapList.size()<2) {
			return mapList.get(0);
		}else {
			return doForwardMapping(1, mapList.size());
		}
	}
	
	public static void doForwardMapping(Map map,Map nextLayer) {
		Set<Entry> entries = map.entrySet();
		for (Entry e : entries) {
			map.put(e.getKey(), nextLayer.get(e.getValue()));
		}
	}
	
	public Map doBackTracking(int fromLayer,int toLayer) {
		if (fromLayer<toLayer) {
			return null;
		}
		int size = mapList.size();
		if (size<1) {
			return null;
		}
		if (toLayer<1) {
			toLayer = 1;
		}
		if (fromLayer>size) {
			fromLayer = size;
		}
		Map map = new HashMap();
		map.putAll(mapList.get(fromLayer-1));
		for (int i = fromLayer-2; i > toLayer-2; i--) {
			doBackTracking(map, mapList.get(i));
		}
		return map;
	}
	
	public Map doBackTracking() {
		if (mapList.isEmpty()) {
			return null;
		}else if (mapList.size()<2) {
			return mapList.get(mapList.size()-1);
		}else {
			return doBackTracking(mapList.size(), 1);
		}
	}
	
	public static void doBackTracking(Map map,Map previousLayer) {
		Set<Entry> entries = previousLayer.entrySet();
		for (Entry e : entries) {
			if (map.containsKey(e.getValue())) {
				map.put(e.getKey(), map.remove(e.getValue()));
			}
		}
	}
	
	public static void main(String[] args) {
		Map layer1 = new HashMap();
		layer1.put("f1", "c2");
		layer1.put("f2", "c2");
		layer1.put("f3", "c2");
		Map layer2 = new HashMap();
		layer2.put("c1", "o1");
		layer2.put("c2", "o2");
		layer2.put("c3", "o3");
		Map layer3 = new HashMap();
		layer3.put("o1", "g1");
		layer3.put("o2", "g2");
		layer3.put("o3", "g3");
		CascadingMapping mapping = new CascadingMapping();
		mapping.addLayer(layer1).addLayer(layer2).addLayer(layer3);
		System.out.println(mapping.doForwardMapping());
		System.out.println(mapping.doBackTracking());
	}
}
