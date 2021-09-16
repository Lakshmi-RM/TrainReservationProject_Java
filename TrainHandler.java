package com.transport.train;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;

public class TrainHandler{

	static Connection con;
	static String userName;

	public TrainHandler(Connection con){
	    this.con=con;
	}

	protected void finalize(){
	    try{
	    	con.close();
	    }catch(Exception e){
		System.out.println("Exception : "+e);
	    }
	}
	public void setUN(String userName){
	    this.userName=userName;
	}
	public String getUN(){
	    return this.userName;
	}

    public static Statement connectionHandler(){
	Statement stmt=null;
	try{	
	con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/task","postgres","postgres");
	stmt = con.createStatement();
	return stmt;
	}catch(Exception e){
	System.out.println("The Exception is "+e);
	}
	return stmt;
    }

    public static void insertToDB(String sql){
	try{
	    Statement stmt = connectionHandler();
	    stmt.executeUpdate(sql);
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

    public String userLoginAndReturnRole()
    {
	String uN,pW;
	Scanner s=new Scanner(System.in);
	System.out.print("\nEnter your username : ");
    	uN=s.nextLine();
    	System.out.print("\nEnter your password : ");
    	pW=s.nextLine();
	try{
	    Statement stmt = connectionHandler();
	    ResultSet rs = stmt.executeQuery( "SELECT ROLE FROM USERLOGIN WHERE USERNAME='"+uN+"' AND PASSWORD='"+pW+"';" );
	    String role="Invalid";
	    if(rs.next()){
		setUN(uN);
	    	role = rs.getString("ROLE");
	    }
            return role;
	}catch(Exception e){
	    System.out.println("The Exception is "+e);
	}
    	return "Invalid";   
    }

    public void addoperator()
    {
	try{
	Scanner s=new Scanner(System.in);	
    	String username,password;
    	System.out.print("\nEnter the username : ");
    	username=s.nextLine();
    	System.out.print("\nEnter the password : ");
    	password=s.nextLine();
	String sql = "INSERT INTO USERLOGIN VALUES('"+username+"','"+password+"','Operator');";
	insertToDB(sql);
	System.out.print("\nOperator added successfully");
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }    

    private static void displayStationDetails(int trainNumber){
	try{
	    Statement stmt = connectionHandler();
	    ResultSet rs = stmt.executeQuery( "SELECT * FROM ROUTEDETAILS WHERE TRAIN_NO='"+trainNumber+"';" );
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
	    Statement stmt = connectionHandler();
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

	try{
	String sql = "INSERT INTO TRAINDETAILS VALUES('"+trainNo+"','"+noOfTickets+"','"+trainName+"','"+sourceStation+"','"+destinationStation+"','"+departureTime+"','"+arrivalTime+"');";
	insertToDB(sql);

	System.out.print("\nEnter the no. of stations (less than 10): ");
        String noOfStations=s.nextLine();
	
	int totalStations=Integer.parseInt(noOfStations)+2;
	String[] stations=new String[totalStations];	
	stations[0]=sourceStation;
	stations[totalStations-1]=destinationStation;

        for(int i=0;i<Integer.parseInt(noOfStations);i++)
        {
            System.out.printf("\nEnter station name : ");
            String stationName=s.nextLine();
	    stations[i+1]=stationName;
            System.out.print("\nEnter Arrival Time as hh:mm format : ");
            String arrTime=s.nextLine();
            System.out.print("\nEnter Departure Time as hh:mm format : ");
            String depTime=s.nextLine();
	    sql = "INSERT INTO ROUTEDETAILS VALUES('"+trainNo+"','"+stationName+"','"+arrTime+"','"+depTime+"');";
	    insertToDB(sql);	
        }

	for(int i=0;i<totalStations;i++)
	{
	    for(int j=i+1;j<totalStations;j++)
	    {
		if(j==i+1){
		    sql = "INSERT INTO TICKETSPLITUPS(TNO,FROMSTATION,TOSTATION,TICKETSAVL,MIDDLESTATION) VALUES('"+trainNo+"','"+stations[i]+"','"+stations[j]+"','"+noOfTickets+"','Nil');";
	            insertToDB(sql);
		}
		for(int k=i;k+1<j;k++)
		{   
		    sql = "INSERT INTO TICKETSPLITUPS(TNO,FROMSTATION,TOSTATION,TICKETSAVL,MIDDLESTATION) VALUES('"+trainNo+"','"+stations[i]+"','"+stations[j]+"','"+noOfTickets+"','"+stations[k+1]+"');";
	    	    insertToDB(sql);
		}
	    }
	}

    	System.out.println("Train added successfully");	
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	
    }

    public void signUp(){
	Scanner s=new Scanner(System.in);	
    	String username,password;
    	System.out.print("\nEnter the username : ");
    	username=s.nextLine();
    	System.out.print("\nEnter the password : ");
    	password=s.nextLine();
	try{
	String sql = "INSERT INTO USERLOGIN VALUES('"+username+"','"+password+"','Passenger');";
	insertToDB(sql);
	System.out.print("\nPassenger added successfully");
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

    public void bookTickets(){
	Scanner s = new Scanner(System.in);
	int trainNo,ticketsReq;
	String fromSt,toSt;
	System.out.print("\nEnter the trainNo : ");
	trainNo=s.nextInt();
	s.nextLine();
	System.out.print("\nEnter the From Station : ");
	fromSt=s.nextLine();
	System.out.print("\nEnter the To Station : ");
	toSt = s.nextLine();
	System.out.print("\nEnter no. of tickets to be booked : ");
	ticketsReq = s.nextInt();
	s.nextLine();
	try{
	Statement stmt = connectionHandler();
	ResultSet rs = stmt.executeQuery("SELECT TNO FROM TICKETSPLITUPS WHERE TNO='"+trainNo+"' AND FROMSTATION='"+fromSt+"' AND TOSTATION='"+toSt+"' AND TICKETSAVL>='"+ticketsReq+"';");
	if(rs.next()){
	    int rs1 = stmt.executeUpdate("update ticketsplitups set ticketsavl=ticketsavl-'"+ticketsReq+"' where (fromstation='"+fromSt+"' or tostation='"+toSt+"' or middlestation='"+fromSt+"' or middleStation='"+toSt+"') and tno in (SELECT TNO FROM TICKETSPLITUPS WHERE TNO='"+trainNo+"' AND FROMSTATION='"+fromSt+"' AND TOSTATION='"+toSt+"' AND TICKETSAVL>='"+ticketsReq+"');");
	    String sql = "INSERT INTO USERTICKETDETAILS VALUES('"+getUN()+"','"+trainNo+"','"+fromSt+"','"+toSt+"','"+ticketsReq+"',now());";
	    insertToDB(sql);
	    System.out.println("Your ticket has been successfully booked");
	}
	else{
	    System.out.println("You have entered wrong credentials");
	}
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}	
    }    

    public void myTickets()
    {
	String uN = getUN();
	try{
	    Statement stmt = connectionHandler();
	    ResultSet rs = stmt.executeQuery("SELECT * FROM USERTICKETDETAILS WHERE USERNAME='"+uN+"';");
	    System.out.printf("\n%-15s %-15s %-15s %-15s","Train No","From Station","To Station","No Of Tickets");
	    while(rs.next()){
		int tNo=rs.getInt("train_no");
		String fromSt=rs.getString("from_station");
		String toSt=rs.getString("to_station");
		int noOfTkts=rs.getInt("no_of_tickets");
		System.out.printf("\n%-15d %-15s %-15s %-15d",tNo,fromSt,toSt,noOfTkts);
	    }
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

}