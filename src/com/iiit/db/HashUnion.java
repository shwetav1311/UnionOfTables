package com.iiit.db;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

public class HashUnion {
	
	int M;
	int numRecords;
	String table1;
	String table2;
	int n;
	int pKey;
	int recordSize;
	int blockSize;
	int modNum;
	
	public HashUnion(int m, int numRecords, String table1, String table2, int n,int recordSize,int blockSize) {
		super();
		M = m;
		this.numRecords = numRecords;
		this.table1 = table1;
		this.table2 = table2;
		this.n = n;
		pKey=1;
		this.recordSize=recordSize;
		this.blockSize=blockSize;
	}
	
	public void union()
	{
			
		int cnt1=1;
		int cnt2=1;
		try {
			cnt2 = countLines(table2);
			cnt1=countLines(table1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int maxLine=(cnt1>cnt2?cnt1:cnt2);
		
		int maxBlocks = (int) Math.ceil((maxLine*recordSize)/blockSize);
		
		int maxFileSize= (int) Math.ceil((maxBlocks/(M-1))*blockSize);
	
		if(maxFileSize==0)
		{
			modNum=10;
		}else
		{
			modNum= (int) (Math.ceil(maxLine*recordSize)/maxFileSize);
		}
		
		
		
		System.out.println("Mod num "+modNum);
		
		System.out.println("Block Size "+blockSize);
		System.out.println("B(R) "+maxBlocks);
		System.out.println("M "+M);
		
		System.out.println("Hashing files...");
		
		
		performHash(table1);
		performHash(table2);
		
		System.out.println("Performing Union...");
		
		performUnion();
		deleteAllOutputFiles();
		
		
		
	}
	
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	
	
	private void performUnion() {
		// TODO Auto-generated method stub
		
		
		int hashSize=10000;
//		int maxSize = (numRecords*M)/hashSize;
		
		HashMap<Integer, String> outFileMap = new HashMap<>();
 		
		int i,hashBucket;
		Vector<Vector<String>> hashMap = new Vector<>(hashSize);
		for( i = 0;i<hashSize;i++)
		{
			hashMap.add(new Vector<>());
		}
		createFile(Constants.OUTPUT);
		Vector<String> output = new Vector<>();
		int cnt=0;
		
		try{
			

			for(i=0;i<modNum;i++)
			{
				//Read table1 i'th file, Read table2 i'th file,perform hash
				
				BufferedReader buff = new BufferedReader(new FileReader(table1+i));
				String line;
				
				while((line = buff.readLine())!=null)
				{
					String tokens[] = line.split(",");
//					int index=Integer.parseInt(tokens[pKey]);
					int index= Math.abs(Objects.hashCode(tokens[pKey]));
					hashBucket = index%(hashSize);
					
					if(hashMap.get(hashBucket).size()>=1)
					{
						int j;
						for(j=0;j<hashMap.get(hashBucket).size();j++)
						{
							if(line.compareTo(hashMap.get(hashBucket).get(j))==0)
							{
								break;
							}
						}
						if(j==hashMap.get(hashBucket).size()) //not duplicate
						{
						
							hashMap.get(hashBucket).add(line);
							output.add(line);
							cnt++;
							
						}
						
						
					}else
					{
						hashMap.get(hashBucket).add(line);
						output.add(line);
						cnt++;
					}
					
					
					
					if(output.size()==numRecords)
					{
						writeToFile(Constants.OUTPUT,output);
						output.removeAllElements();
					}
					
			
				}
				
				buff.close();
				
				
				buff = new BufferedReader(new FileReader(table2+i));
				
				while((line = buff.readLine())!=null)
				{
					String tokens[] = line.split(",");
//					int index=Integer.parseInt(tokens[pKey]);
					int index= Math.abs(Objects.hashCode(tokens[pKey]));
					hashBucket = index%(hashSize);
					
					if(hashMap.get(hashBucket).size()>=1)
					{
						int j;
						for(j=0;j<hashMap.get(hashBucket).size();j++)
						{
							if(line.compareTo(hashMap.get(hashBucket).get(j))==0)
							{
								break;
							}
						}
						if(j==hashMap.get(hashBucket).size()) //not duplicate
						{
						
								hashMap.get(hashBucket).add(line);
								output.add(line);
								cnt++;
							
						}
						
						
					}else
					{
						hashMap.get(hashBucket).add(line);
						output.add(line);
						cnt++;
					}
					
					
					
					if(output.size()==numRecords)
					{
						writeToFile(Constants.OUTPUT,output);
						output.removeAllElements();
					}
					
				}
				
				
				buff.close();
					
				
			}
			
			System.out.println("Same count "+cnt);
			writeToFile(Constants.OUTPUT,output);
			
			for(String fileName :outFileMap.values())
			{
				boolean success = (new File(fileName)).delete();
			}
			

			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
				
		
	}

	void performHash(String fileName)
	{
		int i,hashBucket;
		Vector<Vector<String>> hashMap = new Vector<>(modNum);
		for( i = 0;i<modNum;i++)
		{
			createFile(fileName+i);
			hashMap.add(new Vector<>());
		}
		
		try {
			
			BufferedReader buff = new BufferedReader(new FileReader(fileName));
			String line;
			
			while((line = buff.readLine())!=null)
			{
				String tokens[] = line.split(",");
//				int index=Integer.parseInt(tokens[pKey]);
				int index= Math.abs(Objects.hashCode(tokens[pKey]));
			
				hashBucket = index%(modNum);
//				System.out.println("Hash Bucket : "+hashBucket+" Value: "+index);
	
				hashMap.get(hashBucket).add(line);
				
				if(hashMap.get(hashBucket).size()==numRecords)
				{
					writeToFile(fileName+hashBucket,hashMap.get(hashBucket));
					hashMap.get(hashBucket).removeAllElements();
				}
				
		
			}
			
			buff.close();
			
			
			for( i = 0;i<modNum;i++)
			{
				writeToFile(fileName+i,hashMap.get(i));
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void createFile(String fileName)
	{
		PrintWriter pw = null;
		
		try {
			
			pw = new PrintWriter(new FileWriter(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Driver.myExit("File not found");
		}

        pw.close();
	}

	public  void writeToFile(String fileName,Vector<String> tuples)
	{ 
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter(fileName,true));
			for(String tuple:tuples)
			{
				//System.out.println(tuple);
				pw.write(tuple);
				pw.write("\n");
			}
	        
	        pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean checkForRecord(String fileName,String record)
	{
		try
		{
			BufferedReader buff1 = new BufferedReader(new FileReader(fileName));
			String line;
			
			while((line = buff1.readLine())!=null)
			{
				if(record.compareTo(line)==0)
				{
					return true;
				}
			}
			buff1.close();
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	
	public void deleteAllOutputFiles()
	{
		for(int i=0;i<modNum;i++)
		{
			boolean success = (new File(table1+i)).delete();
		     if (success) {
		        
		     }
		     success = (new File(table2+i)).delete();
		}
		
		
	}
	
}
