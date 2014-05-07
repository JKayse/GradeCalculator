package com.example.gradecalculator;

//Holds information abbout a user.
public class UserInfo {

	private static UserInfo instance = null;
	private  String username;
	private  String password;
	private int	UserId;
	private UserInfo(){}
	
	public static UserInfo getInstance() {
		
		//Creates an instance for the user.
		if(instance == null){
			instance = new UserInfo();
		}
		return instance;
			
	}
	
	//Sets the information
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setUserId(int userId) {
		UserId = userId;
	}
	
	
	//Retrieves the information used.
	public String getPassword() {
		return password;
	}
	
	public int getUserId() {
		return UserId;
	}
	public String getUsername() {
		return username;
	}
	
	
	
}
