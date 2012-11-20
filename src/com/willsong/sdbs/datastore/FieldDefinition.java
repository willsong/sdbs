package com.willsong.sdbs.datastore;

/**
 * Intermediate class to be used when parsing statements.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class FieldDefinition {
	
	public static final int FIELD_TYPE_INTEGER = 1;
	public static final int FIELD_TYPE_STRING = 2;
	public static final int FIELD_TYPE_DOUBLE = 3;
	
	private String mName;
	private int mType;
	
	public FieldDefinition() {
		mName = "";
		mType = -1;
	}
	
	public FieldDefinition(String name, int type) {
		mName = name;
		mType = type;
	}
	
	public int getType() {
		return mType;
	}
	
	public void setType(int type) {
		mType = type;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public Object convertValue(String value) {
		Object newValue = null;
		
		switch (mType) {
			case FIELD_TYPE_INTEGER:
				newValue = new Integer(Integer.parseInt(value));
				break;
			case FIELD_TYPE_STRING:
				newValue = value;
				break;
			case FIELD_TYPE_DOUBLE:
				newValue = new Double(Double.parseDouble(value));
				break;
		}
		
		return newValue;
	}
	
	public String toString() {
		String me = "public ";
		
		switch (mType) {
			case FIELD_TYPE_INTEGER:
				me += "Integer ";
				break;
			case FIELD_TYPE_STRING:
				me += "String ";
				break;
			case FIELD_TYPE_DOUBLE:
				me += "Double ";
				break;
		}
		
		me += mName + ";";
		
		return me;
	}
	
	public String getTypeString() {
		String type = "";
		
		switch (mType) {
			case FIELD_TYPE_INTEGER:
				type += "INTEGER";
				break;
			case FIELD_TYPE_STRING:
				type += "STRING";
				break;
			case FIELD_TYPE_DOUBLE:
				type += "DOUBLE";
				break;
		}
		
		return type;
	}
	
	public static boolean isValidType(int type) {
		boolean isValid = false;
		
		switch (type) {
			case FIELD_TYPE_INTEGER:
			case FIELD_TYPE_STRING:
			case FIELD_TYPE_DOUBLE:
				isValid = true;
				break;
		}
		
		return isValid;
	}
}
