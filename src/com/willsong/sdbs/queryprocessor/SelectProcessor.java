package com.willsong.sdbs.queryprocessor;

import java.util.ArrayList;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.datastore.Database;
import com.willsong.sdbs.datastore.Table;
import com.willsong.sdbs.datastore.Tuple;
import com.willsong.sdbs.queryprocessor.joinengine.SimpleJoin;
import com.willsong.sdbs.statement.FieldDefinition;
import com.willsong.sdbs.statement.SelectStatement;
import com.willsong.sdbs.statement.WhereClause;

/**
 * The SELECT query processor.
 * 
 * @author William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class SelectProcessor extends QueryProcessor {
	
	protected Catalog mCatalog;
	protected SelectStatement mStmt;
	protected ArrayList<FieldDefinition> mFields;
	protected Database mDb;
	protected ArrayList<Table> mTables;
	protected ArrayList<WhereClause> mWheres;
	
	public SelectProcessor(Catalog catalog, SelectStatement stmt) {
		mFields = new ArrayList<FieldDefinition>();
		mTables = new ArrayList<Table>();
		mWheres = new ArrayList<WhereClause>();
		mCatalog = catalog;
		mStmt = stmt;
	}
	
	@Override
	protected void process() throws ProcessorException {
		mapElements();
		optimize();
		execute();
	}
	
	private void mapElements() throws ProcessorException {
		mDb = mCatalog.getCurrentDatabase();
		if (mDb == null) {
			throw new ProcessorException("No database selected");
		}
		
		// Get from
		ArrayList<String> fromList = mStmt.getFromList();
		for (int i = 0; i < fromList.size(); i++) {
			String tableName = fromList.get(i);
			if (!mDb.hasTable(tableName)) {
				throw new ProcessorException("Table does not exist: " + tableName);
			}
			mTables.add(mDb.getTable(tableName));
		}
		
		// Get fields to project
		ArrayList<FieldDefinition> selectList = mStmt.getSelectList();
		ArrayList<String> selectItems = new ArrayList<String>();
		for (FieldDefinition select : selectList) {
			
			boolean isFullReference = select.isFull();
			
			if (!select.isAll()) {
				boolean hasField = false;
				for (Table table : mTables) {
					
					boolean tableMatch = isFullReference ? table.getName().equals(select.getTable()) : true;
					
					if (tableMatch && table.hasField(select)) {
						hasField = true;
						select.setTable(table.getName());
						break;
					}
				}
				if (!hasField) {
					throw new ProcessorException("Field does not exist in SELECT: " + select.getFullString());
				}
				if (selectItems.contains(select.getFullString())) {
					throw new ProcessorException("Field is ambiguous in SELECT: " + select.getFullString());
				}
				selectItems.add(select.getFullString());
				mFields.add(select);
			} else {
				if (isFullReference) {
					boolean hasField = false;
					for (Table table : mTables) {
						if (table.getName().equals(select.getTable())) {
							hasField = true;
							select.setTable(table.getName());
							break;
						}
					}
					if (!hasField) {
						throw new ProcessorException("Table does not exist in SELECT: " + select.getFullString());
					}
				}
				// @TODO: expand * to all fields?
			}
		}
		
		// Get where
		ArrayList<WhereClause> whereList = mStmt.getWhereList();
		for (int i = 0; i < whereList.size(); i++) {
			WhereClause where = whereList.get(i);
			FieldDefinition field = where.getField();
			Object value = where.getValue();
			
			boolean hasField = false;
			boolean isValidField = false;
			boolean isFullReference = field.isFull();
			
			for (Table table : mTables) {
				boolean tableMatch = isFullReference ? table.getName().equals(field.getTable()) : true;
				if (tableMatch && table.hasField(field)) {
					hasField = true;
				}
				if (tableMatch && table.isValidFieldValue(field, value)) {
					isValidField = true;
				}
				
				if (hasField && isValidField) {
					field.setTable(table.getName());
					break;
				}
				hasField = false;
				isValidField = false;
			}
			
			if (!hasField) {
				throw new ProcessorException("Field does not exist in WHERE: " + field.getFullString());
			} else if (!isValidField) {
				throw new ProcessorException("Field value type is invalid in WHERE: " + value);
			}
			
			mWheres.add(where);
		}
	}
	
	private void optimize() throws ProcessorException {}
	
	private void execute() throws ProcessorException {
		Table result = null;
		if (mTables.size() == 1) {
			// Only 1 table; just do it ourselves
			 result = mTables.get(0);
			 
			// Perform selection
			if (mWheres.size() > 0) {
				result = AlgebraicOperations.selection(result, mWheres);
			}
			
			// Perform projection
			if (mFields.size() > 0) {
				result = AlgebraicOperations.projection(result, mFields);
			}
			
		} else {
			// Join
			result = SimpleJoin.join(mTables, mWheres, mFields);
		}
		
		// Format and print
		ArrayList<Tuple> rows = result.getTuples2();
		ArrayList<Object[]> format = new ArrayList<Object[]>();
		
		for (int i = 0; i < rows.size(); i++) {
			Tuple row = rows.get(i);
			if (i == 0) {
				format.add(row.getFieldList());
			}
			format.add(row.getDataAsArray());
		}
		
		System.out.println(formatResult(format));
	}
}
