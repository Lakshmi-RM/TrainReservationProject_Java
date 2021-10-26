<% if (request.isUserInRole("admin")) { %>
<%
   String redirectURL = "admin.jsp";
    response.sendRedirect(redirectURL);
%>
<% } else if(request.isUserInRole("operator")){%>
<%
    String redirectURL = "operator.jsp";
    response.sendRedirect(redirectURL);
%>

<% } else if(request.isUserInRole("passenger")){%>
<%
    String redirectURL = "passenger.jsp";
    response.sendRedirect(redirectURL);
%>
<%} else {%>
<%
    String redirectURL = "error.jsp";
    response.sendRedirect(redirectURL);
%>
<% } %>