
<html>

	<head>
		<title>OTP Confirmation Page</title>
	</head>

	<body>
	
		<% String un=request.getUserPrincipal().getName(); %>
		
		<h3>Hi <%=un%></h3>
		
		<form name="confirmOTP" method="post" action="sendOTP.jsp">
			<p>OTP will be sent to your registered mail id</p>
			<p><input type="submit" value="Send OTP"></input></p>
		</form>
		
	</body>
</html>