package prime.com.beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;


@ManagedBean(name = "cards")
public class SelectOneView {
     
    private String option;   
    private List<String> databases; 
    
    @PostConstruct
    public void init() {
        //Cities
    	databases = new ArrayList<String>();
    	databases.add("349273490561696");
        databases.add("341822614449358");
    }

    public String getOption() {
        return option;
    }
    
    public List<String> getDatabases() {
        return databases;
    }
 
    public void setDatabases(List<String> databases_) {
        this.databases = databases_;
    }
 
    public void setOption(String option) {
        this.option = option;
    }
 
}