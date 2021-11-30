<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.Statement" %>
<%
    String uN   = request.getParameter("username");
    String pW   = request.getParameter("password");
    String mail = request.getParameter("mailid");

    Class.forName("org.postgresql.Driver");
    Connection con = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/trainconsole", "postgres", "12345");
    Statement stmt = con.createStatement();

    String sql = "INSERT INTO userlogin(username, password, mail_id, role) VALUES('"
                 + uN + "','" + pW + "','" + mail + "','passenger');";
    stmt.executeUpdate(sql);
    con.close();
%>
<html>
  <body>
    <h3>Account created successfully!</h3>
    <p><a href="index.jsp">Click here to login</a></p>
  </body>
</html>