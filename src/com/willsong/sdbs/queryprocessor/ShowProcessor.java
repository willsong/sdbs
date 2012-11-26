package com.willsong.sdbs.queryprocessor;

import java.util.ArrayList;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.statement.ShowStatement;

/**
 * The SHOW query processor.
 * 
 * @author William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class ShowProcessor extends QueryProcessor {
	
	protected Catalog mCatalog;
	protected ShowStatement mStmt;
	
	public ShowProcessor(Catalog catalog, ShowStatement showStatement) {
		mCatalog = catalog;
		mStmt = showStatement;
	}

	@Override
	protected void process() throws ProcessorException {
		// Perform basic checks
		if (mCatalog.getCurrentDatabase() == null && !mStmt.isDatabase()) {
			throw new ProcessorException("No database selected");
		}

		// Get the results
		ArrayList<String> names;
		if (mStmt.isDatabase()) {
			names = mCatalog.getDBNames();
			names.add(0, "Databases");
		} else {
			names = mCatalog.getCurrentDatabase().getTableNames();
			names.add(0, "Tables");
		}
		
		ArrayList<Object[]> format = new ArrayList<Object[]>();
		for (int i = 0; i < names.size(); i++) {
			format.add(new Object[] { names.get(i) });
		}
		
		System.out.println(formatResult(format));
	}
}
