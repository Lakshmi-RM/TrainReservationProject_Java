<%@ page errorPage="error.jsp" %>

<html>
    <head>
        <title>Send OTP</title>
    </head>

    <body>

        <%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.Statement, java.sql.ResultSet, java.util.*, javax.mail.*, javax.mail.internet.*, java.util.Properties" %>
        <%
            String un = request.getUserPrincipal().getName();
            Random rand = new Random();
            int otpInt = 100000 + rand.nextInt(900000);
            String otp = String.valueOf(otpInt);
            String email = "";

            try {
                Class.forName("org.postgresql.Driver");
                Class.forName("org.postgresql.Driver"); Connection con1 = DriverManager.getConnection("jdbc:postgresql://localhost:5432/trainconsole", "postgres", "12345");
                Statement stmt1 = con1.createStatement();
                ResultSet rs1 = stmt1.executeQuery("SELECT mail_id FROM userlogin WHERE username='" + un + "';");
                if (rs1.next()) {
                    email = rs1.getString("mail_id");
                }
                con1.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Class.forName("org.postgresql.Driver"); Connection con2 = DriverManager.getConnection("jdbc:postgresql://localhost:5432/trainconsole", "postgres", "12345");
                Statement stmt2 = con2.createStatement();

                ResultSet rs2 = stmt2.executeQuery("SELECT username FROM usersotp WHERE username='" + un + "';");
                if (rs2.next()) {
                    stmt2.executeUpdate("UPDATE usersotp SET otp='" + otp + "' WHERE username='" + un + "';");
                } else {
                    stmt2.executeUpdate("INSERT INTO usersotp (username, otp) VALUES ('" + un + "', '" + otp + "');");
                }
                con2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String smtpHost = application.getInitParameter("host");
            String smtpPort = application.getInitParameter("port");
            String smtpUser = application.getInitParameter("user");
            String smtpPass = application.getInitParameter("pass");

            boolean mailSent = false;
            String mailError = "";

            if (email != null && !email.isEmpty()) {
                try {
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.ssl.trust", smtpHost);
                    props.put("mail.smtp.ssl.protocols", "TLSv1.2");
                    props.put("mail.smtp.host", smtpHost);
                    props.put("mail.smtp.port", smtpPort);

                    Session mailSession = Session.getInstance(props, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(smtpUser, smtpPass);
                        }
                    });

                    Message msg = new MimeMessage(mailSession);
                    msg.setFrom(new InternetAddress(smtpUser));
                    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
                    msg.setSubject("Your OTP for Train Console Login");
                    msg.setText("Hi " + un + ",\n\nYour OTP is: " + otp + "\n\nThis OTP is valid for this session only.\n\nTrain Console");
                    Transport.send(msg);
                    mailSent = true;
                } catch (Exception e) {
                    mailError = e.getMessage();
                    e.printStackTrace();
                }
            }
        %>

        <%
            if (mailSent) {
        %>
            <h3>OTP sent successfully to <%=email%></h3>
            <p>Please check your email and enter the OTP below.</p>
        <%
            } else if (email == null || email.isEmpty()) {
        %>
            <h3>No email registered for this account.</h3>
            <p>Please contact the administrator.</p>
        <%
            } else {
        %>
            <h3>Failed to send OTP. Please try again.</h3>
            <p>Error: <%=mailError%></p>
        <%
            }
        %>

        <form name="verifyForm" method="post" action="verifyOTP.jsp">
            <p>Enter OTP: <input type="text" name="otp" size="10"/></p>
            <p><input type="submit" value="Verify OTP"/></p>
        </form>

    </body>
</html>
