package com.codeforge.bplus;

public class BNode
{
	public int index=-1;
	public BEntry point=null;
	String content;
	BEntry inWhichEntry;
	BNode(int index)
	{
		this.index=index;
	}
	void setPoint(BEntry be)
	{
		this.point=be;
		if (be!=null)
			be.parent=this;		
	}
	void insertLink(BNode middle)
	{
		BEntry former=this.point.rightMost();
		BEntry middleFormer=middle.point.leftMost();
		BEntry middleLatter=middle.point.rightMost();
		if (middleFormer==middleLatter)
			return;
		
		middleLatter.setNext(former.next);
		former.setNext(middleFormer);
	}
	void print()
	{
		if (index>=0)
			System.out.println("node"+index);
	}

	public static void main(String[] args) 
	{
		System.out.println("Hello World!");
	}
}