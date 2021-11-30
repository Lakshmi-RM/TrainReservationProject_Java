<%@ page errorPage="ShowError.jsp" %>

<html>

<head>
<title>Add Route</title>
</head>

<body>
	<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.Statement, java.sql.ResultSet "%>
	<%
		String trainNo = request.getParameter("trainNo");
		String tkts =request.getParameter("tkts");
		String trainName = request.getParameter("trainName");
		String source = request.getParameter("source");
		String dest = request.getParameter("destination");
		String deptime = request.getParameter("depTime");
		String arrtime = request.getParameter("arrTime");
		int noo = Integer.parseInt(request.getParameter("noOfStation"));
		
		String jdbcURL = "jdbc:postgresql://localhost:5432/trainconsole";
	    String user="postgres";
	    String pass="12345";
	    
	    Class.forName("org.postgresql.Driver"); 
		Connection con = DriverManager.getConnection(jdbcURL,user,pass);
	   
	    Statement stmt = con.createStatement();
		
		String sql = "INSERT INTO TRAINDETAILS(TRAIN_NO,NO_OF_TICKETS,TRAIN_NAME,SOURCE_STATION,DESTINATION_STATION,DEPARTURE_TIME,ARRIVAL_TIME) VALUES('"+trainNo+"','"+tkts+"','"+trainName+"','"+source+"','"+dest+"','"+deptime+"','"+arrtime+"');";
		stmt.executeUpdate(sql);
		
		sql = "INSERT INTO RouteDetails(TRAIN_NO,AVLTKTS,STATION_NAME,DEPARTURE_TIME) VALUES('"+trainNo+"','"+tkts+"','"+source+"','"+deptime+"');";
		stmt.executeUpdate(sql);
		
				
	%>
	
	<h2>Add Routes </h2>
        <form name="addRouteForm" method="POST" action="RouteDetails.jsp">
		<%
		
			for(int i=0;i<noo;i++)
			{
				sql = "INSERT INTO RouteDetails(TRAIN_NO,AVLTKTS,STATION_NAME,ARRIVAL_TIME,DEPARTURE_TIME) VALUES('"+trainNo+"','"+tkts+"','','0:0','0:0');";
				stmt.executeUpdate(sql);
		
		%>
			<p>Station Name <%=i+1%>: <input type="text" size="20" name="stationName"/></p>
			<p>Arrival Time : <input type="text" size="20" name="arr"/></p>
			<p>Departure Time : <input type="text" size="20" name="dep"/></p>
			
		<%	
		
			}
			
		%>
		
		<%
			
			sql = "INSERT INTO RouteDetails(TRAIN_NO,AVLTKTS,STATION_NAME,ARRIVAL_TIME) VALUES('"+trainNo+"','"+tkts+"','"+dest+"','"+arrtime+"');";
			stmt.executeUpdate(sql);
			
		%>
		
		<p>  <input type="submit" value="Submit"/></p>
        </form>       
	
	
	
</body>

</html>	