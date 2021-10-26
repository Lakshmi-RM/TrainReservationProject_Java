<%@ page errorPage="error.jsp" %>

<html>

	<head>
		<title>Verify OTP</title>
	</head>

	<body>

		<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.Statement, java.sql.ResultSet, java.util.*" %>
		<%
			String jdbcURL = "jdbc:postgresql://localhost:5432/authority";
			String user="postgres";
			String pass="postgres";
			String un=request.getUserPrincipal().getName();
			String otp="";
			
			Connection con = DriverManager.getConnection(jdbcURL,user,pass);
		   
			Statement stmt = con.createStatement();
			
			String sql="select otp from users where user_name='"+un+"';";
			ResultSet rs=stmt.executeQuery(sql);
			
			if(rs.next())
				otp=rs.getString("otp");
			
			String otpProvidedbyUser=request.getParameter("otp");
		%>
		<h4>Otp by you is <%=otpProvidedbyUser%> OTP in db is <%=otp%></h4>
		<%
			
			if(otp.equals(otpProvidedbyUser))
				response.sendRedirect("index1.jsp");
			else
				response.sendRedirect("error.jsp");
		%>
	</body>
</html>