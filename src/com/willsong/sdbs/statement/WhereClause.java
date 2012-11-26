package com.willsong.sdbs.statement;

import java.lang.reflect.Field;

import com.willsong.sdbs.datastore.TableDefinition;
import com.willsong.sdbs.datastore.Tuple;

/**
 * The WHERE clause class, as part of the SELECT statement.
 * 
 * @author William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class WhereClause {
	
	public static final int COMP_EQ = 1;
	public static final int COMP_GT = 2;
	public static final int COMP_GE = 3;
	public static final int COMP_LT = 4;
	public static final int COMP_LE = 5;
	
	protected FieldDefinition mField;
	protected Object mValue;
	protected int mCompType;
	
	public void setField(FieldDefinition field) {
		mField = field;
	}
	
	public FieldDefinition getField() {
		return mField;
	}

	public void setValue(String value) {
		mValue = value;
	}
	
	public void setValue(Integer value) {
		mValue = value;
	}
	
	public void setValue(Double value) {
		mValue = value;
	}
	
	public void setValue(FieldDefinition value) {
		mValue = value;
	}
	
	public Object getValue() {
		return mValue;
	}
	
	public void setCompType(int type) {
		mCompType = type;
	}
	
	public int getCompType() {
		return mCompType;
	}
	
	/**
	 * Determines whether the compared value is a field reference or not.
	 * 
	 * @return	true if field reference, false otherwise
	 */
	public boolean isReference() {
		return mValue instanceof FieldDefinition;
	}
	
	/**
	 * Performs a comparison of the given Tuple with the condition and variable
	 * of this instance.
	 * 
	 * @param	row	the Tuple row to compare with
	 * @return		true if the condition is met, false otherwise
	 */
	public boolean compare(Tuple row) {
		boolean result = false;
		
		try {
		
			TableDefinition rowData = row.getData();
			Field field = rowData.getClass().getField(mField.getFullStringCode());
			Class<?> fieldType = field.getType();
			
			Field refField = null;
			
			boolean isReference = isReference();
			if (isReference) {
				refField = rowData.getClass().getField(((FieldDefinition) mValue).getFullStringCode());
			}
			
			// Perform casting according to variable type. This should be ok since
			// there will only be a set number of available types
			int compResult = 0;
			if (fieldType.equals(String.class)) {
				if (isReference) {
					compResult = compareTo((String) field.get(rowData), (String) refField.get(rowData));
				} else {
					compResult = compareTo((String) field.get(rowData));
				}
			} else if (fieldType.equals(Integer.class)) {
				if (isReference) {
					compResult = compareTo((Integer) field.get(rowData), (Integer) refField.get(rowData));
				} else {
					compResult = compareTo((Integer) field.get(rowData));
				}
			} else if (fieldType.equals(Double.class)) {
				if (isReference) {
					compResult = compareTo((Double) field.get(rowData), (Double) refField.get(rowData));
				} else {
					compResult = compareTo((Double) field.get(rowData));
				}
			}
			
			// Determine the comparison result
			switch (mCompType) {
				case COMP_EQ:
					result = compResult == 0;
					break;
				case COMP_GT:
					result = compResult > 0;
					break;
				case COMP_GE:
					result = compResult >= 0;
					break;
				case COMP_LT:
					result = compResult < 0;
					break;
				case COMP_LE:
					result = compResult <= 0;
					break;
			}
		
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// @TODO: handle this!!!
		}
		
		return result;
	}
	
	/**
	 * Compare the given subject with the value of this instance, and return the
	 * result.
	 * 
	 * @param	subject	the subject to compare
	 * @return			the result of the comparison
	 */
	protected int compareTo(String subject) {
		return subject.compareTo((String) mValue);
	}
	
	/**
	 * Compare the given subject with the value of this instance, and return the
	 * result.
	 * 
	 * @param	subject	the subject to compare
	 * @return			the result of the comparison
	 */
	protected int compareTo(Integer subject) {
		return subject.compareTo((Integer) mValue);
	}
	
	/**
	 * Compare the given subject with the value of this instance, and return the
	 * result.
	 * 
	 * @param	subject	the subject to compare
	 * @return			the result of the comparison
	 */
	protected int compareTo(Double subject) {
		return subject.compareTo((Double) mValue);
	}
	
	protected int compareTo(String s1, String s2) {
		return s1.compareTo(s2);
	}
	
	protected int compareTo(Integer i1, Integer i2) {
		return i1.compareTo(i2);
	}
	
	protected int compareTo(Double d1, Double d2) {
		return d1.compareTo(d2);
	}
	
	public String toString() {
		return "" + mField + " " + getCompTypeAsString() + " " + mValue;
	}
	
	/**
	 * Returns the comparison operator as a string.
	 * 
	 * @return	the string of the comparison operator
	 */
	protected String getCompTypeAsString() {
		String type = "";
		
		switch (mCompType) {
			case COMP_EQ:
				type = "=";
				break;
			case COMP_GT:
				type = ">";
				break;
			case COMP_GE:
				type = ">=";
				break;
			case COMP_LT:
				type = "<";
				break;
			case COMP_LE:
				type = "<=";
				break;
		}
		
		return type;
	}
}
