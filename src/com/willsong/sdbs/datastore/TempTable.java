package com.willsong.sdbs.datastore;

import java.io.File;
import java.io.IOException;
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
	
//	@Override
//	public void create() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
//		super.create();
//	}

	/**
	 * Remove metadata.
	 */
	public void remove() {
		String fs = System.getProperty("file.separator");
		String targetDir = Catalog.getInstance().getMetaDir();
		String newFile = targetDir + fs + this.mDef.getName();
		
		File src = new File(newFile + ".java");
		src.delete();
		
		File klass = new File(newFile + ".class");
		klass.delete();
	}
}
