<%@ page errorPage="ShowError.jsp" %>

<html>

<head>
<title>Book Tickets</title>
</head>

<body>
	<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.Statement, java.sql.ResultSet "%>
	<%
		String trainNo = request.getParameter("trainNo");
		String fromSt = request.getParameter("fromSt");
		String toSt = request.getParameter("toSt");
		int ticketsReq = Integer.parseInt(request.getParameter("noOfTkts"));
		
		String jdbcURL = "jdbc:postgresql://localhost:5432/authority";
	    String user="postgres";
	    String pass="postgres";
	    
	    Connection con = DriverManager.getConnection(jdbcURL,user,pass);
	   
	    Statement stmt = con.createStatement();
		
		ResultSet rs;
		String sql = "select rid from routedetails where train_no='"+trainNo+"' and station_name='"+fromSt+"';";
		rs = stmt.executeQuery(sql);
		int fromid=0,toid=0;
		
		if(rs.next())
		{
			fromid = rs.getInt("rid");
		}
		sql = "select rid from routedetails where train_no='"+trainNo+"' and station_name='"+toSt+"';";
		rs = stmt.executeQuery(sql);
		
		if(rs.next())
		{
			toid = rs.getInt("rid");
		}
		sql = "select min(avltkts) as avl from routedetails where rid>=(select rid from routedetails where station_name='"+fromSt+"' and train_no='"+trainNo+"') and rid<(select rid from routedetails where station_name='"+toSt+"' and train_no='"+trainNo+"');";
		rs = stmt.executeQuery(sql);
		
		if(rs.next()){

			int avl=rs.getInt("avl");
			if(avl>0){
				if(avl>=ticketsReq){
					int rows = stmt.executeUpdate("update routedetails set avltkts=avltkts-'"+ticketsReq+"' where rid>=(select rid from routedetails where station_name='"+fromSt+"' and train_no='"+trainNo+"') and rid<(select rid from routedetails where station_name='"+toSt+"' and train_no='"+trainNo+"');");
					sql = "INSERT INTO USERTICKETDETAILS(TRAINNO,FROMST,TOST,NO_OF_TICKETS,TIME_OF_BOOKING) VALUES('"+trainNo+"','"+fromSt+"','"+toSt+"','"+ticketsReq+"',now());";
					stmt.executeUpdate(sql);
					out.println("Ticket booked  successfully");
				}
				else{
					out.println(ticketsReq+" tickets are not available");
				}
			}
			else{
				out.println("No such trains or stations available");
			}
	    }		
	%>
	
	<p><a href="bookTickets.jsp" > Book Another Ticket </a></p>
	<p><a href="passenger.jsp" > Go To Main Menu </a></p>
	<p><a href="logout.jsp" > Logout </a></p>
	
</body>

</html>	