package prime.com.beans;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.Lib.AuditLog;
import com.Lib.hashing;


@ManagedBean
public class RegisterUser {

	   private AuditLog Log = new AuditLog();
	    private hashing hash = new hashing();
		final static Logger logger = Logger.getLogger(RegisterUser.class);

		private String Password; 
		private FacesMessage message;
		private String Password2;  
		public String getPassword2() {
			return Password2;
		}
		public void setPassword2(String password2) {
			Password2 = password2;
		}
		private String username;
		private String email;
		
		
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		private int privID;  

		public int getPrivID() {
			return privID;
		}
		public void setPrivID(int privID) {
			this.privID = privID;
		}
		public String getPassword() {
			return Password;
		}
		public void setPassword(String password) {
			Password = password;
		}
		
		
		public void updateUserPassword()
		{	
			//Set the aggregate access control 
			Aggregate ag = new Aggregate();
			HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String userID = (String) origRequest.getSession().getAttribute("userID");
			
	    	//Then we encrypt the password
	    	String salt = "";
	    	String passhash = "";
	    	boolean error = false;
			try {
				salt = hash.createSalt(Password);	    	 
	            passhash = hash.hashPassword(salt, Password);
	        
			} catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
				logger.warn(e.toString());
			}
			
            ag.aggregateControl(0);
            ag.setUserName(username);
    		ag.setUserID(userID);     

    	    boolean invalidate_sessions = ag.aggregateControl(0);

	        
    	    if (invalidate_sessions == true)
    	    {
    	    	origRequest.getSession().invalidate();
    	    
				 Log.SetLog("", "failed to update the password for the User: " + username + " - Session Terminated due to many connections to the database - User account was locked out - Contanct to your administrator", "FAIL!", LocalDateTime.now(), null);
				 message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Update Error - password update failed for user : "+ username + "!", "");  
		         FacesContext.getCurrentInstance().addMessage(null,message);
    	    }
    	    else
    	    {   	
    	
			 //Here we connect to the database by means of a connection string as configured in the web.xml and /META-INF/context.xml 
	    	 Connection conn = null;
			    try {	
				Context initContext = new InitialContext();
				Context webContext  = (Context)initContext.lookup("java:/comp/env");
				DataSource ds = (DataSource)webContext.lookup("jdbc/login_Jdbc");
				conn = ds.getConnection();	
				
				//After successful validation we enter the new user into the database
			     String query = "UPDATE users SET password=?, salt=? WHERE username=?";
				     
				PreparedStatement st = conn.prepareStatement(query);
				
				st.setString(1, passhash);
				st.setString(2, salt);
				st.setString(3, username);

			   st.executeUpdate();
						
				 st.close();
			     conn.close();
			    
				} catch (SQLException | NamingException e) {
					 logger.error("cannot search database. check query" + e.toString() );
					 error = true;
					 Log.SetLog("", "failed to update password for username" + username + " ", "FAIL!", LocalDateTime.now(), null);
					 message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Update Error - password update failed for user : "+ username + "!", "");  
			         FacesContext.getCurrentInstance().addMessage(null,message);
				}
			    if (error == false)
			    {
			    	message = new FacesMessage(FacesMessage.SEVERITY_WARN, "User " + username + "has a new password", "password updated");  
		        	FacesContext.getCurrentInstance().addMessage(null,message);
			    }
    	    }
	 }
			
		
		public boolean userCheck(String user_name){
			
			
			boolean isTrue = false; 
			 //Here we connect to the database by means of a connection string as configured in the web.xml and /META-INF/context.xml 
			Connection conn = null;
		    try {
			
			
			Context initContext = new InitialContext();
			Context webContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)webContext.lookup("jdbc/login_Jdbc");
			conn = ds.getConnection();	
			

			  //Here we select the number of counts from aggregate column in order to verify the number of connections:
		      String query = "SELECT * FROM users WHERE username = ?";
		   
		      //We bind the parameter in order to prevent SQL injections
		      PreparedStatement st = conn.prepareStatement(query);
		      st.setString(1, user_name);
		      
		      // execute the query, and get a java result set


		      ResultSet rs = st.executeQuery();
		      
		      if (!rs.isBeforeFirst() && !rs.next())
		      {
		    	  isTrue = true;
		      }
		      
		      st.close();
		      conn.close();
		    
			} catch (SQLException | NamingException e) {
				 logger.error("cannot search database. check query" + e.toString() );
			}
			return isTrue;
		}
		
		 public void userRegister(){
			 
			 /*
		        Whenever the user gains the ability to register himself or change his user
		        credentials you must always enforce the application to compare the length of the
		        submitted string against the length of the allowed string length in your database
		        structure in order to prevent SQL column truncation.
		        */
			 
			 int length = username.length(); 
			   
			 	/*
		        We now compare the length of the username against the allowed string length in
		        The database structure
		        */
			 if(length >= 21){
		            //If length is to large the function must return false and the result must be logged.
				    Log.SetLog(username, "Username was to long!", "FAIL!", LocalDateTime.now(), null);
	 
		        }
			 
			 //If true then register the user!       
		     if(this.userCheck(username) == true){
		    	// isTrue = true;
		    	 
		    	//Then we encrypt the password
		    	String salt = "";
		    	String passhash = "";
				try {
					salt = hash.createSalt(Password);	    	 
		            passhash = hash.hashPassword(salt, Password);
		        
				} catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
		        
				 //Here we connect to the database by means of a connection string as configured in the web.xml and /META-INF/context.xml 
		    	 Connection conn = null;
				    try {	
					Context initContext = new InitialContext();
					Context webContext  = (Context)initContext.lookup("java:/comp/env");
					DataSource ds = (DataSource)webContext.lookup("jdbc/login_Jdbc");
					conn = ds.getConnection();	
					
					//After successful validation we enter the new user into the database
				     String query = "INSERT INTO users"
				     		+ " (username, password, salt)"
				     		+ " VALUES"
				     		+ " (?, ?, ?)";
					     
					PreparedStatement st = conn.prepareStatement(query);
					
					st.setString(1, username);
					st.setString(2, passhash);
					st.setString(3, salt);

				    ResultSet rs = st.executeQuery();
							
				     rs.close();
					 st.close();
				     conn.close();
				    
					} catch (SQLException | NamingException e) {
						 logger.error("cannot search database. check query" + e.toString() );
					}
				   
				    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "User Registration", "User has been registered");  
			        FacesContext.getCurrentInstance().addMessage(null,message);

		        }
		     else
		     {
		    	 Log.SetLog("", "Username" + username + " already exists!", "FAIL!", LocalDateTime.now(), null);
		    	 message = new FacesMessage(FacesMessage.SEVERITY_WARN, "register Error", " already exists!");  
		         FacesContext.getCurrentInstance().addMessage(null,message);
		     }

		 }
	
	
}
