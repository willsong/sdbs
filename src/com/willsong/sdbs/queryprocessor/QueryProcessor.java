package com.willsong.sdbs.queryprocessor;

import java.util.ArrayList;

/**
 * Parent class of all Query Processors.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public abstract class QueryProcessor {
	
	protected long mProcTime = 0;
	
	public void processQuery() throws ProcessorException {
		long start = System.currentTimeMillis();
		process();
		mProcTime = System.currentTimeMillis() - start;
	}
	
	protected abstract void process() throws ProcessorException;
	
	protected String formatResult(ArrayList<Object[]> result) {
		StringBuffer buffer = new StringBuffer();
		
		if (result.size() > 1) {
			int numCols = result.get(0).length;
			
			String format = "";
			int numChars = 0;
			for (int i = 0; i < numCols; i++) {
				format += "| %" + (i + 1) + "$-15.15s ";
				numChars += 18;
			}
			format += "|\n";
			
			String horizontalRule = "";
			for (int i = 0; i <= numChars; i++) {
				horizontalRule += "-";
			}
			
			buffer.append(horizontalRule + "\n");
			for (int i = 0; i < result.size(); i++) {
				Object[] row = result.get(i);
				buffer.append(String.format(format, row));
				if (i == 0) {
					buffer.append(horizontalRule + "\n");
				}
			}
			buffer.append(horizontalRule + "\n");
		}
		
		double procTime = mProcTime / 1000;
		
		buffer.append("\nQuery returned " + (result.size() - 1) +" row(s) in " + procTime + " seconds.");
		
		return buffer.toString();
	}
}
