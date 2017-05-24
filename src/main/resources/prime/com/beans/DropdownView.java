package prime.com.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
	 
	@ManagedBean
	@SessionScoped
	public class DropdownView implements Serializable {     
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final static Logger LOGGER = Logger.getLogger(DropdownView.class.getCanonicalName());

	    private DropDown selectedDatabase;
	    private List<DropDown> databases;
	    private String databaseid;

	    public DropdownView() {
	    	databases = new ArrayList<DropDown>();
	    	databases.add(new DropDown("login", "login"));
	    	databases.add(new DropDown("auditlogs", "auditlogs"));
	    	databases.add(new DropDown("aggregate_control", "aggregate_control"));    	
	    }
	 
	    public DropDown getSelectedDatabases() {
			return selectedDatabase;
		}

		public void setSelectedDatabases(DropDown selectedDatabases) {
			this.selectedDatabase = selectedDatabases;
		}

		public String getDatabaseid() {
			return databaseid;
		}


		public void setDatabaseid(String databaseid) {
			this.databaseid = databaseid;
		}


		public List<DropDown> getDatabases() {
			return databases;
		}

		public void setDatabases(List<DropDown> databases) {
			this.databases = databases;
		}
		
		
		public DropDown getDbs(String id_) {
			if (id_ == null){
	            throw new IllegalArgumentException("no id provided");
	        }
			this.setDatabaseid(id_);
			
			for (DropDown db : databases)
	        {
	              return db;   		
	        }
	        return null;	
		}
		
		/*
		public DropDown getDbs(String id_) {
			
	        if (id_ == null){
	            throw new IllegalArgumentException("no id provided");
	        }
	        for (DropDown db : databases)
	        {
	        	if (id_.equals(db.getDatabase()))
	        	{
	        		db.setDatabase(id_);
	                return db;
	        	}        		
	        	
	        }
	        return null;
	    }*/
	    
	    public void executeCommand() {
	        FacesMessage msg = null;	        
	        
	        
	        String db = this.getDatabaseid();
	        
		            if(db != null)
			        {		           
						Runtime runtime = Runtime.getRuntime();						
						Process proc = null;
						try {
						  proc = runtime.exec("cmd.exe /c mysqldump -u root --databases " + db + " > C:\\Users\\xvassilakopoulos\\Desktop\\mysql_test_"+ db +".sql" );
						} catch (IOException e1) {
							LOGGER.log(Level.SEVERE, "Problems during execution. Error: {0}", new Object[]{e1.getMessage()});
						}
						msg = new FacesMessage("backup", "backing up database " + db + "");
			        }       
		        
		        else
		        	msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid", "Database is not selected.");
		        
		     
			    FacesContext.getCurrentInstance().addMessage(null, msg);  
			    
	        
	    
	}
	}
