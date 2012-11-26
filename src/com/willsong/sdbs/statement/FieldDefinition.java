package com.willsong.sdbs.statement;

/**
 * Intermediate class to be used when parsing statements.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class FieldDefinition {
	
	public static final int FIELD_TYPE_INTEGER = 1;
	public static final int FIELD_TYPE_STRING = 2;
	public static final int FIELD_TYPE_DOUBLE = 3;
	
	protected String mTable;
	protected String mName;
	protected int mType;
	
	public FieldDefinition() {
		this(null, null, -1);
	}
	
	public FieldDefinition(String table, String name, int type) {
		mTable = table;
		mName = name;
		mType = type;
	}
	
	public FieldDefinition(String name, int type) {
		this(null, name, type);
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
	
	public String getTable() {
		return mTable;
	}
	
	public void setTable(String table) {
		mTable = table;
	}
	
	public boolean isAll() {
		return mName.equals("*");
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
	
	public String getFullString() {
		return "" + (mTable == null ? "" : mTable +".") + mName;
	}
	
	public String getTypeString(boolean camelCase) {
		String type = "";
		
		switch (mType) {
			case FIELD_TYPE_INTEGER:
				type += camelCase ? "Integer" : "INTEGER";
				break;
			case FIELD_TYPE_STRING:
				type += camelCase ? "String" : "STRING";
				break;
			case FIELD_TYPE_DOUBLE:
				type += camelCase ? "Double" : "DOUBLE";
				break;
		}
		
		return type;
	}
	
	public String getTypeString() {
		return getTypeString(false);
	}
	
	/**
	 * Determines whether this instance is a full dot notation or not.
	 * 
	 * @return	true if full, false otherwise
	 */
	public boolean isFull() {
		return mTable != null;
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
