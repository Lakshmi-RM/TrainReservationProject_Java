<%@ page errorPage="ShowError.jsp" %>
<html>
<head><title>Book Tickets</title></head>
<body>
<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.Statement, java.sql.ResultSet" %>
<%
    String trainNo = request.getParameter("trainNo");
    String fromSt = request.getParameter("fromSt");
    String toSt = request.getParameter("toSt");
    int ticketsReq = Integer.parseInt(request.getParameter("noOfTkts"));

    String jdbcURL = "jdbc:postgresql://localhost:5432/trainconsole";
    Class.forName("org.postgresql.Driver");
    Connection con = DriverManager.getConnection(jdbcURL, "postgres", "12345");
    Statement stmt = con.createStatement();

    String sql = "select rid from routedetails where train_no=" + trainNo + " and station_name='" + fromSt + "';";
    ResultSet rs = stmt.executeQuery(sql);
    int fromid = 0, toid = 0;
    if(rs.next()) { fromid = rs.getInt("rid"); }
    rs.close();

    Statement stmt2 = con.createStatement();
    sql = "select rid from routedetails where train_no=" + trainNo + " and station_name='" + toSt + "';";
    ResultSet rs2 = stmt2.executeQuery(sql);
    if(rs2.next()) { toid = rs2.getInt("rid"); }
    rs2.close();

    Statement stmt3 = con.createStatement();
    sql = "select min(avltkts) as avl from routedetails where rid>=" + fromid + " and rid<" + toid + ";";
    ResultSet rs3 = stmt3.executeQuery(sql);

    if(rs3.next()){
        int avl = rs3.getInt("avl");
        if(avl > 0){
            if(avl >= ticketsReq){
                Statement stmt4 = con.createStatement();
                stmt4.executeUpdate("update routedetails set avltkts=avltkts-" + ticketsReq + " where rid>=" + fromid + " and rid<" + toid + ";");

                String un = request.getUserPrincipal().getName();
                Statement stmt5 = con.createStatement();
                ResultSet rs4 = stmt5.executeQuery("select userid from userlogin where username='" + un + "';");
                int userid = 0;
                if(rs4.next()) { userid = rs4.getInt("userid"); }

                Statement stmt6 = con.createStatement();
                sql = "INSERT INTO USERTICKETDETAILS(USERID,TRAINNO,FROMST,TOST,NO_OF_TICKETS,TIME_OF_BOOKING) VALUES("
                      + userid + ",'" + trainNo + "','" + fromSt + "','" + toSt + "'," + ticketsReq + ",now());";
                stmt6.executeUpdate(sql);
                out.println("Ticket booked successfully");
            } else {
                out.println(ticketsReq + " tickets are not available");
            }
        } else {
            out.println("No such trains or stations available");
        }
    }
    con.close();
%>
<p><a href="bookTickets.jsp">Book Another Ticket</a></p>
<p><a href="passenger.jsp">Go To Main Menu</a></p>
<p><a href="logout.jsp">Logout</a></p>
</body>
</html>