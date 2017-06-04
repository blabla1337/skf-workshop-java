package prime.com.beans;

	import java.io.Serializable;

	import javax.faces.bean.ManagedBean;
	import javax.faces.bean.ManagedProperty;
	import javax.faces.bean.RequestScoped;

	@ManagedBean(name = "navigationController", eager = true)
	@RequestScoped
	public class NavigationController implements Serializable {

	   private static final long serialVersionUID = 1L;

	   @ManagedProperty(value="#{param.pageId}")
	   private String pageId;

	   public String moveToSQLtruncationMenu(){
	      return "register_login_menu";
	   }

	   public String moveToMainMenu(){
	      return "PanelMenu";
	   }
	   
	   public String moveToXXE(){
		      return "xxe";
		   }

	   public String moveToHomePassword(){
	      return "password";
	   }

	   public String getPageId() {
	      return pageId;
	   }

	   public void setPageId(String pageId) {
	      this.pageId = pageId;
	   }
	}
	
	
