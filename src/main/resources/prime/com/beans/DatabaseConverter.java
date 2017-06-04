package prime.com.beans;


import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter(value = "DatabaseConverter")
public class DatabaseConverter implements Converter{

	/*
	
	Sometimes standard JSF converters and validators don't go far enough. For example:
	
	    - Converting to objects other than numbers or dates
	
	    - Performing application-specific validation such as checking a credit card 
	
	With a moderate amount of programming, you can implement application-specific converters and validators.
	
	Supply your own conversion and validation code for more complex scenarios

	*/
	 
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