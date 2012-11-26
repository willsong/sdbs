package com.willsong.sdbs.queryprocessor.joinengine;

import java.util.ArrayList;

import com.willsong.sdbs.datastore.Table;
import com.willsong.sdbs.queryprocessor.AlgebraicOperations;
import com.willsong.sdbs.queryprocessor.ProcessorException;
import com.willsong.sdbs.statement.FieldDefinition;
import com.willsong.sdbs.statement.WhereClause;

/**
 * A very simple join engine.
 * 
 * @author William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class SimpleJoin extends JoinEngine {
	
	/**
	 * Perform a brainless join.
	 * 
	 * @param	tables	the tables to join
	 * @param	wheres	the where conditions
	 * @param	fields	the fields to project
	 * @return			the resulting relation
	 * @throws	ProcessorException
	 */
	public static Table join(ArrayList<Table> tables, ArrayList<WhereClause> wheres, ArrayList<FieldDefinition> fields) throws ProcessorException {
		// Get target table
		Table result = null;
		if (tables.size() == 1) {
			 result = tables.get(0);
		} else {
			result = AlgebraicOperations.crossProduct(tables);
		}
		
		// Perform selection
		if (wheres.size() > 0) {
			result = AlgebraicOperations.selection(result, wheres);
		}
		
		// Determine if this is a conditional join or natural/equijoin
		
		// Perform projection
		if (fields.size() > 0) {
			result = AlgebraicOperations.projection(result, fields);
		}
		
		return result;
	}
}
