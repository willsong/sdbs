package com.willsong.sdbs.datastore;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

import com.willsong.sdbs.util.Utilities;

/**
 * Represents an instance of a temporary database table.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class TempTable extends Table {
	
	/**
	 * Mapping class names and their signatures to allow for persistent usage as
	 * a cache.
	 */
	protected static HashMap<String, String> sDefCache = new HashMap<String, String>();
	
	/**
	 * Constructor.
	 * 
	 * @param	database	the database
	 */
	public TempTable(Database database) {
		super(database, ("temp_" + UUID.randomUUID()).replace('-', '_'));
	}
	
	/**
	 * Overridden to allow persistent usage of the java class definitions.
	 */
	@Override
	public void create() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
		try {
			
			String sig = Utilities.getSignature(mFields);
			boolean addToCache = true;
			if (sDefCache.containsKey(sig)) {
				// Table has already been defined, so use it
				mFullName = sDefCache.get(sig);
				addToCache = false;
			}
			
			super.create();
			
			if (addToCache) {
				sDefCache.put(sig, mDef.getName());
			}
			
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("Failed to generate signature: " + e);
		}
	}

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
