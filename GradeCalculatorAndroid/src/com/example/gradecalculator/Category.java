package com.example.gradecalculator;


public class Category {

	String mCategoryName;
	String mPercentage;
	String mCategoryId;
	Grade[] grades;
	
	public Category(){
		mCategoryName = "placeHolder";
		mPercentage = "placeHolder";
	}
	public void setGrades(Grade[] values){
		this.grades = values;
	}
	
	public String getGradeName(Integer position){
		return grades[position].getGradeName();
	}
	
	public String getGradePercentage(Integer position){
		return grades[position].getPercentage();
	}
	
	public Grade getGrade(Integer position){
		return grades[position];
	}
	
	public Grade[] getGrades(){
		return grades;
	}
	
	public void setCategoryName(String value){
		this.mCategoryName = value;
	}
	public String getCategoryName(){
		return mCategoryName;
	}
	public void setPercentage(String value){
		this.mPercentage = value;
	}
	public String getPercentage(){
		return mPercentage;
	}
	public void setCategoryId(String value){
		this.mCategoryId = value;
	}
	public String getCategoryId(){
		return mCategoryId;
	}

}
