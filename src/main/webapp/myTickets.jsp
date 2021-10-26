<%@ page errorPage="ShowError.jsp" %>

<html>
	<head>
	</head>
	<body>
	
		<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.Statement, java.sql.ResultSet" %>
		
		<%
				String jdbcURL = "jdbc:postgresql://localhost:5432/authority";
				String user="postgres";
				String pass="postgres";
				
				Connection con = DriverManager.getConnection(jdbcURL,user,pass);
			   
				Statement stmt = con.createStatement();
				
		%>
		<h1>Ticket Details</h1>
		
		<table>
			
			<tr>
				<th>Ticket ID</th>
				<th>Train No</th>
				<th>From Station</th>
				<th>To Station</th>
				<th>No. of Tickets</th>
				<th>Time Of Booking</th>
				
			</tr>
			
			<%
				ResultSet resultset = stmt.executeQuery("select * from userticketdetails;");
			%>
			
			<% 
				while(resultset.next()){ 
			%>
			
			<tr>
				
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(1) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(2) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(3) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(4) %></td>				
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(5) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset.getString(6) %></td>
				
			</tr>
			
			<%}%>
			
		</table>
	
		<p><a href="passenger.jsp" > Go To Main Menu </a></p>
		<p><a href="logout.jsp" > Logout </a></p>
		
	</body
</html>