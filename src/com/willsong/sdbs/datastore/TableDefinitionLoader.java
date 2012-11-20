package com.willsong.sdbs.datastore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * Loads the table definition classes from the meta directory.
 * Based on: http://www.ibm.com/developerworks/java/tutorials/j-classloader/section6.html
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class TableDefinitionLoader extends ClassLoader {
	
	protected ArrayList<FieldDefinition> mFields;
	
	/**
	 * Set the field definitions.
	 * 
	 * @param	fields	the arraylist of definitions to load
	 */
	public void setFields(ArrayList<FieldDefinition> fields) {
		mFields = fields;
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (mFields == null) {
			throw new ClassNotFoundException("FieldDefinition list is not set");
		}
		
		Class<?> klass = null;
		
		// Have we already loaded this one?
		klass = findLoadedClass(name);
		if (klass != null) {
			return klass;
		}
		
		// Try the parent loaders
		try {
			klass = loadClass(name, true);
			if (klass != null) {
				return klass;
			}
		} catch (ClassNotFoundException e) {
			// Not found by parent loaders, this must be a new class
		}
		
		String fs = System.getProperty("file.separator");
		String targetDir = Catalog.getInstance().getMetaDir();
		String newFile = targetDir + fs + name;
		
		// Does a java file exist?
		File srcFile = new File(newFile + ".java");
		if (!srcFile.exists()) {
			try {
				writeSource(name, newFile + ".java");
			} catch (IOException e) {
				throw new ClassNotFoundException("Failed to create source table definition: " + e);
			}
		}
		
		// Does a class file exist?
		File binFile = new File(newFile + ".class");
		if (!binFile.exists()) {
			compileSource(newFile + ".java");
		}
		
		// Is the class file up to date?
		if (srcFile.lastModified() > binFile.lastModified()) {
			System.out.println("binary out of date");
			compileSource(newFile + ".java");
		}
		
		// Load class from raw bytes, resolve
		klass = loadFromBytes(name, newFile + ".class");
		if (klass != null) {
			resolveClass(klass);
			return klass;
		}
		
		// Last resort...
		klass = findSystemClass(name);
		if (klass != null) {
			return klass;
		}
		
		throw new ClassNotFoundException("Failed to load class '" + name + "'");
	}
	
	/**
	 * Create the java source file.
	 * 
	 * @param	name		the name of the class to create
	 * @param	filePath	the full file path to write
	 * @throws	IOException
	 */
	protected void writeSource(String name, String filePath) throws IOException {
		StringBuffer buffer = new StringBuffer();
		for (FieldDefinition def : mFields) {
			buffer.append(def);
		}
		
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
		
		pw.println("// Generated file, DO NOT MODIFY!!!");
		pw.print("import " + TableDefinition.class.getName() + ";");
		pw.print("public class " + name + " extends TableDefinition{");
		pw.print(buffer.toString());
		pw.print("}");
		
		pw.close();
	}
	
	/**
	 * Compile the given java source file.
	 * 
	 * @param	srcFile	the file to compile
	 * @throws	ClassNotFoundException
	 */
	protected void compileSource(String srcFile) throws ClassNotFoundException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int compRes = compiler.run(null, null, null, srcFile);
		if (compRes != 0) {
			throw new ClassNotFoundException("Failed to compile source file '" + srcFile + '"');
		}
	}
	
	/**
	 * Load the given class file into the VM.
	 * 
	 * @param	name	the name of the class
	 * @param	file	the class file name
	 * @return			the Class definition
	 * @throws	ClassNotFoundException
	 */
	protected Class<?> loadFromBytes(String name, String file) throws ClassNotFoundException {
		try {
		byte[] raw = getBytes(file);
		Class<?> klass = defineClass(name, raw, 0, raw.length);
		return klass;
		} catch (IOException e) {
			throw new ClassNotFoundException(e.getMessage());
		}
	}
	
	/**
	 * Retrieve the raw byte array from the given file.
	 * 
	 * @param	file		the path to the file
	 * @return				the raw byte array
	 * @throws	IOException	if something terrible happened
	 */
	protected byte[] getBytes(String file) throws IOException {
		File binFile = new File(file);
		long len = binFile.length();
		
		byte[] raw = new byte[(int) len];
		
		FileInputStream fis = new FileInputStream(binFile);
		
		int res = fis.read(raw);
		if (res != len) {
			fis.close();
			throw new IOException("Failed to read all bytes from file '" + file + "'");
		}
		
		fis.close();
		return raw;
	}
	
	/**
	 * Clear the metadata folder.
	 * 
	 * @throws	IOException
	 */
	public static void clearDefinitions() throws IOException {
		File file = new File(Catalog.getInstance().getMetaDir());
		if (file.isDirectory()) {
			File[] defs = file.listFiles();
			if (defs != null && defs.length > 0) {
				for (File def : defs) {
					def.delete();
				}
			}
		}
	}
}
