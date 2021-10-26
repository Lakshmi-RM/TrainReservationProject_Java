<html>

	<head>
		<title>Admin Page</title>
	</head>

	<body>

		<h1>Welcome Admin</h1>
		
		<% String un=request.getUserPrincipal().getName(); %>
		
		<h3>Hi <%=un%></h3>
		
		<p><a href="addOperator.jsp" > Add Operator </a></p>
		<p><a href="displayTrain.jsp" > Display Train </a></p>
		<p><a href="dispRoute.jsp" > Display Route of Train </a></p>
		<p><a href="logout.jsp" > Logout </a></p>
		
	</body>

</html>	