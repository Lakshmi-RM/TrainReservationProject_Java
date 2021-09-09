import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.channels.Channel;

public class TrainHandler{
    static String userLoginAndReturnRole()
    {
	Scanner s=new Scanner(System.in);	

	String username,password;
	System.out.print("\nEnter your username : ");
    	username=s.nextLine();
    	System.out.print("\nEnter your password : ");
    	password=s.nextLine();
	try{
	File f=new File("userlogin.txt");
	BufferedReader br=new BufferedReader(new FileReader(f));
	String line;
	while((line = br.readLine()) != null) {  
	    String words[] = line.split(" ");  	
	    if(words[0].equals(username))
		if(words[1].equals(password)){
		    br.close();
		    return words[2];}
	}
	}catch(Exception e){
	System.out.println("The Exception is "+e);}
    	return "1";   
    }

    static void addoperator()
    {
	Scanner s=new Scanner(System.in);	
    	String username,password;
    	System.out.print("\nEnter the username : ");
    	username=s.nextLine();
    	System.out.print("\nEnter the password : ");
    	password=s.nextLine();
    	try {
	    BufferedWriter bw=new BufferedWriter(new FileWriter("userlogin.txt",true));
	    String stringToBeWritten = System.lineSeparator()+username+" "+password+" Operator";
	    bw.write(stringToBeWritten);
	    System.out.print("\nOperator added successfully");
    	    bw.close();
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }    

    static void displayStationDetails(String trainNo){
	try{
	File f=new File("routedetails.txt");
	BufferedReader br=new BufferedReader(new FileReader(f));
	String line;
	while((line = br.readLine()) != null) { 
	    String words[] = line.split(" "); 
	    if(words[0].equals(trainNo)){	
	    	for(int i=1;i<words.length;i++)
		    System.out.printf("%s ",words[i]);
//		System.out.println(line);
		System.out.printf("\n%-100s "," ");
	    }
	}
	}catch(Exception e){
	    System.out.println("\nException : "+e);
	}
    }

    static void displayTrainDetails(){
	System.out.println("Train No        No Of Tickets   Train Name      Source          Destination     Departure Time  Arrival Time   Station Name    ArrivalTime     DestinationTime");	
	try{
	File f=new File("traindetails.txt");
	BufferedReader br=new BufferedReader(new FileReader(f));
	String line;
	while((line = br.readLine()) != null) { 
	    System.out.println();
	    System.out.print(line);
	    String words[] = line.split(" "); 
		/*System.out.println(); 	
	    for(int i=0;i<words.length;i++)
		System.out.printf("%-15s ",words[i]);
	     */
	    //System.out.printf("%-15s "," ");
	    displayStationDetails(words[0]);
        }
	}catch(Exception e){
	    System.out.println("\nException : "+e);
	}
    }

    static int addTrain(){
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

	try {
	    BufferedWriter bw=new BufferedWriter(new FileWriter("traindetails.txt",true));
	    String formatStr = "%-15s %-15s %-15s %-15s %-15s %-15s %s%n";
	    bw.write(String.format(formatStr, trainNo, noOfTickets, trainName, sourceStation, destinationStation, departureTime, arrivalTime));
	    	
	    /*String stringToBeWritten = System.lineSeparator()+trainNo+" "+noOfTickets+" "+trainName+" "+sourceStation+" "+destinationStation+" "+departureTime+" "+arrivalTime;
	    bw.write(stringToBeWritten);*/
    	    bw.close();
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}

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
	    try {
	    	BufferedWriter bw=new BufferedWriter(new FileWriter("routedetails.txt",true));		
		String formatStr = "%-15s %-15s %-15s %-15s%n";
		bw.write(String.format(formatStr, trainNo, stationName, arrTime, depTime));
	    	/*String stringToBeWritten = System.lineSeparator()+trainNo+" "+stationName+" "+arrTime+" "+depTime;
	    	bw.write(stringToBeWritten);*/
    	    	bw.close();
	    }catch(Exception e){
	    	System.out.println("Exception : "+e);
	    }
        }
	System.out.println("Train added successfully");
	return 1;	
	
    }

    static int signUp(){
	Scanner s=new Scanner(System.in);	
    	String username,password;
    	System.out.print("\nEnter the username : ");
    	username=s.nextLine();
    	System.out.print("\nEnter the password : ");
    	password=s.nextLine();
    	try {
	    BufferedWriter bw=new BufferedWriter(new FileWriter("userlogin.txt",true));
	    String stringToBeWritten = System.lineSeparator()+username+" "+password+" Passenger";
	    bw.write(stringToBeWritten);
	    System.out.print("\nPassenger added successfully");
    	    bw.close();
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return 1;
    }


}