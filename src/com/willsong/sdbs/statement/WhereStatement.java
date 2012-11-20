package com.willsong.sdbs.statement;

/**
 * Represents an SQL statement which includes the WHERE clause.
 * 
 * @author William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public abstract class WhereStatement extends Statement {
	
	public abstract void addWhere(WhereClause where);
}
