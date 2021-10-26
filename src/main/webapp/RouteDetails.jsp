<%@ page errorPage="ShowError.jsp" %>

<html>

<head>
<title>Route Details</title>
</head>

<body>
	<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.Statement, java.sql.ResultSet "%>
	<%
		
		String jdbcURL = "jdbc:postgresql://localhost:5432/authority";
	    String user="postgres";
	    String pass="postgres";
	    
	    Connection con = DriverManager.getConnection(jdbcURL,user,pass);
	   
	    Statement stmt = con.createStatement();
		
		String sql = "select rid from routedetails order by rid desc;";
		ResultSet rs = stmt.executeQuery(sql);
		int stid = 0;
		
		if(rs.next())
		{
			stid = rs.getInt("rid");
		}
			stid--;
			
			String[] stNames = request.getParameterValues("stationName");
			String[] arrTimes = request.getParameterValues("arr");
			String[] depTimes = request.getParameterValues("dep");
		   
			for(int j = stNames.length-1; j >=0; j--){
				sql = "update routedetails set station_name = '"+stNames[j]+"', arrival_time = '"+arrTimes[j]+"', departure_time = '"+depTimes[j]+"' where rid = '"+stid+"';";
				stmt.executeUpdate(sql);
				stid--;
			}
				
	%>
	
	<p><a href="addTrain.jsp" > Add Another Train </a></p>
	<p><a href="operator.jsp" > Go To Main Menu </a></p>
	<p><a href="logout.jsp" > Logout </a></p>      
	
	
</body>

</html>	