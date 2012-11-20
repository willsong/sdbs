package com.willsong.sdbs.statement;

import java.util.ArrayList;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.queryprocessor.ProcessorException;
import com.willsong.sdbs.queryprocessor.SelectProcessor;

/**
 * The SELECT statement class.
 * 
 * @author William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class SelectStatement extends WhereStatement {
	
	protected ArrayList<String> mSelect;
	protected ArrayList<String> mFrom;
	protected ArrayList<WhereClause> mWhere;
	protected boolean mIsAll;
	
	public SelectStatement() {
		mSelect = new ArrayList<String>();
		mFrom = new ArrayList<String>();
		mWhere = new ArrayList<WhereClause>();
	}
	
	public void addSelect(String select) {
		mSelect.add(select);
		if (select.equals("*")) {
			mIsAll = true;
		}
		addStringPart(select);
	}
	
	public boolean isAll() {
		return mIsAll;
	}
	
	public ArrayList<String> getSelectList() {
		return mSelect;
	}
	
	public void addFrom(String from) {
		mFrom.add(from);
		addStringPart(from);
	}
	
	public ArrayList<String> getFromList() {
		return mFrom;
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
		SelectProcessor processor = new SelectProcessor(catalog, this);
		processor.processQuery();
	}
}
