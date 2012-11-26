package com.willsong.sdbs.statement;

import java.util.ArrayList;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.queryprocessor.CreateProcessor;
import com.willsong.sdbs.queryprocessor.ProcessorException;

/**
 * The CREATE statement.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class CreateStatement extends Statement {
	
	protected boolean mIsDatabase;
	protected String mName;
	protected ArrayList<FieldDefinition> mFields;
	
	public CreateStatement() {
		mIsDatabase = false;
		mName = "";
		mFields = new ArrayList<FieldDefinition>();
	}
	
	public void setIsDatabase(boolean value) {
		mIsDatabase = value;
	}
	
	public boolean isDatabase() {
		return mIsDatabase;
	}
	
	public void setName(String value) {
		mName = value;
		addStringPart(value);
	}
	
	public String getName() {
		return mName;
	}
	
	public void addField(FieldDefinition field) {
		mFields.add(field);
		addStringPart(field.getFullStringCode() + " " + field.getTypeString());
	}
	
	public ArrayList<FieldDefinition> getFieldList() {
		return mFields;
	}

	@Override
	public void process(Catalog catalog) throws ProcessorException {
		CreateProcessor processor = new CreateProcessor(catalog, this);
		processor.processQuery();
	}
}
