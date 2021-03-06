package com.example.gradecalculator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//Adapter for the list view of all the classes.
public class ClassesAdapter extends ArrayAdapter<ClassItem>{

	public static ClassItem[] classes;
	private final Context context;
	
	//Class constructor for the adapter.
	public ClassesAdapter(Context context, ClassItem[] classes) {
		super(context, R.layout.class_item, classes);
	    this.classes = classes;
	    this.context = context;
	}

	//Gets the view at a position and inflates it if needed.
	public View getView(final int position, View convertView, ViewGroup parent) {
		 View view = convertView;
	
		 //Inflates the view if it hadn't been yet.
		 if (view == null) {
		      LayoutInflater inflater = LayoutInflater.from(context);
		      view = inflater.inflate(R.layout.class_item, null);  
		 }
		
		 TextView className = (TextView)view.findViewById(R.id.className);
		 TextView professor = (TextView)view.findViewById(R.id.professor);
		 
		 //Sets a click listener when you click on a class. Starts an intent with the class information.
		 view.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ClassInfo.class);
					ArrayList<Integer> classNumber = new ArrayList(1);
					classNumber.add(position);	
					intent.putIntegerArrayListExtra(ClassInfo.CLASS_KEY, classNumber);
			    	Classes.activity.startActivityForResult(intent,Classes.CLASS_REQUEST);
				}
			});
 
		 
		 className.setText(classes[position].getClassName());
		 professor.setText(classes[position].getProfessor());
		 
		 //Returns the view.
		 return view;
	  }

}
