package com.transport.train;

import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.BodyPart;
import javax.mail.Multipart;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;

import javax.activation.FileDataSource;
import javax.activation.DataHandler;
import javax.activation.DataSource;

import java.util.Properties;
import java.io.File;

public class SendEmail {

    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String USERNAME = "<username>@gmail.com";
    private static final String PASSWORD = "<password>";

    private static final String EMAIL_FROM = "<username>@gmail.com";
    
    private static final String EMAIL_SUBJECT = "Ticket Details";
    private static final String EMAIL_TEXT = "Your ticket has been booked successfully. \n The booked ticket has been attached as a PDF.";

    public static void sendMail(String email, String f){
        Properties prop = System.getProperties();

        prop.put("mail.smtp.auth", "true");
	prop.put("mail.smtp.starttls.enable", "true");
	prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
	prop.put("mail.smtp.ssl.protocols","TLSv1.2");
	prop.put("mail.smtp.port", "587");

        Session session = Session.getInstance(prop, null);

        Message msg = new MimeMessage(session);

        try {
            msg.setFrom(new InternetAddress(EMAIL_FROM));

            msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email, false));

            msg.setSubject(EMAIL_SUBJECT);

            BodyPart messageBodyPart1 = new MimeBodyPart();  
    	    messageBodyPart1.setText(EMAIL_TEXT);  
      
    	    MimeBodyPart messageBodyPart2 = new MimeBodyPart();  
  
    	    DataSource source = new FileDataSource(f);  
    	    messageBodyPart2.setDataHandler(new DataHandler(source));  
    	    messageBodyPart2.setFileName(new File(f).getName());  
          
    	    Multipart multipart = new MimeMultipart();  
    	    multipart.addBodyPart(messageBodyPart1);  
    	    multipart.addBodyPart(messageBodyPart2);  
  
  	    msg.setContent(multipart);    
            
	    SMTPTransport t = new SMTPTransport(session,null);

            t.connect(SMTP_SERVER, USERNAME, PASSWORD);

            t.sendMessage(msg, msg.getAllRecipients());

            System.out.println("Mail sent successfully");
            t.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
