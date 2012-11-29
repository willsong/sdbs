package com.codeforge.bplus;
public class BEntryProxy 
{
	BEntry host;
	BEntryProxy(BEntry host)
	{
		this.host=host;
	}
	void delete(BNode node)
	{		
		if (this.host.isRoot)
		{
			this.host.deleteFromVector(node);
			if (this.host.nodes.size()==1)//根节点空了
			{
				this.host.giveRootTo(this.host.firstNode.point);
				this.host.isRoot=false;
				this.host.firstNode.point=null;
			}
			return;
		}
		BNode nodeParent=node.inWhichEntry.parent;
		BNode siblingParentNode=null;
		//node.inWhichEntry.print(0);
		BEntry entryParent=nodeParent.inWhichEntry;
		this.host.deleteFromVector(node);
		
		
		//System.out.println(entryParent.info());
		//System.out.println(nodeParent.index);
		if (this.host.notEnough())
		{
			int parentNo=entryParent.nodes.indexOf(nodeParent);
			if (parentNo==0)
			{
			    siblingParentNode=(BNode)(entryParent.nodes.elementAt(parentNo+1));	
			}
			else
			{               
                siblingParentNode=(BNode)(entryParent.nodes.elementAt(parentNo-1));	
			}
			BEntry siblingEntry=siblingParentNode.point;
			if (siblingEntry.justEnough())//并
			{
				if (siblingEntry.isLeaf())
				{					
					//System.out.println(siblingParentNode.index);
					if (parentNo==0)
					{						
						for (int i=1;i<siblingEntry.nodes.size() ;i++ )
						{
							BNode tempnode=(BNode)(siblingEntry.nodes.elementAt(i));
							this.host.addToVector(tempnode);
						}
						
						this.host.setNext(siblingEntry.next);
						siblingEntry.setNext(null);
						nodeParent.inWhichEntry.delete(siblingParentNode);
					}
					else
					{
						for (int i=1;i<this.host.nodes.size() ;i++ )
						{
							BNode tempnode=(BNode)(this.host.nodes.elementAt(i));
							siblingEntry.addToVector(tempnode);
						}
						siblingEntry.setNext(this.host.next);
						this.host.setNext(null);
						nodeParent.inWhichEntry.delete(this.host.parent);
					}						
					
				}
				else//if (siblingEntry.isLeaf()) not leaf
				{
					if (parentNo==0)
					{
						siblingParentNode.setPoint(siblingEntry.firstNode.point);
						//siblingEntry.firstNode.setPoint(this.firstNode.point);
						
						entryParent.delete(siblingParentNode);
						this.host.addToVector(siblingParentNode);
						for (int i=1;i<siblingEntry.nodes.size() ;i++ )
						{
							BNode tempnode=(BNode)(siblingEntry.nodes.elementAt(i));							
							this.host.addToVector(tempnode);
						}
					}
					else
					{
						this.host.parent.setPoint(this.host.firstNode.point);
						entryParent.delete(this.host.parent);
						siblingEntry.addToVector(this.host.parent);
						for (int i=1;i<this.host.nodes.size() ;i++ )
						{
							BNode tempnode=(BNode)(this.host.nodes.elementAt(i));
							siblingEntry.addToVector(tempnode);
						}
					}
					
				}
			}
			else//借
			{
				if(parentNo==0)//向后借
				{
					if (this.host.isLeaf())//叶子节点,借第二个
					{
						BNode firstnode=(BNode)(siblingEntry.nodes.elementAt(1));//considing the node -1 ,actually firstnode is the 2nd node 
						siblingEntry.delete(firstnode);
						BNode second=(BNode)(siblingEntry.nodes.elementAt(1));//it is actually the 3nd node last moment.
						this.host.addToVector(firstnode);
						siblingParentNode.index=second.index;
					}
					else
					{
						BNode firstnode=(BNode)(siblingEntry.nodes.elementAt(1));
						siblingEntry.delete(firstnode);
						//swap the firstNode.point with the firstnode.point
						BEntry temppoint=siblingEntry.firstNode.point;
						siblingEntry.firstNode.setPoint(firstnode.point);
						firstnode.setPoint(temppoint);
						//swap the firstnode.index with the siblingParentNode.index
						int temp=firstnode.index;
						firstnode.index=siblingParentNode.index;
						siblingParentNode.index=temp;

						this.host.addToVector(firstnode);
					}
				}
				else  
				{
					BNode lastnode=(BNode)(siblingEntry.nodes.lastElement());

					siblingEntry.delete(lastnode);
					this.host.addToVector(lastnode);
					//swap the index of lastnode with nodeParent
					int tempint=nodeParent.index;
					nodeParent.index=lastnode.index;
					lastnode.index=tempint;
					//swap lastnode.point with this.firstnode.point,because lastnode's point should be the first.point
					BEntry temp=lastnode.point;
					lastnode.setPoint(this.host.firstNode.point);
					this.host.firstNode.setPoint(temp);
				}
			}
			
		}
	}

	void insert(BNode node)
	{
		if (!host.isFull())
		{
			host.addToVector(node);
		}
		else
		{
			host.addToVector(node);
			BEntry newEntry=new BEntry(host.n);
			
			BNode midNode=(BNode)(host.nodes.elementAt(host.leafhalf));
			int nodesize=host.nodes.size();
			for (int i=host.leafhalf;i<nodesize ;i++ )
			{
				BNode tempnode=(BNode)host.nodes.elementAt(i);
				newEntry.addToVector(tempnode);
			}
			for (int i=host.leafhalf;i<nodesize ;i++ )
			{
				host.nodes.remove(host.leafhalf);
			}
			if(host.isLeaf())
			{
				BNode tempnode=new BNode(midNode.index);
				//this.lastNode().setPoint(newEntry);
				midNode=tempnode;
				host.link2(newEntry);
			}
			else
			{
				newEntry.firstNode.setPoint(midNode.point);				
				newEntry.nodes.remove(midNode);

			}
			
			if (host.isRoot)
				host.createNewRootFor(midNode);
			else
			{				
				host.parent.inWhichEntry.insert(midNode);
			}
			midNode.setPoint(newEntry);
		}
	}
	public static void main(String[] args) 
	{
		System.out.println("Hello World!");
	}
}