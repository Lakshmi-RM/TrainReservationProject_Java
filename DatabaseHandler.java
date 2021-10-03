package com.transport.train;

import java.util.LinkedList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseHandler
{

    public static LinkedList insertIntoDB(LinkedList tableValues){
	try{
System.out.println("c "+tableValues);
	    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/trainconsole","postgres","postgres");;
	    Statement stmt = con.createStatement();
	    String sql = "insert into "+tableValues.get(0)+" (";
System.out.println("d "+sql);
	    for(int i=1;i<tableValues.size();i=i+2){
		sql += tableValues.get(i);
		if(i<tableValues.size()-2)
		     sql+=",";
	    }
System.out.println("e "+sql);
	    sql += ") values (";
System.out.println("f "+sql);
	    for(int i=2;i<tableValues.size();i=i+2){
		sql += "'"+tableValues.get(i)+"'";
		if(i<tableValues.size()-2)
		     sql+=",";
	    }
System.out.println("g "+sql);
	    sql += ");";
System.out.println("h "+sql);
	    int rows = stmt.executeUpdate(sql);
	}catch(Exception e){
	    System.out.println("Exception is "+e);
	}
	return tableValues;
    }
}