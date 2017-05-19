	package prime.com.beans;

	import java.io.File;
	import java.io.IOException;
	import java.io.Serializable;
	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	import java.time.LocalDateTime;
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

	import com.Lib.AuditLog;
	import com.Lib.Hashing;
	import com.Lib.InputValidation;
	 
	@ManagedBean
	@SessionScoped
	public class UserLoginVulnerable  implements Serializable {
		
		private static final long serialVersionUID = 1L;
		final static Logger logger = Logger.getLogger(UserLogin.class);
		private String password ;
	    public static String username ;
	    public String passwordHash;
	    public String userId;
		private String salt;
		private String AUTH_KEY = "User: ";
		private String email;		
		private String token;

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
	    	UserLoginVulnerable.username = username;
	    }
	 
	    public String getPassword() {
	        return password;
	    }
	 
	    public void setPassword(String password) {
	        this.password = password;
	    }
	    
		public String getToken() {
			return token;
		}
		
		public void setToken(String token) {
			this.token = token;
		}
		
		public String UserIDfromDB(String uname, String datasource, String initcontext){
			
			String connect  = "" ; 
			 
	        Connection conn = null;
		    try {
				
			 Context initContext = new InitialContext();
			 Context webContext  = (Context)initContext.lookup(initcontext);
			 DataSource ds = (DataSource)webContext.lookup(datasource);
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
		    	  this.setUsername(rs.getString("username"));
		    	  this.setPasswordHash(rs.getString("password"));
	              this.setSalt(rs.getString("salt"));
	              this.setUserId(rs.getString("userID"));
	              connect = this.getUserId() ; 
		      }
		      
		      st.close();
		      conn.close();
		    
			} catch (SQLException | NamingException e) {
				 logger.error("cannot search database. check query" + e.toString() );
			}
			return connect ;
			
		}
		   
		public boolean dbconnection(String uname, String datasource, String initcontext){
			
			boolean connect  = false ; 
			 
	        Connection conn = null;
		    try {
				
			 Context initContext = new InitialContext();
			 Context webContext  = (Context)initContext.lookup(initcontext);
			 DataSource ds = (DataSource)webContext.lookup(datasource);
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
		    	  this.setUsername(rs.getString("username"));
		    	  this.setPasswordHash(rs.getString("password"));
	              this.setSalt(rs.getString("salt"));
	              this.setUserId(rs.getString("userID"));
	              connect = true ; 
		      }
		      
		      st.close();
		      conn.close();
		    
			} catch (SQLException | NamingException e) {
				 logger.error("cannot search database. check query" + e.toString() );
			}
			return connect ;
			
		}
	    public String getSalt() {
			return salt;
		}

		public void setSalt(String salt) {
			this.salt = salt;
		}

		public String getPasswordHash() {
			return passwordHash;
		}

		public void setPasswordHash(String passwordHash) {
			this.passwordHash = passwordHash;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void login(ActionEvent event) throws IOException {
	    	//First we include the audit log class.
	    	AuditLog Log = new AuditLog();
	    		
	    	//Second we include the password hash.
	    	Hashing hash = new Hashing();

	    	//Last we include the random input validation class.
	    	InputValidation validate = new InputValidation();
	        RequestContext context = RequestContext.getCurrentInstance();
	        HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	        HttpServletResponse origResponse = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
	        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(AUTH_KEY, username);
	        FacesMessage message = null;
	        boolean loggedIn = false;
	        String user_name = "";         
	        String uname = this.getUsername(); 
	       
	        String userId = "";

	        //we also validate the username input, if it was bad we empty the string:
	        //if (validate.validateInput(username, username, "alphanumeric", "LOW", "0") != true) { username = ""; }
	     
	        //Here we connect to the database by means of a connection string as configured in the web.xml and /META-INF/context.xml 
		    boolean connect = this.dbconnection(uname,"jdbc/login_Jdbc","java:/comp/env");
		    
		    /*
	        We validate the password see "Password storage(salting stretching Hashing)" in the code examples
	        for more detailed information:
	        */
		   if (connect == true)
	       if (hash.Validate(this.getPasswordHash(), this.getSalt(), password) == true)
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
	        	 
	        	 //now create a new cookie with this UUID value
	        	 
	        	 /*
	             Put id in a session for query identifier based authentication
	             See "identifier based authentication" code example for more information
	              */
	        	 
	             origRequest.getSession().setAttribute("userID", userId);

	        	 Cookie newCookie = new Cookie("AuthToken", randomUUIDString);   
	        	 
	        	 origResponse.addCookie(newCookie);
	        	 
	        	//the connection has to be reported into the log files
	             Log.SetLog("User: " + uname, "succesfull validation of user credentials ", "login was OK!",  LocalDateTime.now(), "SUCCESS", "");    

	             origRequest.getSession().setAttribute(AUTH_KEY, uname);
	            
	        }
	        else
	        {
	            //the connection has to be reported into the log files
	            Log.SetLog("User: " + username, "", "Login failed!", LocalDateTime.now() , "FAIL", "NULL");
	            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");  
	            FacesContext.getCurrentInstance().getExternalContext().redirect("UserLogin.xhtml");
	        }	
	    
	         String passhash = hash.hashPassword(salt, password);
	         
	         if(username != null && "admin".equals(user_name) && password != null && passhash.equals(passwordHash)) {
	             loggedIn = true;
	             message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);
	             FacesContext.getCurrentInstance().getExternalContext().redirect("admin_page_Vulnerable.xhtml");
	         } 
	         else if(username != null && !"admin".equals(user_name) && password != null && passhash.equals(passwordHash)){
	       
	             loggedIn = false;
	             message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);
	             FacesContext.getCurrentInstance().getExternalContext().redirect("no_admin_page_Vulnerable.xhtml");
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
	        Xpath x = new Xpath();
	                  
	        if (log_out.isCookie_rem())
	        {
	        	 try {
					FacesContext.getCurrentInstance().getExternalContext().redirect("Menu.xhtml");
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
	        	String authkey = (String) fc.getExternalContext().getSessionMap().get(AUTH_KEY); 
	        	boolean connect_db = this.dbconnection(authkey, "jdbc/login_Jdbc","java:/comp/env");
	        	File fXmlFile = new File("C:\\xmldb\\users.xml");
	        	String login_xpath = x.checkuser(fXmlFile,authkey);
	        	
	        	if (fc.getExternalContext().getSessionMap().get(AUTH_KEY) != null)
		        if ( (connect_db != true && login_xpath.equals("")) || (connect_db == true && !login_xpath.equals(""))){
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
