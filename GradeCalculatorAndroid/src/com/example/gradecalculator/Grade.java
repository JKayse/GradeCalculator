package com.example.gradecalculator;

public class Grade {

	String mGradeName;
	String mPercentage;
	String mGradeId;
	
	public void setGradeName(String value){
		mGradeName = value;
	}
	public void setPercentage(String value){
		mPercentage = value;
	}
	public void setGradeId(String value){
		mGradeId = value;
	}
	
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
