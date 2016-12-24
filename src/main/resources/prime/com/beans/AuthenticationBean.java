package prime.com.beans;

import javax.faces.context.FacesContext;

public class AuthenticationBean {
	  public static final String AUTH_KEY = "app.user.name";

	  private String name;
	  public String getName() { return name; }
	  public void setName(String name) { this.name = name; }

	  public boolean isLoggedIn() {
	    return FacesContext.getCurrentInstance().getExternalContext()
	        .getSessionMap().get(AUTH_KEY) != null;
	  }

	  public String login() {
	    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
	        AUTH_KEY, name);
	    return "secret";
	  }

	  public String logout() {
	    FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
	        .remove(AUTH_KEY);
	    return null;
	  }
	}