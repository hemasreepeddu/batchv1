package com.emp.batchv1.model;

public class Employee {
	private String id;
	private String firstName;
	private String lastName;
	public String getId() {
		System.out.println("id"+id);
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
 
}
