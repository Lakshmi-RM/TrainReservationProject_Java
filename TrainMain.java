
import com.transport.train.TrainHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;

public class TrainMain {
	
    static Connection con;
    static TrainHandler th=new TrainHandler(con);	

    private static void addoperator(){

	Scanner s=new Scanner(System.in);	
    	String username,password;

    	System.out.print("\nEnter the username : ");
    	username=s.nextLine();
    	System.out.print("\nEnter the password : ");
    	password=s.nextLine();

	if(th.addoperator(username,password)==1)
	    System.out.print("\nOperator added successfully.");
	else
	    System.out.print("\nOperator was not added.");

    }

    private static void displayTrainDetails(){

	System.out.println("Train ID       Train No        No Of Tickets   Train Name      Source          Destination     Departure Time  Arrival Time   Station Name    ArrivalTime     DepartureTime");	

    try{
	ResultSet rs = th.displayTrainDetails();

	while(rs.next()){

	    int trainId = rs.getInt("tid");
	    int trainNo = rs.getInt("train_no");
	    int noOfTickets = rs.getInt("no_of_tickets");

	    String trainName = rs.getString("train_name");
	    String sourceStation = rs.getString("source_station");
	    String destinationStation = rs.getString("destination_station");
	    String departureTime = rs.getString("departure_time");
	    String arrivalTime = rs.getString("arrival_time");
		
	    System.out.printf("\n%-15d%-15d %-15d %-15s %-15s %-15s %-15s %-15s",trainId,trainNo,noOfTickets,trainName,sourceStation,destinationStation,departureTime,arrivalTime);

	    ResultSet rs1 = th.displayStationDetails(trainNo,sourceStation,destinationStation);

	    while(rs1.next()){

		int trainNo1 = rs1.getInt("train_no");
		String stationName = rs1.getString("station_name");
		String arrivalTime1 = rs1.getString("arrival_time");
		String departureTime1 = rs1.getString("departure_time");

		if(trainNo1==trainNo){
		    System.out.printf("%-15s %-15s %-15s\n%-126s",stationName,arrivalTime1,departureTime1," ");    
		}
 	    }
	}
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	   
    }

    private static int subMenuAdmin(){
	Scanner s=new Scanner(System.in);	
	
	int choice=0;
	System.out.print("\nWelcome Admin!");

    	while(choice!=3)
    	{
            System.out.print("\n1. Add Operator\n2. Display Train\n3. Log Out");
            System.out.print("\nYour choice : ");
            choice=s.nextInt();

            switch(choice)
            {
            case 1:
                addoperator();
        	break;

            case 2:
                displayTrainDetails();
                break;

            case 3:
                System.out.print("\nLogging Out...\n");
                MainMenu(1);
                break;

            default:
                System.out.print("\nWrong choice.");

           }
       }
	return 1;
    }

    private static void addTrain(){

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
	
	System.out.print("\nEnter the no. of stations (less than 10): ");
        String noOfStations=s.nextLine();
	
	th.addTrain(trainNo,noOfTickets,trainName,sourceStation,destinationStation,departureTime,arrivalTime);

	for(int i=0;i<Integer.parseInt(noOfStations);i++)
        {
	    System.out.printf("\nEnter station name : ");
            String stationName=s.nextLine();
	    System.out.print("\nEnter Arrival Time as hh:mm format : ");
            String arrTime=s.nextLine();
            System.out.print("\nEnter Departure Time as hh:mm format : ");
            String depTime=s.nextLine();

	    th.addRoute(trainNo,stationName,arrTime,depTime,noOfTickets);
	}
	 
	if(th.addDestination(trainNo,destinationStation,noOfTickets,arrivalTime)==1){
	     System.out.println("Train was added successfully");
	}
	   
    }

    private static int subMenuOperator()
    {
	Scanner s=new Scanner(System.in);	
	
        System.out.print("\nWelcome Operator!");
        int choice=1;

        while(choice!=2)
        {
            System.out.print("\n1. Add Train\n2. Log Out");
            System.out.print("\nYour choice : ");
            choice=s.nextInt();

            switch(choice)
            {
            case 1:
                addTrain();
                break;

            case 2:
                System.out.print("\nLogging Out...\n");
                MainMenu(1);
                break;

            default:
                System.out.print("\nWrong choice");
            }
        }
	return 1;
    }

    private static int bookTickets(){
	try{
	Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/trainconsole","postgres","postgres");	   
	Statement stmt = con.createStatement();

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
	
	int avl=th.findAvailablity(trainNo,fromSt,toSt);

	if(avl!=Integer.MAX_VALUE)
	{
	    System.out.println("The available Tickets are : "+avl);

	    System.out.print("\nEnter the no. of tickets required : ");
	    ticketsReq=s.nextInt();
	    	    
	    if(avl>=ticketsReq){

	 	th.updateAndInsertToTable(ticketsReq,fromSt,toSt,trainNo);

		int tid=0;

		ResultSet rs = stmt.executeQuery("SELECT TICKET_ID FROM USERTICKETDETAILS ORDER BY TICKET_ID DESC LIMIT 1");
	
		if(rs.next()){
		    tid = rs.getInt("ticket_id");
		}	

		for(int i=0;i<ticketsReq;i++){
		    System.out.println("Passenger "+(i+1));
		    System.out.print("\nEnter name : ");
		    String name = s.next();
		    System.out.print("\nEnter age : ");
		    int age = s.nextInt();
		    th.updatePassengerDetails(tid,name,age);
	    	}

		    
		System.out.println("Ticket has been booked successfully");
		
		th.writeToPDFAndSendMail();

		return 1;
	    }
	    else{
		System.out.println(ticketsReq+" tickets are not available");
		return 0;
	    }
	}
	else{
	    System.out.println("No such trains or stations available");
	    return 0;
	}
	}catch(Exception e){
	    System.out.println("Exception is "+e);
	}	
	return 0;
    }

    private static void myTickets(){

    System.out.printf("\n%-15s %-15s %-15s %-15s %-15s","Ticket ID","Train No","From Station","To Station","No Of Tickets");
    try{	

	ResultSet rs=th.myTickets();

	while(rs.next()){

	    int tID=rs.getInt("ticket_id");
	    int trainID=rs.getInt("train_id");
	    String fromID=rs.getString("from_id");
	    String toID=rs.getString("to_id");
	    String noOfTkts=rs.getString("no_of_tickets");
	    String randKey=th.getKey();
	    
	    fromID=th.symDecry(fromID,Integer.parseInt(randKey));
	    toID=th.symDecry(toID,Integer.parseInt(randKey));
	    noOfTkts=th.symDecry(noOfTkts,Integer.parseInt(randKey));

	    int tNo=0;
	    String fromSt="",toSt="";

	    ResultSet rs1 = th.executingQuery("select train_no from traindetails where tid='"+trainID+"'");
	    if(rs1.next()){
		tNo=rs1.getInt("train_no");
	    }
	
	    rs1 = th.executingQuery("select station_name,departure_time from routedetails where rid='"+Integer.parseInt(fromID)+"';");
	    ResultSet rs2 = th.executingQuery("select station_name,arrival_time from routedetails where rid='"+Integer.parseInt(toID)+"';");
	   
	    if(rs1.next()){
		fromSt=rs1.getString("station_name");
	    }

	    if(rs2.next()){
		toSt=rs2.getString("station_name");
	    }
	    System.out.printf("\n%-15d %-15d %-15s %-15s %-15s",tID,tNo,fromSt,toSt,noOfTkts);

    	}
        }catch(Exception e){
	    System.out.println("Exception : "+e);
        }
    }
    private static int cancelTicket(){
	Scanner s = new Scanner(System.in);
	try{
	
	    ResultSet rs = th.cancelTicket();		
	    if(rs.next()){

		System.out.println("Enter the Ticket ID to cancel ticket : ");
		int tID=s.nextInt();

		ResultSet rs1 = th.passengerDetails(tID);
		if(!rs1.next()){
		    System.out.println("You have entered a invalid ticket id");
		    return 0;
		}

		int pID=0,age=0;
		String name="";

		rs1 = th.passengerDetails(tID);

		System.out.println("Passenger details are : ");
		System.out.printf("\n%-15s %-15s %-15s","Pass ID","Name","Age");

		while(rs1.next()){
		    pID = rs1.getInt("passengerid");		    
		    name = rs1.getString("name");
		    age = rs1.getInt("age");
		    System.out.printf("\n%-15d %-15s %-15d",pID,name,age);
		}

		int trainID=rs.getInt("train_id");
		String tkts=rs.getString("no_of_tickets");
		String fromID=rs.getString("from_id");
		String toID=rs.getString("to_id");

		System.out.println("\n1. Cancel the total ticket ");
		System.out.println("2. Cancel some passengers");
		System.out.println("Your choice : ");

		int ch = s.nextInt();
		int noOfPass = 0;

		if(ch==1){
		    th.updateAndDelete(tkts,fromID,toID,trainID,tID);
		    th.deleteAllPassengers(tID);
		    return 1;
		}

		else if(ch==2){

		    System.out.print("\nEnter the no. of passengers that you would like to cancel the ticket : ");
		    noOfPass = s.nextInt();
		    tkts=th.symDecry(tkts,Integer.parseInt(th.getKey()));
		    if(noOfPass<=Integer.parseInt(tkts)){

		    	for(int i = 0;i<noOfPass;i++){

		    	    System.out.println("Enter the passenger id that you would like to cancel : ");
			    int passid = s.nextInt();

			    th.updateAndDelete("1",fromID,toID,trainID,tID);
			    th.deletePassenger(passid);
		    	}				
		    }
		    else{
			System.out.println("You only have "+tkts+" tickets booked in this ticket id");
			System.out.println("So, your ticket cannot be cancelled.");
			return 0;
		    }

		    System.out.println("Your ticket has been cancelled successfully. ");
		    return 1;
		}
		else{
		    System.out.println("You have entered a wrong choice.");
		    return 0;
		}
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

    private static int subMenuPassenger()
    {
	Scanner s=new Scanner(System.in);
	
        System.out.print("\nWelcome Passenger");
        int choice=1;

        while(choice!=5)
        {
	    System.out.print("\n\n1. Display Train\n2. Book Tickets\n3. My Tickets\n4. Cancel Ticket\n5. Log Out");
            System.out.print("\nYour choice : ");
            choice=s.nextInt();

            switch(choice)
            {
            case 1:
                displayTrainDetails();
                break;

            case 2:
		bookTickets();
		break;

	    case 3:
		myTickets();
		break;

	    case 4:
		cancelTicket();
		break;

	    case 5:
                System.out.print("\nLogging Out...\n");
                MainMenu(1);
                break;

            default:
                System.out.print("\nWrong choice ");

            }
        }
	return 1;
    }    

    private static void signUp(){

	Scanner s=new Scanner(System.in);	
    	String username,password,email;

    	System.out.print("\nEnter the username : ");
    	username=s.nextLine();
    	System.out.print("\nEnter the password : ");
    	password=s.nextLine(); 
	System.out.print("\nEnter the mail id  : ");
    	email=s.nextLine();

	if(th.signUp(username,password,email)==1)
	    System.out.print("\nPassenger added successfully.");
	else
	    System.out.print("\nPassenger was not added.");
    }

    private static void login()
    {
	String uN,pW;
	Scanner s=new Scanner(System.in);

	System.out.print("\nEnter your username : ");
    	uN=s.nextLine();
    	System.out.print("\nEnter your password : ");
    	pW=s.nextLine();
	
	String roleofuser=th.userLoginAndReturnRole(uN,pW);
	
        if(roleofuser.equals("Admin"))
        {
	    subMenuAdmin();
        }
        else if(roleofuser.equals("Operator"))
        {
            subMenuOperator();
        }
        else if(roleofuser.equals("Passenger"))
        {
            subMenuPassenger();
        }
        else
        {
            System.out.print("\nSorry! Wrong username or password");
            System.out.print("\nTry Again");
        }	
    }

    private static int MainMenu(int choice){

	Scanner s=new Scanner(System.in);	
	
	while(choice!=3)
        {
            System.out.println("\n1. SignUp\n2. Login\n3. Exit");
	    System.out.print("\nYour choice : ");
            choice=s.nextInt();

	    switch(choice)
            {
	    case 1:
		signUp();
		break;

	    case 2:
                login();
                break;

            case 3:
		System.out.println("\n------------------------------------------");
		System.out.println("\nThanks for using the Train Console Application");
		System.out.println("\n------------------------------------------");
		System.exit(0);

            default:
                System.out.print("\nWrong Choice");
	        break;
	    }
        }
	return 1;
    }

    public static void main(String[] args) {

        System.out.println("\n------------------------------------------");
	System.out.println("\n   Welcome to Train Console Application ");
	System.out.println("\n------------------------------------------");

	try{

	    String jdbcURL = "jdbc:postgresql://localhost:5432/trainconsole";
	    String user="postgres";
	    String pass="postgres";
	    
	    Connection con = DriverManager.getConnection(jdbcURL,user,pass);
	   
	    Statement stmt = con.createStatement();

	    String sql = "CREATE TABLE IF NOT EXISTS USERLOGIN(USERID SERIAL PRIMARY KEY,USERNAME TEXT UNIQUE NOT NULL ,PASSWORD TEXT NOT NULL , MAIL_ID TEXT, ROLE TEXT NOT NULL , KEY TEXT NOT NULL);";
            stmt.executeUpdate(sql);

	    sql = "CREATE TABLE IF NOT EXISTS TRAINDETAILS(TID SERIAL PRIMARY KEY, TRAIN_NO INTEGER UNIQUE NOT NULL ,NO_OF_TICKETS INTEGER NOT NULL , TRAIN_NAME TEXT NOT NULL, SOURCE_STATION TEXT NOT NULL, DESTINATION_STATION TEXT NOT NULL, DEPARTURE_TIME TIME , ARRIVAL_TIME TIME );";
            stmt.executeUpdate(sql);

	    sql = "CREATE TABLE IF NOT EXISTS ROUTEDETAILS(RID SERIAL PRIMARY KEY,TRAIN_NO INTEGER NOT NULL ,STATION_NAME TEXT NOT NULL, ARRIVAL_TIME TIME, DEPARTURE_TIME TIME,AVLTKTS INTEGER ,CONSTRAINT FK FOREIGN KEY(TRAIN_NO) REFERENCES TRAINDETAILS(TRAIN_NO) ON DELETE CASCADE);";
            stmt.executeUpdate(sql);

	    sql = "CREATE TABLE IF NOT EXISTS USERTICKETDETAILS(TICKET_ID SERIAL PRIMARY KEY,USERID INTEGER,TRAIN_ID INTEGER,FROM_ID TEXT , TO_ID TEXT , NO_OF_TICKETS TEXT, TIME_OF_BOOKING TIMESTAMP,CONSTRAINT FK FOREIGN KEY(TRAIN_ID) REFERENCES TRAINDETAILS(TID) ON DELETE CASCADE,CONSTRAINT FK1 FOREIGN KEY(USERID) REFERENCES USERLOGIN(USERID) ON DELETE CASCADE);";
            stmt.executeUpdate(sql);

	    sql = "CREATE TABLE IF NOT EXISTS USERKEY(KEYID SERIAL PRIMARY KEY, MOD NUMERIC, PUB_EXP NUMERIC, PRIV_EXP NUMERIC);";
            stmt.executeUpdate(sql);

	    sql = "CREATE TABLE IF NOT EXISTS PASSENGERDETAILS(TICKETID INTEGER, PASSENGERID SERIAL PRIMARY KEY, NAME TEXT NOT NULL, AGE INTEGER NOT NULL, CONSTRAINT FK FOREIGN KEY(TICKETID) REFERENCES USERTICKETDETAILS(TICKET_ID) ON DELETE CASCADE);";
            stmt.executeUpdate(sql);
	
            stmt.close();
	    con.close();


	}catch(Exception e){
	    System.out.println("Exception raised is : "+e);
	}
	MainMenu(1);

    }
}



