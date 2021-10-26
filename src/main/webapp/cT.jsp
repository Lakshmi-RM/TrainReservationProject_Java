<%@ page errorPage="ShowError.jsp" %>

<html>

<head>
<title>Cancel Tickets</title>
</head>

<body>
	<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.Statement, java.sql.ResultSet "%>
	<%
		String tid = request.getParameter("ticketID");
		int noOfTickets = Integer.parseInt(request.getParameter("noOfTkts"));
		
		String jdbcURL = "jdbc:postgresql://localhost:5432/authority";
	    String user="postgres";
	    String pass="postgres";
	    
	    Connection con = DriverManager.getConnection(jdbcURL,user,pass);
	   
	    Statement stmt = con.createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM USERTICKETDETAILS WHERE TICKET_ID='"+tid+"';");
		if(!rs.next())
			out.println("You have entered a invalid ticket id");
		else{
			
			rs = stmt.executeQuery("SELECT * FROM userticketdetails WHERE TICKET_ID='"+tid+"';");
			int tick=0;
			
			if(rs.next())
				tick = rs.getInt("no_of_tickets");
			String fromst="",tost="";
			int fromid=0,toid=0;
			int trainno=0;
			
			if(noOfTickets>tick)
				out.println(noOfTickets+" tickets are not available");
			else
			{
				String sql;
				trainno= rs.getInt("trainno");
				fromst = rs.getString("fromst");
				tost = rs.getString("tost");
				
				if(noOfTickets==tick)
				{
					sql = "delete from userticketdetails where ticket_id='"+tid+"';";
				}
				else
				{
					int diff = tick-noOfTickets;
					sql = "update userticketdetails set no_of_tickets = "+diff+" where ticket_id = "+tid+";";
				}
				stmt.executeUpdate(sql);
				
				sql = "select trainno from userticketdetails where ticket_id = '"+tid+"';";
				rs = stmt.executeQuery(sql);
				
				if(rs.next())
					trainno = rs.getInt("trainno");
				
				sql = "select rid from routedetails where train_no = '"+trainno+"' and station_name = '"+fromst+"';";
				rs = stmt.executeQuery(sql);
				
				if(rs.next())
					fromid = rs.getInt("rid");
				
				sql = "select rid from routedetails where train_no = '"+trainno+"' and station_name = '"+tost+"';";
				rs = stmt.executeQuery(sql);
				
				if(rs.next())
					toid = rs.getInt("rid");
				
				stmt.executeUpdate("update routedetails set avltkts=avltkts+'"+noOfTickets+"' where rid>='"+fromid+"' and rid<'"+toid+"';");	    
				
				out.println("Ticket has been cancelled successfully");
			}
		}
			
	%>
	
	<p><a href="cancelTicket.jsp" > Cancel Another Ticket </a></p>
	<p><a href="passenger.jsp" > Go To Main Menu </a></p>
	<p><a href="logout.jsp" > Logout </a></p>
	
</body>

</html>	