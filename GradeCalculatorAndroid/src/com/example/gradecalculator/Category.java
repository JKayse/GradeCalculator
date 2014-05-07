package com.example.gradecalculator;

//Class that contains all the information for each category of a user.
public class Category {

	String mCategoryName;
	String mPercentage;
	String mCurrentPercentage;
	String mCategoryId;
	Grade[] grades;
	
	public Category(){
		mCategoryName = "placeHolder";
		mPercentage = "placeHolder";
	}
	
	//Gets the array of grades for a category and sets it.
	public void setGrades(Grade[] values){
		this.grades = values;
		//calculate the percentage from the grades given.
	}
	
	//Returns the name of a specific grade.
	public String getGradeName(Integer position){
		return grades[position].getGradeName();
	}
	
	//Returns the percentage of a specific grade.
	public String getGradePercentage(Integer position){
		return grades[position].getPercentage();
	}
	
	//Returns a certain grade.
	public Grade getGrade(Integer position){
		return grades[position];
	}
	
	//Returns the list of grades.
	public Grade[] getGrades(){
		return grades;
	}
	
	//Sets the name of the category.
	public void setCategoryName(String value){
		this.mCategoryName = value;
	}
	
	//Returns the name of the category.
	public String getCategoryName(){
		return mCategoryName;
	}
	
	//Sets the percentage of a category.
	public void setPercentage(String value){
		this.mPercentage = value;
	}
	
	//Returns the percentage of a category.
	public String getPercentage(){
		return mPercentage;
	}
	
	//Sets the id of a category.
	public void setCategoryId(String value){
		this.mCategoryId = value;
	}
	
	//Returns the id of a category.
	public String getCategoryId(){
		return mCategoryId;
	}
	
	//Turns the category name to a string.
	public String toString(){
		return mCategoryName;
	}

}
