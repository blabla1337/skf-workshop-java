package prime.com.beans;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.Lib.Employee;
import com.Lib.MyHandler;
import com.Lib.InputValidation;

@ManagedBean(name="XMLDataPreview")
@SessionScoped
public class XMLFileUploader implements Serializable {
	
	private String destination="C:\\Users\\xvassilakopoulos\\Desktop\\test\\";
	
	private static final long serialVersionUID = 1L;
	
	InputValidation validate = new InputValidation();
	
	private UIComponent component;
	   
	private List<Employee> emps;
	
    public List<Employee> getEmps() {
		return emps;
	}

	public UIComponent getComponent() {
		return component;
	}
    
    public void clearEmployees()
    {
    	if (emps != null)
    		{
    		emps.clear();
    		emps = null; 
    		}
    }

	public void setComponent(UIComponent component) {
		this.component = component;
	}
	
    private Employee emp;
    
    public Employee getEmp() {
		return emp;
	}
	
	final static Logger logger = Logger.getLogger(XMLFileUploader.class);

	private UploadedFile uploadedFile;

    public UploadedFile getUploadedFile() {
       return uploadedFile;
    }

    public void setUploadedFile(UploadedFile value) {
        uploadedFile = value;
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
             
             } catch (IOException e) {
            	 logger.info(e.toString());
             }
 }  
 
    

	public void  upload(FileUploadEvent event) throws SAXException, IOException, ParserConfigurationException{	
		FacesContext context = FacesContext.getCurrentInstance();
        boolean continueFunction = true;
        
		/*
		The overall prevention method for loading external entities is adding the following line of code:
		This line of code function tells the underlying libxml parsing to not try to interpret the values 
		of the entities in the incoming XML and leave the entity references intact.
		*/

		/*
		 Both DocumentBuilderFactory and SAXParserFactory XML Parsers can be configured using the same techniques
		 to protect them against XXE.
		 The JAXP DocumentBuilderFactory setFeature method allows a developer to control which implementation-specific XML processor features are enabled or disabled. 
		 The features can either be set on the factory or the underlying XMLReader setFeature method. 
		 Each XML processor implementation has its own features that govern how DTDs and external entities are processed.
		*/
		
        SAXParserFactory spf = SAXParserFactory.newInstance();
		javax.xml.parsers.SAXParser saxParser = spf.newSAXParser();
		XMLReader reader = saxParser.getXMLReader();
		
		try {
		      // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
		      // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
		 
		      // Using the SAXParserFactory's setFeature
		      spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
		      // Using the XMLReader's setFeature
		      reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
		  
		      // Xerces 2 only - http://xerces.apache.org/xerces-j/features.html#external-general-entities
		     reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			  
			  reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
			  
			  reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false); 
			  
			  spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			  
			  spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			  
			  spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);

		 
		      // remaining parser logic
		      // We get the filename for doing different types of tests on it
		      
		     // DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    //  String FEATURE = null;
		      
		     
		      
		    	
		      // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
		      // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
		    //  FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
		   //   dbf.setFeature(FEATURE, true);		      
		      // If you can't completely disable DTDs, then at least do the following:
		      // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
		      // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
		      // JDK7+ - http://xml.org/sax/features/external-general-entities    
		    //  FEATURE = "http://xml.org/sax/features/external-general-entities";
		    //  dbf.setFeature(FEATURE, false);
		      
		      // Disable external DTDs as well
		    //  FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
		    //  dbf.setFeature(FEATURE, false);
		      
		      // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks" (see reference below)
		    //  dbf.setXIncludeAware(false);
		    //  dbf.setExpandEntityReferences(false);
		      
        
		     final String fileName = event.getFile().getFileName().toString();

		        /*
		        First we check if the value is alphanumeric only to prevent uploading out of intended directory, 
		        as well as other injections
		        */
		        if (validate.validateInput("", fileName,"alphanummeric", "validation failed", "HIGH") == false)
		        {
		          continueFunction = false;
		        }
		                
		        /*
		        The next step would be checking if the file contains the right extension in order to prevent
		        a user from uploading files which could be used to harm your system. in this example 
		        we check if the last extension found in the file name is xml. whenever
		        an application just regexes for the extension an attacker could
		        bypass the check by uploading an file like: "filename.jpg.php".
		        */   
		 
		        String  StrSpli = FilenameUtils.getExtension(fileName);

		        if (!StrSpli.equals("xml"))
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
		        	   logger.info(e.toString());
		           }
		    	   
		       }
		     
		        if (continueFunction == true)
		        { 
		        	MyHandler handler = new MyHandler();
		        	
		        	//ServletContext cont = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		            // String filepath = cont.getRealPath(fileName); 
		        	
		        	saxParser.parse(new File(destination + fileName), handler);
		      
			     	//Get Employees list
			        List<Employee> empList = handler.getEmpList();    
		        
		         
		        for(Employee em : empList)
		      	{
		        	
		        	emps = em.createEmployees(em.getId(), em.getName(), em.getGender(), em.getAge(), em.getRole());	
		      	} 
		        
		        }
		        	        
		        if (continueFunction == false)
		        {    
			    	context.addMessage(component.getClientId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, "FAIL!", "FAIL! file has not been parsed"));
			    	
		        }
			      
			    if (continueFunction == true)
			    {
			    	ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();        
			        nav.performNavigation("ViewFromXML");
			       
			    } 
		
  
		    } catch (SAXException | IOException e) {

		    	logger.info(e.toString());
		    	
		    }	
	}}
	

