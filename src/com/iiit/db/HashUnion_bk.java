package com.iiit.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

public class HashUnion_bk {
	
	int M;
	int numRecords;
	String table1;
	String table2;
	int n;
	int pKey;
	
	public HashUnion_bk(int m, int numRecords, String table1, String table2, int n) {
		super();
		M = m;
		this.numRecords = numRecords;
		this.table1 = table1;
		this.table2 = table2;
		this.n = n;
		pKey=0;
	}
	
	public void union()
	{
			
		performHash(table1);
		performHash(table2);
		performUnion();
		deleteAllOutputFiles();
		
	}
	
	private void performUnion() {
		// TODO Auto-generated method stub
		
		
		int hashSize=1000;
		int maxSize = (numRecords*M)/hashSize;
		
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
			

			for(i=0;i<M-1;i++)
			{
				//Read table1 i'th file, Read table2 i'th file,perform hash
				
				BufferedReader buff = new BufferedReader(new FileReader(table1+i));
				String line;
				
				while((line = buff.readLine())!=null)
				{
					String tokens[] = line.split(",");
					int index=Integer.parseInt(tokens[pKey]);
					hashBucket = index%(hashSize);
					
					if(hashMap.get(hashBucket).size()>=1)
					{
						int j;
						for(j=0;j<hashMap.get(hashBucket).size();j++)
						{
							if(line.compareTo(hashMap.get(hashBucket).get(j))==0)
							{
//								System.out.println("Found same");
//								cnt++;
								break;
							}
						}
						if(j==hashMap.get(hashBucket).size()) //not duplicate
						{
							if(outFileMap.containsKey(hashBucket))
							{
								if(!checkForRecord(outFileMap.get(hashBucket),line))
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
							
							
						}
						
						if(hashMap.get(hashBucket).size()==maxSize)
						{
							if(outFileMap.containsKey(hashBucket))
							{
								writeToFile(outFileMap.get(hashBucket),hashMap.get(hashBucket));
							}else
							{
								outFileMap.put(hashBucket,Constants.OUTPUT+hashBucket);
								writeToFile(outFileMap.get(hashBucket),hashMap.get(hashBucket));
							}
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
					int index=Integer.parseInt(tokens[pKey]);
					hashBucket = index%(hashSize);
					
					if(hashMap.get(hashBucket).size()>=1)
					{
						int j;
						for(j=0;j<hashMap.get(hashBucket).size();j++)
						{
							if(line.compareTo(hashMap.get(hashBucket).get(j))==0)
							{
//								System.out.println("Found same");
//								cnt++;
								break;
							}
						}
						if(j==hashMap.get(hashBucket).size()) //not duplicate
						{
							if(outFileMap.containsKey(hashBucket))
							{
								if(!checkForRecord(outFileMap.get(hashBucket),line))
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
							
							
						}
						
						if(hashMap.get(hashBucket).size()==maxSize)
						{
							if(outFileMap.containsKey(hashBucket))
							{
								writeToFile(outFileMap.get(hashBucket),hashMap.get(hashBucket));
							}else
							{
								outFileMap.put(hashBucket,Constants.OUTPUT+hashBucket);
								writeToFile(outFileMap.get(hashBucket),hashMap.get(hashBucket));
							}
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
//				boolean success = (new File(fileName)).delete();
			}
			

			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
				
		
	}

	void performHash(String fileName)
	{
		int i,hashBucket;
		Vector<Vector<String>> hashMap = new Vector<>(M-1);
		for( i = 0;i<M-1;i++)
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
				int index=Integer.parseInt(tokens[pKey]);
				hashBucket = index%(M-1);
//				System.out.println("Hash Bucket : "+hashBucket+" Value: "+index);
	
				hashMap.get(hashBucket).add(line);
				
				if(hashMap.get(hashBucket).size()==numRecords)
				{
					writeToFile(fileName+hashBucket,hashMap.get(hashBucket));
					hashMap.get(hashBucket).removeAllElements();
				}
				
		
			}
			
			buff.close();
			
			
			for( i = 0;i<M-1;i++)
			{
				writeToFile(fileName+i,hashMap.get(i));
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void createFile(String fileName)
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
		for(int i=0;i<M-1;i++)
		{
			boolean success = (new File(table1+i)).delete();
		     if (success) {
		        
		     }
		     success = (new File(table2+i)).delete();
		}
		
		
	}
	
}
