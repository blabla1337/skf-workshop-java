package prime.com.beans;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
 
@ManagedBean(name="user")
@SessionScoped
public final class User{
	
	
 
  public void Creds(ComponentSystemEvent event){
        
    FacesContext fc = FacesContext.getCurrentInstance();
    
    if (!"admin".equals(fc.getExternalContext().getSessionMap().get("role"))){
      ConfigurableNavigationHandler nav 
      = (ConfigurableNavigationHandler) 
        fc.getApplication().getNavigationHandler();
    
      nav.performNavigation("access-denied");
    }
    
  }
  
}