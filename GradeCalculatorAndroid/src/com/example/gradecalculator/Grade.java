package com.example.gradecalculator;

//Class that holds information about a grade.
public class Grade {

	String mGradeName;
	String mPercentage;
	String mGradeId;
	
	//Functions to set the values.
	public void setGradeName(String value){
		mGradeName = value;
	}
	public void setPercentage(String value){
		mPercentage = value;
	}
	public void setGradeId(String value){
		mGradeId = value;
	}
	
	//Functions to get the values.
	public String getGradeName(){
		return mGradeName;
	}
	public String getPercentage(){
		return mPercentage;
	}	
	public String getGradeId(){
		return mGradeId;
	}


}
