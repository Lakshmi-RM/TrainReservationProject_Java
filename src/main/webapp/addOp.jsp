<html>

<head>
<title>Add Operator</title>
</head>

<body>
	<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.Statement" %>
	<%
		String uN = request.getParameter("username");
		String pW = request.getParameter("password");
		String mail=request.getParameter("mailid");
		
		String jdbcURL = "jdbc:postgresql://localhost:5432/authority";
	    String user="postgres";
	    String pass="postgres";
	    
	    Connection con = DriverManager.getConnection(jdbcURL,user,pass);
	   
	    Statement stmt = con.createStatement();

		String sql = "INSERT INTO USERS(USER_NAME,USER_PASS,MAIL_ID) VALUES('"+uN+"','"+pW+"','"+mail+"'); ";
		stmt.executeUpdate(sql);
		
		sql = "INSERT INTO USER_ROLES(USER_NAME,ROLE_NAME) VALUES('"+uN+"','operator'); ";
		stmt.executeUpdate(sql);
		
		out.println("Operator added  successfully");
	%>
	
	<p><a href="addOperator.jsp" > Add Another Operator </a></p>
	<p><a href="admin.jsp" > Go To Main Menu </a></p>
	<p><a href="logout.jsp" > Logout </a></p>
	
</body>

</html>	