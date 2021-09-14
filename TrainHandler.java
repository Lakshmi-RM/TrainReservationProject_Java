package com.transport.train;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;

public class TrainHandler{

    public static void insertToDB(String sql){
	try{
	    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/task","postgres","postgres");
	    Statement stmt = con.createStatement();
	    stmt.executeUpdate(sql);
	    stmt.close();
	    con.close();
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

    public String userLoginAndReturnRole()
    {
	Scanner s=new Scanner(System.in);	

	String username,password;
	System.out.print("\nEnter your username : ");
    	username=s.nextLine();
    	System.out.print("\nEnter your password : ");
    	password=s.nextLine();
	try{
	    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/task","postgres","postgres");
	    Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery( "SELECT * FROM USERLOGIN;" );
            while(rs.next()){
	        String userNameFromDB = rs.getString("username");
		String passWordFromDB = rs.getString("password");
		String roleFromDB = rs.getString("role");
		if((username.equals(userNameFromDB))&&(password.equals(passWordFromDB))){
		    con.close();
		    return roleFromDB;
		}
	    }
	}catch(Exception e){
	System.out.println("The Exception is "+e);}
    	return "1";   
    }

    public void addoperator()
    {
	Scanner s=new Scanner(System.in);	
    	String username,password;
    	System.out.print("\nEnter the username : ");
    	username=s.nextLine();
    	System.out.print("\nEnter the password : ");
    	password=s.nextLine();
	String sql = "INSERT INTO USERLOGIN VALUES('"+username+"','"+password+"','Operator');";
	insertToDB(sql);
	System.out.print("\nOperator added successfully");
    }    

    private static void displayStationDetails(int trainNumber){
	try{
	    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/task","postgres","postgres");
	    Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery( "SELECT * FROM ROUTEDETAILS;" );
            while(rs.next()){
		int trainNo = rs.getInt("train_no");
		String stationName = rs.getString("station_name");
		String arrivalTime = rs.getString("arrival_time");
		String departureTime = rs.getString("departure_time");

		if(trainNo==trainNumber){
		    System.out.printf("%-15s %-15s %-15s\n%-111s",stationName,arrivalTime,departureTime," ");    
		}
 	    }
	}catch(Exception e){
	    System.out.println("\nException : "+e);
	}
    }

    public void displayTrainDetails(){
	System.out.println("Train No        No Of Tickets   Train Name      Source          Destination     Departure Time  Arrival Time   Station Name    ArrivalTime     DepartureTime");	
	try{
	    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/task","postgres","postgres");
	    Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery( "SELECT * FROM TRAINDETAILS;" );
            while(rs.next()){
	        int trainNo = rs.getInt("train_no");
		int noOfTickets = rs.getInt("no_of_tickets");
		String trainName = rs.getString("train_name");
		String sourceStation = rs.getString("source_station");
		String destinationStation = rs.getString("destination_station");
		String departureTime = rs.getString("departure_time");
		String arrivalTime = rs.getString("arrival_time");
		
		System.out.printf("\n%-15d %-15d %-15s %-15s %-15s %-15s %-15s",trainNo,noOfTickets,trainName,sourceStation,destinationStation,departureTime,arrivalTime);
		displayStationDetails(trainNo);
	    }
	}catch(Exception e){
	    System.out.println("\nException : "+e);
	}
    }

    public void addTrain(){
        Scanner s=new Scanner(System.in);
	
        System.out.print("\nEnter Train Number : ");
        String trainNo=s.nextLine();
        System.out.print("\nEnter number of tickets in train : ");
        String noOfTickets=s.nextLine();
        System.out.print("\nEnter Train Name : ");
        String trainName=s.nextLine();
        System.out.print("\nEnter Source Station : ");
        String sourceStation=s.nextLine();
        System.out.print("\nEnter Destination Station : ");
        String destinationStation=s.nextLine();
        System.out.print("\nEnter Departure Time as hh:mm format : ");
        String departureTime=s.nextLine();
        System.out.print("\nEnter Arrival Time as hh:mm format : ");
        String arrivalTime=s.nextLine();

	String sql = "INSERT INTO TRAINDETAILS VALUES('"+trainNo+"','"+noOfTickets+"','"+trainName+"','"+sourceStation+"','"+destinationStation+"','"+departureTime+"','"+arrivalTime+"');";
	insertToDB(sql);

	System.out.print("\nEnter the no. of stations (less than 10): ");
        String noOfStations=s.nextLine();
        for(int i=0;i<Integer.parseInt(noOfStations);i++)
        {
            System.out.printf("\nEnter station name : ");
            String stationName=s.nextLine();
            System.out.print("\nEnter Arrival Time as hh:mm format : ");
            String arrTime=s.nextLine();
            System.out.print("\nEnter Departure Time as hh:mm format : ");
            String depTime=s.nextLine();
	    sql = "INSERT INTO ROUTEDETAILS VALUES('"+trainNo+"','"+stationName+"','"+arrTime+"','"+depTime+"');";
	    insertToDB(sql);	
        }
    	System.out.println("Train added successfully");	
	
    }

    public void signUp(){
	Scanner s=new Scanner(System.in);	
    	String username,password;
    	System.out.print("\nEnter the username : ");
    	username=s.nextLine();
    	System.out.print("\nEnter the password : ");
    	password=s.nextLine();
	String sql = "INSERT INTO USERLOGIN VALUES('"+username+"','"+password+"','Passenger');";
	insertToDB(sql);
	System.out.print("\nPassenger added successfully");
    }

}