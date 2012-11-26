package com.willsong.sdbs.util;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import com.willsong.sdbs.statement.FieldDefinition;

/**
 * A collection of utility methods which do not fit anywhere else.
 * 
 * @author	William Song, ID: 2012-23953, Email: willsong@kdb.snu.ac.kr
 */
public class Utilities {
	
	/**
	 * Retrieve a unique signature identifying the definition of the given instance.
	 * 
	 * @param	subject	the instance to get the signature of
	 * @return			signature string
	 * @throws 	NoSuchAlgorithmException
	 */
	public static String getSignature(Object subject) throws NoSuchAlgorithmException {
		StringBuffer bf = new StringBuffer();
		Field[] fields = subject.getClass().getFields();
		for (Field f : fields) {
			bf.append(f.getType().getName())
				.append("_")
				.append(f.getName())
				.append("~");
		}
		
		return md5(bf.toString());
	}
	
	/**
	 * Retrieve a unique signature identifying the potential definition of the given
	 * set of FieldDefinitions.
	 * 
	 * @param	fields	the list of fields to make up a potential new class
	 * @return			signature string
	 * @throws	NoSuchAlgorithmException
	 */
	public static String getSignature(ArrayList<FieldDefinition> fields) throws NoSuchAlgorithmException {
		StringBuffer bf = new StringBuffer();
		for (FieldDefinition fd : fields) {
			bf.append(fd.getTypeString(true))
				.append("_")
				.append(fd.getName())
				.append("~");
		}
		
		return md5(bf.toString());
	}
	
	/**
	 * Retrieve the MD5 hash of the given String.
	 * 
	 * @param	subject	the string to hash
	 * @return			the hash string
	 * @throws	NoSuchAlgorithmException
	 */
	public static String md5(String subject) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(subject.getBytes());
		
		StringBuffer bf = new StringBuffer();
		byte[] rawBytes = md.digest();
		
		for (byte b : rawBytes) {
			bf.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		
		return bf.toString();
	}
}
