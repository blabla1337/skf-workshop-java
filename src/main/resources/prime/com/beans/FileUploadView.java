package prime.com.beans;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
 
@ManagedBean
public class FileUploadView {
     
    private File file;
 
    public File getFile() {
        return file;
    }
 
    public void setFile(File file) {
        this.file = file;
    }
     
    public void upload(FileUploadEvent event) {
        
    	file = (File) event.getFile();
    	
    	if(file != null) {
        	
        	
        	
    		try (FileInputStream fis = new FileInputStream(file)) {

    		    	int content;
    			while ((content = fis.read()) != -1) {
    				// convert to char and display it
    				System.out.print((char) content);
    			}

    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
        	
            FacesMessage message = new FacesMessage("Succesful", file.getName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
