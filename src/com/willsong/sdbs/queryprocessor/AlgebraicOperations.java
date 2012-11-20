package com.willsong.sdbs.queryprocessor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.willsong.sdbs.datastore.FieldDefinition;
import com.willsong.sdbs.datastore.Table;
import com.willsong.sdbs.datastore.TableDefinition;
import com.willsong.sdbs.datastore.TempTable;
import com.willsong.sdbs.datastore.Tuple;
import com.willsong.sdbs.statement.WhereClause;

/**
 * Collection of static methods performing common relational algebraic functions.
 * 
 * @author William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class AlgebraicOperations {
	
	/**
	 * Perform the given selection on the given table, and return a TempTable containing
	 * the results.
	 * 
	 * @param	table		the table to perform the selection on
	 * @param	whereList	the list of WhereClause selection conditions
	 * @return				the result
	 * @throws	ProcessorException
	 */
	public static TempTable selection(Table table, ArrayList<WhereClause> whereList) throws ProcessorException {
		try {
			// Define selection table
			TempTable result = new TempTable(table.getDatabase());
			for (FieldDefinition fd : table.getFields()) {
				result.addField(fd);
			}
			
			// Create and load definition into memory
			result.create();
			
			ArrayList<Tuple> rows = table.getTuples2();
			for (Tuple row : rows) {
				// Check each where clause (assume AND list only)
				boolean addRow = true;
				for (WhereClause where : whereList) {
					if (!where.compare(row)) {
						addRow = false;
						break;
					}
				}
				if (addRow) {
					result.insertTuple(row);
				}
			}
			
			return result;
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
			throw new ProcessorException("Failed to perform selection: " + e);
		}
	}

	public static TempTable projection(Table table, ArrayList<String> fieldList) throws ProcessorException {
		try {
			
			// Remove unwanted fields
			int numFields = fieldList.size();
			TempTable result = new TempTable(table.getDatabase());
			for (FieldDefinition fd : table.getFields()) {
				if (numFields > 0 && fieldList.contains(fd.getName())) {
					result.addField(fd);
				}
			}
			
			// Create and load definition into memory
			result.create();
			
			// Copy rows
			ArrayList<Tuple> rows = table.getTuples2();
			if (rows.size() > 0) {
				
				Class<?> tupleDef = rows.get(0).getData().getClass();
				Field[] fieldDefs = tupleDef.getFields();
				
				Class<?> newDef = result.getDefinition();
				
				for (Tuple row : rows) {
					TableDefinition origData = row.getData();
					TableDefinition newData = (TableDefinition) newDef.newInstance();
					
					for (Field fd : fieldDefs) {
						String fieldName = fd.getName();
						Object fieldValue = fd.get(origData);
						
						if (numFields > 0 && fieldList.contains(fieldName)) {
							newDef.getField(fieldName).set(newData, fieldValue);
						}
					}
					
					Tuple newTuple = new Tuple(result);
					newTuple.setData(newData);
					result.insertTuple(newTuple);
				}
			}
			
			return result;
		
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
			throw new ProcessorException("Failed to perform projection: " + e);
		}
	}
	
	/**
	 * Perform the cross product of the given tables, and return a TempTable containing
	 * the results.
	 * 
	 * @param	tables	the tables to cross product
	 * @return			the result
	 * @throws	ProcessorException
	 */
	public static TempTable crossProduct(ArrayList<Table> tables) throws ProcessorException {
		Table prev = null;
		TempTable product = null;
		
		for (int i = 0; i < tables.size(); i++) {
			if (i == 0) {
				continue;
			}
			
			prev = tables.get(i - 1);
			if (i == 1) {
				product = crossProductPair(prev, tables.get(i));
			} else {
				product = crossProductPair(product, tables.get(i));
			}
		}
		
		return product;
	}
	
	/**
	 * Perform the cross product of two tables, and return a TempTable containing
	 * the results.
	 * 
	 * @param	one	the first table
	 * @param	two	the second table
	 * @return		the result
	 * @throws	ProcessorException
	 */
	private static TempTable crossProductPair(Table one, Table two) throws ProcessorException {
		try {
			// Define product table
			TempTable tempTable = new TempTable(one.getDatabase());
			for (FieldDefinition fd : one.getFields()) {
				tempTable.addField(fd);
			}
			for (FieldDefinition fd : two.getFields()) {
				tempTable.addField(fd);
			}
			
			// Create and load definition into memory
			tempTable.create();
			
			// Populate table with product rows
			Class<?> tempDef = tempTable.getDefinition();
			for (Tuple oneRow : one.getTuples2()) {
				
				// Get fields from table one
				Class<?> fieldDef = oneRow.getData().getClass();
				Field[] attrDef = fieldDef.getFields();
				
				// All "inserts" done at the nested level
				for (Tuple twoRow : two.getTuples2()) {
					
					// New row instance
					TableDefinition newRow = (TableDefinition) tempDef.newInstance();
					
					// Set fields for table one
					for (Field f : attrDef) {
						String fieldName = f.getName();
						Object fieldVal = f.get(oneRow.getData());
						
						if (!tempTable.isValidFieldValue(fieldName, fieldVal)) {
							throw new ProcessorException("Failed to build cross product pair: tuple domains do not match");
						}
						
						tempDef.getField(fieldName).set(newRow, fieldVal);
					}
					
					// Get fields from table two
					Class<?> fieldDef2 = twoRow.getData().getClass();
					Field[] attrDef2 = fieldDef2.getFields();
					
					// Set fields for table two
					for (Field f : attrDef2) {
						String fieldName = f.getName();
						Object fieldVal = f.get(twoRow.getData());
						
						if (!tempTable.isValidFieldValue(fieldName, fieldVal)) {
							throw new ProcessorException("Failed to build cross product pair: tuple domains do not match");
						}
						
						tempDef.getField(fieldName).set(newRow, fieldVal);
					}
					
					// Wrap in Tuple and insert
					Tuple tuple = new Tuple(tempTable);
					tuple.setData(newRow);
					tempTable.insertTuple(tuple);
				}
			}
			
			return tempTable;
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
			throw new ProcessorException("Failed to build cross product pair: " + e);
		}
	}
}
