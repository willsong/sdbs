package com.willsong.sdbs.indexes;

import java.lang.reflect.Field;

import com.waltdestler.bptree.BPTree;
import com.willsong.sdbs.datastore.Table;
import com.willsong.sdbs.datastore.Tuple;

public class BPTreeIndex {
	
	protected String mName;
	protected BPTree<Object, Tuple> mTree;
	
	public void build(String name, Field k, Table targetTable) {
		mName = name;
		
		mTree = new BPTree<Object, Tuple>();
		
//		if (k.getType().equals(Integer.class)) {
//			mTree = new BPTree<Integer, Tuple>();
//		} else if (k.getType().equals(Double.class)) {
//			mTree = new BPTree<Double, Tuple>();
//		} else if (k.getType().equals(String.class)) {
//			mTree = new BPTree<String, Tuple>();
//		}
		
		for (Tuple rid : targetTable.getTuples2()) {
			try {
				
				if (k.getType().equals(Integer.class)) {
					Integer kValue = (Integer) rid.getValue(k);
					mTree.put(kValue, rid);
				} else if (k.getType().equals(Double.class)) {
					Double kValue = (Double) rid.getValue(k);
					mTree.put(kValue, rid);
				} else if (k.getType().equals(String.class)) {
					String kValue = (String) rid.getValue(k);
					mTree.put(kValue, rid);
				}
				
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {}
		}
	}
	
	public Tuple get(Object k) {
		return mTree.get(k);
	}
	
	public void remove(Object rid) {
		mTree.remove(rid);
	}
}
