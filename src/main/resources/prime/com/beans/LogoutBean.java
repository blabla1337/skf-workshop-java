package prime.com.beans;

	 

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
	 
	@ManagedBean	
	@RequestScoped
	@SessionScoped
	public class LogoutBean {
		
		public static final String AUTH_KEY = "User: ";
		public  static boolean cookie_rem = false;
	     
	    public boolean logout(ActionEvent actionEvent) {

	    	
	         HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	         HttpServletResponse origResponse = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
	         FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(AUTH_KEY);
	    	 FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
			 Cookie cookie = null;
	   	  	 Cookie[] cookies = null;
	         // Get an array of Cookies associated with this domain
	         cookies = origRequest.getCookies();
	             
	         for (Cookie cookie2 : cookies) {
	             cookie = cookie2;
	             
	         if (cookie.getName().equals("JSESSIONID"))
	         {        	 
	       		 	cookie.setValue(null);	       		
	       		    origResponse.addCookie(cookie);
	       		    this.setCookie_rem(true); 
	         }         
	         		
	         
	     		
	        	}
	         return cookie_rem;
	         
		
	    }

		public boolean isCookie_rem() {
			return cookie_rem;
		}

		public void setCookie_rem(boolean cookie_rem) {
			LogoutBean.cookie_rem = cookie_rem;
		}
	     
	    }
	
	

