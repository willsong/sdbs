package com.willsong.sdbs.queryprocessor;

import java.io.IOException;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.datastore.Database;
import com.willsong.sdbs.datastore.Table;
import com.willsong.sdbs.statement.CreateStatement;

/**
 * Process the CREATE statements.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class CreateProcessor extends QueryProcessor {
	
	protected Catalog mCatalog;
	protected CreateStatement mStmt;
	
	public CreateProcessor(Catalog catalog, CreateStatement stmt) {
		mCatalog = catalog;
		mStmt = stmt;
	}

	@Override
	protected void process() throws ProcessorException {
		boolean isDb = mStmt.isDatabase();
		String name = mStmt.getName();
		
		// Perform basic checks
		if (isDb) {
			if (mCatalog.dbExists(name)) {
				throw new ProcessorException("Database with name '" + name + "' already exists");
			}
		} else {
			Database db = mCatalog.getCurrentDatabase();
			if (db == null) {
				throw new ProcessorException("No database selected");
			}
			
			if (db.hasTable(name)) {
				throw new ProcessorException("Table with name '" + name + "' already exists");
			}
		}
		
		// Process
		if (isDb) {
			createDatabase();
		} else {
			createTable();
		}
	}
	
	protected void createTable() throws ProcessorException {
		Database db = mCatalog.getCurrentDatabase();
		String tName = mStmt.getName();
		Table table = new Table(db, tName);
		table.setFields(mStmt.getFieldList());
		try {
			table.create();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
			throw new ProcessorException("Failed to create table: " + e);
		}
		System.out.println("Table '" + tName + "' created successfully");
	}
	
	protected void createDatabase() throws ProcessorException {
		String dbName = mStmt.getName();
		Database db = new Database(mCatalog, dbName);
		mCatalog.addDb(db);
		System.out.println("Database '" + dbName + "' created successfully");
	}
}
