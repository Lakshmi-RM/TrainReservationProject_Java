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
		
		<h1>Route Details </h1> 
		<h2>Train No : <%=request.getParameter("trainNo")%></h2>
		
		
		
		<table>
			
			<tr>
				<th>Station ID</th>
				<th>Station Name</th>
				<th>Arrival Time</th>
				<th>Departure Time</th>
				
			</tr>
			
			<%
				ResultSet resultset1 = stmt.executeQuery("select * from routedetails where train_no='"+request.getParameter("trainNo")+"' order by rid asc;");
			%>
			
			<% while(resultset1.next()){ %>
			
			<tr>
			
				<td style="text-align: center; vertical-align: middle;"> <%= resultset1.getString(1)%></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset1.getString(3) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset1.getString(4) %></td>
				<td style="text-align: center; vertical-align: middle;"> <%= resultset1.getString(5) %></td>
				
			</tr>
			
			<% } %>
			
		</table>
	
		<p><a href="dispRoute.jsp" > Display Route of Train </a></p>
		<p><a href="passenger.jsp" > Go To Main Menu </a></p>
		<p><a href="logout.jsp" > Logout </a></p>
		
	</body
</html>