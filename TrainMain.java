
import com.transport.train.TrainHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class TrainMain {
    private static int subMenuAdmin(){
	Scanner s=new Scanner(System.in);	
	TrainHandler th=new TrainHandler();	
	
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
	TrainHandler th=new TrainHandler();	
	
        System.out.print("\nWelcome Passenger");
        int choice=1;
        while(choice==1)
        {
	    System.out.print("\n1. Display Train\n2. Log Out");
            System.out.print("\nYour choice : ");
            choice=s.nextInt();
            switch(choice)
            {
            case 1:
                th.displayTrainDetails();
                break;
            case 2:
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
	TrainHandler th=new TrainHandler();	
	
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
	TrainHandler th=new TrainHandler();	
	
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
	    String jdbcURL = "jdbc:postgresql://localhost:5432/task";
	    String user="postgres";
	    String pass="postgres";
	    Connection con = DriverManager.getConnection(jdbcURL,user,pass);
	   
	    Statement stmt = con.createStatement();
	    String sql = "CREATE TABLE IF NOT EXISTS USERLOGIN(USERNAME TEXT PRIMARY KEY NOT NULL ,PASSWORD TEXT NOT NULL , ROLE TEXT NOT NULL)";
            stmt.executeUpdate(sql);
	    sql = "CREATE TABLE IF NOT EXISTS TRAINDETAILS(TRAIN_NO INTEGER PRIMARY KEY NOT NULL ,NO_OF_TICKETS INTEGER NOT NULL , TRAIN_NAME TEXT NOT NULL, SOURCE_STATION TEXT NOT NULL, DESTINATION_STATION TEXT NOT NULL, DEPARTURE_TIME TIME NOT NULL, ARRIVAL_TIME TIME NOT NULL)";
            stmt.executeUpdate(sql);
	    sql = "CREATE TABLE IF NOT EXISTS ROUTEDETAILS(TRAIN_NO INTEGER NOT NULL ,STATION_NAME TEXT NOT NULL, ARRIVAL_TIME TIME NOT NULL, DEPARTURE_TIME TIME NOT NULL,CONSTRAINT FK FOREIGN KEY(TRAIN_NO) REFERENCES TRAINDETAILS(TRAIN_NO))";
            stmt.executeUpdate(sql);
            stmt.close();
	    con.close();
	}catch(Exception e){
	    System.out.println("Exception raised is : "+e);
	}
	
	MainMenu(1);

    }
}