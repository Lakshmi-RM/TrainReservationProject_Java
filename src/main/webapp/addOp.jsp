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
		
		String jdbcURL = "jdbc:postgresql://localhost:5432/trainconsole";
	    String user="postgres";
	    String pass="12345";
	    
	    Class.forName("org.postgresql.Driver"); 
		Connection con = DriverManager.getConnection(jdbcURL,user,pass);
	   
	    Statement stmt = con.createStatement();

		String sql = "INSERT INTO USERLOGIN(USERNAME,PASSWORD,MAIL_ID,ROLE) VALUES('"+uN+"','"+pW+"','"+mail+"','operator'); ";
		stmt.executeUpdate(sql);
		
		out.println("Operator added  successfully");
	%>
	
	<p><a href="addOperator.jsp" > Add Another Operator </a></p>
	<p><a href="admin.jsp" > Go To Main Menu </a></p>
	<p><a href="logout.jsp" > Logout </a></p>
	
</body>

</html>	