package com.willsong.sdbs.statement;

import java.util.ArrayList;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.queryprocessor.ProcessorException;
import com.willsong.sdbs.queryprocessor.UpdateProcessor;

/**
 * The UPDATE statement.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class UpdateStatement extends WhereStatement {
	
	protected String mName;
	protected FieldDefinition mField;
	protected Object mValue;
	protected ArrayList<WhereClause> mWhere;
	
	public void setName(String name) {
		mName = name;
		mWhere = new ArrayList<WhereClause>();
		addStringPart(name);
	}
	
	public String getName() {
		return mName;
	}
	
	public void setField(FieldDefinition field) {
		mField = field;
		addStringPart(field.getName());
	}
	
	public FieldDefinition getField() {
		return mField;
	}
	
	public void setValue(Object value) {
		mValue = value;
		addStringPart(value.toString());
	}
	
	public Object getValue() {
		return mValue;
	}
	
	@Override
	public void addWhere(WhereClause where) {
		mWhere.add(where);
		addStringPart(where.toString());
	}
	
	public ArrayList<WhereClause> getWhereList() {
		return mWhere;
	}

	@Override
	public void process(Catalog catalog) throws ProcessorException {
		UpdateProcessor processor = new UpdateProcessor(catalog, this);
		processor.processQuery();
	}
}
