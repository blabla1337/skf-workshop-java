package com.Lib;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "employee")
@ApplicationScoped
public class Employee {
	
	
    private int id;
    private String name;
    private String gender;
    private int age;
    private String role;
        
    private static ArrayList<Employee> employees = new ArrayList<Employee>();	

    public Employee(){
    	super();   
    	
    	 	
    }
    
    public void clear_Employees()
    {
    	if (employees != null)
    	{
    		employees.clear();
    		employees.remove(this);
    	}
    	
    	
    }
    
    public Employee (int id, String Name, String Gender, int Age, String Role)
    {
    	this.id = id;
    	this.name = Name; 
    	this.gender = Gender; 
    	this.age = Age; 
    	this.role = Role; 
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public List<Employee> createEmployees(int id, String Name, String Gender, int Age, String Role) {
        	employees.add(new Employee(getId(), getName(), getGender(), getAge(), getRole()));
        return employees;
    }    
}