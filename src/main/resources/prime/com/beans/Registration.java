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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.Lib.AuditLog;
import com.Lib.Hashing;

@ManagedBean
public class Registration {
	
	final static Logger logger = Logger.getLogger(Registration.class);
	 private AuditLog Log = new AuditLog();
	private String Password; 
	private String Password2;
	public String getPassword2() {
		return Password2;
	}
	public void setPassword2(String password2) {
		Password2 = password2;
	}
	private String username;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	private int userID;  

	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
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
	
	
	
	public void register() throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException
	{
		    FacesMessage fail_message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "User Info", "failed to add user");
		    FacesMessage success_message = new FacesMessage(FacesMessage.SEVERITY_INFO, "User Info", "User Added Succesfully!");
			Hashing hasher = new Hashing();	
	    	String salt = hasher.createSalt(Password);  
	    	boolean added = false ; 
	    	
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
			    Log.SetLog(username, "Username was to long!", "FAIL!", LocalDateTime.now(), "");
			    added = false ;
	        }
		 
		 
		 
		 
		 //If true then register the user!       
	     if(this.userCheck(username) == true){
	    	/*
            Then we encrypt the password 
            */	    	
	    	String newpassword = hasher.hashPassword(salt, Password);
	    	Connection conn = null;
		    try {
				
			Context initContext = new InitialContext();
			Context webContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)webContext.lookup("jdbc/login_Jdbc");
			conn = ds.getConnection();	

			    
		     	//First we update the new password for the user
		  	    String query ="INSERT INTO users (username,password,salt,userID) VALUES (?,?,?,?)";
		  	    
		  	    //Then we destroy the reset token by setting it's value to NO
			    PreparedStatement st = conn.prepareStatement(query);
			    st.setString(1, username);
			    st.setString(2, newpassword); 
			    st.setString(3, salt); 
			    st.setInt(4, userID); 
			    
			    // execute the query, and get a java result set
			    st.executeUpdate(); 
			    
			    conn.close();
			    added=true;
		 
		    } catch (SQLException | NamingException e) {
		    	 added=false;
				 logger.error("cannot insert  values into the database. check query " + e.toString() );
			}    
	}
	     if(added != true)
	    	  RequestContext.getCurrentInstance().showMessageInDialog(fail_message);
	     else
	    	 RequestContext.getCurrentInstance().showMessageInDialog(success_message);
		
	}
	
}
	
	
	
