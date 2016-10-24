package com.iiit.db;

import java.util.Date;

public class Driver {

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//union(Table1,Table2,n,M,type_of_index)
		
		int recordLen=54;
		int blockSize=1*1024*1024; //1MB
		
		
		String table1=args[0];
		String table2=args[1];
		Integer n=Integer.parseInt(args[2]);
		Integer M=Integer.parseInt(args[3]);
		Integer type= Integer.parseInt(args[4]);
		
		int numRecords = blockSize/recordLen;  //No.of records in one block
		
		long beginTime = new Date().getTime();
		
		if(type==Constants.TYPE_BTREE)
		{
			BtreeUnion bUnion = new BtreeUnion(M, numRecords, table1, table2, n,blockSize,recordLen);
			bUnion.union();
		}else
		{
			HashUnion hUnion = new HashUnion(M, numRecords, table1, table2, n,recordLen,blockSize);
			hUnion.union();
		}
		
		long endTime = new Date().getTime();
		
		double time = (endTime-beginTime*1.0)/(1000.0*60);
		System.out.println("Time Taken: "+time+" mins");
		
		//1GB : 4.09 mins
		//2GB : 15.20561 mins
		//10Mb : 0.04716 mins
		
		
	}

	public static void myExit(String string) {
		// TODO Auto-generated method stub
		System.out.println(string);
	}

}
