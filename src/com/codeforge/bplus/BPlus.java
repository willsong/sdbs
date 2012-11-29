package com.codeforge.bplus;

public class BPlus
{
	public int n;
	BEntry root;
	static boolean DEBUG=false;
	static boolean DEBUG2=true;
	BPlus(int n)
	{
		this.n=n;
		root=new BEntry(n);
		root.addRootListener(this);
		root.isRoot=true;
	}
	void insert(int index)
	{
		BNode node=findNode(index);
		if (node==null)
		{
			BEntry entry=findEntry(index);
			BNode newNode=new BNode(index);
			entry.insert(newNode);
		}		
	}
	void delete(int index)
	{
		BNode node=findNode(index);
		if (node!=null)
		{
			BEntry entry=node.inWhichEntry;
			//System.out.println("   "+node.inWhichEntry);
			entry.delete(node);
		}
	}

	void print()
	{
		root.print(0);
	}
	BNode findNode(int index)
	{
		return (root.findEntry(index)).findNode(index);
	}
	BEntry findEntry(int index)
	{
		return root.findEntry(index);
	}
	
	void RootChanged(BEntry entry)
	{
		//System.out.println("root"+root.info());
		//System.out.println();
		this.root=entry;
		//System.out.println("new root is"+root.info());
		entry.isRoot=true;
		entry.addRootListener(this);
	}
	String printdebug()
	{
		StringBuffer sb=new StringBuffer();
		root.printdebug(0,sb);
		return sb.toString();
	}
	void travel()
	{
		this.print();
		System.out.println("++++++++++++++++++++++++");
		for (BEntry entry=root.leftMost();entry!=null;entry=entry.next )
		{
			entry.print(0);
		}
		System.out.println("-------------------------------------------------");
	}
	void travel2()
	{
		this.print();
		System.out.println("++++++++++++++++++++++++");
		for (BEntry entry=root.rightMost();entry!=null;entry=entry.previous )
		{
			entry.print(0);
		}
		System.out.println("-------------------------------------------------");
	}
//	public static void main(String[] args) 
//	{
//		//如果n为偶数,就不完全observe rules
//		BPlus b=new BPlus(5);
//
///*
//		b.insert(10);
//		if (DEBUG)b.travel();
//		b.insert(50);
//		if (DEBUG)b.travel();
//		b.insert(20);
//		if (DEBUG)b.travel();
//		b.insert(40);
//		if (DEBUG)b.travel();
//		b.insert(100);
//		if (DEBUG)b.travel();
//		b.insert(120);
//		if (DEBUG)b.travel();
//		b.insert(150);
//		if (DEBUG)b.travel();
//
//		b.delete(120);
//		if (DEBUG2)b.travel();
//		b.delete(100);
//		if (DEBUG2)b.travel();
//		b.delete(20);
//		if (DEBUG2)b.travel();
//		b.delete(40);
//		if (DEBUG2)b.travel();
//		b.delete(150);
//		if (DEBUG2)b.travel();
//		b.delete(50);
//		if (DEBUG2)b.travel();*/
//		
//		for(int j=0;j<2;j++){
//			for (int i=0;i<20 ;i++ )
//			{
//				int x=(int)(Math.random()*30);
//				System.out.println("adding"+x);
//				b.insert(x);
//				//b.travel();	
//				b.print();		
//			}		
//			for (int i=0;i<20 ;i++ )
//			{
//				int x=(int)(Math.random()*30);
//				System.out.println("deleting "+x);
//				b.delete(x);
//				b.print();	
//			}
//			
//		}
//	}
}
