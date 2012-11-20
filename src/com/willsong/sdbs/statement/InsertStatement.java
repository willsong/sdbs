package com.willsong.sdbs.statement;

import java.util.ArrayList;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.queryprocessor.InsertProcessor;
import com.willsong.sdbs.queryprocessor.ProcessorException;

/**
 * The INSERT statement.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class InsertStatement extends Statement {
	
	protected String mName;
	protected ArrayList<Object> mValues;
	
	public InsertStatement() {
		mName = "";
		mValues = new ArrayList<Object>();
	}
	
	public void setName(String name) {
		mName = name;
		addStringPart(name);
	}
	
	public String getName() {
		return mName;
	}
	
	public void addValue(Object value) {
		mValues.add(value);
		addStringPart(value.toString());
	}
	
	public ArrayList<Object> getValueList() {
		return mValues;
	}

	@Override
	public void process(Catalog catalog) throws ProcessorException {
		InsertProcessor processor = new InsertProcessor(catalog, this);
		processor.processQuery();
	}
}
