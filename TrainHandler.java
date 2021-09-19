package com.transport.train;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;

import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import com.itextpdf.text.Document;  
import com.itextpdf.text.DocumentException;  
import com.itextpdf.text.Paragraph;  
import com.itextpdf.text.pdf.PdfWriter;  

import java.security.MessageDigest;

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

    public ResultSet executingQuery(String sql){
	ResultSet rs=null;
	try{

	    Statement stmt = connectionHandler();
	    rs = stmt.executeQuery(sql);
	    return rs;

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return rs;
    }

    public int signUp(String username,String password){
	
	try{
	  
	    String pwd = doHash(password);	    
	    String sql = "INSERT INTO USERLOGIN(USERNAME,PASSWORD,ROLE) VALUES('"+username+"','"+pwd+"','Passenger');";
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
	    String pass=doHash(pW);
	    ResultSet rs = stmt.executeQuery( "SELECT ROLE,USERID FROM USERLOGIN WHERE USERNAME='"+uN+"' AND PASSWORD='"+pass+"';" );
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
	    String pwd = doHash(password);	    
	    
	    String sql = "INSERT INTO USERLOGIN(USERNAME,PASSWORD,ROLE) VALUES('"+username+"','"+pwd+"','Operator');";
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
	    rs = stmt.executeQuery( "SELECT * FROM ROUTEDETAILS WHERE TRAIN_NO='"+trainNumber+"' AND STATION_NAME!='"+from+"' AND STATION_NAME!='"+to+"' ORDER BY RID ;" );

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

    public int findAvailablity(int trainNo,String fromSt,String toSt){
	
	try{
	    Statement stmt = connectionHandler();
	    
	    ResultSet rs = stmt.executeQuery("select min(avltkts) as avl from routedetails where rid>=(select rid from routedetails where station_name='"+fromSt+"' and train_no='"+trainNo+"') and rid<(select rid from routedetails where station_name='"+toSt+"' and train_no='"+trainNo+"');");
	    if(rs.next()){

		int avl=rs.getInt("avl");
		return avl;
		
	    } 	    

 	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}

	return Integer.MAX_VALUE;
    }    

    public void updateAndInsertToTable(int ticketsReq,String fromSt,String toSt,int trainNo){

	try{	

	Statement stmt = connectionHandler();
	int tid=0,fromID=0,toID=0;
	    
	int rows = stmt.executeUpdate("update routedetails set avltkts=avltkts-'"+ticketsReq+"' where rid>=(select rid from routedetails where station_name='"+fromSt+"' and train_no='"+trainNo+"') and rid<(select rid from routedetails where station_name='"+toSt+"' and train_no='"+trainNo+"');");
	
	ResultSet rs=stmt.executeQuery("select tid from traindetails where train_no='"+trainNo+"';");

	if(rs.next()){
	    tid=rs.getInt("tid");
	}

	ResultSet rs1=stmt.executeQuery("select rid from routedetails where train_no='"+trainNo+"' and station_name='"+fromSt+"';");
	
	if(rs1.next()){
	    fromID=rs1.getInt("rid");
	}

	ResultSet rs2=stmt.executeQuery("select rid from routedetails where train_no='"+trainNo+"' and station_name='"+toSt+"';");
	
	if(rs2.next()){
	    toID=rs2.getInt("rid");
	}
	
	String sql = "INSERT INTO USERTICKETDETAILS(USERID,TRAIN_ID,FROM_ID,TO_ID,NO_OF_TICKETS,TIME_OF_BOOKING) VALUES('"+getUID()+"','"+tid+"','"+fromID+"','"+toID+"','"+ticketsReq+"',now());";
	insertToDB(sql);

        }catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

    public void writeToPDF()  
    {  
	try  
	{  
	    ResultSet rs=null,rs1=null,rs2=null;
	    String uID = getUID();
	    Statement stmt = connectionHandler();
	    Statement stmt1 = connectionHandler();
	    Statement stmt2 = connectionHandler();
	    Statement stmt3 = connectionHandler();
	    Statement stmt4 = connectionHandler();
	    
	    rs = stmt.executeQuery("SELECT * FROM USERTICKETDETAILS WHERE USERID='"+uID+"' order by time_of_booking desc limit 1;");

	    int tID=0,trainID=0,fromID=0,toID=0,noOfTkts=0,tNo=0;
	    String time="",uN="",tName="",fromSt="",toSt="",fromTime="",toTime="";

	    if(rs.next()){

		tID=rs.getInt("ticket_id");
		trainID=rs.getInt("train_id");
		fromID=rs.getInt("from_id");
		toID=rs.getInt("to_id");
		noOfTkts=rs.getInt("no_of_tickets"); 
		time=rs.getString("time_of_booking");

	    }

	    rs1 = stmt1.executeQuery("SELECT USERNAME FROM USERLOGIN WHERE USERID='"+uID+"';");
	    
	    if(rs1.next()){
		uN=rs1.getString("username");
	    }

	    rs2 = stmt2.executeQuery("select train_no,train_name from traindetails where tid='"+trainID+"'");

	    if(rs2.next()){
		tNo=rs2.getInt("train_no");
		tName=rs2.getString("train_name");
	    }  

	    rs1 = stmt3.executeQuery("select station_name,departure_time from routedetails where rid='"+fromID+"';");
	    rs2 = stmt4.executeQuery("select station_name,arrival_time from routedetails where rid='"+toID+"';");
	   
	    if(rs1.next()){
		fromSt=rs1.getString("station_name");
		fromTime=rs1.getString("departure_time");
	    }

	    if(rs2.next()){
		toSt=rs2.getString("station_name");
		toTime=rs2.getString("arrival_time");
	    }

	    rs1 = stmt1.executeQuery("select * from routedetails where rid>(select rid from routedetails where station_name='"+fromSt+"' and train_no='"+tNo+"') and rid<(select rid from routedetails where station_name='"+toSt+"' and train_no='"+tNo+"');");
	    	
	    String f="C:\\Users\\WELCOME\\Desktop\\Task1\\PDF\\Ticket";
	    f = f + Integer.toString(tID);
	    f = f + ".pdf";

  	    Document doc = new Document();  	
	    
	    PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(f));  
	    doc.open();  

	    doc.add(new Paragraph("User Name      : "+uN));

       	    doc.add(new Paragraph("Ticket ID      : "+tID));   
	    doc.add(new Paragraph("Train No       : "+tNo));
	    doc.add(new Paragraph("Train Name     : "+tName));   
	    doc.add(new Paragraph("From Station   : "+fromSt+"   Time : "+fromTime));   
	    doc.add(new Paragraph("To Station     : "+toSt+"   Time : "+toTime));   
	    doc.add(new Paragraph("Route Details  : "));

	    
	    while(rs1.next()){
		doc.add(new Paragraph("                "+rs1.getString("station_name")+"  "+rs1.getString("arrival_time")+"  "+rs1.getString("departure_time")));		
	    }
	    
	    rs1 = stmt1.executeQuery("select * from routedetails where rid>(select rid from routedetails where station_name='"+fromSt+"' and train_no='"+tNo+"') and rid<(select rid from routedetails where station_name='"+toSt+"' and train_no='"+tNo+"');");
	    if(!rs1.next()){
		doc.add(new Paragraph("                Empty"));
	    }

	    doc.add(new Paragraph("No. Of Tickets : "+noOfTkts));
	    doc.add(new Paragraph("Time of Booking: "+time)); 

	    doc.close();  
	    writer.close();  	    
	      
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

    public void updateAndDelete(int tkts,int fromID,int toID,int trainID,int tID){
	try{

	    Statement stmt = connectionHandler();
	    Statement stmt1 = connectionHandler();
	
	    int tNo=0;
	    String from="",to="";

	    ResultSet rs1 = stmt.executeQuery("select train_no from traindetails where tid='"+trainID+"'");
	    if(rs1.next()){
		tNo=rs1.getInt("train_no");
	    }

	    ResultSet rs2 = stmt.executeQuery("select station_name,departure_time from routedetails where rid='"+fromID+"';");
	    ResultSet rs3 = stmt1.executeQuery("select station_name,arrival_time from routedetails where rid='"+toID+"';");
	   
	    if(rs2.next()){
		from=rs2.getString("station_name");
	    }

	    if(rs3.next()){
		to=rs3.getString("station_name");
	    }

	    int rows = stmt.executeUpdate("update routedetails set avltkts=avltkts+'"+tkts+"' where id>=(select id from routedetails where station_name='"+from+"' and train_no='"+tNo+"') and id<(select id from routedetails where station_name='"+to+"' and train_no='"+tNo+"');");

	    rows = stmt.executeUpdate("DELETE FROM USERTICKETDETAILS WHERE TICKET_ID='"+tID+"';");		

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

    public static String doHash(String pwd){
	try{
	    MessageDigest mD=MessageDigest.getInstance("MD5");
	    mD.update(pwd.getBytes());
	    byte[] result = mD.digest();
	    StringBuilder sb = new StringBuilder();

	    for(byte b : result)
		sb.append(String.format("%02x",b));
	    return sb.toString();
	}catch(Exception e){
	    System.out.println("Exception is "+e);
	}
	return "";
    }
}
