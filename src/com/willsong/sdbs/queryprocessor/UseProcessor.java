package com.willsong.sdbs.queryprocessor;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.statement.UseStatement;

/**
 * The USE query processor.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class UseProcessor extends QueryProcessor {
	
	protected Catalog mCatalog;
	protected UseStatement mStmt;
	
	public UseProcessor(Catalog catalog, UseStatement useStatement) {
		mCatalog = catalog;
		mStmt = useStatement;
	}

	@Override
	protected void process() throws ProcessorException {
		String dbName = mStmt.getDatabase();
		mCatalog.useDatabase(dbName);
		System.out.println("Using database '" + dbName + "'");
	}
}
