
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
import java.util.HexFormat;
import java.security.Key;

//BigInteger and BigDecimal
import java.math.BigInteger;
import java.math.BigDecimal;

//Linked List Collection
import java.util.LinkedList;

//Local Time
import java.time.LocalDateTime;

//JSON array and objects
import org.json.JSONArray;
import org.json.JSONObject;

public class TrainHandler{

	static String Postgrespassword="12345";
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
	    
	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("userkey");
	    
	    columnName.add("count(*)");

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    
	    
	    int i=0;
	    while(i<json.length()){
		JSONObject obj = json.getJSONObject(i);
		rows = obj.getInt("count"); 

		System.out.println(rows);
		i++;   
	    }

	    if(rows == 0){
	    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
 	    kpg.initialize(2048);

 	    KeyPair kp = kpg.genKeyPair();

 	    KeyFactory fact = KeyFactory.getInstance("RSA");

 	    RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),RSAPublicKeySpec.class);
	    RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),RSAPrivateKeySpec.class);
	
	    tableName.clear();
	    columnName.clear();
	    LinkedList<BigInteger> columnValues = new LinkedList<BigInteger>();
	    
	    tableName.add("userkey");

	    columnName.add("mod");
	    columnName.add("pub_exp");
	    columnName.add("priv_exp");

	    columnValues.add(pub.getModulus());	     
	    columnValues.add(pub.getPublicExponent());
	    columnValues.add(priv.getPrivateExponent());
	    
	    int row = DatabaseHandler.insertIntoDB(tableName, columnName, columnValues);
	    }

	    publicKey = readPublicKey();	
	    privateKey = readPrivateKey();

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

	    con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/trainconsole","postgres",Postgrespassword);
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
	    
	LinkedList<String> tableName = new LinkedList<String>();
	LinkedList<String> columnName = new LinkedList<String>();
	LinkedList<String> conditionValues = new LinkedList<String>();
	LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("userkey");
	    
	    columnName.add("mod");
	    columnName.add("pub_exp");

	    String str = "keyid = 1";

	    conditionValues.add(str);
	 
	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);

	    int i=0;
	    while(i<json.length()){
		JSONObject obj = json.getJSONObject(i); 
		i++;   

	    BigDecimal d = obj.getBigDecimal("mod");
            BigInteger m = d.toBigInteger();

            BigDecimal d1 = obj.getBigDecimal("pub_exp");
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
	    
	LinkedList<String> tableName = new LinkedList<String>();
	LinkedList<String> columnName = new LinkedList<String>();
	LinkedList<String> conditionValues = new LinkedList<String>();
	LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("userkey");
	    
	    columnName.add("mod");
	    columnName.add("priv_exp");

	    conditionValues.add("keyid=1");

	JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	int i=0;
	    while(i<json.length()){
		JSONObject obj = json.getJSONObject(i);
		i++;  

            BigDecimal d = obj.getBigDecimal("mod");
            BigInteger m = d.toBigInteger();

            BigDecimal d1 = obj.getBigDecimal("priv_exp");
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

	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> columnValues = new LinkedList<String>();
	    
	    tableName.add("userlogin");

	    columnName.add("username");
	    columnName.add("password");
	    columnName.add("mail_id");
	    columnName.add("role");
	    columnName.add("key");

	    columnValues.add(username);	     
	    columnValues.add(pwd);
	    columnValues.add(email);
	    columnValues.add("Passenger");
	    columnValues.add(HexFormat.of().formatHex(symKey));

	    int row = DatabaseHandler.insertIntoDB(tableName, columnName, columnValues);
	    if(row == 1)
	        return 1;
	    return 0;
	
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

	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("userlogin");
	    
	    columnName.add("role");
	    columnName.add("userid");
	    columnName.add("key");

	    String str = "username = '"+uN+"'";
	    conditionValues.add(str);
	    str="password = '"+pass+"'";
	    conditionValues.add(str);

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    String role="Invalid";

	    int iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

	    	role = obj.getString("role");
		int ID = obj.getInt("userid");
		setUID(ID);
		String key = obj.getString("key");

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
	    
	    //Insert itno userlogin table
	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> columnValues = new LinkedList<String>();
	    
	    tableName.add("userlogin");

	    columnName.add("username");
	    columnName.add("password");
	    columnName.add("role");
	    columnName.add("key");

	    columnValues.add(username);	     
	    columnValues.add(pwd);
	    columnValues.add("Operator");
	    columnValues.add(HexFormat.of().formatHex(symKey));

	    int rows = DatabaseHandler.insertIntoDB(tableName,columnName,columnValues);
	    if(rows == 1)
	        return 1;
	    return 0;
	
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return 0;
    }    

    public JSONArray displayStationDetails(int trainNumber,String from,String to)
    {
	try{

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");
	    columnName.add("train_no");
	    columnName.add("station_name");
	    columnName.add("arrival_time");
	    columnName.add("departure_time");
	    columnName.add("avltkts");

	    conditionValues.add("train_no = "+Integer.toString(trainNumber));
	    conditionValues.add("station_name != '"+from+"'");
	    conditionValues.add("station_name != '"+to+"'");

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    
	    
            return json;

	}catch(Exception e){
	    System.out.println("\nException : "+e);
	}
	return null;
    }

    public JSONArray displayTrainDetails(){

	ResultSet rs=null;
	try{

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("traindetails");
	    
	    columnName.add("tid");
	    columnName.add("train_no");
	    columnName.add("no_of_tickets");
	    columnName.add("train_name");
	    columnName.add("source_station");
	    columnName.add("destination_station");
	    columnName.add("departure_time");
	    columnName.add("arrival_time");

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    return json;

	}catch(Exception e){
	    System.out.println("\nException : "+e);
	}
	return null;
    }

    public int addTrain(String trainNo,String noOfTickets,String trainName,String sourceStation,String destinationStation,String departureTime,String arrivalTime){
        
	try{

	    //Insert into traindetails table
	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> columnValues = new LinkedList<String>();
	    
	    tableName.add("traindetails");

	    columnName.add("train_no");
	    columnName.add("no_of_tickets");
	    columnName.add("train_name");
	    columnName.add("source_station");
	    columnName.add("destination_station");
	    columnName.add("departure_time");
	    columnName.add("arrival_time");

	    columnValues.add(trainNo);	     
	    columnValues.add(noOfTickets);
	    columnValues.add(trainName);
	    columnValues.add(sourceStation);
	    columnValues.add(destinationStation);
	    columnValues.add(departureTime);
	    columnValues.add(arrivalTime);

	    int rows = DatabaseHandler.insertIntoDB(tableName, columnName, columnValues);
	    if(rows == 1){

	    tableName.clear();
	    columnName.clear();
	    columnValues.clear();

	    //Insert into routedetails table
	    tableName.add("routedetails");

	    columnName.add("train_no");
	    columnName.add("station_name");    
	    columnName.add("avltkts");
	    columnName.add("departure_time");
	    columnName.add("arrival_time");

	    columnValues.add(trainNo);	
	    columnValues.add(sourceStation); 
	    columnValues.add(noOfTickets);
	    columnValues.add(departureTime);
	    columnValues.add(null);

	    rows = DatabaseHandler.insertIntoDB(tableName, columnName, columnValues);
	    if(rows==1)
		return 1;

	    }
	    		
	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}	
	return 0;
    }

    public int addRoute(String trainNo,String stationName,String arrTime,String depTime,String noOfTickets){
	
	LinkedList<String> tableName = new LinkedList<String>();
	LinkedList<String> columnName = new LinkedList<String>();
	LinkedList<String> columnValues = new LinkedList<String>();
	    
	    //Insert into routedetails table
	    tableName.add("routedetails");

	    columnName.add("train_no");
	    columnName.add("station_name");    
	    columnName.add("avltkts");
	    columnName.add("departure_time");
	    columnName.add("arrival_time");

	    columnValues.add(trainNo);	
	    columnValues.add(stationName); 
	    columnValues.add(noOfTickets);
	    columnValues.add(depTime);
	    columnValues.add(arrTime);

	    int rows = DatabaseHandler.insertIntoDB(tableName, columnName, columnValues);
	    if(rows==1)
		return 1;
	return 0;
    }

    public int addDestination(String trainNo,String destinationStation,String noOfTickets,String arrivalTime){

	    LinkedList<String> tableName = new LinkedList<String>();
	LinkedList<String> columnName = new LinkedList<String>();
	LinkedList<String> columnValues = new LinkedList<String>();
	    
	    //Insert into routedetails table
	    tableName.add("routedetails");

	    columnName.add("train_no");
	    columnName.add("station_name");    
	    columnName.add("avltkts");
	    columnName.add("arrival_time");
	    columnName.add("departure_time");

	    columnValues.add(trainNo);	
	    columnValues.add(destinationStation); 
	    columnValues.add(noOfTickets);
	    columnValues.add(arrivalTime);
	    columnValues.add(null);

	    int rows = DatabaseHandler.insertIntoDB(tableName, columnName, columnValues);
	    if(rows==1)
		return 1;
	return 0;
    }

    public int findAvailablity(int trainNo,String fromSt,String toSt){
	
	try{
	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	    
	    int from_id=0, to_id=0;

	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    String str = "station_name = '"+fromSt+"'";
	    conditionValues.add(str);
	    str = "train_no = '"+Integer.toString(trainNo)+"'";
	    conditionValues.add(str);

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    int iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		from_id = obj.getInt("rid");
	    }

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    str = "station_name = '"+toSt+"'";
	    conditionValues.add(str);
	    str = "train_no = '"+Integer.toString(trainNo)+"'";
	    conditionValues.add(str);

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		to_id = obj.getInt("rid");
	    }

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("min(avltkts) as avl");

	    str = "rid >= "+Integer.toString(from_id);
	    conditionValues.add(str);
	    str = "rid < "+Integer.toString(to_id);
	    conditionValues.add(str);

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		int avl=obj.getInt("avl");

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
	   
	LinkedList<String> tableName = new LinkedList<String>();
	LinkedList<String> columnName = new LinkedList<String>();
	LinkedList<String> conditionValues = new LinkedList<String>();
	LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("traindetails");
	    
	    columnName.add("tid");

	    conditionValues.add("train_no = '"+Integer.toString(trainNo)+"'");

	JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	int iter=0;
	while(iter<json.length()){
	    JSONObject obj = json.getJSONObject(iter);
            iter++;   

	    tid=obj.getInt("tid");
	}

	tableName.clear();
	columnName.clear();
	conditionValues.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    conditionValues.add("train_no = '"+Integer.toString(trainNo)+"'");
	    conditionValues.add("station_name = '"+fromSt+"'");

	json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	iter=0;
	while(iter<json.length()){
	    JSONObject obj = json.getJSONObject(iter);
	    iter++;   

	    fromID=Integer.toString(obj.getInt("rid"));
	}

	tableName.clear();
	columnName.clear();
	conditionValues.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    conditionValues.add("train_no = "+Integer.toString(trainNo));
	    conditionValues.add("station_name = '"+toSt+"'");

	json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	iter=0;
	while(iter<json.length()){
	    JSONObject obj = json.getJSONObject(iter);
	    iter++;   

	    toID=Integer.toString(obj.getInt("rid"));
	}

	fromID=symEncry(fromID,Integer.parseInt(getKey()));
	toID=symEncry(toID,Integer.parseInt(getKey()));
	tktsReq=symEncry(Integer.toString(ticketsReq),Integer.parseInt(getKey()));

	tableName.clear();
	columnName.clear();
	LinkedList<String> columnValues = new LinkedList<String>();
	    
	    //Insert into userticketdetails table
	    tableName.add("userticketdetails");

	    columnName.add("userid");
	    columnName.add("train_id");    
	    columnName.add("from_id");
	    columnName.add("to_id");
	    columnName.add("no_of_tickets");
	    columnName.add("time_of_booking");

	    columnValues.add(getUID());	
	    columnValues.add(Integer.toString(tid)); 
	    columnValues.add(fromID);
	    columnValues.add(toID);
	    columnValues.add(tktsReq);
	    columnValues.add(LocalDateTime.now().toString());

	    int rows = DatabaseHandler.insertIntoDB(tableName, columnName, columnValues);

	tableName.clear();
	columnName.clear();
	conditionValues.clear();
	clauses.clear();

	int fromid=0,toid=0;

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    conditionValues.add("station_name = '"+fromSt+"'");
	    conditionValues.add("train_no = '"+trainNo+"'");

	json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	iter=0;
	while(iter<json.length()){
	    JSONObject obj = json.getJSONObject(iter);
	    iter++;   

	    fromid=obj.getInt("rid");
	}

	tableName.clear();
	columnName.clear();
	conditionValues.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    conditionValues.add("train_no = '"+Integer.toString(trainNo)+"'");
	    conditionValues.add("station_name = '"+toSt+"'");

	json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	iter=0;
	while(iter<json.length()){
	    JSONObject obj = json.getJSONObject(iter);
	    iter++;   

	    toid=obj.getInt("rid");
	}

	tableName.clear();
	conditionValues.clear();
	LinkedList<String> updationValues = new LinkedList<String>();
	    
	    //Update routedetails table
	    tableName.add("routedetails");

	    updationValues.add("avltkts = avltkts - '"+ticketsReq+"'");
	    conditionValues.add("rid >= '"+fromid+"'");
	    conditionValues.add("rid < '"+toid+"'");

	    rows = DatabaseHandler.updateDB(tableName, conditionValues, updationValues);

	con.close();

        }catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return 1;
    }

    public int updatePassengerDetails(int tid,String name,int age)
    {

	LinkedList<String> tableName = new LinkedList<String>();
	LinkedList<String> columnName = new LinkedList<String>();
	LinkedList<String> columnValues = new LinkedList<String>();
	    
	    //Insert into passengerdetails table
	    tableName.add("passengerdetails");

	    columnName.add("ticketid");
	    columnName.add("name");    
	    columnName.add("age");

	    columnValues.add(Integer.toString(tid));	
	    columnValues.add(name); 
	    columnValues.add(Integer.toString(age));

	    int rows = DatabaseHandler.insertIntoDB(tableName, columnName, columnValues);

	return rows;
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

	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("userticketdetails");
	    
	    columnName.add("ticket_id");
	    columnName.add("userid");
	    columnName.add("train_id");
	    columnName.add("from_id");
	    columnName.add("to_id");
	    columnName.add("no_of_tickets");
	    columnName.add("time_of_booking");

	    conditionValues.add("userid = '"+uID+"'");

	    clauses.add("order by time_of_booking desc limit 1");

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    
	    
	    int tID=0,trainID=0,tNo=0;
	    String fromID="",toID="",noOfTkts="";
	    String time="",tName="",fromSt="",toSt="",fromTime="",toTime="";

	    int iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

 		tID=obj.getInt("ticket_id");
	    	trainID=obj.getInt("train_id");
	    	fromID=obj.getString("from_id");
	    	toID=obj.getString("to_id");
	    	noOfTkts=obj.getString("no_of_tickets");
		time=obj.getString("time_of_booking");

		fromID=symDecry(fromID,Integer.parseInt(getKey()));
	    	toID=symDecry(toID,Integer.parseInt(getKey()));
	    	noOfTkts=symDecry(noOfTkts,Integer.parseInt(getKey()));

	    }

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("userlogin");
	    
	    columnName.add("username");
	    columnName.add("mail_id");

	    conditionValues.add("userid = '"+uID+"'");
	 
	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);

	    
	    String uN="",email="";
	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		uN = obj.getString("username");
		email = obj.getString("mail_id");

	    }

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("traindetails");
	    
	    columnName.add("train_no");
	    columnName.add("train_name");

	    conditionValues.add("tid = "+Integer.toString(trainID));

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		tNo=obj.getInt("train_no");
		tName=obj.getString("train_name");
	    }  

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("station_name");
	    columnName.add("departure_time");

	    conditionValues.add("rid = "+fromID);

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("station_name");
	    columnName.add("arrival_time");

	    conditionValues.add("rid = "+toID);

	    JSONArray json1 = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		fromSt=obj.getString("station_name");
		fromTime = obj.getString("departure_time");
	    }
	    
	    iter=0;
	    while(iter<json1.length()){
		JSONObject obj = json1.getJSONObject(iter);
		iter++;   

		toSt=obj.getString("station_name");
		toTime=obj.getString("arrival_time");
	    }
	    
	    int from_id=0, to_id=0;

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    String str = "station_name = '"+fromSt+"'";
	    conditionValues.add(str);
	    str = "train_no = '"+Integer.toString(tNo)+"'";
	    conditionValues.add(str);

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		from_id = obj.getInt("rid");
	    }

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    str = "station_name = '"+toSt+"'";
	    conditionValues.add(str);
	    str = "train_no = '"+Integer.toString(tNo)+"'";
	    conditionValues.add(str);

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		to_id = obj.getInt("rid");
	    }

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");
	    columnName.add("train_no");
	    columnName.add("avltkts");
	    columnName.add("station_name");
	    columnName.add("departure_time");
	    columnName.add("arrival_time");

	    str = "rid >= "+Integer.toString(from_id);
	    conditionValues.add(str);
	    str = "rid < "+Integer.toString(to_id);
	    conditionValues.add(str);

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    
	
	    String f = System.getProperty("user.dir") + "\\PDF\\Ticket";
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
	    	    
	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		doc.add(new Paragraph("                "+obj.getString("station_name")+"  "+obj.getString("arrival_time")+"  "+obj.getString("departure_time")));		
	    }
	    
	    
	    from_id=0;
	    to_id=0;

	    tableName.clear();
 	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    str = "station_name = '"+fromSt+"'";
	    conditionValues.add(str);
	    str = "train_no = '"+Integer.toString(tNo)+"'";
	    conditionValues.add(str);

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		from_id = obj.getInt("rid");
	    }

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    str = "station_name = '"+toSt+"'";
	    conditionValues.add(str);
	    str = "train_no = '"+Integer.toString(tNo)+"'";
	    conditionValues.add(str);

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		to_id = obj.getInt("rid");
	    }

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");

	        columnName.add("rid");
	    columnName.add("train_no");
	    columnName.add("avltkts");
	    columnName.add("station_name");
	    columnName.add("departure_time");
	    columnName.add("arrival_time");

	    str = "rid >= "+Integer.toString(from_id);
	    conditionValues.add(str);
	    str = "rid < "+Integer.toString(to_id);
	    conditionValues.add(str);

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    
	
	    iter=0;
	    while(json.length()==0){

		doc.add(new Paragraph("                Empty"));
	    }
	    
	    doc.add(new Paragraph("No. Of Tickets : "+noOfTkts));
	    doc.add(new Paragraph("Time of Booking: "+time)); 

	    doc.add(new Paragraph("Passenger Details : "));
	    doc.add(new Paragraph(""));

	    int passno = 1;

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("passengerdetails");
	    
	    columnName.add("ticketid");
	    columnName.add("passengerid");
	    columnName.add("name");
	    columnName.add("age");

	    conditionValues.add("ticketid = "+Integer.toString(tID));

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		String name = obj.getString("name");
		int age = obj.getInt("age");
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

    public JSONArray myTickets()
    {
	String uID = getUID();
	try{

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("userticketdetails");
	    
	    columnName.add("ticket_id");
	    columnName.add("userid");
	    columnName.add("train_id");
	    columnName.add("from_id");
	    columnName.add("to_id");
	    columnName.add("no_of_tickets");
	    columnName.add("time_of_booking");

	    conditionValues.add("userid = '"+uID+"'");

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    return json;

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return null;
    }

    public JSONArray cancelTicket()
    {
	String uID = getUID();
	try{

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("userticketdetails");
	    
	    columnName.add("ticket_id");
	    columnName.add("userid");
	    columnName.add("train_id");
	    columnName.add("from_id");
	    columnName.add("to_id");
	    columnName.add("no_of_tickets");
	    columnName.add("time_of_booking");

	    conditionValues.add("userid = '"+uID+"'");

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    return json;

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return null;

    }

    public JSONArray passengerDetails(int tid)
    {
	try{

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("passengerdetails");
	    
	    columnName.add("ticketid");
	    columnName.add("passengerid");
	    columnName.add("name");
	    columnName.add("age");

	    conditionValues.add("ticketid = "+Integer.toString(tid));

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    return json;

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return null;
    }

    public JSONArray ticketDetails(int tid)
    {
	try{

	    Connection con = getConnection();
	    Statement stmt = con.createStatement();
	
	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("userticketdetails");
	    
	    columnName.add("ticket_id");
	    columnName.add("userid");
	    columnName.add("train_id");
	    columnName.add("from_id");
	    columnName.add("to_id");
	    columnName.add("no_of_tickets");
	    columnName.add("time_of_booking");

	    conditionValues.add("ticket_id = "+Integer.toString(tid));

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    return json;

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return null;
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


	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();
	 
	    tableName.add("traindetails");

	    columnName.add("train_no");

	    conditionValues.add("tid = "+Integer.toString(trainID));

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);

	    int iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		tNo=obj.getInt("train_no");
	    }

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("station_name");

	    conditionValues.add("rid = "+fromID);

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		from=obj.getString("station_name");
	    }


	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("station_name");
	    columnName.add("arrival_time");

	    conditionValues.add("rid = "+toID);

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		to=obj.getString("station_name");
	    }

	    tableName.clear();
 	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	int fromid=0,toid=0;

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    conditionValues.add("station_name = '"+from+"'");
	    conditionValues.add("train_no = '"+tNo+"'");

	json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	iter=0;
	while(iter<json.length()){
	    JSONObject obj = json.getJSONObject(iter);
	    iter++;   

	    fromid=obj.getInt("rid");
	}

	tableName.clear();
	columnName.clear();
	conditionValues.clear();

	    tableName.add("routedetails");
	    
	    columnName.add("rid");

	    conditionValues.add("train_no = '"+Integer.toString(tNo)+"'");
	    conditionValues.add("station_name = '"+to+"'");

	json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	iter=0;
	while(iter<json.length()){
	    JSONObject obj = json.getJSONObject(iter);
	    iter++;   

	    toid=obj.getInt("rid");
	}

	tableName.clear();
	conditionValues.clear();
	LinkedList<String> updationValues = new LinkedList<String>();
	    
	    //Update routedetails table
	    tableName.add("routedetails");

	    updationValues.add("avltkts = avltkts+ '"+tkts+"'");
	    conditionValues.add("rid >= '"+fromid+"'");
	    conditionValues.add("rid < '"+toid+"'");

	    int rows = DatabaseHandler.updateDB(tableName, conditionValues, updationValues);


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

	    LinkedList<String> tableName = new LinkedList<String>();
	    LinkedList<String> columnName = new LinkedList<String>();
	    LinkedList<String> conditionValues = new LinkedList<String>();
	    LinkedList<String> clauses = new LinkedList<String>();

	    tableName.add("passengerdetails");
	    
	    columnName.add("ticketid");

	    conditionValues.add("passengerid = "+Integer.toString(passid));

	    JSONArray json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    int iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		tickid = obj.getInt("ticketid");
	    }	    

	    tableName.clear();
	    columnName.clear();
	    conditionValues.clear();
	    clauses.clear();

	    tableName.add("userticketdetails");
	    
	    columnName.add("no_of_tickets");

	    conditionValues.add("ticket_id = "+Integer.toString(tickid));

	    json = DatabaseHandler.selectFromDB(tableName,columnName,conditionValues,clauses);    

	    int tktno = 100;
	    iter=0;
	    while(iter<json.length()){
		JSONObject obj = json.getJSONObject(iter);
		iter++;   

		tkts = obj.getString("no_of_tickets");
		tkts = symDecry(tkts,Integer.parseInt(getKey()));

		tkts = Integer.toString(Integer.parseInt(tkts)-1);
		tktno = Integer.parseInt(tkts);

		tkts = symEncry(tkts,Integer.parseInt(getKey()));

	    }

	    int rows = stmt.executeUpdate("DELETE FROM PASSENGERDETAILS WHERE PASSENGERID='"+passid+"';");			
	    if(tktno!=0)
	    {
		tableName.clear();
		conditionValues.clear();
	        LinkedList<String> updationValues = new LinkedList<String>();
			    
	        tableName.add("userticketdetails");

	        String condition = "ticket_id = '";
	  	condition+=Integer.toString(tickid)+"'";
		conditionValues.add(condition);
	
		String updation = "no_of_tickets = '";
	  	updation+=tkts+"'";
	        updationValues.add(updation);

		rows = DatabaseHandler.updateDB(tableName,conditionValues,updationValues);
	    	
	    }
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