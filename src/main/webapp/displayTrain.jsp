<html>
	<head>
	</head>
	<body>
	
		<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.Statement, java.sql.ResultSet" %>
		
		<%
				String jdbcURL = "jdbc:postgresql://localhost:5432/trainconsole";
				String user="postgres";
				String pass="12345";
				
				Class.forName("org.postgresql.Driver"); Connection con = DriverManager.getConnection(jdbcURL,user,pass);
			   
				Statement stmt = con.createStatement();
				
		%>
		<h1>Train Details</h1>
		
		<table>
			
			<tr>
				<th>Train ID</th>
				<th>Train No</th>
				<th>No of Tickets</th>
				<th>Train Name</th>
				<th>Source Station</th>
				<th>Destination Station</th>
				<th>Departure Time</th>
				<th>Arrival Time</th>
				
			</tr>
			
			<%
				ResultSet resultset = stmt.executeQuery("select * from traindetails;");
			%>
			
			<% while(resultset.next()){ %>
			
			<tr>
				
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(1) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(2) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(3) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(4) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(5) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(6) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(7) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(8) %></td>
				
			</tr>
			
			<% } %>
			
		</table>
	
		<p><a href="dispRoute.jsp" > Display Route of Train </a></p>
		<p><a href="admin.jsp" > Go To Main Menu </a></p>
		<p><a href="logout.jsp" > Logout </a></p>
		
	</body
</html>