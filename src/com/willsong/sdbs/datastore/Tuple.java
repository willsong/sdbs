package com.willsong.sdbs.datastore;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Represents a Tuple instance.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class Tuple {
	
	private Table mTable;
	private TableDefinition mData;
	
	public Tuple(Table table) {
		mTable = table;
	}
	
	public void setData(TableDefinition data) {
		mData = data;
	}
	
	public TableDefinition getData() {
		return mData;
	}
	
	public boolean equals(String name, Object value) {
		try {
			return mData.getClass().getField(name).get(mData).equals(value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return false;
		}
	}
	
	public Object[] getDataAsArray() {
		Field[] fList = mData.getClass().getFields();
		Object[] fieldArray = new Object[fList.length];
		
		for (int i = 0; i < fList.length; i++) {
			try {
				fieldArray[i] = fList[i].get(mData);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// @TODO: handle this better!!!
				fieldArray[i] = null;
			}
		}
		
		return fieldArray;
	}
	
	public String[] getFieldList() {
		Field[] fList = mData.getClass().getFields();
		String[] fieldList = new String[fList.length];
		
		for (int i = 0; i < fList.length; i++) {
			try {
				fieldList[i] = fList[i].getName();
			} catch (IllegalArgumentException e) {
				// @TODO: handle this better!!!
				fieldList[i] = null;
			}
		}
		
		return fieldList;
	}
}
