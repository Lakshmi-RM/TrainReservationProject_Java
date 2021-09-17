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
	    con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/trainconsole","postgres","postgres");
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

    private static void displayStationDetails(int trainNumber,String from,String to)
    {
	try{
	    Statement stmt = connectionHandler();
	    ResultSet rs = stmt.executeQuery( "SELECT * FROM ROUTEDETAILS WHERE TRAIN_NO='"+trainNumber+"' AND STATION_NAME!='"+from+"' AND STATION_NAME!='"+to+"' ORDER BY ID ;" );

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
		displayStationDetails(trainNo,sourceStation,destinationStation);
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
	
	System.out.print("\nEnter the no. of stations (less than 10): ");
        String noOfStations=s.nextLine();
	
	String sql = "INSERT INTO TRAINDETAILS VALUES('"+trainNo+"','"+noOfTickets+"','"+trainName+"','"+sourceStation+"','"+destinationStation+"','"+departureTime+"','"+arrivalTime+"');";
	insertToDB(sql);

	sql = "INSERT INTO ROUTEDETAILS(train_no,station_name,avltkts,departure_time) VALUES('"+trainNo+"','"+sourceStation+"','"+noOfTickets+"','"+departureTime+"');";
	insertToDB(sql);

        for(int i=0;i<Integer.parseInt(noOfStations);i++)
        {
            System.out.printf("\nEnter station name : ");
            String stationName=s.nextLine();
	    System.out.print("\nEnter Arrival Time as hh:mm format : ");
            String arrTime=s.nextLine();
            System.out.print("\nEnter Departure Time as hh:mm format : ");
            String depTime=s.nextLine();
	    sql = "INSERT INTO ROUTEDETAILS(train_no,station_name,arrival_time,departure_time,avltkts) VALUES('"+trainNo+"','"+stationName+"','"+arrTime+"','"+depTime+"','"+noOfTickets+"');";
	    insertToDB(sql);	
        }	    
	
	sql = "INSERT INTO ROUTEDETAILS(train_no,station_name,avltkts,arrival_time) VALUES('"+trainNo+"','"+destinationStation+"','"+noOfTickets+"','"+arrivalTime+"');";
	insertToDB(sql);

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

    public int bookTickets(){
	Scanner s = new Scanner(System.in);
	int trainNo,ticketsReq=0;
	String fromSt,toSt;
	System.out.print("\nEnter the trainNo : ");
	trainNo=s.nextInt();
	s.nextLine();
	System.out.print("\nEnter the From Station : ");
	fromSt=s.nextLine();
	System.out.print("\nEnter the To Station : ");
	toSt = s.nextLine();	
	

	try{
	    Statement stmt = connectionHandler();
	    
	    ResultSet rs = stmt.executeQuery("select min(avltkts) as avl from routedetails where id>=(select id from routedetails where station_name='"+fromSt+"' and train_no='"+trainNo+"') and id<(select id from routedetails where station_name='"+toSt+"' and train_no='"+trainNo+"');");
	    if(rs.next()){

		int avl=rs.getInt("avl");
		if(avl!=0){
		System.out.println("The available Tickets are : "+avl);

		System.out.print("\nEnter the no. of tickets required : ");
		ticketsReq=s.nextInt();
	
		if(avl>=ticketsReq){

		    int rows = stmt.executeUpdate("update routedetails set avltkts=avltkts-'"+ticketsReq+"' where id>=(select id from routedetails where station_name='"+fromSt+"' and train_no='"+trainNo+"') and id<(select id from routedetails where station_name='"+toSt+"' and train_no='"+trainNo+"');");
		    
		}
		else{
		    System.out.println(ticketsReq+" tickets are not available");
		    return 0;
		}}
		else{
		    System.out.println("No such trains or stations available");
		    return 0;
		}
	    }

	    String sql = "INSERT INTO USERTICKETDETAILS(USERNAME,TRAIN_NO,FROM_STATION,TO_STATION,NO_OF_TICKETS,TIME_OF_BOOKING) VALUES('"+getUN()+"','"+trainNo+"','"+fromSt+"','"+toSt+"','"+ticketsReq+"',now());";
	    insertToDB(sql);
	    System.out.println("Ticket has been booked successfully");
	    	    

 	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return 1;
    }    

    public void myTickets()
    {
	
	String uN = getUN();
	try{

	    Statement stmt = connectionHandler();
	    ResultSet rs = stmt.executeQuery("SELECT * FROM USERTICKETDETAILS WHERE USERNAME='"+uN+"';");

	    System.out.printf("\n%-15s %-15s %-15s %-15s %-15s","Ticket ID","Train No","From Station","To Station","No Of Tickets");

	    while(rs.next()){
		int tID=rs.getInt("ticket_id");
		int tNo=rs.getInt("train_no");
		String fromSt=rs.getString("from_station");
		String toSt=rs.getString("to_station");
		int noOfTkts=rs.getInt("no_of_tickets");
		
		System.out.printf("\n%-15d %-15s %-15s %-15s %-15d",tID,tNo,fromSt,toSt,noOfTkts);
	    }

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

    public int cancelTicket()
    {
	Scanner s = new Scanner(System.in);

	String uN = getUN();
	try{

	    Statement stmt = connectionHandler();
	    ResultSet rs = stmt.executeQuery("SELECT * FROM USERTICKETDETAILS WHERE USERNAME='"+uN+"';");
	    if(rs.next()){

		System.out.println("Enter the Ticket ID to cancel ticket : ");
		int tID=s.nextInt();

		int tNo=rs.getInt("train_no");
		int tkts=rs.getInt("no_of_tickets");
		String from=rs.getString("from_station");
		String to=rs.getString("to_station");

		int rows = stmt.executeUpdate("update routedetails set avltkts=avltkts+'"+tkts+"' where id>=(select id from routedetails where station_name='"+from+"' and train_no='"+tNo+"') and id<(select id from routedetails where station_name='"+to+"' and train_no='"+tNo+"');");

		rows = stmt.executeUpdate("DELETE FROM USERTICKETDETAILS WHERE TICKET_ID='"+tID+"';");

		System.out.println("Your ticket with Ticket ID "+tID+" has been cancelled. ");
		return 1;

	    }
	    else{
		System.out.println("You have not booked any tickets yet.");
		return 0;
	    }

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return 0;

    }

}
