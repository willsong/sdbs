package com.willsong.sdbs.datastore;

import java.io.File;
import java.util.UUID;

/**
 * Represents an instance of a temporary database table.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class TempTable extends Table {
	
	public TempTable(Database database) {
		super(database, ("temp_" + UUID.randomUUID()).replace('-', '_'));
		// @TODO: use persistent name? for caching?
	}
	
	public void remove() {
		String fs = System.getProperty("file.separator");
		String targetDir = Catalog.getInstance().getMetaDir();
		String newFile = targetDir + fs + mDatabase.getName() + "_" + mName;
		
		File src = new File(newFile + ".java");
		src.delete();
		
		File klass = new File(newFile + ".class");
		klass.delete();
	}
}
