package uk.ac.imperial.smartmeter.smx;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class MyAuthenticator extends Authenticator {
	   
		   /**
		   * Called when password authorization is needed.
		   * @return The PasswordAuthentication collected from the
		   * user, or null if none is provided.
		   */
		   protected PasswordAuthentication getPasswordAuthentication()
		      {
		      return new PasswordAuthentication ( "Operator", "Sirius2015!".toCharArray() );
		      }
}
