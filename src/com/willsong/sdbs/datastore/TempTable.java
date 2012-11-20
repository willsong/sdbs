package com.willsong.sdbs.datastore;

import java.util.UUID;

/**
 * Represents an instance of a temporary database table.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class TempTable extends Table {
	
	public TempTable(Database database) {
		super(database, ("temp_" + UUID.randomUUID()).replace('-', '_'));
		// @TODO: add removal code!!
		// @TODO: use persistent name? for caching?
	}
}
