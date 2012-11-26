package com.willsong.sdbs.queryprocessor;

import java.util.ArrayList;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.datastore.Database;
import com.willsong.sdbs.datastore.Table;
import com.willsong.sdbs.datastore.TableDefinition;
import com.willsong.sdbs.datastore.Tuple;
import com.willsong.sdbs.statement.FieldDefinition;
import com.willsong.sdbs.statement.InsertStatement;

/**
 * Process the INSERT statements.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class InsertProcessor extends QueryProcessor {
	
	protected Catalog mCatalog;
	protected InsertStatement mStmt;
	
	public InsertProcessor(Catalog catalog, InsertStatement stmt) {
		mCatalog = catalog;
		mStmt = stmt;
	}

	@Override
	protected void process() throws ProcessorException {
		String targetName = mStmt.getName();
		ArrayList<Object> values = mStmt.getValueList();
		
		// Perform basic checks
		Database db = mCatalog.getCurrentDatabase();
		if (db == null) {
			throw new ProcessorException("No database selected");
		}
		
		if (!db.hasTable(targetName)) {
			throw new ProcessorException("Table with name '" + targetName + "' does not exist");
		}
		
		Table table = db.getTable(targetName);
		
		if (table.getNumFields() != values.size()) {
			throw new ProcessorException("Value count is incorrect");
		}
		
		for (int i = 0; i < values.size(); i++) {
			Object val = values.get(i);
			if (!table.isValidFieldValue(i, val)) {
				throw new ProcessorException("Value at index " + i + " is invalid: " + val);
			}
		}
		
		// Process
		try {
			Class<?> def = table.getDefinition();
			TableDefinition obj = (TableDefinition) def.newInstance();
			
			for (int i = 0; i < values.size(); i++) {
				FieldDefinition fd = table.getFieldDefinition(i);
				def.getField(fd.getFullStringCode()).set(obj, values.get(i));
			}
			
			Tuple row = new Tuple(table);
			row.setData(obj);
			table.insertTuple(row);
			
			System.out.println("Inserted 1 new record.");
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
			throw new ProcessorException("Failed to insert new record: "+ e);
		}
	}
}
