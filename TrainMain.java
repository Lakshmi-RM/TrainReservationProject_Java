
import com.transport.train.TrainHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class TrainMain {
	
    static Connection con;
    static TrainHandler th=new TrainHandler(con);	

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
                th.addoperator();
        	break;
            case 2:
                th.displayTrainDetails();
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


    private static int subMenuPassenger()
    {
	Scanner s=new Scanner(System.in);
	
        System.out.print("\nWelcome Passenger");
        int choice=1;
        while(choice!=4)
        {
	    System.out.print("\n\n1. Display Train\n2. Book Tickets\n3. My Tickets\n4. Log Out");
            System.out.print("\nYour choice : ");
            choice=s.nextInt();
            switch(choice)
            {
            case 1:
                th.displayTrainDetails();
                break;
            case 2:
		th.bookTickets();
		break;
	    case 3:
		th.myTickets();
		break;
	    case 4:
                System.out.print("\nLogging Out...\n");
                MainMenu(1);
                break;
            default:
                System.out.print("\nWrong choice ");
            }
        }
	return 1;
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
                th.addTrain();
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

    private static int MainMenu(int choice){

	Scanner s=new Scanner(System.in);	
	
	while(choice!=3)
        {
            System.out.println("\n1. SignUp\n2. Login\n3. Exit");
	    System.out.print("\nYour choice : ");
            choice=s.nextInt();
	    String roleofuser=" ";
	    switch(choice)
            {
	    case 1:
		th.signUp();
		break;
            case 2:
                roleofuser=th.userLoginAndReturnRole();
		
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
	    String sql = "CREATE TABLE IF NOT EXISTS USERLOGIN(USERNAME TEXT PRIMARY KEY NOT NULL ,PASSWORD TEXT NOT NULL , ROLE TEXT NOT NULL);";
            stmt.executeUpdate(sql);
	    sql = "CREATE TABLE IF NOT EXISTS TRAINDETAILS(TRAIN_NO INTEGER PRIMARY KEY NOT NULL ,NO_OF_TICKETS INTEGER NOT NULL , TRAIN_NAME TEXT NOT NULL, SOURCE_STATION TEXT NOT NULL, DESTINATION_STATION TEXT NOT NULL, DEPARTURE_TIME TIME NOT NULL, ARRIVAL_TIME TIME NOT NULL);";
            stmt.executeUpdate(sql);
	    sql = "CREATE TABLE IF NOT EXISTS ROUTEDETAILS(ID SERIAL PRIMARY KEY,TRAIN_NO INTEGER NOT NULL ,STATION_NAME TEXT NOT NULL, ARRIVAL_TIME TIME, DEPARTURE_TIME TIME,AVLTKTS INTEGER ,CONSTRAINT FK FOREIGN KEY(TRAIN_NO) REFERENCES TRAINDETAILS(TRAIN_NO) ON DELETE CASCADE);";
            stmt.executeUpdate(sql);
	    sql = "CREATE TABLE IF NOT EXISTS USERTICKETDETAILS(USERNAME TEXT, TRAIN_NO INTEGER NOT NULL ,FROM_STATION TEXT NOT NULL, TO_STATION TEXT NOT NULL, NO_OF_TICKETS INTEGER NOT NULL, TIME_OF_BOOKING TIMESTAMP PRIMARY KEY,CONSTRAINT FK FOREIGN KEY(TRAIN_NO) REFERENCES TRAINDETAILS(TRAIN_NO) ON DELETE CASCADE,CONSTRAINT FK1 FOREIGN KEY(USERNAME) REFERENCES USERLOGIN(USERNAME) ON DELETE CASCADE);";
            stmt.executeUpdate(sql);
	    
            stmt.close();
	    con.close();
	}catch(Exception e){
	    System.out.println("1Exception raised is : "+e);
	}
	
	MainMenu(1);

    }
}