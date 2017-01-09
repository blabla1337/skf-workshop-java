package prime.com.beans;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.Lib.AuditLog;
import com.Lib.randomizer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@FacesComponent(value = "Prime.com.beans.AntiCSRF")
public class AntiCSRF extends UIComponentBase {
	
	final static Logger logger = Logger.getLogger(AntiCSRF.class);
	//First we include the audit log class.
	AuditLog Log = new AuditLog();
	
	public void generateToken(){
		
		HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

    	//we include the random password/token class.
  	    randomizer CSRF = new randomizer();
		 /*
        Now we create a random value for our CSRF tokens. See "Random password token generation" in
        the code examples for more detailed information:
        */
        String CSRftoken = CSRF.generate(25);
                
        //Set an accesor session.
        origRequest.getSession(false);
        origRequest.getSession().setAttribute("CSRF", CSRftoken);
       
	}

    public void antiCSRF() throws IOException
    {		
        HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpServletResponse origResponse = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
        String AUTH_KEY =  (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("AUTH_KEY");
       	FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(AUTH_KEY);
   		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
   		Cookie cookie = null;
   		Cookie[] cookies = null;
   	    // Get an array of Cookies associated with this domain
   	    cookies = origRequest.getCookies();		         
   		for (Cookie cookie2 : cookies) 
   		{
   			cookie = cookie2;		         
   				if (cookie.getName().equals("JSESSIONID"))
   				{        	 
	   				cookie.setValue(null);	       		
	   				origResponse.addCookie(cookie);
	   			
	                Log.SetLog("", "", "Cookie has been desroyed!", LocalDateTime.now(), "", "");    
   				} 
   		}		     
    }
	
		public void decode(FacesContext context) {
			 FacesContext fc = FacesContext.getCurrentInstance();

			// access the hidden input field value
			ExternalContext external = context.getExternalContext();
			Map<?, ?> requestMap = external.getRequestParameterMap();
			String value = String.valueOf(requestMap.get("_CSRFToken"));

			// access the session and get the token
			HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
			String token = (String) session.getAttribute("CSRF");

			// check if the token exists
			if (value == null || "".equals(value)) {
				try {
					this.antiCSRF();
				} catch (IOException e) {
					logger.error(e.toString());
				}
				Log.SetLog("", "", "antiCSRF token doesnt match! Failed attempt", LocalDateTime.now(), "", ""); 
				logger.info("antiCSRF token doesnt match! Failed attempt");
				ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler(); 
    			nav.performNavigation("csrf");
			}

			// check the values for equality
			if (!value.equalsIgnoreCase(token)) {
				try {
					this.antiCSRF();
				} catch (IOException e) {
					logger.error(e.toString());
				}
				Log.SetLog("", "", "antiCSRF token doesnt match! Failed attempt", LocalDateTime.now(), "", ""); 
				logger.info("antiCSRF token doesnt match! Failed attempt");
				ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler(); 
    			nav.performNavigation("Menu");
			}

		}

	    @Override public void encodeEnd(FacesContext context) throws IOException 
	    {
	    	//generate new token in every request
	    	this.generateToken();
	    	// get the session (don't create a new one!)
	    	HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
	    	// get the token from the session
	    	String token = (String) session.getAttribute("CSRF");
	    	// write the component HTML to the response
	    	ResponseWriter responseWriter = context.getResponseWriter();
	    	responseWriter.startElement("input", null);
	    	responseWriter.writeAttribute("type", "hidden", null);
	    	responseWriter.writeAttribute("name", "_CSRFToken", "");
	    	responseWriter.writeAttribute("value", token, "CSRF");
	    	responseWriter.endElement("input");
	    }

		@Override
		public String getFamily() {
			// TODO Auto-generated method stub
			return null;
		}
}
