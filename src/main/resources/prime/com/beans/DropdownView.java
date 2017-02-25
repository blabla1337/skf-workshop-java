package prime.com.beans;

	import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
	 
	@ManagedBean
	@ViewScoped
	public class DropdownView implements Serializable {
	     
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		 private final static Logger LOGGER = Logger.getLogger(DropdownView.class.getCanonicalName());
		private Map<String,Map<String,String>> data = new HashMap<String, Map<String,String>>();
	    private String database; 
	    private Map<String,String> databases;
	     
	    @PostConstruct
	    public void init() {
	    	databases  = new HashMap<String, String>();
	    	databases.put("Login", "Login");	
	    }
	 
	    public Map<String, Map<String, String>> getData() {
	        return data;
	    }
	 
	    public String getDatabase() {
	        return database;
	    }
	 
	    public void setDatabase(String database) {
	        this.database = database;
	    }
	 

	    public Map<String, String> getDatabases() {
	        return databases;
	    }

	    
	    public void executeCommand() {
	        FacesMessage msg;
	        
	        if(database != null)
	        {
	           
				Runtime runtime = Runtime.getRuntime();						
				Process proc = null;
				try {
				  proc = runtime.exec("cmd.exe /c C:\\xampp\\mysql\\bin\\mysqldump.exe -u root --databases" + database + " > C:\\Users\\xvassilakopoulos\\Desktop\\mysql_test.sql" );
				} catch (IOException e1) {
					LOGGER.log(Level.SEVERE, "Problems during execution. Error: {0}", new Object[]{e1.getMessage()});
				}

				 msg = new FacesMessage("backup", "backing up database " + database + "");
	        }
	        else
	            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid", "Database is not selected."); 
	             
	        FacesContext.getCurrentInstance().addMessage(null, msg);  
	    }
	}

