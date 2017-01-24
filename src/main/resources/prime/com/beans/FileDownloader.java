package prime.com.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;

import com.Lib.AuditLog;
import com.Lib.inputvalidation;
import com.Lib.whitelist;
 

@ManagedBean(name="fileDownloadController")
@MultipartConfig
public class FileDownloader {
     
	private final static Logger LOGGER = Logger.getLogger(FileUploader.class.getCanonicalName());
	private AuditLog Log = new AuditLog(); 
	private whitelist wl = new whitelist();
	inputvalidation validate = new inputvalidation();
	private UIComponent component;
    private String file;
    private File fileplace;
    
    public FileDownloader() {        
    	
    	FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        FacesContext.getCurrentInstance().getExternalContext().setResponseContentType("text/html;charset=UTF-8");
        
        String action = ""; 
        boolean proceed = false ;
        String mimetype = "";
        
		 // Create path components to save the file
         // The location of stored files should always be outside of your root
         
        file = "C:\\Users\\xvassilakopoulos\\Desktop\\test\\tsec.jpg";
        
        fileplace = new File(file); 
        
        
        String fileNameformat = fileplace.getName();
        
        String filenameparts[] = fileNameformat.split(Pattern.quote("."));
        String fileName = filenameparts[0];
        String afterdot = filenameparts[1];
         
        /*
        First we check if the value is alphanumeric only to prevent uploading out of intended directory, 
        as well as other injections
        */
        
        /* in normal situations the userID should be retrieved from session or from the web page made the request. 
         * For demonstration purposes we assume that the usedID is always 2, which indicated the Administration ID number. 
         */
         
        if (validate.validateInput("2", fileName, "alphanummeric", "validation failed",request.getRemoteAddr(),"HIGH") == "validation failed")
        {
           proceed = false;
           action = "validation failed";
        }
        
        else if (validate.validateInput("2", fileName, "alphanummeric", "Session Termination",request.getRemoteAddr(),"HIGH") == "terminate")
        {
        	proceed = false;
        	action = "terminate";
        }   
        
        else if (validate.validateInput("2", fileName, "alphanummeric", "Block access",request.getRemoteAddr(),"HIGH") == "block")
        {
        	proceed = false;
        	action = "block";
        }  
        else 
        {
        	Log.SetLog("2", "Validated Successfully" , "SUCCESS", LocalDateTime.now(),request.getRemoteAddr(),  "");
        	action = "Validated Successfully";
        	proceed = true;
        }
        
        if (proceed == true)
        {
        	 //Here we connect to the database by means of a connection string as configured in the web.xml and /META-INF/context.xml 
			Connection conn = null;
		    try {
					
			Context initContext = new InitialContext();
			Context webContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)webContext.lookup("jdbc/myJdbc");
			conn = ds.getConnection();	

			  //Here we select the number of counts from aggregate column in order to verify the number of connections:
		      String query = "SELECT * FROM privileges WHERE privilegeID=?";
		   
		      PreparedStatement st = conn.prepareStatement(query);
		      
		      /* in normal situations the privilegeID should be retrieved from database based on UserID retrieved from the active session 
		       * or from the web page made the request. 
		       * For demonstration purposes we assume that the privilegeID is always 1, which indicated the Administration privilege ID number. 
		       */
		      
		      st.setString(1, "1");

		      // execute the query, and get a java result set
		      //We bind the parameter in order to prevent SQL injections

		      ResultSet rs = st.executeQuery();
		      
		      while (rs.next())
		      {
		    	  mimetype   = rs.getString("mimeType");
		      }
		      st.close();
	          conn.close();
			} catch (SQLException | NamingException e) {
				LOGGER.log(Level.SEVERE, "cannot read from database. check query = {0}", e.toString());
			}	  

        	/*
            We also define the mime-type per download file.
            This is because whenever a user can only download images it is not necessary to set
            an uncommon content-type header for it.
            NOTE: These mime-types should not be stored based upon the mime-type which was send 
            the response header when the user uploaded the file. This value can be easily 
            manipulated with an intercepting PROXY. You should get the mime-type from the file
            itself after it was stored on the server.
            */
        	response.reset();
        	response.setContentType(mimetype);
        	response.addHeader("Cache-Control", "no-cache");
        	response.addHeader("Content-Disposition", "attachment; filename=" + fileName + "." + afterdot + ";");
        	
        	OutputStream out;
			try {
				out = response.getOutputStream();
			
        	FileInputStream in = new FileInputStream(fileplace);
        	byte[] buffer = new byte[4096];
        	int length;
        	while ((length = in.read(buffer)) > 0){
        	    out.write(buffer, 0, length);
        	}
        	in.close();
        	out.flush();
        	
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Cannot download file = {0}", e.toString());
			
			}
          
        }
        else if (file == null)
        {
        	action = "empty";
        }
		
		if (action.equals("terminate"))
		{
			 request.getSession().invalidate();
	    	 request.setAttribute("msg","Session terminated! file has not been downloaded");		     
		     ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		     try {
				ec.redirect(ec.getRequestContextPath() + "/Menu.xhtml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.SEVERE, "Cannot redirect = {0}", e.toString());
			}
		}
		if (action.equals("validation failed"))
		{
			 request.getSession().invalidate();
	    	 context.addMessage(component.getClientId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, "FAIL!", "Session terminated! file has not been downloaded"));
		    
		}
		if (action.equals("block"))
		{ 
			 request.getSession().invalidate();		     
			 context.addMessage(component.getClientId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, "FAIL!", "Session terminated with Blocked Access! file has not been downloaded"));
		}
		if (action.equals("Validated Successfully"))
		{
    
			context.addMessage(component.getClientId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, "SUCCESS!", "file downloaded"));
		     
		}
	}
	
	 public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void fixedDownloads(String file, String download, HttpServletResponse response)
     {
		 
		 /*
         The second example is for whenever you are providing users with fixed downloads
         such as manuals etc. We do not only check if the file just exists, because that would
         allow an attacker to also download important other files from your server, so instead
         we white-list them.
         */
		  if (wl.whitelisting(file, download) != false)
          {
			  
			    response.reset();
	        	response.setContentType("text/plain");
	        	response.addHeader("Cache-Control", "no-cache");
	        	response.addHeader("Content-Disposition", "attachment; filename=" + file + ";");
	        	
	        	OutputStream out;
				try {
				
				out = response.getOutputStream();
			
	        	FileInputStream in = new FileInputStream(file);
	        	byte[] buffer = new byte[4096];
	        	int length;
	        	while ((length = in.read(buffer)) > 0){
	        	    out.write(buffer, 0, length);
	        	}
	        	in.close();
	        	out.flush();
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "Cannot download file = {0}", e.toString());
			
				}
			  
          }
		 
		 
     }
	
 
}