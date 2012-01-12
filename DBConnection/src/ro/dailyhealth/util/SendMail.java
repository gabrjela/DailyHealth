package ro.dailyhealth.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;


public class SendMail {

	  private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	  private static final String SMTP_AUTH_USER = "bgabriela";
	  private static final String SMTP_AUTH_PWD  = "broscuto";
	
	  private static final String emailSubjectTxt  = "New updates from Daily Health";
	  private static final String emailFromAddress = "bgabriela@gmail.com";
	
	  private Properties props = null;
	  private Authenticator auth = null;
	  
	  public SendMail() {
		     //Set the host smtp address
		     props = new Properties();
		     props.put("mail.smtp.host", SMTP_HOST_NAME);
		     props.put("mail.smtp.auth", "true");
		     props.put("mail.smtp.starttls.enable","true");
		     
		     auth = new SMTPAuthenticator();
	  }
	
	  public void sendMail(String recipients[ ], String emailMsgTxt) throws MessagingException {
		    Session session = Session.getDefaultInstance(props, auth);
		
		    // create a message
		    Message msg = new MimeMessage(session);
		
		    // set the from and to address
		    InternetAddress addressFrom = new InternetAddress(emailFromAddress);
		    msg.setFrom(addressFrom);
		
		    InternetAddress[] addressTo = new InternetAddress[recipients.length];
		    for (int i = 0; i < recipients.length; i++) {
		        addressTo[i] = new InternetAddress(recipients[i]);
		    }
		    msg.setRecipients(Message.RecipientType.TO, addressTo);
		
		    // Setting the Subject and Content Type
		    msg.setSubject(emailSubjectTxt);
		    msg.setContent(emailMsgTxt, "text/plain");
		    Transport.send(msg);
	 }
	
	
	/**
	* SimpleAuthenticator is used to do simple authentication
	* when the SMTP server requires it.
	*/
	private class SMTPAuthenticator extends javax.mail.Authenticator {
	
	    public PasswordAuthentication getPasswordAuthentication() {
	        String username = SMTP_AUTH_USER;
	        String password = SMTP_AUTH_PWD;
	        return new PasswordAuthentication(username, password);
	    }
	    
	}

}

