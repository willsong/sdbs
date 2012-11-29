package com.codeforge.bplus;

import java.util.*;
public class BEntry 
{
	int n;
	int leafhalf;
	Vector nodes;
	BNode firstNode;
	BEntry next;
	BEntry previous;
	int level;
	boolean isRoot=false;
	BNode parent=null;
	String name;
	final int SMALL=-1;
	BPlus bp;
	BEntryProxy entryproxy=new BEntryProxy(this);
	BEntry(int n)
	{
		this.n=n;
		nodes=new Vector(n+1);
		leafhalf=(n>>1)+1;//[n-1/2]+1
		firstNode=new BNode(SMALL);
		this.addToVector(firstNode);
	}
	BNode lastNode()
	{
		return (BNode)(this.nodes.lastElement());
	}
	
	BEntry rightMost()
	{
		BEntry entry=this;
		while(true)
		{			
			if (entry.isLeaf())
				return entry;
			else
				entry=entry.lastNode().point;
		}
	}
	BEntry leftMost()
	{
		BEntry entry=this;
		while(true)
		{					
			if (entry.isLeaf())
				return entry;
			else
				entry=entry.firstNode.point;
		}
	}
	void link(BEntry latter)
	{
		BEntry formerLeaf=this.rightMost();
		BEntry latterLeaf=latter.leftMost();
		formerLeaf.setNext(latterLeaf);
	}
	void link2(BEntry latter)
	{
		latter.setNext(this.next);
		this.setNext(latter);
	}

	void setNext(BEntry entry)
	{
		this.next=entry;
		if (entry==null)
			return;
		
		
		entry.previous=this;
	}
	void deleteFromVector(BNode node)
	{
		nodes.remove(node);
		node.inWhichEntry=null;
	}
	void addToVector(BNode node)
	{
		int i=0;
		for (;i<nodes.size();i++ )
		{
			BNode tempNode=(BNode)nodes.elementAt(i);
			if (tempNode.index>node.index)
			{
				nodes.add(i,node);
				node.inWhichEntry=this;
				return ;
			}
		}
		nodes.add(i,node);
		node.inWhichEntry=this;
	}
	BNode findNode(int index)
	{
		for (int i=0;i<nodes.size() ;i++ )
		{
			BNode tempnode=(BNode)nodes.elementAt(i);
			if (tempnode.index>index)
			{
				return null;
			}
			if (tempnode.index==index)
				return tempnode;
		}
		return null;
	}
	BEntry findEntry(int index)
	{
		if (isLeaf())
		{
			return this;
		}
		for (int i=0;i<nodes.size() ;i++ )
		{
			BNode tempNode=(BNode)nodes.elementAt(i);
			if (tempNode.index>index)
			{
				BNode tempNode2=(BNode)nodes.elementAt(i-1);
				if (tempNode2.point.isLeaf())
				{
					return tempNode2.point;
				}
				else
				{
					return tempNode2.point.findEntry(index);
				}
			}
		}
                BEntry entry=((BNode)(nodes.lastElement())).point;
                if (entry.isLeaf()) 
                    return entry;
                else
                    return entry.findEntry(index);
	}
	String info()
	{
		StringBuffer s=new StringBuffer();
		s.append("Entry[");
		for (int i=0;i<nodes.size() ;i++ )
		{
			BNode node=(BNode)nodes.elementAt(i);
			if (node.index>=0)
				s=s.append(node.index+",");
		}
		s.append("]");
		return s.toString();
	}

	void print(int tab)
	{
		for (int i=0;i<tab ;i++ )
		{
			System.out.print("\t");
		}
		System.out.println(info());
		if (this.isLeaf())
		{
			return;
		}
		for (int i=0;i<nodes.size() ;i++ )
		{
			BNode node=(BNode)nodes.elementAt(i);
			//node.print();
			if (node.point!=null)
			{
				node.point.print(tab+1);
			}
			
		}
	}

    void printdebug(int tab,StringBuffer sb)
        {
           for (int i=0;i<tab ;i++ )
            {
                   sb=sb.append("\t");
            }
           sb.append(info()+"\n");
           for (int i=0;i<nodes.size() ;i++ )
            {
                    BNode node=(BNode)nodes.elementAt(i);
                    //node.print();
                    if (node.point!=null)
                    {
                            node.point.printdebug(tab+1,sb);
                    }
            }
        }
	boolean isLeaf()
	{
		return (firstNode.point==null);
	}
	boolean isFull()
	{
		return (nodes.size()==n);
	}
	boolean notEnough()
	{
		return (nodes.size()<leafhalf);
	}
	boolean justEnough()
	{
		return (nodes.size()==leafhalf);
	}
	void delete(BNode node)
	{
		entryproxy.delete(node);
	}
	void insert(BNode node)
	{
		entryproxy.insert(node);
	}
	void createNewRootFor(BNode node)
	{
		BEntry entry=new BEntry(n);
		entry.firstNode.setPoint(this);
		entry.addToVector(node);
		this.isRoot=false;
		giveRootTo(entry);
	}
	void giveRootTo(BEntry be)
	{
		this.isRoot=false;			
		bp.RootChanged(be);
	}
	void addRootListener(BPlus bp)
	{
		this.bp=bp;
	}
}
