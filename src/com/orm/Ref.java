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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * A small Reflection tool kit.
 * @author 帮杰
 *
 */
public class Ref {

	public static Class<?> cloneClass(Class<?> clazz){
		Class<?> newClazz = null;
		try {
			newClazz = Class.forName(clazz.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return newClazz;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> clazz){
		T t = null;
		try {
			t = (T) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public static Field[] getBeanFields(Class<?> beanClazz){
		List<Field> fieldList = new ArrayList<Field>();
		Class<?> superClass = cloneClass(beanClazz);
		Field[] fields = null;
		while(superClass!=null){
			fields = superClass.getDeclaredFields();
			if(fields!=null){
				for(Field field:fields){
					if (isBeanField(field)) {
						fieldList.add(field);
					}
				}
			}
			superClass = superClass.getSuperclass();
		}
		return fieldList.isEmpty()?null:fieldList.toArray(fields);
	}
	
	public static boolean isBeanField(Field field) {
		int modifiers = field.getModifiers();
		return !(Modifier.isStatic(modifiers)||Modifier.isTransient(modifiers)||Modifier.isFinal(modifiers));
	}
	
	public static Field getFieldByName(Class<?> beanClazz,String fieldName){
		Class<?> superClass = cloneClass(beanClazz);
		while(superClass!=null){
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				superClass = superClass.getSuperclass();
			} catch (SecurityException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	public static Object getFieldVal(Object obj,String fieldName){
		try {
			Field field = getFieldByName(obj.getClass(),fieldName);
			field.setAccessible(true);
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object getFieldVal(Object obj,Field field){
		try {
			field.setAccessible(true);
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setFieldVal(Object obj,String fieldName,Object value){
		Field field = getFieldByName(obj.getClass(),fieldName);
		if(field!=null){
			try {
				field.setAccessible(true);
				field.set(obj, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public static void setFieldVal(Object obj,Field field,Object value){
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
