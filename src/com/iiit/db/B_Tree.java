package com.iiit.db;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class B_Tree {
	
	private Node myRootNode;
	private int T ;
	
	class Node {
		public int numKeys = 0;
		public int[] keys = new int[2 * T - 1];
		public String[] records = new String[2 * T - 1];
		public Node[] childNodes = new Node[2 * T];
		public boolean isLeaf;
		public Node nextNode;
		public int fileNum;
	}
	
	
	

	public B_Tree(int T) {
		this.T = T;
		myRootNode = new Node();
		myRootNode.isLeaf = true;
		
	}
	
	public boolean insertKey(int key,String object) {
		
		String res = search(key);
		
		if(res!=null && res.compareTo(object)==0)
		{
			
			return false;
		}
		
		Node rootNode = myRootNode;
		if (rootNode.numKeys == (2 * T - 1)) {
			Node newRootNode = new Node();
			myRootNode = newRootNode;
			newRootNode.isLeaf = false;
			myRootNode.childNodes[0] = rootNode;
			splitChildNode(newRootNode, 0, rootNode); 
			insertIntoNode(newRootNode, key,object); 
		} else {
			insertIntoNode(rootNode, key,object); 
		}
		
		return true;
	}
	
	
	void splitChildNode(Node parentNode, int i, Node node) {
		Node newNode = new Node();
		newNode.isLeaf = node.isLeaf;
		newNode.numKeys = T;
		for (int j = 0; j < T; j++) { // copy the last t elements of node into newnode. keep the median key as duplicate in the first key of newnode.
			newNode.keys[j] = node.keys[j + T - 1];
			newNode.records[j] = node.records[j + T - 1];
		}
		if (!newNode.isLeaf) {
			for (int j = 0; j < T + 1; j++) { // Copy the last T + 1 pointers of node into newNode.
				newNode.childNodes[j] = node.childNodes[j + T - 1];
			}
			for (int j = T; j <= node.numKeys; j++) {
				node.childNodes[j] = null;
			}
		} else {
			
			newNode.nextNode = node.nextNode;
			node.nextNode = newNode;
		}
		for (int j = T - 1; j < node.numKeys; j++) {
			node.keys[j] = 0;
			node.records[j] = null;
		}
		node.numKeys = T - 1;
		
	
		for (int j = parentNode.numKeys; j >= i + 1; j--) {
			parentNode.childNodes[j + 1] = parentNode.childNodes[j];
		}
		parentNode.childNodes[i + 1] = newNode;	
		for (int j = parentNode.numKeys - 1; j >= i; j--) {
			parentNode.keys[j + 1] = parentNode.keys[j];
			parentNode.records[j + 1] = parentNode.records[j];
		}
		parentNode.keys[i] = newNode.keys[0];
		parentNode.records[i] = newNode.records[0];
		parentNode.numKeys++;
	}
	
	
	void insertIntoNode(Node node, int key,String object) {
		int i = node.numKeys - 1;
		if (node.isLeaf) {
		
		while (i >= 0 && key < node.keys[i]) {
			
			node.keys[i + 1] = node.keys[i];
			node.records[i + 1] = node.records[i];
			i--;
		}
		i++;
		node.keys[i] = key;
		node.records[i] = object;
		node.numKeys++;
			
			
			
		} else {
		
			while (i >= 0 && key < node.keys[i]) {
				i--;
			}
			i++;
			if (node.childNodes[i].numKeys == (2 * T - 1)) {
				splitChildNode(node, i, node.childNodes[i]);
				if (key > node.keys[i]) {
					i++;
				}
			}
			insertIntoNode(node.childNodes[i], key,object);
		}
	}	
	

	// Inorder walk over the tree.
	public String toString() {
		
		int cnt=0;
		String string = "";
		Node node = myRootNode;		
		while (!node.isLeaf) {			
			node = node.childNodes[0];
		}		
		while (node != null) {
			for (int i = 0; i < node.numKeys; i++) {
//				string += node.mObjects[i] + ", ";
//				System.out.println(node.keys[i]);
				
			}
			cnt++;
			node = node.nextNode;
		}
		System.out.println("Total leaves "+cnt);
		return string;
	}
	
	public void assignSecondaryStorage()
	{
		Node node = myRootNode;
		int cnt=0;
		while (!node.isLeaf) {			
			node = node.childNodes[0];
		}		
		while (node != null) {
			node.fileNum=cnt;
			HashUnion.createFile(Constants.OUTPUT+cnt);
			cnt++;
			node = node.nextNode;
		}
	}

	public void insertRecordsToFile(int key,String record)
	{
		Integer fileNum = getFileNum(myRootNode,key);
		writeToFile(Constants.OUTPUT+fileNum, record);

	}
	
	
	public  void writeToFile(String fileName,String tuple)
	{ 
		try
		{
			
			PrintWriter pw;
			pw = new PrintWriter(new FileWriter(fileName,true));
			pw.write(tuple);
			pw.write("\n");
	        pw.close();
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	
	public Integer getFileNum(Node node,int key)
	{
	
		int i = 0;
		while (i < node.numKeys && key > node.keys[i]) {
			i++;
		}
		if (i < node.numKeys && key == node.keys[i] ) {
			return node.fileNum;
		}
		if (node.isLeaf) {
			return null;
		} else {
			return getFileNum(node.childNodes[i], key);
		}	
	}
	
	public int writeRecordsToOutputFile()
	{
		Node node = myRootNode;
		int cnt=0;
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter(Constants.OUTPUT,true));
			
			while (!node.isLeaf) {			
				node = node.childNodes[0];
			}		
			String prev="";
			while (node != null) {
				for (int i = 0; i < node.numKeys; i++) {
			
					if(prev.compareTo(node.records[i])!=0)
					{
						pw.write(node.records[i]);
						pw.write("\n");
						cnt++;
					}
				    prev = node.records[i];	
				}
				node = node.nextNode;
			}
			
			pw.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cnt;
	}
	
	
	public String search(Node node, int key) {		
		int i = 0;
		while (i < node.numKeys && key > node.keys[i]) {
			i++;
		}
		if (i < node.numKeys && key == node.keys[i]) {
			return node.records[i];
		}
		if (node.isLeaf) {
			return null;
		} else {
			return search(node.childNodes[i], key);
		}	
	}
	
	public String search(int key) {
		return search(myRootNode, key);
	}

	public void combineFiles() {
		// TODO Auto-generated method stub
		
	}

	public int getLeaves() {
		// TODO Auto-generated method stub
		int cnt=0;
		Node node = myRootNode;		
		while (!node.isLeaf) {			
			node = node.childNodes[0];
		}		
		while (node != null) {
			cnt++;
			node = node.nextNode;
		}
		
		return cnt;
	}
	
	
}
