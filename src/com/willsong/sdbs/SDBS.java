package com.willsong.sdbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.willsong.sdbs.datastore.Catalog;
import com.willsong.sdbs.parser.ParseException;
import com.willsong.sdbs.parser.SQLParser;
import com.willsong.sdbs.queryprocessor.ProcessorException;
import com.willsong.sdbs.statement.ExitStatement;
import com.willsong.sdbs.statement.Statement;

/**
 * This is the main class for the SNU Database Management System, a simple, in-memory
 * database management system.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class SDBS {
	
	public static final int VERSION_MAJOR = 1;
	public static final int VERSION_MINOR = 0;
	public static final int VERSION_RELEASE = 2;
	
	private SQLParser mParser;
	private Catalog mCatalog;
	private Properties mProps;
	private boolean mScriptMode;
	private File mScriptFile;
	
	public SDBS(Properties props) throws Exception {
		mProps = props;
		loadEssentials();
	}
	
	private void loadEssentials() throws Exception {
		System.out.println("Loading essentials...");
		
		String scriptFile = mProps.getProperty("f");
		if (scriptFile != null && scriptFile.length() > 0) {
			mScriptFile = new File(scriptFile);
			if (mScriptFile.canRead()) {
				mScriptMode = true;
			} else {
				throw new IOException("Cannot read script file: " + scriptFile);
			}
		}
		
		mCatalog = Catalog.getInstance();
		mCatalog.setDataDir(mProps.getProperty("d"));
		mCatalog.setMetaDir(mProps.getProperty("m"));
		mCatalog.load();
		
		if (mScriptMode) {
			System.out.println("Switching to script mode...");
			mParser = new SQLParser(new FileInputStream(mScriptFile));
		} else {
			mParser = new SQLParser(System.in);
		}
		
		System.out.println("Loading complete!");
	}
	
	public void run() {
		System.out.println("");
		System.out.println(getVersion());
		
		boolean isRunning = true;
		while (isRunning) {
			System.out.print("=> ");
			try {
				mParser.SQL();
				Statement stmt = mParser.getCurrentStmt();
				if (stmt != null) {
					if (mScriptMode) {
						System.out.println(stmt);
					}
					if (stmt instanceof ExitStatement) {
						isRunning = false;
					} else {
						stmt.process(mCatalog);
					}
				}
			} catch (ParseException e) {
				System.out.println("Syntax error! " + e.getMessage());
			} catch (ProcessorException e) {
				System.out.println("Logic error! " + e.getMessage());
			} catch (Exception e) {
				System.out.println("Unexpected error!" + e.getMessage());
				e.printStackTrace();
			} finally {
				if (!mScriptMode) {
					mParser.ReInit(System.in);
				}
			}
		}
		
		// @TODO: CLEAN UP CODE GOES HERE
		System.out.println("Exiting... ciao!");
	}
	
	private String getVersion() {
		return "SDB version " + VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_RELEASE;
	}
	
	public static void main(String[] args) {
		Properties props = checkArguments(args);
		
		try {
			SDBS db = new SDBS(props);
			db.run();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}
	}
	
	private static Properties checkArguments(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: java -jar SDBS.jar -d=<path_to_data_dir> -m=<path_to_metadata_dir> -f=<path_to_script>");
			System.exit(0);
		}
		
		Properties props = new Properties();
		for (String option : args) {
			if (option.charAt(0) != '-') {
				continue;
			}
			
			option = option.substring(1);
			String[] parts = option.split("=");
			if (parts.length != 2) {
				continue;
			}
			
			if (parts[0].equals("d") || parts[0].equals("m") || parts[0].equals("f")) {
				props.setProperty(parts[0], parts[1]);
			}
		}
		
		if (props.isEmpty()) {
			System.out.println("Command line arguments are not valid");
			System.exit(0);
		}
		
		return props;
	}
}
