package com.example.gradecalculator;

public class ClassItem {

	String mClassName;
	String mProfessor;
	String mClassId;

	
	//The class constructor
	public ClassItem(String className, String professor, String classId){
		mClassName = className;
		mProfessor = professor;
		mClassId = classId;
	}

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
