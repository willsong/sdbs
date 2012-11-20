package com.willsong.sdbs.datastore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Represents an instance of a database.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class Database {

	private String mDataDir;
	private String mName;
	private String mFileSeparator;
	private String[] mTableNames;
	private HashMap<String, Table> mTables;
	private Catalog mCatalog;
	
	public Database(Catalog catalog, String name) {
		mCatalog = catalog;
		mName = name;
		mFileSeparator = System.getProperty("file.separator");
		mTables = new HashMap<String, Table>();
	}
	
	public void load(String dataDir, String[] tableNames) throws IOException {
		System.out.println("Loading database " + mName + "...");
		
		mDataDir = dataDir;
		mTableNames = tableNames;
		
		// Make sure data dir is valid
		for (String name : mTableNames) {
			String tFilePath = mDataDir + mFileSeparator + mName + "." + name + ".sdb";
			File cFile = new File(tFilePath);
			if (!(cFile.canRead() && cFile.canWrite())) {
				throw new IOException("Cannot access data file: " + tFilePath);
			}
			
			DBTable table = new DBTable(this, name);
			table.load(cFile);
			mTables.put(name, table);
		}
	}
	
	public String getName() {
		return mName;
	}
	
	public boolean hasTable(String name) {
		return mTables.containsKey(name);
	}
	
	public Table getTable(String name) {
		return mTables.get(name);
	}
	
	public void addTable(String name, Table table) {
		mTables.put(name, table);
	}
	
	public ArrayList<String> getTableNames() {
		ArrayList<String> names = new ArrayList<String>();
		
		Iterator<String> it = mTables.keySet().iterator();
		while (it.hasNext()) {
			// @TODO: sort!
			names.add(mTables.get(it.next()).getName());
		}
		
		return names;
	}
	
	/**
	 * Perform exit clean up.
	 */
	public void onExit() {
		Iterator<String> it = mTables.keySet().iterator();
		while (it.hasNext()) {
			Table table = mTables.get(it.next());
			if (table instanceof TempTable) {
				((TempTable) table).remove();
			}
		}
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Database: ").append(mName).append("\n")
			.append("Number of tables: ").append(mTables.size()).append("\n");

		Iterator<String> it = mTables.keySet().iterator();
		while (it.hasNext()) {
			buffer.append(mTables.get(it.next())).append("\n");
		}
		
		return buffer.toString();
	}
}
