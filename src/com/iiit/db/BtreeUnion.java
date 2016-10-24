package com.iiit.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BtreeUnion {

	int M;
	int numRecords;
	String table1;
	String table2;
	int n;
	int pKey;
	B_Tree bTree;
	int blockSize;
	int recordSize;
	public BtreeUnion(int m, int numRecords, String table1, String table2, int n,int blockSize,int recordSize) {
		super();
		M = m;
		this.numRecords = numRecords;
		this.table1 = table1;
		this.table2 = table2;
		this.n = n;
		this.pKey=0;
		this.blockSize=blockSize;
		this.recordSize=recordSize;
	}
	
	
	public void union()
	{
//		bTree = new B_Tree(500);
		bTree = new B_Tree(1000);
		constructBtree(table1);
		constructBtree(table2);
		System.out.println(bTree);
		
		System.out.println("Performing Union");
		
		try {
			performUnion();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void performUnion() throws IOException{
		// TODO Auto-generated method stub
		
		bTree.assignSecondaryStorage();
		insertRecords(table1);
		insertRecords(table2);
		
		System.out.println("Insertion over. Writing to output file");
		HashUnion.createFile(Constants.OUTPUT);
		
		int cnt = bTree.getLeaves();
		int sum=0;
		int dup=0;
		for(int  i=0;i<cnt;i++)
		{
			B_Tree bTreeLocal = new B_Tree(100);
			BufferedReader buff = new BufferedReader(new FileReader(Constants.OUTPUT+i));
			String line;
			
			while((line = buff.readLine())!=null)
			{
				int index= Math.abs(line.hashCode());
				boolean inserted = bTreeLocal.insertKey(index,line);
				if(!inserted)
				{
					dup++;
//					System.out.println(line);
				}
			}
				buff.close();
			
				sum =sum + bTreeLocal.writeRecordsToOutputFile();
				
		}
		
		for(int i=0;i<cnt;i++)
		{
			boolean success = (new File(Constants.OUTPUT+i)).delete();
		     if (success) {
		        
		     }
		}
		
		
		System.out.println("Total records in Union "+sum);
//		System.out.println("Duplicate in file  "+dup);
		
		
	}

	
	private void insertRecords(String fileName) {
		// TODO Auto-generated method stub
		try {
			
			BufferedReader buff = new BufferedReader(new FileReader(fileName));
			String line;
			while((line = buff.readLine())!=null)
			{
				String tokens[] = line.split(" ");
//				int index= Math.abs(tokens[pKey].hashCode());
				int index= Math.abs(line.hashCode());
				bTree.insertRecordsToFile(index,line);
			}
				buff.close();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}


	void constructBtree(String fileName)
	{
		try {
		
		BufferedReader buff = new BufferedReader(new FileReader(fileName));
		String line;
		while((line = buff.readLine())!=null)
		{
			String tokens[] = line.split(" ");
//			Integer index= Math.abs(tokens[pKey].hashCode());
			Integer index= Math.abs(line.hashCode());
			bTree.insertKey(index,index.toString());
	
		}
			buff.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
