package prime.com.beans;


import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter(value = "DatabaseConverter")
public class DatabaseConverter implements Converter{

	 
 @Override
 public Object getAsObject(FacesContext ctx, UIComponent component,String dbId) {
	 ValueExpression vex =
             ctx.getApplication().getExpressionFactory()
             	.createValueExpression(ctx.getELContext(),"#{dropdownView}", DropdownView.class);

	 DropdownView databases = (DropdownView)vex.getValue(ctx.getELContext());
	 
     return databases.getDbs(dbId);
 }

 @Override
 public String getAsString(FacesContext fc, UIComponent uic, Object o) {
     //This will return view-friendly output for the dropdown menu
	 return ((DropDown) o).getDatabase(); 
 }

}