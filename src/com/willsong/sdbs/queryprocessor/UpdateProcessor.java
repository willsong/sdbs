package com.willsong.sdbs.queryprocessor;

import java.util.ArrayList;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.datastore.Database;
import com.willsong.sdbs.datastore.Table;
import com.willsong.sdbs.datastore.TableDefinition;
import com.willsong.sdbs.datastore.Tuple;
import com.willsong.sdbs.statement.UpdateStatement;
import com.willsong.sdbs.statement.WhereClause;

/**
 * The UPDATE query processor.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class UpdateProcessor extends QueryProcessor {
	
	protected Catalog mCatalog;
	protected UpdateStatement mStmt;
	
	public UpdateProcessor(Catalog catalog, UpdateStatement stmt) {
		mCatalog = catalog;
		mStmt = stmt;
	}
	
	@Override
	protected void process() throws ProcessorException {
		check();
		execute();
	}
	
	private void check() throws ProcessorException {
		String tableName = mStmt.getName();
		String field = mStmt.getField();
		Object value = mStmt.getValue();
		WhereClause where = mStmt.getWhereList().get(0);
		
		// Perform basic checks
		Database db = mCatalog.getCurrentDatabase();
		if (db == null) {
			throw new ProcessorException("No database selected");
		}
		
		if (!db.hasTable(tableName)) {
			throw new ProcessorException("Table does not exist: " + tableName);
		}
		
		Table table = db.getTable(tableName);
		if (!table.hasField(field)) {
			throw new ProcessorException("Field does not exist in UPDATE: " + field);
		}
		if (!table.isValidFieldValue(field, value)) {
			throw new ProcessorException("Field value type is invalid in UPDATE: " + value);
		}
		
		if (where != null) {
			String whereField = where.getField();
			Object whereValue = where.getValue();
			if (!table.hasField(whereField)) {
				throw new ProcessorException("Field does not exist in WHERE: " + whereField);
			}
			if (!table.isValidFieldValue(whereField, whereValue)) {
				throw new ProcessorException("Field value type is invalid in WHERE: " + whereValue);
			}
		}
	}
	
	private void execute() throws ProcessorException {
		Database db = mCatalog.getCurrentDatabase();
		Table table = db.getTable(mStmt.getName());
		Class<?> def = table.getDefinition();
		
		ArrayList<Tuple> result = table.getTuples(mStmt.getWhereList().get(0));
		for (Tuple row : result) {
			TableDefinition obj = row.getData();
			try {
				def.getField(mStmt.getField()).set(obj, mStmt.getValue());
			} catch (IllegalArgumentException | IllegalAccessException	| NoSuchFieldException | SecurityException e) {
				throw new ProcessorException("Failed to update records: "+ e);
			}
		}
		System.out.println("Updated " + result.size() + " records.");
	}
}
