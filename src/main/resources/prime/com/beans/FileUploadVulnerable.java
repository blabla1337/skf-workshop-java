package prime.com.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.FileUploadEvent;

import com.Lib.AuditLog;
import com.Lib.InputValidation;

@ManagedBean(name="fileUploadVulnerableController")
	public class FileUploadVulnerable {
		
	   private String destination="C:\\Users\\xvassilakopoulos\\Desktop\\test\\";
	   private final static Logger LOGGER = Logger.getLogger(FileUploader.class.getCanonicalName());
	   private AuditLog Log = new AuditLog(); 
	   InputValidation validate = new InputValidation();
	   private UIComponent component;
	 
	    public void upload(FileUploadEvent event) throws IOException, ServletException { 
	        FacesContext context = FacesContext.getCurrentInstance();
	        FacesContext.getCurrentInstance().getExternalContext().setResponseContentType("text/html;charset=UTF-8");
	 
   
	    	   try {
	               copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
	           } catch (IOException e) {
	        	   LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", new Object[]{e.getMessage()});
	           }
	    	   

	   	context.addMessage(component.getClientId(), new FacesMessage(FacesMessage.SEVERITY_INFO, "SUCCESS!", "SUCCESS! file uploaded!"));
	
	 
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
	

