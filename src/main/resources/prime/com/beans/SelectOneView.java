package prime.com.beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

 
@ManagedBean
public class SelectOneView {
     
    private String option;   
    private List<String> databases; 
    
    @PostConstruct
    public void init() {
        //Cities
    	databases = new ArrayList<String>();
    	databases.add("San Francisco");
        databases.add("London");
    }

    public String getOption() {
        return option;
    }
    
    public List<String> getDatabases() {
        return databases;
    }
 
    public void setDataabses(List<String> databases_) {
        this.databases = databases_;
    }
 
    public void setOption(String option) {
        this.option = option;
    }
 
}