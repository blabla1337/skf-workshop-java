package prime.com.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.Lib.AuditLog;
import com.Lib.hashing;
import com.Lib.inputvalidation; 

@ManagedBean
@SessionScoped
public class xpath  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	//First we include the audit log class.
	AuditLog Log = new AuditLog();
		
	//Second we include the password hash.
	hashing hash = new hashing();

	
	final static Logger logger = Logger.getLogger(xpath.class);
	
	public static final String AUTH_KEY = "User: ";
	
	private String employeeID;
	private String employeeRole;
	private String username;
	private String password; 


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

	public String getEmployeeRole() {
		return employeeRole;
	}

	public void setEmployeeRole(String employeeRole) {
		this.employeeRole = employeeRole;
	}

	public String getEmployeeID() {
        return employeeID;
    }
 
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }
    
   	
	public void loginAction(String userId){
		
		
    	//we include the random input validation class.
        
        HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpServletResponse origResponse = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(AUTH_KEY, username);
        
        String uname = this.getUsername(); 
       
        
 
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
             FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(AUTH_KEY, uname);
      
		
	}

	 public void xpathLogin(ActionEvent event) throws IOException 
     {
          /*
          In order to prevent x-path injections we have to treat these query's similar as 
          to the SQL query's. An option would be to use a pre-compiled XPath query.
          But since this is a third party library i consider it untrusted and would
          rather use our own crafted escaping function.

          NOTE: if you want to look into the pre-compiled x-path library you can find more
          detailed information about it on: http://www.onjava.com/pub/a/onjava/2005/01/12/xpath.html
          */

          /*
          As with every injection prevention we first focus on the expected user values
          in this case we expect an integer we use our single input validation method for integers
          See the "input validation" code example for more detailed information.

          For the purpose of this example we use the following XML snippet 
          
          		<?xml version="1.0" encoding="utf-8"?>
						<Employees>
						   <Employee ID="1">
						      <FirstName>Arnold</FirstName>
						      <LastName>Baker</LastName>
						      <UserName>ABaker</UserName>
							  <id>1</id>
						      <Password>+udURk9/QIFiHTT0cZruIrdvB57hJtNfKQ==</Password>
							  <salt>5hUCuJMMu/rK/WTy449Ysv+e8b1/LtodLw==</salt>
						      <Type>Admin</Type>
						   </Employee>
						   <Employee ID="2">
						      <FirstName>Peter</FirstName>
						      <LastName>Pan</LastName>
						      <UserName>PPan</UserName>
							  <id>2</id>
						      <Password>Cftv1ip25PJdmf8DdfU5NM/K/cjEqEAthw==</Password>  <!-- NoTelling -->
							  <salt>/KAMdM09RVNZ8nOWHAsqKzrfyY6VEZgRQA==</salt>
						      <Type>User</Type>
						   </Employee>
						</Employees>
          */
		 RequestContext context = RequestContext.getCurrentInstance();
		 FacesMessage message = null;
	     boolean loggedIn = false;
		 boolean continueFunction = true;         
         inputvalidation validate = new inputvalidation();
  
         //Here we put the variable in our input validation method in order to prevent untrusted user input from parsing
         //NOTE: logging and countering is also done in your validation method
         
         //Input used into an XPATH expression must not contains any of the characters below:

         //	 ( ) = ' [ ] : , * / WHITESPACE
         
         //Another method of avoiding XPath injections is by using variable into XPATH expression with a variable resolver enabled evaluator. 
         //See XPath parameterization example
         
	     if (validate.validateInput(username,username,"symbols", "x-path input validation", "HIGH") == false) 
	     { continueFunction = false; }
	     
	     if (validate.validateInput(username,password,"symbols", "x-path input validation", "HIGH") == false) 
	     { continueFunction = false; }
	     
	
         //Only if our validation function returned true we put the user input in the function
         //fXmlFile is the java.io.File object of the example XML document.
         
         File fXmlFile = new File("C:\\xmldb\\users.xml");
          
         if (continueFunction == true)
         {     	     	 
				try { 		
					
					//The evaluate methods in the XPath and XPathExpression interfaces 
					//are used to parse an XML document with XPath expressions.					
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	       			DocumentBuilder builder = factory.newDocumentBuilder();
	       		    //Create an InputSource for the example XML document.
	        	    //An InputSource is a input class for an XML entity.	        	    
	       			Document document = builder.parse(new InputSource(new FileInputStream(fXmlFile)));
	       		    //The XPathFactory class is used to create an XPath object.
					//Create an XPathFactory object with the static newInstance method of the XPathFactory class.
	        	    XPathFactory xPathfactory = XPathFactory.newInstance();
	        	    //Create an XPath object from the XPathFactory object with the newXPath method.  
	        	    XPath xpath = xPathfactory.newXPath();
	        	    //Create and compile an XPath expression with the compile method of the XPath object. 
	        	    //As an example, select the user ID attribute.
	        	    //An attribute in an XPath expression is specified with an @ symbol. 
	        	    //For further reference on XPath expressions, 
	        	    //see the XPath specification for examples on creating an XPath expression.
	        	    
	          	    //The evaluate method of the XPathExpression interface evaluates
	        	    //either an InputSource or a node/node list of the types org.w3c.dom.
	        	    //Node, org.w3c.dom.NodeList, or org.w3c.dom.Document.
	        	    //Evaluate the XPath expression with the InputSource of the example XML document to evaluate over.	
	        	    
	        	   	        	    
	        	    String salt= "/Employees/Employee[UserName='" + username + "']/salt";
	        	    XPathExpression salt_expr = xpath.compile(salt);	              	    
	        	    String Salt_result = salt_expr.evaluate(document, XPathConstants.STRING).toString();         	 
	        	  
	        	    String Password = hash.hashPassword(Salt_result, password);
	        	    
	        	    String userID= "/Employees/Employee[UserName='" + username + "' and  Password='" + Password + "']/id";
	        	    XPathExpression userID_expr = xpath.compile(userID);
	        	    String userID_result = userID_expr.evaluate(document, XPathConstants.STRING).toString();
	        	    
	        	    String login = "/Employees/Employee[UserName='" + username + "' and  Password='" + Password + "']/Type";   	    	        	    	        	 
	        	    XPathExpression login_expr = xpath.compile(login);
	        	    String login_result = login_expr.evaluate(document, XPathConstants.STRING).toString(); 
	        	    
				 	
				 	if (login_result.equals(""))
				 	{
				 		//the connection has to be reported into the log files
			            Log.SetLog(username, "", "Login failed!", LocalDateTime.now(), "FAIL", "NULL");
			            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials"); 
				 		FacesContext.getCurrentInstance().getExternalContext().redirect("xpath.xhtml");
				 	}
				 	else if (login_result.equals("Admin"))
				 	{
				 		this.loginAction(userID_result);
				 		//the connection has to be reported into the log files
			            Log.SetLog(username, "", "Login successfully!", LocalDateTime.now(), "SUCCESS", "");
				 		loggedIn = true;
			            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);
				 		FacesContext.getCurrentInstance().getExternalContext().redirect("admin_page.xhtml");
				 	}
				 	else if (login_result.equals("User"))
				 	{
				 		this.loginAction(userID_result);
				 		Log.SetLog(username, "", "Login successfully!", LocalDateTime.now(), "SUCCESS", "");
				 		loggedIn = true;
			            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);
				 		FacesContext.getCurrentInstance().getExternalContext().redirect("user_page.xhtml");
				 	}	
				 	  
			        FacesContext.getCurrentInstance().addMessage(null, message);
			        context.addCallbackParam("loggedIn", loggedIn);
				 	
				} catch (Exception e) {
	       			e.printStackTrace();
	       		}      	 
          }         
     }
}
