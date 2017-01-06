package com.edw;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class inputvalidation {
	
	private AuditLog Log = new AuditLog(); 
	public static String validate = "pass"; 
	
	 public String validateInput(String user_id,String input, String type, String logMessage, String remote_address, String theatLevel)
     {
         /*
         we want to filter the filenames for expected values. For this example we use only a-z/0-9
         Whenever the values are tampered with, we can assume an attacker is trying to inject malicious input.           
         */
         String validator = "";
    	 if(type.equals("symbols"))
		 {
			 //Characters that may be used to interfere with the XPath query should be blocked, including ( ) = � [ ] : , * / and all whitespace. 
        	 // Any input that does not match the white list should be rejected, not sanitized.
    		 validator = "()='[]:,*/ ";
		 }
		 else if (type.equals("alphanummeric"))		 
			 validator = "^[a-zA-Z0-9]<>?\"\"+$#!";  		 
		 else if (type.equals("nummeric"))
			 validator = "^[0-9]*$";
		 else
			 validator = "";

	      // Create a Pattern object
	      Pattern r = Pattern.compile(validator);
	     // String validate = "pass" ; 
	      // Now create matcher object.
	      Matcher m = r.matcher(input);
	      if (!m.find()) {	
	    	  
	    	    //If there was a match this function returns "pass"
	    	    validate = "validatation failed"; 
	    	   
	    	    //this breach has to be reported into the log files
				Log.SetLog(user_id, logMessage , "FAIL", LocalDateTime.now(), remote_address, theatLevel);
				
				/*
                Set counter; if counter hits 3, the user's session must be terminated.
                After 3 hits the user's account must be blocked.
                For detailed information see the "Audit logs" in code examples.
                */
				
				String result = Log.counter(1,Integer.parseInt(user_id));
				
				if (result.equals("SQL insert query error in update access" )){
					Log.SetLog(user_id, logMessage , "SQL insert query error in update access", LocalDateTime.now(),remote_address,  "");
				}
				if (result.equals("block")){
					Log.SetLog(user_id, logMessage , "block", LocalDateTime.now(), remote_address,  "HIGH");
					validate = "block";
				}
				if (result.equals("terminate")){
					Log.SetLog(user_id, logMessage , "terminate", LocalDateTime.now(), remote_address,  "HIGH");
					validate = "terminate";
				}
				else if (validate.equals("validation failed"))
				{
					Log.SetLog(user_id, logMessage , "validation failed", LocalDateTime.now(), remote_address,  "HIGH");
				}
	      }		
	            
         
		return validate;

     }	 
	 
	 public boolean validateInput(String username, String input, String type, String logMessage, String theatLevel)
     {
         /*
         we want to filter the filenames for expected values. For this example we use only a-z/0-9
         Whenever the values are tampered with, we can assume an attacker is trying to inject malicious input.           
         */
		 String validator = "";
		 
		 if(type.equals("symbols"))
		 {
			 //Characters that may be used to interfere with the XPath query should be blocked, including ( ) = � [ ] : , * / and all whitespace. 
        	 // Any input that does not match the white list should be rejected, not sanitized.
    		 validator = "()='[]:,*/ \t\n\r\f\\x0b";
    		 
		 }
		 else if (type.equals("alphanummeric"))		 
			 validator = "^[a-zA-Z0-9]<>?\"\"+$#!";  		 
		 else if (type.equals("nummeric"))
			 validator = "^[0-9]*$";
		 else
			 validator = "";

	      // Create a Pattern object
	      Pattern r = Pattern.compile(validator);
	      boolean validate = false ; 
	      // Now create matcher object.
	      Matcher m = r.matcher(input);
	      if (!m.find()) {	
	    	  
	    	    //If there was a match this function returns false
	    	    validate = true; 
	    	   
	    	    //this breach has to be reported into the log files
				Log.SetLog("", logMessage , "SUCCESS", LocalDateTime.now(),  theatLevel);   
	      }		
	      else
	      {
	    	    Log.SetLog("", logMessage , "FAIL", LocalDateTime.now(), "NULL");
	    	  
	      }        
         
		return validate;

     }	 
	 
}
