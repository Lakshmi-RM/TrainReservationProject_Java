package com.transport.train;

//DATABASE CONNECTIONS AND RESULTS
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

//RANDOM
import java.util.Random;

//PDF FILE ERRORS HANDLERS AND ITEXT
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import com.itextpdf.text.Document;  
import com.itextpdf.text.DocumentException;  
import com.itextpdf.text.Paragraph;  
import com.itextpdf.text.pdf.PdfWriter;  

//SYMMETRIC ENCRYPTION & DECRYPTION
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

//HASHING
import java.security.MessageDigest;

//ASSYMMETRIC ENCRYPTION & DECRYPTION
import java.security.KeyPair;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

//BigInteger and BigDecimal
import java.math.BigInteger;
import java.math.BigDecimal;

//Linked List Collection
import java.util.LinkedList;

//Local Time
import java.time.LocalDateTime;

public class TrainHandler{

    static Connection con;
    static int userID;
    static String userKey;

    //Symmetric Encryption
    static byte[] input;
    static byte[] keyBytes;
    static byte[] ivBytes = "input123".getBytes();
    static IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
    static Cipher cipher;
    static byte[] cipherText;
    static int ctLength; 

    //Assymetric Encryption 
    private static final String RSA = "RSA";
    private static KeyPairGenerator keyPairGenerator;
    private static KeyPair keyPair;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    static{
	try{
	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    int rows = Integer.MAX_VALUE;
	    
	    ResultSet rs = stmt.executeQuery( "SELECT COUNT(*) AS ROWS FROM USERKEY;" );

	    if(rs.next()){
		rows = rs.getInt("rows");	
	    }
	    if(rows == 0){
	    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
 	    kpg.initialize(2048);

 	    KeyPair kp = kpg.genKeyPair();

 	    KeyFactory fact = KeyFactory.getInstance("RSA");

 	    RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),RSAPublicKeySpec.class);
	    RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),RSAPrivateKeySpec.class);

	    String sql = "INSERT INTO USERKEY(mod,pub_exp,priv_exp) VALUES('"+pub.getModulus()+"','"+pub.getPublicExponent()+"','"+priv.getPrivateExponent()+"');";
	    stmt.executeUpdate(sql);
	
	    con.close();
	    }

	    publicKey = readPublicKey();	
	    privateKey = readPrivateKey();
	    con.close();

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
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

    public void setKey(String key){
	this.userKey=key;
    }

    public String getKey(){
	return this.userKey;
    }

    public static Connection getConnection(){

	con=null;
	try{	

	    con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/trainconsole","postgres","postgres");
	    return con;

	}catch(Exception e){
	    System.out.println("The Exception is "+e);
	}

	return con;
    }

    

    public ResultSet executingQuery(String sql){
	ResultSet rs=null;
	Statement stmt=null;
	try{
	    Connection con = getConnection();
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(sql);

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return rs;
    }

    private static PublicKey readPublicKey(){
	PublicKey pubKey = null;
	try {

	Connection con = getConnection();
	Statement stmt = con.createStatement();
	    
	ResultSet rs = stmt.executeQuery( "SELECT MOD,PUB_EXP FROM USERKEY WHERE KEYID=1;" );
	    
	if(rs.next()){
	    BigDecimal d = rs.getBigDecimal("mod");
            BigInteger m = d.toBigInteger();

            BigDecimal d1 = rs.getBigDecimal("pub_exp");
            BigInteger e = d1.toBigInteger();            

	    KeyFactory fact = KeyFactory.getInstance("RSA");
            
	    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
	    pubKey = fact.generatePublic(keySpec);
	}
	con.close();
        return pubKey;

    	} catch (Exception e) {
            System.out.println("Exception : "+e);
    	} 
	return pubKey;
    }

    private static PrivateKey readPrivateKey(){
	PrivateKey priKey = null;
	try {
        Connection con = getConnection();
	Statement stmt = con.createStatement();
	    
	ResultSet rs = stmt.executeQuery( "SELECT MOD,PRIV_EXP FROM USERKEY WHERE KEYID=1;" );
	    
	if(rs.next()){
            BigDecimal d = rs.getBigDecimal("mod");
            BigInteger m = d.toBigInteger();

            BigDecimal d1 = rs.getBigDecimal("priv_exp");
            BigInteger e = d1.toBigInteger();
            
	    KeyFactory fact = KeyFactory.getInstance("RSA");
            
	    RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
	    priKey = fact.generatePrivate(keySpec);
	}
	    con.close();
            return priKey;

    	} catch (Exception e) {
            System.out.println("Exception : "+e);
    	} 
	return priKey;
    }

    public int signUp(String username,String password,String email){
	
	try{
	    Random r = new Random();
	    int randKey = r.nextInt(100000000);
	    while(randKey<10000000)
	    	randKey*=10;

	    byte[] symKey = assEncry(Integer.toString(randKey));

	    String pwd = doHash(password);

	    LinkedList<String> tableValues = new LinkedList<String>();
	    tableValues.add("userlogin");
	    tableValues.add("username");
	    tableValues.add(username);	     
	    tableValues.add("password");
	    tableValues.add(pwd);
	    tableValues.add("mail_id");
	    tableValues.add(email);
	    tableValues.add("role");
	    tableValues.add("Passenger");
	    tableValues.add("key");
	    tableValues.add(new String(DatatypeConverter.printHexBinary(symKey)));

	    tableValues = DatabaseHandler.insertIntoDB(tableValues);

	    return 1;
	
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return 0;
    }

    public String userLoginAndReturnRole(String uN,String pW)
    {

	try{
	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    String pass=doHash(pW);
	    ResultSet rs = stmt.executeQuery( "SELECT ROLE,USERID,KEY FROM USERLOGIN WHERE USERNAME='"+uN+"' AND PASSWORD='"+pass+"';" );
	    String role="Invalid";
	    
	    if(rs.next()){

		int ID = rs.getInt("USERID");
		setUID(ID);
	    	role = rs.getString("ROLE");
		String key = rs.getString("KEY");

		byte[] val = new byte[key.length() / 2];
      		for (int i = 0; i < val.length; i++) {
         	    int index = i * 2;
         	    int j = Integer.parseInt(key.substring(index, index + 2), 16);
         	    val[i] = (byte) j;
      		}
      
		setKey(assDecry(val));

	    }
	    con.close();
            return role;

	}catch(Exception e){
	    System.out.println("The Exceeption is "+e);
	}

    	return "Invalid";   
    }

    public int addoperator(String username, String password)
    {
	try{
	    Random r = new Random();
	    int randKey = r.nextInt(100000000);
	    while(randKey<10000000)
	    	randKey*=10;

	    byte[] symKey = assEncry(Integer.toString(randKey));

	    String pwd = doHash(password);
	    
	    LinkedList<String> tableValues = new LinkedList<String>();
	    tableValues.add("userlogin");
	    tableValues.add("username");
	    tableValues.add(username);	     
	    tableValues.add("password");
	    tableValues.add(pwd);
	    tableValues.add("role");
	    tableValues.add("Operator");
	    tableValues.add("key");
	    tableValues.add(new String(DatatypeConverter.printHexBinary(symKey)));

	    tableValues = DatabaseHandler.insertIntoDB(tableValues);

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

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
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

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    rs = stmt.executeQuery( "SELECT * FROM TRAINDETAILS;" );

	    return rs;

	}catch(Exception e){
	    System.out.println("\nException : "+e);
	}
	return rs;
    }

    public void addTrain(String trainNo,String noOfTickets,String trainName,String sourceStation,String destinationStation,String departureTime,String arrivalTime){
        
	try{

	    LinkedList<String> tableValues = new LinkedList<String>();

	    //Insert into traindetails table
	    tableValues.add("traindetails");
	    tableValues.add("train_no");
	    tableValues.add(trainNo);	     
	    tableValues.add("no_of_tickets");
	    tableValues.add(noOfTickets);
	    tableValues.add("train_name");
	    tableValues.add(trainName);
	    tableValues.add("source_station");
	    tableValues.add(sourceStation);
	    tableValues.add("destination_station");
	    tableValues.add(destinationStation);
	    tableValues.add("departure_time");
	    tableValues.add(departureTime);
	    tableValues.add("arrival_time");
	    tableValues.add(arrivalTime);

	    tableValues = DatabaseHandler.insertIntoDB(tableValues);

	    LinkedList<String> tableValues1 = new LinkedList<String>();
	    //Insert into routedetails table
	    tableValues1.add("routedetails");
	    tableValues1.add("train_no");
	    tableValues1.add(trainNo);	
	    tableValues1.add("station_name");
	    tableValues1.add(sourceStation);     
	    tableValues1.add("avltkts");
	    tableValues1.add(noOfTickets);
	    tableValues1.add("departure_time");
	    tableValues1.add(departureTime);

	    tableValues1 = DatabaseHandler.insertIntoDB(tableValues1);
	    		
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}	
    }

    public void addRoute(String trainNo,String stationName,String arrTime,String depTime,String noOfTickets){
	
	LinkedList<String> tableValues = new LinkedList<String>();
	
	    //Insert into routedetails table
	    tableValues.add("routedetails");
	    tableValues.add("train_no");
	    tableValues.add(trainNo);	
	    tableValues.add("station_name");
	    tableValues.add(stationName); 
	    tableValues.add("arrival_time");
	    tableValues.add(arrTime); 
	    tableValues.add("departure_time");
	    tableValues.add(depTime);    
	    tableValues.add("avltkts");
	    tableValues.add(noOfTickets);

	    tableValues = DatabaseHandler.insertIntoDB(tableValues);

    }

    public int addDestination(String trainNo,String destinationStation,String noOfTickets,String arrivalTime){

	    LinkedList<String> tableValues = new LinkedList<String>();

	    //Insert into routedetails table
	    tableValues.add("routedetails");
	    tableValues.add("train_no");
	    tableValues.add(trainNo);	
	    tableValues.add("station_name");
	    tableValues.add(destinationStation);     
	    tableValues.add("avltkts");
	    tableValues.add(noOfTickets);
	    tableValues.add("arrival_time");
	    tableValues.add(arrivalTime);

	    tableValues = DatabaseHandler.insertIntoDB(tableValues);

	return 1;
    }

    public int findAvailablity(int trainNo,String fromSt,String toSt){
	
	try{
	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	    
	    ResultSet rs = stmt.executeQuery("select min(avltkts) as avl from routedetails where rid>=(select rid from routedetails where station_name='"+fromSt+"' and train_no='"+trainNo+"') and rid<(select rid from routedetails where station_name='"+toSt+"' and train_no='"+trainNo+"');");
	    if(rs.next()){

		int avl=rs.getInt("avl");

		con.close();
		return avl;
	    } 	    

 	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}

	return Integer.MAX_VALUE;
    }    

    public int updateAndInsertToTable(int ticketsReq,String fromSt,String toSt,int trainNo){

	try{	
	
	Connection con = getConnection();
	Statement stmt = con.createStatement();
	
	int tid=0;
	String fromID="",toID="",tktsReq="";
	    
	ResultSet rs=stmt.executeQuery("select tid from traindetails where train_no='"+trainNo+"';");

	if(rs.next()){
	    tid=rs.getInt("tid");
	}

	ResultSet rs1=stmt.executeQuery("select rid from routedetails where train_no='"+trainNo+"' and station_name='"+fromSt+"';");
	
	if(rs1.next()){
	    fromID=Integer.toString(rs1.getInt("rid"));
	}

	ResultSet rs2=stmt.executeQuery("select rid from routedetails where train_no='"+trainNo+"' and station_name='"+toSt+"';");
	
	if(rs2.next()){
	    toID=Integer.toString(rs2.getInt("rid"));
	}

	fromID=symEncry(fromID,Integer.parseInt(getKey()));
	toID=symEncry(toID,Integer.parseInt(getKey()));
	tktsReq=symEncry(Integer.toString(ticketsReq),Integer.parseInt(getKey()));

	LinkedList<String> tableValues = new LinkedList<String>();
	
	    //Insert into userticketdetails table
	    tableValues.add("userticketdetails");
	    tableValues.add("userid");
	    tableValues.add(getUID());	
	    tableValues.add("train_id");
	    tableValues.add(Integer.toString(tid)); 
	    tableValues.add("from_id");
	    tableValues.add(fromID); 
	    tableValues.add("to_id");
	    tableValues.add(toID);    
	    tableValues.add("no_of_tickets");
	    tableValues.add(tktsReq);
	    tableValues.add("time_of_booking");
	    tableValues.add(LocalDateTime.now().toString());
	    
	    tableValues = DatabaseHandler.insertIntoDB(tableValues);

	int rows = stmt.executeUpdate("update routedetails set avltkts=avltkts-'"+ticketsReq+"' where rid>=(select rid from routedetails where station_name='"+fromSt+"' and train_no='"+trainNo+"') and rid<(select rid from routedetails where station_name='"+toSt+"' and train_no='"+trainNo+"');");	
	con.close();

        }catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return 1;
    }

    public int updatePassengerDetails(int tid,String name,int age)
    {

	LinkedList<String> tableValues = new LinkedList<String>();
	
	    //Insert into passengerdetails table
	    tableValues.add("passengerdetails");
	    tableValues.add("ticketidid");
	    tableValues.add(Integer.toString(tid));	
	    tableValues.add("name");
	    tableValues.add(name); 
	    tableValues.add("age");
	    tableValues.add(Integer.toString(age));
	    
	    tableValues = DatabaseHandler.insertIntoDB(tableValues);

	return 1;
    }

    public void writeToPDFAndSendMail()  
    {  
	try  
	{  
	    ResultSet rs=null,rs1=null,rs2=null;
	    String uID = getUID();
	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    Statement stmt1 = con.createStatement();
	    Statement stmt2 = con.createStatement();
	    Statement stmt3 = con.createStatement();
	    Statement stmt4 = con.createStatement();
	    
	    rs = stmt.executeQuery("SELECT * FROM USERTICKETDETAILS WHERE USERID='"+uID+"' order by time_of_booking desc limit 1;");

	    int tID=0,trainID=0,tNo=0;
	    String fromID="",toID="",noOfTkts="";
	    String time="",uN="",email="",tName="",fromSt="",toSt="",fromTime="",toTime="";

	    if(rs.next()){
 		tID=rs.getInt("ticket_id");
	    	trainID=rs.getInt("train_id");
	    	fromID=rs.getString("from_id");
	    	toID=rs.getString("to_id");
	    	noOfTkts=rs.getString("no_of_tickets");
		time=rs.getString("time_of_booking");

		fromID=symDecry(fromID,Integer.parseInt(getKey()));
	    	toID=symDecry(toID,Integer.parseInt(getKey()));
	    	noOfTkts=symDecry(noOfTkts,Integer.parseInt(getKey()));

	    }

	    rs1 = stmt1.executeQuery("SELECT USERNAME,MAIL_ID FROM USERLOGIN WHERE USERID='"+uID+"';");
	    
	    if(rs1.next()){
		uN=rs1.getString("username");
		email = rs1.getString("mail_id");
	    }

	    rs2 = stmt2.executeQuery("select train_no,train_name from traindetails where tid='"+trainID+"'");

	    if(rs2.next()){
		tNo=rs2.getInt("train_no");
		tName=rs2.getString("train_name");
	    }  

	    rs1 = stmt3.executeQuery("select station_name,departure_time from routedetails where rid='"+Integer.parseInt(fromID)+"';");
	    rs2 = stmt4.executeQuery("select station_name,arrival_time from routedetails where rid='"+Integer.parseInt(toID)+"';");
	
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

	    doc.add(new Paragraph("Passenger Details : "));
	    doc.add(new Paragraph(""));

	    int passno = 1;
	    rs1 = stmt1.executeQuery("select * from passengerdetails where ticketid = '"+tID+"';");
	    while(rs1.next()){
		String name = rs1.getString("name");
		int age = rs1.getInt("age");
		doc.add(new Paragraph("Passenger "+passno));
		doc.add(new Paragraph("Name : "+name));
		doc.add(new Paragraph("Age  : "+age));
		passno++;
	    }

	    doc.close();  
	    writer.close(); 
	    SendEmail.sendMail(email,f);
	    con.close();

	}catch (Exception e)  {  
	    System.out.println("Exception : "+e);  
	}     
    } 

    public ResultSet myTickets()
    {
	ResultSet rs=null;
	String uID = getUID();
	try{

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
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

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    rs = stmt.executeQuery("SELECT * FROM USERTICKETDETAILS WHERE USERID='"+uID+"';");
	    return rs;

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return rs;

    }

    public ResultSet passengerDetails(int tid)
    {
	ResultSet rs=null;
	try{

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    rs = stmt.executeQuery("SELECT * FROM PASSENGERDETAILS WHERE TICKETID='"+tid+"';");
	   
	    return rs;

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return rs;
    }

    public void updateAndDelete(String tkts,String fromID,String toID,int trainID,int tID){
	try{

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    
	    int tNo=0;
	    String from="",to="";

	    fromID=symDecry(fromID,Integer.parseInt(getKey()));
	    toID=symDecry(toID,Integer.parseInt(getKey()));
	    if(!tkts.equals("1"))
	    	tkts=symDecry(tkts,Integer.parseInt(getKey()));

	    ResultSet rs = stmt.executeQuery("select train_no from traindetails where tid='"+trainID+"'");
	    if(rs.next()){
		tNo=rs.getInt("train_no");
	    }

	    rs = stmt.executeQuery("select station_name,departure_time from routedetails where rid='"+Integer.parseInt(fromID)+"';");
	    
	    if(rs.next()){
		from=rs.getString("station_name");
	    }

	    rs = stmt.executeQuery("select station_name,arrival_time from routedetails where rid='"+Integer.parseInt(toID)+"';");

	    if(rs.next()){
		to=rs.getString("station_name");
	    }

	    int rows = stmt.executeUpdate("update routedetails set avltkts=avltkts+'"+Integer.parseInt(tkts)+"' where rid>=(select rid from routedetails where station_name='"+from+"' and train_no='"+tNo+"') and rid<(select rid from routedetails where station_name='"+to+"' and train_no='"+tNo+"');");	    

	    con.close();

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

    public void deleteAllPassengers(int tID)
    {
	try{

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    int rows = stmt.executeUpdate("DELETE FROM USERTICKETDETAILS WHERE TICKET_ID='"+tID+"';");		
	    rows = stmt.executeUpdate("DELETE FROM PASSENGERDETAILS WHERE TICKETID='"+tID+"';");			
	    con.close();

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

    public void deletePassenger(int passid)
    {
	try{

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    
	    int tickid=0;
	    String tkts="";

	    ResultSet rs = stmt.executeQuery("SELECT TICKETID FROM PASSENGERDETAILS WHERE PASSENGERID='"+passid+"';");

	    while(rs.next()){
		tickid = rs.getInt("ticketid");
	    }	    

	    rs = stmt.executeQuery("SELECT NO_OF_TICKETS FROM USERTICKETDETAILS WHERE TICKET_ID='"+tickid+"';");
	    int tktno = 100;
	    while(rs.next()){

		tkts = rs.getString("no_of_tickets");
		tkts = symDecry(tkts,Integer.parseInt(getKey()));

		tkts = Integer.toString(Integer.parseInt(tkts)-1);
		tktno = Integer.parseInt(tkts);

		tkts = symEncry(tkts,Integer.parseInt(getKey()));

	    }

	    int rows = stmt.executeUpdate("DELETE FROM PASSENGERDETAILS WHERE PASSENGERID='"+passid+"';");			
	    if(tktno!=0)	    	
	    	rows = stmt.executeUpdate("update userticketdetails set no_of_tickets='"+tkts+"' where ticket_id='"+tickid+"';");   
	    else
		rows = stmt.executeUpdate("delete from userticketdetails where ticket_id = '"+tickid+"';");
	
	    con.close();

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
    }

    public String doHash(String pwd){
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

    private String symEncry(String textToBeEncrypted,int randKey){
	try{
	    String s=Integer.toString(randKey);
	    keyBytes = s.getBytes();

	    SecretKeySpec key = new SecretKeySpec(keyBytes,"DES");

	    byte[] cleartext = textToBeEncrypted.getBytes("UTF8");      
	    Cipher cipher = Cipher.getInstance("DES");
 
	    cipher.init(Cipher.ENCRYPT_MODE, key);

	    String encypedPwd = Base64.getEncoder().encodeToString(cipher.doFinal(cleartext));

	    return encypedPwd;

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}    

	return " ";
    }


    public String symDecry(String textToBeDecrypted,int randKey){
	try{
	    String s=Integer.toString(randKey);
	    keyBytes = s.getBytes();

	    SecretKeySpec key = new SecretKeySpec(keyBytes,"DES");

	    byte[] encrypedPwdBytes = Base64.getDecoder().decode(textToBeDecrypted);

	    Cipher cipher = Cipher.getInstance("DES");
	    cipher.init(Cipher.DECRYPT_MODE, key);
	    byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));

	    return new String(plainTextPwdBytes);

	}catch(Exception e){
	    System.out.println("eException : "+e);
	}
	return " ";
    }

    public byte[] assEncry(String plainText)throws Exception
    {
	Cipher cipher = Cipher.getInstance(RSA);
	cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	byte[] a = cipher.doFinal(plainText.getBytes());
	return a;
    }

    public String assDecry(byte[] cipherText)throws Exception
    {
	Cipher cipher = Cipher.getInstance(RSA);
	cipher.init(Cipher.DECRYPT_MODE,privateKey);
	return new String(cipher.doFinal(cipherText));
    }

}