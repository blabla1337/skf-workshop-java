package prime.com.beans;


import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.edw.AuditLog;
import com.edw.hashing;
import com.edw.inputvalidation;
 
@ManagedBean
@SessionScoped
public class UserLogin  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(UserLogin.class);
	private String password ;
	private String username;
	private int userID;
	private String salt;
	private String access ;
	private int privilege;	 
	private String password2;
	
	private String email;
	
	public String Sessiontoken ;
	
	private String token;
	
	

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public static final String AUTH_KEY = "User: ";
     

    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getSessiontoken() {
		
		return Sessiontoken;
	}
	public void setSessiontoken(String sessiontoken) {
		Sessiontoken = sessiontoken;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	   
    public void login(ActionEvent event) throws IOException {
    	//First we include the audit log class.
    	AuditLog Log = new AuditLog();
    		
    	//Second we include the password hash.
    	hashing hash = new hashing();

    	//Last we include the random input validation class.
        inputvalidation validate = new inputvalidation();
        RequestContext context = RequestContext.getCurrentInstance();
        HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpServletResponse origResponse = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(AUTH_KEY, username);
        FacesMessage message = null;
        boolean loggedIn = false;
        String user_name = ""; 
        
        String uname = this.getUsername(); 
        
        
        String passwordHash = "";
        String userId = "";

        //we also validate the username input, if it was bad we empty the string:
        if (validate.validateInput(username, username, "alphanumeric", "LOW", "0") != true) { username = ""; }
     
    	//Here we connect to the database by means of a connection string as configured in the web.xml and /META-INF/context.xml  
        Connection conn = null;
	    try {
			
		 Context initContext = new InitialContext();
		 Context webContext  = (Context)initContext.lookup("java:/comp/env");
		 DataSource ds = (DataSource)webContext.lookup("jdbc/login_Jdbc");
		 conn = ds.getConnection();	

		 //Here we select the user from the users table
	      String query = "SELECT * from users WHERE username = ?";
	   
	      PreparedStatement st = conn.prepareStatement(query);
	      st.setString(1, uname);
	      
	      //execute the query, and get a java result set
	      //We bind the parameter in order to prevent SQL injections

	      ResultSet rs = st.executeQuery();
	      
	      while (rs.next())
	      {
	    	  user_name   = rs.getString("username");
	    	  passwordHash = rs.getString("password");
              salt = rs.getString("salt");
              userId = rs.getString("userID");
	      }
	      
	      st.close();
	      conn.close();
	    
		} catch (SQLException | NamingException e) {
			 logger.error("cannot search database. check query" + e.toString() );
		}
	    
	    /*
        We validate the password see "Password storage(salting stretching hashing)" in the code examples
        for more detailed information:
        */
        if (hash.Validate(passwordHash, salt, password) == true)
        {
        	/*
            This is is to prevent session fixation, after login we create a new cookie which
            we then use to authenticate. This value can not be fixated since it is set after 
            login.

            create a new UUID and save into the session:
            */
        	 UUID uuid = UUID.randomUUID();
        	 String randomUUIDString = uuid.toString();
        	 //initiate a session
        	 origRequest.getSession(true);
        	 origRequest.getSession().setAttribute("AuthToken", randomUUIDString);     	
        	// now create a new cookie with this UUID value
        	 Cookie newCookie = new Cookie("AuthToken", randomUUIDString);   
        	 
        	 origResponse.addCookie(newCookie);
        	 
        	//the connection has to be reported into the log files
             Log.SetLog("", "", "login was OK!", null, "SUCCESS", "NULL");    
                
             /*
             Put id in a session for query identifier based authentication
             See "identifier based authentication" code example for more information
              */

             origRequest.getSession().setAttribute("userID", userId);
             FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(AUTH_KEY, user_name);
            
        }
        else
        {
            //the connection has to be reported into the log files
            Log.SetLog("", "null", "Login failed!", null, "FAIL", "NULL");
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");  
            FacesContext.getCurrentInstance().getExternalContext().redirect("UserLogin.xhtml");
        }	
    
         String passhash = hash.hashPassword(salt, password);
         
         if(username != null && "admin".equals(user_name) && password != null && passhash.equals(passwordHash)) {
             loggedIn = true;
             message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);
             FacesContext.getCurrentInstance().getExternalContext().redirect("admin_page.xhtml");
         } 
         else if(username != null && !"admin".equals(user_name) && password != null && passhash.equals(passwordHash)){
       
             loggedIn = false;
             message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);
             FacesContext.getCurrentInstance().getExternalContext().redirect("no_admin_page.xhtml");
         }
        else 
        {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");  
            FacesContext.getCurrentInstance().addMessage(null,message);
        }
         
        FacesContext.getCurrentInstance().addMessage(null, message);
        context.addCallbackParam("loggedIn", loggedIn);
    }
          
    public boolean isLoggedIn() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(AUTH_KEY) != null;     
    }
    
    public void isAuthenticated(ComponentSystemEvent event){
        
        FacesContext fc = FacesContext.getCurrentInstance();
        Logout log_out = new Logout();
                  
        if (log_out.isCookie_rem())
        {
        	 try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("UserLogin.xhtml");
			} catch (IOException e) {
				logger.warn(e.toString());
			}
        	 log_out.setCookie_rem(false);
        }
        
        else if (FacesContext.getCurrentInstance().getExternalContext().getSession(false) == null){
        		 ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler(); 
        			nav.performNavigation("UserLogin");}
        
        else if (this.isLoggedIn())
        {
        	if (fc.getExternalContext().getSessionMap().get(AUTH_KEY) != null)
	        if (!this.getUsername().equals(fc.getExternalContext().getSessionMap().get(AUTH_KEY))){
	          ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();        
	          nav.performNavigation("access-denied");
	        }
        }
        else if (!this.isLoggedIn())
	   {
		   ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler(); 
		   nav.performNavigation("access-denied");
	   } 
    }
}