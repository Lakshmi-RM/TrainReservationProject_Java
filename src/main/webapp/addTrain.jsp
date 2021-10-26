<html>
	<head>
		<title> </title>
	</head>
	<body>
        <h2>Add Train </h2>
        <form name="addTicketForm" method="POST" action="addRoute.jsp">
            <p>Train Number: <input type="text" name="trainNo" size="20" required="required"/></p>
			<p>No. of Tickets: <input type="text" name="tkts" size="20" required="required"/></p>
			<p>Train Name : <input type="text" name="trainName" size="20" required="required"/></p>
            <p>Source Station: <input type="text" size="20" name="source" required="required"/></p>
			<p>Destination Station  : <input type="text" size="20" name="destination" required="required"/></p>
			<p>Departure Time : <input type="text" size="20" name="depTime" required="required"/></p>
			<p>Arrival Time : <input type="text" size="20" name="arrTime" required="required"/></p>
			<p>No of Stations : <input type="text" size="20" name="noOfStation" required="required"/></p>
            <p>  <input type="submit" value="Submit"/></p>
        </form>       
	</body>
</html>