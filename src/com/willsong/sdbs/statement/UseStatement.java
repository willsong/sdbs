package com.willsong.sdbs.statement;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.queryprocessor.ProcessorException;
import com.willsong.sdbs.queryprocessor.UseProcessor;

/**
 * The USE statement class.
 * 
 * @author William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class UseStatement extends Statement {
	
	protected String mDatabase;
	
	public void setDatabase(String db) {
		mDatabase = db;
		addStringPart(db);
	}
	
	public String getDatabase() {
		return mDatabase;
	}
	
	@Override
	public void process(Catalog catalog) throws ProcessorException {
		UseProcessor processor = new UseProcessor(catalog, this);
		processor.processQuery();
	}
}
