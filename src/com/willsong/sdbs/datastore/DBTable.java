package com.willsong.sdbs.datastore;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.willsong.sdbs.statement.FieldDefinition;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Represents an instance of a permanent database table.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class DBTable extends Table {
	
	public DBTable(Database database, String name) {
		super(database, name);
	}

	public void load(File file) throws IOException {
		System.out.println("Loading table " + getFullDisplayName() + "...");
		CSVReader reader = new CSVReader(new FileReader(file));
		
		// First line is the field definition
		String[] fields = reader.readNext();
		if (fields == null) {
			reader.close();
			throw new IOException("Invalid data for table " + mName + ": fields not defined");
		}
		
		if (fields.length == 0) {
			reader.close();
			throw new IOException("Invalid data for table " + mName + ": fields not defined");
		}
		
		boolean parseError = false;
		for (int i = 0; i < fields.length; i++) {
			// Each field definition in the form of name:type
			String[] parts = fields[i].split(":");
			if (parts.length <= 1) {
				parseError = true;
				break;
			}
			
			// Make sure the values are valid
			try {
				String name = parts[0];
				int type = Integer.parseInt(parts[1]);
				
				if (name.length() <= 0) {
					parseError = true;
					break;
				}
				
				if (!FieldDefinition.isValidType(type)) {
					parseError = true;
					break;
				}
				
				// Create instance and add to list
				FieldDefinition fd = new FieldDefinition(mName, name, type);
				mFields.add(fd);
				
			} catch (NumberFormatException e) {
				parseError = true;
				break;
			}
		}
		
		if (parseError) {
			reader.close();
			throw new IOException("Field definition is invalid: " + fields);
		}
		
		try {
			create();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			reader.close();
			throw new IOException(e.getMessage());
		}
		
		// Now get the tuples
		String[] line;
		int lineNo = 1;
		while ((line = reader.readNext()) != null) {
			// Make sure field count matches definition
			if (line.length != mFields.size()) {
				reader.close();
				throw new IOException("Tuple size (" + line.length + ") does not match field definition size (" + mFields.size() + ") on line " + lineNo);
			}
			
			try {
				Tuple tuple = new Tuple(this);
				TableDefinition data = (TableDefinition) mDef.newInstance();
				
				// For each field, set the value in the data instance
				for (int i = 0; i < line.length; i++) {
					FieldDefinition fd = mFields.get(i);
					String name = fd.getFullStringCode();
					String value = line[i];
					
					Object convValue = fd.convertValue(value);
					
					mDef.getField(name).set(data, convValue);
				}
				
				// Set tuple data, add tuple to list
				tuple.setData(data);
				mTuples.add(tuple);
				lineNo++;
				
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
				reader.close();
				throw new IOException("Failed to create tuple: " + e);
			}
		}
		
		reader.close();
	}

}
