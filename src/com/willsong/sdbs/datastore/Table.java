package com.willsong.sdbs.datastore;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import com.willsong.sdbs.datastore.Tuple.TupleComparator;
import com.willsong.sdbs.statement.FieldDefinition;
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
	public boolean hasField(FieldDefinition fieldName) {
		if (mDef == null) {
			return false;
		}
		
		try {
			mDef.getField(fieldName.getFullStringCode());
		} catch (NoSuchFieldException | SecurityException e) {
			// Try again with the full name
			fieldName.setTable(mName);
			try {
				mDef.getField(fieldName.getFullStringCode());
			} catch (NoSuchFieldException | SecurityException e1) {
				fieldName.setTable(null);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Attempts to get the Field object of this table of the corresponding fielddefinition
	 * object given.
	 * 
	 * @param	fieldName	the field to look for
	 * @return				the Field object if found, null otherwise
	 */
	public Field getField(FieldDefinition fieldName) {
		if (mDef == null) {
			return null;
		}
		
		try {
			return mDef.getField(fieldName.getFullStringCode());
		} catch (NoSuchFieldException | SecurityException e) {
			// Try again with the full name
			fieldName.setTable(mName);
			try {
				return mDef.getField(fieldName.getFullStringCode());
			} catch (NoSuchFieldException | SecurityException e1) {
				fieldName.setTable(null);
				return null;
			}
		}
	}
	
	/**
	 * Empty the current tuples.
	 */
	public void empty() {
		mTuples = new ArrayList<Tuple>();
	}
	
	/**
	 * Determines whether the given value is a valid type for the given field or not.
	 * 
	 * @param	field		the reference of the field to check
	 * @param	value		the value to check
	 * @return				true or false
	 */
	public boolean isValidFieldValue(FieldDefinition field, Object value) {
		if (mDef == null) {
			return false;
		}
		
		try {
			Field fieldDef = mDef.getField(field.getFullStringCode());
			if (value instanceof FieldDefinition) {
				Table t = mDatabase.getTable(((FieldDefinition) value).getTable());
				return fieldDef.getType().equals(t.getFieldType((FieldDefinition) value).getType());
			} else {
				return fieldDef.getType().equals(value.getClass());
			}
		} catch (NoSuchFieldException | SecurityException e) {
			// Try again with the full name
			try {
				Field fieldDef = mDef.getField(field.getFullStringCode());
				field.setTable(mName);
				if (value instanceof FieldDefinition) {
					Table t = mDatabase.getTable(((FieldDefinition) value).getTable());
					return fieldDef.getType().equals(t.getFieldType((FieldDefinition) value).getType());
				} else {
					return fieldDef.getType().equals(value.getClass());
				}
			} catch (NoSuchFieldException | SecurityException e1) {
				field.setTable(null);
				return false;
			}
		}
	}
	
	public Field getFieldType(FieldDefinition field) {
		if (mDef == null) {
			return null;
		}
		
		try {
			return mDef.getField(field.getFullStringCode());
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Determines whether the given value is a valid type for the given field or not.
	 * 
	 * @param	field		the reference of the field to check
	 * @param	value		the value to check
	 * @return				true or false
	 */
	public boolean isValidFieldValue(String field, Object value) {
		if (mDef == null) {
			return false;
		}
		
		try {
			Field fieldDef = mDef.getField(field);
			return fieldDef.getType().equals(value.getClass());
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
	
	/**
	 * Retrieve the full display name (in <database>.<table> notation) of this table.
	 * 
	 * @return	the full display name
	 */
	public String getFullDisplayName() {
		return mDatabase.getName() + "." + mName;
	}
	
	/**
	 * Get the name of this table.
	 * 
	 * @return	the name of the table
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * Retrieve the number of fields in this table.
	 * 
	 * @return	the number of fields
	 */
	public int getNumFields() {
		return mFields.size();
	}
	
	/**
	 * Retrieve the number of tuples in this table.
	 * 
	 * @return	the number of tuples
	 */
	public int getNumRows() {
		return mTuples.size();
	}
	
	/**
	 * Retrieve the database for this table.
	 * 
	 * @return	the database object
	 */
	public Database getDatabase() {
		return mDatabase;
	}
	
	/**
	 * Retrieves the tuple at the given row.
	 * 
	 * @param	rowNum	the tuple row to retrieve
	 * @return			the corresponding tuple
	 */
	public Tuple getTuple(int rowNum) {
		return getTuple(rowNum, false);
	}
	
	/**
	 * Retrieves the tuple at the given row, with the option of removing it from
	 * the data set.
	 * 
	 * @param	rowNum	the tuple row to retrieve
	 * @param	remove	set to true to remove it from the data set, false to just fetch
	 * @return			the corresponding tuple
	 */
	public Tuple getTuple(int rowNum, boolean remove) {
		if (remove) {
			return mTuples.remove(rowNum);
		} else {
			return mTuples.get(rowNum);
		}
	}
	
	public ArrayList<Tuple> getTuples(WhereClause where) {
		if (where == null) {
			return mTuples;
		} else {
			FieldDefinition field = where.getField();
			Object value = where.getValue();
			
			ArrayList<Tuple> result = new ArrayList<Tuple>();
			
			for (Tuple row : mTuples) {
				if (row.equals(field.getFullStringCode(), value)) {
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
	
	public void sort(Field field) {
		TupleComparator comp = new Tuple.TupleComparator(field);
		Collections.sort(mTuples, comp);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Table: ").append(getFullDisplayName()).append("\n")
			.append("   Number of fields: ").append(mFields.size()).append("\n")
			.append("   Number of tuples: ").append(mTuples.size()).append("\n");
		
		return buffer.toString();
	}
}
