package com.transport.train;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;

import java.io.FileOutputStream;  
import com.itextpdf.text.Document;  
import com.itextpdf.text.Paragraph;  
import com.itextpdf.text.pdf.PdfWriter;  


public class TrainHandler{

    static Connection con;
    static int userID;

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

    public void setUID(int userID){
	this.userID=userID;
    }

    public String getUID(){
	return Integer.toString(this.userID);
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

    public int signUp(String username,String password){
	
	try{

	    String sql = "INSERT INTO USERLOGIN(USERNAME,PASSWORD,ROLE) VALUES('"+username+"','"+password+"','Passenger');";
	    insertToDB(sql);
	    return 1;
	
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return 0;
    }

    public String userLoginAndReturnRole(String uN,String pW)
    {

	try{
	    Statement stmt = connectionHandler();
	    ResultSet rs = stmt.executeQuery( "SELECT ROLE,USERID FROM USERLOGIN WHERE USERNAME='"+uN+"' AND PASSWORD='"+pW+"';" );
	    String role="Invalid";
	    
	    if(rs.next()){
		int ID = rs.getInt("USERID");
		setUID(ID);
	    	role = rs.getString("ROLE");
	    }

            return role;

	}catch(Exception e){
	    System.out.println("The Exception is "+e);
	}

    	return "Invalid";   
    }

    public int addoperator(String username, String password)
    {
	try{
	    
	    String sql = "INSERT INTO USERLOGIN(USERNAME,PASSWORD,ROLE) VALUES('"+username+"','"+password+"','Operator');";
	    insertToDB(sql);

	    return 1;

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return 0;
    }    

    public ResultSet displayStationDetails(int trainNumber,String from,String to)
    {
	ResultSet rs=null;
	try{

	    Statement stmt = connectionHandler();
	    rs = stmt.executeQuery( "SELECT * FROM ROUTEDETAILS WHERE TRAIN_NO='"+trainNumber+"' AND STATION_NAME!='"+from+"' AND STATION_NAME!='"+to+"' ORDER BY ID ;" );

            return rs;

	}catch(Exception e){
	    System.out.println("\nException : "+e);
	}

	return rs;
    }

    public ResultSet displayTrainDetails(){

	ResultSet rs=null;
	try{

	    Statement stmt = connectionHandler();
	    rs = stmt.executeQuery( "SELECT * FROM TRAINDETAILS;" );

	    return rs;

	}catch(Exception e){
	    System.out.println("\nException : "+e);
	}

	return rs;
    }

    public void addTrain(String trainNo,String noOfTickets,String trainName,String sourceStation,String destinationStation,String departureTime,String arrivalTime){
        
	try{
	
	String sql = "INSERT INTO TRAINDETAILS(TRAIN_NO,NO_OF_TICKETS,TRAIN_NAME,SOURCE_STATION,DESTINATION_STATION,DEPARTURE_TIME,ARRIVAL_TIME) VALUES('"+trainNo+"','"+noOfTickets+"','"+trainName+"','"+sourceStation+"','"+destinationStation+"','"+departureTime+"','"+arrivalTime+"');";
	insertToDB(sql);

	sql = "INSERT INTO ROUTEDETAILS(train_no,station_name,avltkts,departure_time) VALUES('"+trainNo+"','"+sourceStation+"','"+noOfTickets+"','"+departureTime+"');";
	insertToDB(sql);	    
		
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}	
    }

    public void addRoute(String trainNo,String stationName,String arrTime,String depTime,String noOfTickets){

	String sql = "INSERT INTO ROUTEDETAILS(train_no,station_name,arrival_time,departure_time,avltkts) VALUES('"+trainNo+"','"+stationName+"','"+arrTime+"','"+depTime+"','"+noOfTickets+"');";
	insertToDB(sql);

    }

    public int addDestination(String trainNo,String destinationStation,String noOfTickets,String arrivalTime){

	String sql = "INSERT INTO ROUTEDETAILS(train_no,station_name,avltkts,arrival_time) VALUES('"+trainNo+"','"+destinationStation+"','"+noOfTickets+"','"+arrivalTime+"');";
	insertToDB(sql);
	return 1;

    }

    public int bookTickets(int trainNo,String fromSt,String toSt){
	
	try{
	    Statement stmt = connectionHandler();
	    
	    ResultSet rs = stmt.executeQuery("select min(avltkts) as avl from routedetails where id>=(select id from routedetails where station_name='"+fromSt+"' and train_no='"+trainNo+"') and id<(select id from routedetails where station_name='"+toSt+"' and train_no='"+trainNo+"');");
	    if(rs.next()){

		int avl=rs.getInt("avl");
		return avl;
		
	    } 	    

 	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}

	return 0;
    }    

    public void updateAndInsertToTable(int ticketsReq,String fromSt,String toSt,int trainNo){

	try{	

	Statement stmt = connectionHandler();
	    
	int rows = stmt.executeUpdate("update routedetails set avltkts=avltkts-'"+ticketsReq+"' where id>=(select id from routedetails where station_name='"+fromSt+"' and train_no='"+trainNo+"') and id<(select id from routedetails where station_name='"+toSt+"' and train_no='"+trainNo+"');");
	String sql = "INSERT INTO USERTICKETDETAILS(USERID,TRAIN_NO,FROM_STATION,TO_STATION,NO_OF_TICKETS,TIME_OF_BOOKING) VALUES('"+getUID()+"','"+trainNo+"','"+fromSt+"','"+toSt+"','"+ticketsReq+"',now());";
	insertToDB(sql);    

        }catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

    public void writeToPDF()  
    {  
	try  
	{  
	    ResultSet rs=null,rs1=null;
	    String uID = getUID();
	    Statement stmt = connectionHandler();
	    Statement stmt1 = connectionHandler();
	    
	    rs = stmt.executeQuery("SELECT * FROM USERTICKETDETAILS WHERE USERID='"+uID+"';");
	    rs1 = stmt1.executeQuery("SELECT USERNAME FROM USERLOGIN WHERE USERID='"+uID+"';");

	    if(rs.next()){  
	    	    
	    	int tID=rs.getInt("ticket_id");
	
	    	String f="C:\\Users\\WELCOME\\Desktop\\Task1\\PDF\\Ticket";
	    	f = f + Integer.toString(tID);
	    	f = f + ".pdf";

	    	Document doc = new Document();  	
	    
	    	PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(f));  
	    	doc.open();  
	  
	    	int tNo=rs.getInt("train_no");
	    	String fromSt=rs.getString("from_station");
	    	String toSt=rs.getString("to_station");
	    	int noOfTkts=rs.getInt("no_of_tickets");

		String uN="";

		if(rs1.next()){
		    uN=rs1.getString("username");
		}

		doc.add(new Paragraph("User Name      : "+uN));

	    	doc.add(new Paragraph("Ticket ID      : "+tID));   
	    	doc.add(new Paragraph("Train No       : "+tNo));   
	    	doc.add(new Paragraph("From Station   : "+fromSt));   
	    	doc.add(new Paragraph("To Station     : "+toSt));   
	    	doc.add(new Paragraph("No. Of Tickets : "+noOfTkts)); 

	    	doc.close();  
	    	writer.close();  
	    }	    
	      
	}catch (Exception e)  {  
	    System.out.println("Exception : "+e);  
	}     
    } 

    public ResultSet myTickets()
    {
	ResultSet rs=null;
	String uID = getUID();
	try{

	    Statement stmt = connectionHandler();
	    rs = stmt.executeQuery("SELECT * FROM USERTICKETDETAILS WHERE USERID='"+uID+"';");
	    return rs;

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return rs;
    }

    public ResultSet cancelTicket()
    {
	ResultSet rs=null;
	String uID = getUID();
	try{

	    Statement stmt = connectionHandler();
	    rs = stmt.executeQuery("SELECT * FROM USERTICKETDETAILS WHERE USERID='"+uID+"';");
	    return rs;

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return rs;

    }

    public void updateAndDelete(int tkts,String from,String to,int tNo,int tID){
	try{

	    Statement stmt = connectionHandler();
	    int rows = stmt.executeUpdate("update routedetails set avltkts=avltkts+'"+tkts+"' where id>=(select id from routedetails where station_name='"+from+"' and train_no='"+tNo+"') and id<(select id from routedetails where station_name='"+to+"' and train_no='"+tNo+"');");
	    rows = stmt.executeUpdate("DELETE FROM USERTICKETDETAILS WHERE TICKET_ID='"+tID+"';");		

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

}
