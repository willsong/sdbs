package com.willsong.sdbs.queryprocessor;

/**
 * Thrown by Query Processors.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class ProcessorException extends Exception {
	
	public ProcessorException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}
