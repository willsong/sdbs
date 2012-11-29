package com.willsong.sdbs.queryprocessor.joinengine;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import com.willsong.sdbs.datastore.Table;
import com.willsong.sdbs.datastore.TableDefinition;
import com.willsong.sdbs.datastore.TempTable;
import com.willsong.sdbs.datastore.Tuple;
import com.willsong.sdbs.indexes.BPTreeIndex;
import com.willsong.sdbs.queryprocessor.ProcessorException;
import com.willsong.sdbs.queryprocessor.RowOperations;
import com.willsong.sdbs.statement.FieldDefinition;
import com.willsong.sdbs.statement.WhereClause;

public class IndexNestedLoopJoin extends JoinEngine {
	
	public static Table join(ArrayList<Table> tables, ArrayList<WhereClause> wheres, ArrayList<FieldDefinition> fields) throws ProcessorException {
		if (tables.size() > 2) {
			throw new ProcessorException("Index Nested Loop Join Engine only supports up to 2 tables");
		}
		
		Table prev = null;
		Table curr = null;
		TempTable joined = null;
		
		for (int i = 0; i < tables.size(); i++) {
			if (i == 0) {
				continue;
			}
			
			prev = i == 1 ? tables.get(i - 1) : joined;
			curr = tables.get(i);
			
			// Determine pivot field
			FieldDefinition pivot = null;
			for (Iterator<WhereClause> it = wheres.iterator(); it.hasNext();) {
				WhereClause where = it.next();
				if (!where.isReference()) {
					continue;
				}
				
				// @TODO: support more than 1 reference!
				pivot = where.getField();
				it.remove();
			}
			
			if (pivot == null) {
				throw new ProcessorException("Failed to determine predicate field");
			}
			
			// Determine which is outer and which is inner
			if (prev.getNumRows() > curr.getNumRows()) {
				joined = nestedLoopJoin(curr, prev, pivot);
			} else {
				joined = nestedLoopJoin(prev, curr, pivot);
			}
		}
		
		// Now that all the tables are merged, apply selection and projection
		if (wheres.size() > 0) {
			joined = RowOperations.selection(joined, wheres);
		}
		
		if (fields.size() > 0) {
			joined = RowOperations.projection(joined, fields);
		}
		
		return joined;
	}
	
	private static TempTable nestedLoopJoin(Table outer, Table inner, FieldDefinition pivot) throws ProcessorException {
		try {
			// Result table
			TempTable result = new TempTable(outer.getDatabase());
			for (FieldDefinition fd : outer.getFields()) {
				result.addField(fd);
			}
			for (FieldDefinition fd : inner.getFields()) {
				result.addField(fd);
			}
			result.create();
			Class<?> newDef = result.getDefinition();
			
			// Get pivot fields
			Field outerField = outer.getField(pivot);
			Field innerField = inner.getField(pivot);
			
			// Build key on inner table
			BPTreeIndex index = createIndex(innerField, inner);
			
			// Loop
			for (Tuple outerRow : outer.getTuples2()) {
				Object searchKey = outerRow.getValue(outerField);
				
				boolean keepLooking = true;
				while (keepLooking) {
					Tuple innerRow = index.get(searchKey);
					if (innerRow != null) {
						// @TODO: terrible!! fix!!
						index.remove(searchKey);
						
						TableDefinition newData = (TableDefinition) newDef.newInstance();
						
						// Get fields from table outer
						Class<?> fieldDef = outerRow.getData().getClass();
						Field[] attrDef = fieldDef.getFields();
						
						// Set fields for table outer
						for (Field f : attrDef) {
							String fieldName = f.getName();
							Object fieldVal = f.get(outerRow.getData());
							
							if (!result.isValidFieldValue(fieldName, fieldVal)) {
								throw new ProcessorException("Failed to build new joined table: tuple domains do not match");
							}
							
							newDef.getField(fieldName).set(newData, fieldVal);
						}
						
						// Get fields from table two
						Class<?> fieldDef2 = innerRow.getData().getClass();
						Field[] attrDef2 = fieldDef2.getFields();
						
						// Set fields for table two
						for (Field f : attrDef2) {
							String fieldName = f.getName();
							Object fieldVal = f.get(innerRow.getData());
							
							if (!result.isValidFieldValue(fieldName, fieldVal)) {
								throw new ProcessorException("Failed to build new joined table: tuple domains do not match");
							}
							
							newDef.getField(fieldName).set(newData, fieldVal);
						}
						
						// Wrap in Tuple and insert
						Tuple tuple = new Tuple(result);
						tuple.setData(newData);
						result.insertTuple(tuple);
					} else {
						keepLooking = false;
					}
				}
			}
			
			return result;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException | IllegalArgumentException | SecurityException | NoSuchFieldException e) {
			throw new ProcessorException("Failed to perform nested loop join: " + e);
		}
	}
	
	private static BPTreeIndex createIndex(Field f, Table t) {
		BPTreeIndex index = new BPTreeIndex();
		index.build("bpt_index_" + System.currentTimeMillis(), f, t);
		return index;
	}
}
