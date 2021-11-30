
import java.util.LinkedList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseHandler
{
	static String Postgrespassword="12345";
    public static int insertIntoDB(LinkedList tableName, LinkedList columnName, LinkedList columnValues)
    {
	int rows=0;
	try{
	    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/trainconsole","postgres",Postgrespassword);;
	    Statement stmt = con.createStatement();
	    String sql = "insert into "+tableName.get(0)+" (";

	    for(int i=0;i<columnName.size();i++){
		sql += columnName.get(i);
		if(i<columnName.size()-1)
		     sql+=",";
	    }

	    sql += ") values (";

	    for(int i=0;i<columnValues.size();i++){
			if(columnValues.get(i) == null)
				sql += "NULL";
			else
				sql += "'"+columnValues.get(i)+"'";
			if(i<columnValues.size()-1)
				sql+=",";
		}

	    sql += ");";

	    rows = stmt.executeUpdate(sql);

	    con.close();
	}catch(Exception e){
	    System.out.println("Exception is "+e);
	}
	return rows;
    }

    public static int updateDB(LinkedList tableName, LinkedList conditionValues, LinkedList updationValues)
    {
	int rows = 0;
	try
	{

	    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/trainconsole","postgres",Postgrespassword);;
	    Statement stmt = con.createStatement();

	    String sql = "update "+tableName.get(0)+" set ";
	
	    int i=0;
	    for(;i<updationValues.size();i++)
	    {
		if(i!=0)
		    sql+=" and ";
		sql += updationValues.get(i);
	    }
	    
	    sql+=" where ";
	    for(int j=0;j<conditionValues.size();j++)
	    {
		if(j!=0)
		    sql+=" and ";

		sql += conditionValues.get(j);
	    }
	    sql+=";";

	    rows = stmt.executeUpdate(sql);

	}catch(Exception e){
	    System.out.println("Exception : "+e);
	}
	return rows;
    }

    public static JSONArray selectFromDB(LinkedList tableName, LinkedList columnName, LinkedList conditionValues,LinkedList clauses)
    {
	int rows=0;
	try{
	    Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/trainconsole","postgres",Postgrespassword);;
	    Statement stmt = con.createStatement();
	    String sql = "select ";

	    for(int i=0;i<columnName.size();i++)
	    {
		sql += columnName.get(i);
		if(i!=columnName.size()-1)
		     sql += ",";
	    }

	    sql += " from "+tableName.get(0);
	  		
	    if(conditionValues.size()!=0)
		sql += " where ";

	    for(int i=0;i<conditionValues.size();i++){
		sql += conditionValues.get(i);
		if(i<conditionValues.size()-1)
		     sql+=" and ";
	    }

	    for(int i=0;i<clauses.size();i++)
		sql+=clauses.get(i)+" ";

	    sql += ";";

	    ResultSet rs = stmt.executeQuery(sql);

	    JSONArray json = new JSONArray();

	    while(rs.next()) 
	    {
  		JSONObject obj = new JSONObject();
  		for (int i=0; i<columnName.size(); i++) 
		{
		    String colName = "";
		    colName += columnName.get(i);
		    if(colName.contains("(*)"))
		    {
			colName = colName.replaceAll("\\*","");
			colName = colName.replaceAll("\\(","");
			colName = colName.replaceAll("\\)","");		
		    }

		    if(colName.contains(" as "))
	  	    {
			String separator =" as ";
		        int sepPos = colName.indexOf(separator);
			colName = colName.substring(sepPos+separator.length());
			colName = colName.replaceAll("\\s","");
		    }

			Object val = rs.getObject(colName);
			if(val instanceof java.sql.Time || val instanceof java.sql.Timestamp || val instanceof java.sql.Date)
				obj.put(colName, val.toString());
			else
				obj.put(colName, val);

  		}
  		json.put(obj);
	    }
	    con.close();

	    return json;

	}catch(Exception e){
	    System.out.println("Exception is "+e);
	}
	return null;
    }

}