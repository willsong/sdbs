package com.willsong.sdbs.statement;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.queryprocessor.ProcessorException;
import com.willsong.sdbs.queryprocessor.ShowProcessor;

/**
 * The SHOW statement class.
 * 
 * @author William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class ShowStatement extends Statement {
	
	protected boolean mIsDatabase;
	
	public ShowStatement() {
		mIsDatabase = true;
	}

	@Override
	public void process(Catalog catalog) throws ProcessorException {
		ShowProcessor processor = new ShowProcessor(catalog, this);
		processor.processQuery();
	}
	
	public void setIsDatabase(boolean value) {
		mIsDatabase = value;
	}
	
	public boolean isDatabase() {
		return mIsDatabase;
	}
}
