# Project Title: Train Reservation system Console in Java

This project is divided into two parts. One is implemented only using Java and it is executed in Command prompt. And the other part involves creating a console as a localhost website. 

Both parts mainly focusses on creating a user console for train reservation system. This project involves terminal based ticket console in Java which handles the multi user authentication for administrators, operators and passengers by storing all the information including login credentials, train information, booking information, etc. using a PostgreSQL database. 

## Features:
1. Multi user authentication system
* Administrators: Can login and then add operator's username and password into the database and can view the trains added by the operators
* Operators: Can login and then add train details
* Passengers: Can sign up, login, view train details added by the operators, book tickets, view their booked tickets, cancel tickets. 
* All these user credentials and their roles are stored in a table named 'userlogin' in the 'trainconsole' database

2. Train Information 
* Users with role "Operator" can only add train details into the system and the admin and passenger can only view them.
* The train details are Train Number, Number of tickets in train, Train Name, Source Station, Destination Station, Departure Time, Arrival Time, No. of stations (only up to 10 stations can be added), and each station should contain Station Name, Arrival Time, and Departure Time at the station.
* All these train details gets stored and retrieved using the table 'traindetails' in the database 'trainconsole' 
* All the route details gets stored and retrieved using the table 'routedetails' in the database 'trainconsole' where each station is paired with the train number. 

## Part 1 - Working with Java console  
Just concentrate with the files available under src/main/java folder

### Built With 
* Language: Java
* Compiler: JDK on Windows
* IDE used: Windows Command Prompt

### Getting Started
Follow the below steps to set up and run this project locally on your machine.

#### Pre-requisites
Please make sure you have a Java JDK (version 8 or 11 recommended) installed in your machine.
Download PostgreSQL (any recent version), remember the password that you use while creating.
And check if they are working by opening command prompt on Windows and typing java -version and psql -version.
Download the below required JAR files (added with code, but if better versions are available, you can download it)
* PostgreSQL JDBC      - postgresql-42.7.11.jar
* org.json             - json-20240303.jar
* iText PDF            - itextpdf-5.5.13.3.jar
* JavaMail             - javax.mail-1.6.2.jar
* JavaBeans Activation - javax.activation-1.2.0.jar


#### Setup - Java Code
* Download the code to your local repository
* Have the Java code, and the jar files in the java/ folder
* Update the sample email id and App Password (not your regular Gmail password — generate one at myaccount.google.com → Security → App Passwords) of your email in the SendEmail.java file

#### Setup - PostgreSQL
* Open pgAdmin4 using start menu, using the password you used for installing PostgreSQL, you get connected. 
* Once connected, click on the query tool button available in the Object Explorer menu on the right.
* Create a new database: 
	CREATE DATABASE trainconsole;
* Update the Postgres credentials in the 3 Java files in the beginning of the class

#### Run Java Code - Command Prompt
* First we have to create class file for all java files along with the jar files using the following command
 	javac -cp ".;itextpdf-5.5.13.3.jar;javax.activation-1.2.0.jar;javax.mail-1.6.2.jar;json-20240303.jar;postgresql-42.7.11.jar" TrainHandler.java DatabaseHandler.java SendEmail.java TrainMain.java
* Next run the created TrainMain class file using the following command
	java -cp ".;itextpdf-5.5.13.3.jar;javax.activation-1.2.0.jar;javax.mail-1.6.2.jar;json-20240303.jar;postgresql-42.7.11.jar" TrainMain

#### Code Run For First Time:
* When the program is run, type 1 to signup.
* Create a user, the user will be marked as Passenger
* Go back to PostgreSQL and edit the role to Admin, so next time that id will be used as admin
* To create a operator, login as admin and go to Add Operator
* To create a passenger, just go into sign up, and create one.

## Part 2 - Working with Web Application  
Just concentrate with the files available under src/main/webapp folder

### Built With 
* Language  : Java Servlet Pages (JSP)
* Database  : PostgreSQL
* Web Server: Apache Tomcat
* Compiler  : JDK on Windows
* IDE used  : Windows Command Prompt or VS code to build war files and run Tomcat server

### Getting Started
Follow the below steps to set up and run this project locally on your machine.

#### Pre-requisites
Please make sure you have a Java JDK (version 8 or 11 recommended) installed in your machine.
Download PostgreSQL (any recent version), remember the password that you use while creating (I have used 12345 as passowrd, if you wish not to make many changes in code, try to keep it to 12345), it should run on port localhost:5432.
Download Apache Tomcat (any version) and make sure it is present in a easily accessible folder.
And check if they are working by opening command prompt on Windows and typing java -version and psql -version.
Download the below required JAR files and store them at src\main\webapp\WEB-INF\lib (added with code, but if better versions are available, you can download it)
* PostgreSQL JDBC      - postgresql-42.7.11.jar
* org.json             - json-20240303.jar
* iText PDF            - itextpdf-5.5.13.3.jar
* JavaMail             - javax.mail-1.6.2.jar
* JavaBeans Activation - javax.activation-1.2.0.jar

#### Setup - JSP Code
* Download the code to your local repository
* Have the JSP code in src\main\webapp folder, and the jar files in the src\main\webapp\WEB-INF\lib folder
* Update the sample email id and App Password (not your regular Gmail password — generate one at myaccount.google.com → Security → App Passwords) of your email in the src/main/webapp/WEB-INF/web.xml file

#### Setup - PostgreSQL
* Open pgAdmin4 using start menu, using the password you used for installing PostgreSQL, you get connected. 
* Once connected, click on the query tool button available in the Object Explorer menu on the right.
* Create a new database: 
	CREATE DATABASE trainconsole;
* Create the tables in the database trainconsole by pasting the below queries in the query tool one by one 
	** CREATE TABLE userlogin (userid SERIAL PRIMARY KEY, username VARCHAR(100) UNIQUE NOT NULL, password VARCHAR(255), mail_id VARCHAR(100), role VARCHAR(50));
	** CREATE TABLE traindetails (tid SERIAL PRIMARY KEY, train_no INTEGER, no_of_tickets INTEGER, train_name VARCHAR(100), source_station VARCHAR(100), destination_station VARCHAR(100), departure_time VARCHAR(50), arrival_time VARCHAR(50));
	** CREATE TABLE routedetails (rid SERIAL PRIMARY KEY, train_no INTEGER, station_name VARCHAR(100), arrival_time VARCHAR(50), departure_time VARCHAR(50), avltkts INTEGER);
	** CREATE TABLE userticketdetails (ticket_id SERIAL PRIMARY KEY, userid INTEGER, trainno VARCHAR(20), fromst VARCHAR(100), tost VARCHAR(100), no_of_tickets INTEGER, time_of_booking VARCHAR(100));
	** CREATE TABLE usersotp (username VARCHAR(100), otp VARCHAR(10));
* After creating the tables, check if they are created.
* Create the admin user alone prior starting to run the code using the following (use a valid email that you have access to, so that you receive the email for otp verification)
	INSERT INTO userlogin (username, password, mail_id, role) VALUES ('<admin_username>', '<admin_password>', '<youremail@gmail.com>', 'admin');
* Update the Postgres credentials in the all the JSP files. If you wish not change it use 12345 while creating user in Postgres (default password I have used)

#### Run JSP Code and Tomcat - Visual Studio Code 
* First we have to create war file including all the war files using the following command. Project Location is where the project files reside (ie. if the project is stored in C:/Project/Train/src/main/webapp is your file location, then use C:/Project/Train as Project Location) and Apacahe Tomcat Download Location is where the tomcat folder was unzipped.
 	cp "<Project_Location>/trainapp.war" \ "<ApacheTomcat_DownloadLocation>/webapps/"
* Next start the Tomcat using the following command
	"<ApacheTomcat_DownloadLocation>/bin/startup.bat"
While performing this command, new window opens up and wait for some time to see the last line in the window appears to be 
	Server startup in [XXXX] milliseconds
* Access the app by opening any browser and moving to http://localhost:8080/trainapp/

#### Tomcat Configuration File Changes
The following changes has to be made in the files present under <ApacheTomcat_DownloadLocation>/conf files
##### conf/ tomcat-user.xml
This file defines the roles that will be used by the app, replace the file with below contents

<?xml version="1.0" encoding="UTF-8"?>
<tomcat-users xmlns="http://tomcat.apache.org/xml"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd"
              version="1.0">

  <role rolename="admin"/>
  <role rolename="operator"/>
  <role rolename="passenger"/>

  <user username="admin" password="admin123" roles="admin"/>

</tomcat-users>

##### conf/server.xml
Tomcat must be able to authenticate the users based on the data available in Postgres table, so find the <Enginename="Catalina"> tag and replace the <Realm> block inside it with the below lines

<Realm className="org.apache.catalina.realm.LockOutRealm">
  <Realm className="org.apache.catalina.realm.JDBCRealm"
         driverName="org.postgresql.Driver"
         connectionURL="jdbc:postgresql://localhost:5432/trainconsole"
         connectionName="postgres"
         connectionPassword="12345"
         userTable="userlogin"
         userNameCol="username"
         userCredCol="password"
         userRoleTable="userlogin"
         roleNameCol="role"/>
</Realm>

#### Code Run For First Time:
* When you run the code for first time, add a passenger using Signup button available on the login screen.
* You can login as admin with the login details added in userlogin table using insert command. Post which you can add operators as a admin.
* The login flows from entering credentials, click send otp button, check otp in mail (if valid mail credentials are given) or check in database in usersotp table, then enter the otp and click submit, you will be redirected to the menu available for that particular role.
* The flow continues according to which user you login as mentioned earlier.
* After you complete the run, when you wish to close, either close the Tomcat window opened earlier or just type below command in VS code, 
	"<ApacheTomcat_DownloadLocation>/bin/shutdown.bat"

#### Rebuilding if you have changes
* If you make any changes to the jsp files, and want to update the changes to war before running th app for the changes to get effective, run the following commands
	cp -r "<Project_Location>/src/main/webapp/"* \ "<Project_Location>/build/webapp/"

	cd "<Project_Location>/build/webapp" jar -cvf "<Project_Location>/trainapp.war" .

	rm -rf "<ApacheTomcat_DownloadLocation>/webapps/trainapp"
	rm "<ApacheTomcat_DownloadLocation>/webapps/trainapp.war"
	cp "<Project_Location>/trainapp.war" \ "<ApacheTomcat_DownloadLocation>/webapps/"

## License
Distributed under the MIT License. See LICENSE for more information.
