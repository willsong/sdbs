package com.willsong.sdbs.datastore;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.willsong.sdbs.statement.WhereClause;

/**
 * Represents an instance of a table.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class Table {

	protected Database mDatabase;
	protected String mName;
	protected String mFullName;
	protected ArrayList<FieldDefinition> mFields;
	protected ArrayList<Tuple> mTuples;
	protected Class<?> mDef;
	
	public Table(Database database, String name) {
		mDatabase = database;
		mName = name;
		mFullName = database.getName() + "_" + name;
		mFields = new ArrayList<FieldDefinition>();
		mTuples = new ArrayList<Tuple>();
	}
	
	/**
	 * Adds a field definition.
	 * 
	 * @param	def	the field to add
	 */
	public void addField(FieldDefinition def) {
		mFields.add(def);
	}
	
	/**
	 * Replaces the current field list with the given object.
	 * 
	 * @param	fields	the field list to set
	 */
	public void setFields(ArrayList<FieldDefinition> fields) {
		mFields = fields;
	}
	
	public ArrayList<FieldDefinition> getFields() {
		return mFields;
	}
	
	/**
	 * Determines whether the field of the given name exists in this table or not.
	 * 
	 * @param 	fieldName	the name of the field to check
	 * @return				true or false
	 */
	public boolean hasField(String fieldName) {
		if (mDef == null) {
			return false;
		}
		
		try {
			mDef.getField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Determines whether the given value is a valid type for the given field or not.
	 * 
	 * @param	fieldName	the name of the field to check
	 * @param	value		the value to check
	 * @return				true or false
	 */
	public boolean isValidFieldValue(String fieldName, Object value) {
		if (mDef == null) {
			return false;
		}
		
		try {
			Field field = mDef.getField(fieldName);
			return field.getType().equals(value.getClass());
		} catch (NoSuchFieldException | SecurityException e) {
			return false;
		}
	}
	
	public boolean isValidFieldValue(int fieldNo, Object value) {
		if (mDef == null) {
			return false;
		}
		
		try {
			Field field = mDef.getField(mFields.get(fieldNo).getName());
			return field.getType().equals(value.getClass());
		} catch (NoSuchFieldException | SecurityException e) {
			return false;
		}
	}
	
	public FieldDefinition getFieldDefinition(int fieldNo) {
		return mFields.get(fieldNo);
	}
	
	public void create() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
		TableDefinitionLoader loader = new TableDefinitionLoader();
		loader.setFields(mFields);
		mDef = loader.loadClass(mFullName);
		if (mDef == null) {
			throw new ClassNotFoundException("Failed to load table definition");
		}
		mDatabase.addTable(mName, this);
	}
	
	public String getFullDisplayName() {
		return mDatabase.getName() + "." + mName;
	}
	
	public String getName() {
		return mName;
	}
	
	public int getNumFields() {
		return mFields.size();
	}
	
	public Database getDatabase() {
		return mDatabase;
	}
	
	public ArrayList<Tuple> getTuples(WhereClause where) {
		if (where == null) {
			return mTuples;
		} else {
			String field = where.getField();
			Object value = where.getValue();
			
			ArrayList<Tuple> result = new ArrayList<Tuple>();
			
			for (Tuple row : mTuples) {
				if (row.equals(field, value)) {
					result.add(row);
				}
			}
			
			return result;
		}
	}
	
	public ArrayList<Tuple> getTuples2() {
		return mTuples;
	}
	
	public void insertTuple(Tuple row) {
		mTuples.add(row);
	}
	
	public Class<?> getDefinition() {
		return mDef;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Table: ").append(getFullDisplayName()).append("\n")
			.append("   Number of fields: ").append(mFields.size()).append("\n")
			.append("   Number of tuples: ").append(mTuples.size()).append("\n");
		
		return buffer.toString();
	}
}
