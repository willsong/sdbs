package com.willsong.sdbs.statement;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.queryprocessor.ProcessorException;

/**
 * The base class for all the different types of SQL statements.
 * 
 * @author William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public abstract class Statement {
	
	protected StringBuffer mStringVersion;
	
	protected Statement() {
		mStringVersion = new StringBuffer();
	}
	
	public void addString(String part) {
		addStringPart(part.toUpperCase());
	}
	
	public String toString() {
		return mStringVersion.toString();
	}
	
	public abstract void process(Catalog catalog) throws ProcessorException;
	
	protected void addStringPart(String part) {
		mStringVersion.append(" ").append(part);
	}
}
