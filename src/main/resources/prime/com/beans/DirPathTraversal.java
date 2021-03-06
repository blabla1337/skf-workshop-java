package prime.com.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.log4j.Logger;
import com.Lib.InputValidation;
import com.Lib.WhiteList;


public final class DirPathTraversal {
	
	private File getFile;
	final static Logger logger = Logger.getLogger(DirPathTraversal.class);
	
	public void DirValidation() throws IOException
	{
	InputValidation validate = new InputValidation();
	WhiteList listme = new WhiteList();

	/*
    First, we want to filter the filenames for expected values. For this example we only use 0-9
    Whenever the values are tampered with, we can assume an attacker is trying to inject malicious input.           
    */
    boolean validated = true;

    //see the "input validation" code example for more detailed information about this function
    if (validate.validateInput(getFile.toString(), "nummeric", "Failed to get file", "HIGH", null) == false) { validated = false; }

    /*
    see the "white listing" code example for more detailed information about this function
    Let's assume there are three files named 1,2,3
    */

    if (listme.WhiteListing("1,2,3", getFile.toString()) == false) { validated = false; }

    //Only if the pattern was true we allow the variable into the stream reader function
    FileInputStream fis = null; 
    if (validated == true)
    {
    	String canonicalPath = getFile.getCanonicalPath();
    	if (!canonicalPath.equals("C:\\....\\WEB-INF" + getFile)) 
    	{
    	   // Invalid file; handle error
    	}            	 
    	fis = new FileInputStream(canonicalPath); 
    	
    	
    }         
    else
    {
    	logger.error("invalid userinput was detected!");  
    }
    if(fis != null)
    	fis.close();

	}
}
