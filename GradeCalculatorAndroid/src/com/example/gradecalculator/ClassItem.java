package com.example.gradecalculator;


//Stores all the basic information for a class.
public class ClassItem {

	String mClassName;
	String mProfessor;
	String mClassId;

	
	//The class constructor that sets the values.
	public ClassItem(String className, String professor, String classId){
		mClassName = className;
		mProfessor = professor;
		mClassId = classId;
	}

	//Functions to get the information from this class.
	public String getClassName(){
		return mClassName;
	}
	
	public String getProfessor(){
		return mProfessor;
	}
	
	public String getClassId(){
		return mClassId;
	}
}
