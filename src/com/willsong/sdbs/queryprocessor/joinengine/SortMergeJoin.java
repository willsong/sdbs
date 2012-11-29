package com.willsong.sdbs.queryprocessor.joinengine;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import com.willsong.sdbs.datastore.Table;
import com.willsong.sdbs.datastore.TempTable;
import com.willsong.sdbs.datastore.Tuple;
import com.willsong.sdbs.queryprocessor.ProcessorException;
import com.willsong.sdbs.queryprocessor.RowOperations;
import com.willsong.sdbs.statement.FieldDefinition;
import com.willsong.sdbs.statement.WhereClause;

public class SortMergeJoin extends JoinEngine {
	
	/**
	 * Perform a sort-merge join.
	 * 
	 * @param	tables	the tables to join
	 * @param	wheres	the where conditions
	 * @param	fields	the fields to project
	 * @return			the resulting relation
	 * @throws	ProcessorException
	 */
	public static Table join(ArrayList<Table> tables, ArrayList<WhereClause> wheres, ArrayList<FieldDefinition> fields) throws ProcessorException {
		if (tables.size() > 2) {
			throw new ProcessorException("Sort Merge Join Engine only supports up to 2 tables");
		}
		
		Table prev = null;
		Table curr = null;
		TempTable merged = null;
		
		for (int i = 0; i < tables.size(); i++) {
			if (i == 0) {
				continue;
			}
			
			prev = i == 1 ? tables.get(i - 1) : merged;
			curr = tables.get(i);
			
			// Determine the left and right fields
			Field leftField = null;
			Field rightField = null;
			for (WhereClause where : wheres) {
				if (!where.isReference()) {
					continue;
				}
				
				leftField = prev.getField(where.getField());
				rightField = curr.getField((FieldDefinition) where.getValue());
				
				if (leftField != null && rightField != null) {
					wheres.remove(where);
					break;
				}
			}
			
			if (leftField == null || rightField == null) {
				throw new ProcessorException("Failed to determine predicate field");
			}
			
//			System.out.println("===left field: " + leftField + " right field: " + rightField);
			
			merged = sortMerge(prev, curr, leftField, rightField);
//			System.out.println("***********merged: " + merged);
		}
		
		// Now that all the tables are merged, apply selection and projection
		if (wheres.size() > 0) {
			merged = RowOperations.selection(merged, wheres);
		}
		
		if (fields.size() > 0) {
			merged = RowOperations.projection(merged, fields);
		}
		
		return merged;
	}
	
	/**
	 * Perform sort-merge on the given two tables.
	 * 
	 * @param	left		the first table
	 * @param	right		the second table
	 * @param	leftField	the pivot field of the first table
	 * @param	rightField	the pivot field of the second table
	 * @return				the resulting table
	 * @throws				ProcessorException
	 */
	private static TempTable sortMerge(Table left, Table right, Field leftField, Field rightField) throws ProcessorException {
		try {
			
			// Create output
			TempTable output = null;
			
			// Sort left and right
			TempTable leftSorted = new TempTable(left.getDatabase());
			for (FieldDefinition fd : left.getFields()) {
				leftSorted.addField(fd);
			}
			TempTable rightSorted = new TempTable(right.getDatabase());
			for (FieldDefinition fd : right.getFields()) {
				rightSorted.addField(fd);
			}
			leftSorted.create();
			rightSorted.create();
			for (Tuple row : left.getTuples2()) {
				leftSorted.insertTuple(row);
			}
			for (Tuple row : right.getTuples2()) {
				rightSorted.insertTuple(row);
			}
			leftSorted.sort(leftField);
			rightSorted.sort(rightField);
			
			// Create subsets
			TempTable leftSubset = new TempTable(left.getDatabase());
			for (FieldDefinition fd : left.getFields()) {
				leftSubset.addField(fd);
			}
			TempTable rightSubset = new TempTable(right.getDatabase());
			for (FieldDefinition fd : right.getFields()) {
				rightSubset.addField(fd);
			}
			leftSubset.create();
			rightSubset.create();
			
			// For persistence
			AdvanceResult leftAdv = new AdvanceResult();
			leftAdv.key = null;
			leftAdv.subset = leftSubset;
			leftAdv.sorted = leftSorted;
			AdvanceResult rightAdv = new AdvanceResult();
			rightAdv.key = null;
			rightAdv.subset = rightSubset;
			rightAdv.sorted = rightSorted;
			
//			System.out.println("left sorted: " + leftSorted + " right sorted: " + rightSorted);
			
			// Init advancement
//			System.out.println(1);
			leftAdv = advance(leftAdv, leftField);
//			System.out.println(2);
			rightAdv = advance(rightAdv, rightField);
			
			ArrayList<Table> forCP = new ArrayList<Table>();
			
			while (leftAdv.sorted.getNumRows() > 0 && rightAdv.sorted.getNumRows() > 0) {
				int compResult = compare(leftAdv.key, rightAdv.key, leftField);
				
				if (compResult == 0) {
//					System.out.println("comp: " + compResult);
					// Join predicate satisfied
					
					// Add cross product of subsets to output
					forCP.clear();
					forCP.add(leftAdv.subset);
					forCP.add(rightAdv.subset);
					TempTable cpRes = RowOperations.crossProduct(forCP);
					if (output == null) {
						output = cpRes;
//						System.out.println("created output table");
					} else {
						for (Tuple cpRow : cpRes.getTuples2()) {
							output.insertTuple(cpRow);
//							System.out.println("append to output table");
						}
					}
//					System.out.println(output);
					
//					System.out.println(3);
//					System.out.println("left: " + leftField + " right: " + rightField);
					leftAdv = advance(leftAdv, leftField);
//					System.out.println(4);
					rightAdv = advance(rightAdv, rightField);
				} else if (compResult < 0) {
//					System.out.println(5);
					leftAdv = advance(leftAdv, leftField);
				} else {
//					System.out.println(6);
					rightAdv = advance(rightAdv, rightField);
				}
			}
			
			return output;
			
		} catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException | InstantiationException | IOException | NoSuchFieldException | SecurityException e) {
//			e.printStackTrace();
			throw new ProcessorException("Failed to perform sort-merge: " + e);
		}
	}
	
	private static AdvanceResult advance(AdvanceResult res, Field a) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		if (res.sorted.getNumRows() <= 0) {
			return res;
		}
		
		res.subset.empty();
		Tuple curr = res.sorted.getTuple(0);
		res.key = curr.getValue(a);
		
		Object val = curr.getValue(a);
//		System.out.println("initial value: " + val);
		
		while (res.sorted.getNumRows() > 0 && compare(val, res.key, a) == 0) {
			// Remove pivot and insert into subset
			res.subset.insertTuple(curr);
			res.sorted.getTuple(0, true);
			
			// Reset pivot
			if (res.sorted.getNumRows() > 0) {
				curr = res.sorted.getTuple(0);
			}
//			System.out.println("sorted: " +res.sorted);
//			System.out.println("curr: " + curr.getData());
//			System.out.println("but a: " + a.getDeclaringClass());
			val = curr.getValue(a);
//			System.out.println("new value: "+ val);
		}
		return res;
	}
	
	/**
	 * Performs a comparison of two objects, casting them based on the type of
	 * the given field. The two objects must be of the same type.
	 * 
	 * @param	one		the first object
	 * @param	two		the second object
	 * @param	field	the field type
	 * @return			same as that of compareTo()
	 */
	private static int compare(Object one, Object two, Field field) {
		Class<?> fieldType = field.getType();
		
		int compResult = 0;
		
		if (fieldType.equals(String.class)) {
			compResult = ((String) one).compareTo((String) two);
		} else if (fieldType.equals(Integer.class)) {
			compResult = ((Integer) one).compareTo((Integer) two);
		} else if (fieldType.equals(Double.class)) {
			compResult = ((Double) one).compareTo((Double) two);
		}
		
		return compResult;
	}
	
	private static class AdvanceResult {
		
		public Object key;
		public Table subset;
		public Table sorted;
	}
}
