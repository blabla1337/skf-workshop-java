package prime.com.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.FileUploadEvent;
import com.Lib.AuditLog;
import com.Lib.WinRegistry;
import com.Lib.inputvalidation;

@ManagedBean(name="fileUploadController")
public class FileUploader {
	
   private String destination="C:\\test";
   private final static Logger LOGGER = Logger.getLogger(FileUploader.class.getCanonicalName());
   private AuditLog Log = new AuditLog(); 
   inputvalidation validate = new inputvalidation();
   private UIComponent component;
 
    public void upload(FileUploadEvent event) throws IOException, ServletException { 
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        FacesContext.getCurrentInstance().getExternalContext().setResponseContentType("text/html;charset=UTF-8");
        
        UserLogin usr = new UserLogin();
        
        //suppose we got the user ID from the user name of the logged in user. 
        //For the purposes of this demo we assume that the logged in user is admin 
        
        String userID = usr.UserIDfromDB("admin","jdbc/login_Jdbc","java:/comp/env");
        
        boolean continueFunction = true;
        boolean sessiontermination = false;
        boolean blockaccess = false ;
        // Create path components to save the file
        // The location of stored files should always be outside of your root
     	final File f = new File(destination);
       
        //We get the filename for doing different types of tests on it
        final String fileName = event.getFile().getFileName().toString();
                
        /*
        First we check if the value is alphanumeric only to prevent uploading out of intended directory, 
        as well as other injections
        */
        
        if (validate.validateInput(userID, fileName, "alphanummeric", "validation failed",origRequest.getRemoteAddr(),"HIGH") == "validation failed")
        {
           continueFunction = false;
        }
        
        else if (validate.validateInput(userID, fileName, "alphanummeric", "Session Termination",origRequest.getRemoteAddr(),"HIGH") == "terminate")
        {
           origRequest.getSession().invalidate();
           continueFunction = false;
           sessiontermination=true;
        }   
        
        else if (validate.validateInput(userID, fileName, "alphanummeric", "Block access",origRequest.getRemoteAddr(),"HIGH") == "block")
        {
           continueFunction = false;
           blockaccess=true;
        }  
        else 
        {
        	Log.SetLog(userID, "Validated Successfully" , "SUCCESS", LocalDateTime.now(),origRequest.getRemoteAddr(),  ""); 
        	
        }
        
        /*
        The next step would be checking if the file contains the right extension in order to prevent
        a user from uploading files which could be used to harm your system. in this example 
        we check if the last extension found in the file name is a jpg or a png. whenever
        an application just regexes for the extension an attacker could
        bypass the check by uploading an file like: "filename.jpg.php".
        */   
 
        String  StrSpli = FilenameUtils.getExtension(fileName);

        if (!StrSpli.equals("jpg") && !StrSpli.equals("png") )
        {
            continueFunction = false;
     
        }
              
        /*
        If the file came through all the different checks, it is time to upload the file to your system. 
        */
       if (continueFunction == true)
       {     
    	   try {
               copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
           } catch (IOException e) {
        	   LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", new Object[]{e.getMessage()});
           }
    	   
       }
              
       /*
       Now we check the uploaded file for the right mime-type
       We do this after the upload instead of checking the content type header since that header 
       can easily manipulated by an attacker. 
        */             
       List<String> ls = null;
       String key = null;
		try {
			ls = WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE,"SOFTWARE\\Classes\\");
			key = ls.stream().filter(st -> st.matches("."+StrSpli)).findAny().orElse(null);
			
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			LOGGER.log(Level.SEVERE, "Problems reading the extension key on Windows registry. Error: {0}", new Object[]{e.getMessage()});
			
		}
       		
       	String mimeType = "application/unknown";
			try {
				mimeType = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, "SOFTWARE\\Classes\\"+key, "Content Type");
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.SEVERE, "Problems reading the extension value on Windows registry. Error: {0}", new Object[]{e.getMessage()});
			}

 
			if (mimeType == null || !mimeType.equals("image/jpeg"))
       	   {
       		  //If the mimetype is not valid we delete the file from the system.
				
			  f.deleteOnExit();
       		  continueFunction = false;
       		
       	   }   
			    if (continueFunction == false && sessiontermination == false && blockaccess == false)
		        {    
			    	context.addMessage(component.getClientId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, "FAIL!", "FAIL! file has not been uploaded"));
		        }
			      
			    if (continueFunction == true && sessiontermination == false && blockaccess == false)
			    {
			    	context.addMessage(component.getClientId(), new FacesMessage(FacesMessage.SEVERITY_INFO, "SUCCESS!", "SUCCESS! file uploaded!"));
			    }      
			    
			    if (continueFunction == false && sessiontermination == false && blockaccess == true)
		        {  

			    	 context.addMessage(component.getClientId(), new FacesMessage(FacesMessage.SEVERITY_INFO, "Blocked!", "Access Blocked!"));
			    	
		        }
			    
			    if (continueFunction == false && sessiontermination == true && blockaccess == false)
		        {  
			    	 context.addMessage(component.getClientId(), new FacesMessage(FacesMessage.SEVERITY_INFO, "terminated!", "Session terminated!"));
			    	
		        }
 
    }  
 
    public UIComponent getComponent() {
		return component;
	}

	public void setComponent(UIComponent component) {
		this.component = component;
	}

	public void copyFile(String fileName, InputStream in) {
    	
           try {
                 
                // write the inputStream to a FileOutputStream
                OutputStream out = new FileOutputStream(new File(destination + fileName));
              
                int read = 0;
                byte[] bytes = new byte[1024];
              
                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
              
                in.close();
                out.flush();
                out.close();
              
                LOGGER.log(Level.ALL, "File {0} being uploaded to {1}" ,  new Object[]{fileName, destination});
                            
                } catch (IOException e) {
                	 LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", new Object[]{e.getMessage()});
                }
    }  
    
}