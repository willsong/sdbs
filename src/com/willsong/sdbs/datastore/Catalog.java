package com.willsong.sdbs.datastore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.willsong.sdbs.queryprocessor.ProcessorException;

/**
 * The Catalog class.  The top level class from which all data store aspects of 
 * SDBS stems out.
 * 
 * @author William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class Catalog {
	
	private static Catalog sSelf;
	
	private HashMap<String, Database> mCatalog;
	private String mDataDir;
	private String mMetaDir;
	private String mFileSeparator;
	private Database mCurrentDb;
	
	private Catalog() {
		mFileSeparator = System.getProperty("file.separator");
		mCatalog = new HashMap<String, Database>();
	}
	
	public void setDataDir(String dataDir) {
		mDataDir = dataDir;
	}
	
	public String getDataDir() {
		return mDataDir;
	}
	
	public void setMetaDir(String metaDir) {
		mMetaDir = metaDir;
	}
	
	public String getMetaDir() {
		return mMetaDir;
	}
	
	public void useDatabase(String name) throws ProcessorException {
		if (!dbExists(name)) {
			throw new ProcessorException("Database does not exist: " + name);
		}
		
		mCurrentDb = mCatalog.get(name);
	}
	
	public Database getCurrentDatabase() {
		return mCurrentDb;
	}
	
	public void load() throws IOException {
		System.out.println("Loading catalog...");
		
		// Make sure data dir is valid
		String cFilePath = mDataDir + mFileSeparator + "_catalog.sdb";
		File cFile = new File(cFilePath);
		if (!(cFile.canRead() && cFile.canWrite())) {
			throw new IOException("Cannot access catalog file: " + cFilePath);
		}
		
		// Make sure metadata dir is valid
		File mFile = new File(mMetaDir);
		if (!(mFile.isDirectory() && mFile.canRead() && mFile.canWrite())) {
			throw new IOException("Cannot access metadata dir: " + cFilePath);
		}
		
		// Clear metadata dir
		System.out.println("Clearing metadata...");
		TableDefinitionLoader.clearDefinitions();
		
		Properties props = new Properties();
		props.load(new FileInputStream(cFilePath));
		
		Set<String> set = props.stringPropertyNames();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			// Get the database name
			String key = it.next();
			String[] parts = key.split("\\.");
			if (parts.length != 2) {
				continue;
			}
			String dbName = parts[0];
			
			// Now get the table names
			String tablePart = props.getProperty(key).trim();
			String[] tableNames = new String[0]; 
			if (tablePart.length() > 0) {
				tableNames = tablePart.split(",");
			}
			
			Database db = new Database(this, dbName);
			db.load(mDataDir, tableNames);
			mCatalog.put(dbName, db);
		}
	}
	
	public boolean dbExists(String name) {
		return mCatalog.containsKey(name);
	}
	
	public ArrayList<String> getDBNames() {
		ArrayList<String> names = new ArrayList<String>();
		
		Iterator<String> it = mCatalog.keySet().iterator();
		while (it.hasNext()) {
			// @TODO: sort!
			names.add(mCatalog.get(it.next()).getName());
		}
		
		return names;
	}
	
	/**
	 * Adds the given database object to the catalog.
	 * 
	 * @param	db	the database to add
	 */
	public void addDb(Database db) {
		String dbName = db.getName();
		if (!dbExists(dbName)) {
			mCatalog.put(dbName, db);
		}
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Number of databases: ").append(mCatalog.size()).append("\n");
		
		Iterator<String> it = mCatalog.keySet().iterator();
		while (it.hasNext()) {
			buffer.append(mCatalog.get(it.next()).toString()).append("\n");
		}
		
		return buffer.toString();
	}
	
	public static Catalog getInstance() {
		if (sSelf == null) {
			sSelf = new Catalog();
		}
		
		return sSelf;
		
	}
}
