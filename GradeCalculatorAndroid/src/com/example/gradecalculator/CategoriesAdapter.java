package com.example.gradecalculator;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.example.gradecalculator.ClassInfo.deleteClassRequest;
import com.example.gradecalculator.ClassInfo.gradeRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


//Class to handle the expandable list view.
public class CategoriesAdapter extends BaseExpandableListAdapter{

	LayoutInflater inflater;
	Context context;
	Category[] categories;

	
	//Class constructor that takes in the data and the context.
	CategoriesAdapter(Context context, Category[] data){
		super();
		this.context = context;
		categories = data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	//Gets the child at a position.
	public Grade getChild(int groupPosition, int childPosition) {
        return categories[groupPosition].getGrade(childPosition);
    }
	
	//Gets the child view and inflates it.
	public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        final Grade currentGrade = getChild(groupPosition, childPosition);
         
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grade_item, null);
        }
         
        TextView gradeName = (TextView) convertView.findViewById(R.id.gradeName);
        TextView gradePercentage = (TextView) convertView.findViewById(R.id.percentage);
        final RelativeLayout notSwiped = (RelativeLayout) convertView.findViewById(R.id.notSwiped);
        final RelativeLayout swiped = (RelativeLayout) convertView.findViewById(R.id.swiped);
        
		Button deleteGrade = (Button) convertView.findViewById(R.id.deleteGrade);
		Button editGrade = (Button) convertView.findViewById(R.id.editGrade);
		
		//Creates a click listener to delete the grade.
		deleteGrade.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				deleteGrade(currentGrade.getGradeId());
			}
		});
		
		//Creaates a click listener to edit the grade.
		editGrade.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				editGrade(currentGrade);
			}
		});
        
        gradeName.setText(currentGrade.getGradeName());
        gradePercentage.setText(currentGrade.getPercentage()+"%");
        
        
        //Adds a swipe listener on the item that then switches the view of the grade.
        final SwipeDetector swipeDetector = new SwipeDetector();
        convertView.setOnTouchListener(swipeDetector);
        convertView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				 if (swipeDetector.swipeDetected()){
                     Log.d("JLK", "SWIPED!");
                     if (notSwiped.getVisibility() == View.VISIBLE) {
                    	 swiped.setVisibility(View.VISIBLE);
                         notSwiped.setVisibility(View.GONE);
                     }
                     else{
                    	 swiped.setVisibility(View.GONE);
                         notSwiped.setVisibility(View.VISIBLE);
                     }
                     
                 }
			}
		});
        
        
        
        return convertView;
	}
	
	
	//Gets the categories and inflates them.
	public View getGroupView(int groupPosition, boolean isExpanded,
	            View convertView, ViewGroup parent) {
			Category temp = getGroup(groupPosition);
	        if (convertView == null) {
	            LayoutInflater secondInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = secondInflater.inflate(R.layout.category_item,null);
	        }
	        TextView categoryName = (TextView) convertView.findViewById(R.id.category);
	        TextView percentage = (TextView) convertView.findViewById(R.id.percentage);
	        categoryName.setText(temp.getCategoryName());
	        percentage.setText(temp.getPercentage()+"%");

	        return convertView;
	    }

	//Returns the number of categories.
	@Override
	public int getGroupCount() {
		return categories.length;
	}

	//Returns the number of grades for a certain category.
	@Override
	public int getChildrenCount(int groupPosition) {
		return categories[groupPosition].getGrades().length;
	}

	//Returns the category at a certain position.
	@Override
	public Category getGroup(int groupPosition) {
		return categories[groupPosition];
	}

	//Returns the id of the category.
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	//Returns the id of the grade.
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	//Makes the each id is stable...
	@Override
	public boolean hasStableIds() {
		return true;
	}

	
	//Says that you can select a child.
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	//Function to the delete the grade.
	public void deleteGrade(String gradeId){
		new deleteGradeRequest(context).execute(gradeId);
	}
	
	
	//Function to show the alert dialog for editing a grade.
	public void editGrade(final Grade grade){
		Log.d("JLK", "test edit grade button");
			final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			LayoutInflater inflater = Classes.activity.getLayoutInflater();
			View inflateThis = inflater.inflate(R.layout.edit_grade,null);
			dialog.setView(inflateThis);
		
			final EditText gradeName = (EditText) inflateThis.findViewById(R.id.addGradeName);
			gradeName.setText(grade.getGradeName());
			final EditText startScore = (EditText) inflateThis.findViewById(R.id.topNumber);
			final EditText endScore = (EditText) inflateThis.findViewById(R.id.bottomNumber);
			final Button submitGrade = (Button) inflateThis.findViewById(R.id.addGrade);
			final Button cancelSubmit = (Button) inflateThis.findViewById(R.id.cancelAddGrade);


			//Adds a click listener to the alert to hide the alert.
			final AlertDialog dialogFinal = dialog.create();
			dialogFinal.show();
			cancelSubmit.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					dialogFinal.dismiss();
				}
			});
			
			//Adds a click listener to submit the new changes for the edit. Checks for proper values.
			submitGrade.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					String newGradeName = gradeName.getText().toString();
					String newStartScore = startScore.getText().toString();
					String newEndScore = endScore.getText().toString();
					if(newGradeName.length() == 0){
						gradeName.setError("Please enter a name.");
						return;
					}
					else if(newStartScore.length() == 0){
						startScore.setError("Please enter a value.");
						return;
					}
					else if(newEndScore.length() == 0 || newEndScore.equals("0")){
						endScore.setError("Please enter a value not equal to 0.");
						return;
					}
					else{        	
			        	Integer startGrade = Integer.parseInt(newStartScore);
			        	Integer endGrade = Integer.parseInt(newEndScore);
			        	Double finalScore = (double)(startGrade)/(double) (endGrade) * 100;
			        	String totalScore = String.valueOf(finalScore);
			        	Log.d("JLK", totalScore);
			        	//Closes the dialog and sends the data to an async task.
			        	dialogFinal.dismiss();
			        	new editGradeRequest(context).execute(newGradeName, totalScore, grade.getGradeId());
					}
				}
			});			
	}
	
	//Async task to delete the certain grade.
	class deleteGradeRequest extends AsyncTask<String, Void, String>{
		Context context;
	    private deleteGradeRequest(Context context) {
	        this.context = context.getApplicationContext();
	    }
		
		@Override
		protected String doInBackground(String... params) {
			HttpResponse response = null;
			HttpClient client= new DefaultHttpClient();
			HttpPost post = new HttpPost("http://54.200.94.110/GradeCalculator/api/index.php/DeleteGrade");

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("gradeId", params[0]));
			
			try {
				post.setEntity(new UrlEncodedFormEntity(pairs));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				response = client.execute(post);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String responseString="";
			HttpEntity temp = response.getEntity();
			try {
				responseString = EntityUtils.toString(temp);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			return responseString;
	
		}
		
		
		@Override
		protected void onPostExecute(String response) {
			CharSequence text = "";
			Log.d("JLK", response);

			//Refreshes the activity when it finishes.
			Intent intent = new Intent();
			intent.setAction("com.example.gradecalculator.REFRESH");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ArrayList<Integer> classNumber = new ArrayList(1);
			classNumber.add(ClassInfo.current);	
			intent.putIntegerArrayListExtra(ClassInfo.CLASS_KEY, classNumber);
			ClassInfo.activity.finish();
			context.startActivity(intent);
			return;
	     }
	}

	//Async task for editing a grade.
	class editGradeRequest extends AsyncTask<String, Void, String>{
		Context context;
	    private editGradeRequest(Context context) {
	        this.context = context.getApplicationContext();
	    }
		
		@Override
		protected String doInBackground(String... params) {
			HttpResponse response = null;
			HttpClient client= new DefaultHttpClient();
			HttpPost post = new HttpPost("http://54.200.94.110/GradeCalculator/api/index.php/EditGrade");

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("gradeName", params[0]));
			pairs.add(new BasicNameValuePair("percentage", params[1]));
			pairs.add(new BasicNameValuePair("gradeId", params[2]));
			
			try {
				post.setEntity(new UrlEncodedFormEntity(pairs));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				response = client.execute(post);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String responseString="";
			HttpEntity temp = response.getEntity();
			try {
				responseString = EntityUtils.toString(temp);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			return responseString;
	
		}
		
		
		@Override
		protected void onPostExecute(String response) {
			CharSequence text = "";
			Log.d("JLK", response);

			//Refreshes the activity when finished.
			Intent intent = new Intent();
			intent.setAction("com.example.gradecalculator.REFRESH");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ArrayList<Integer> classNumber = new ArrayList(1);
			classNumber.add(ClassInfo.current);	
			intent.putIntegerArrayListExtra(ClassInfo.CLASS_KEY, classNumber);
			ClassInfo.activity.finish();
			context.startActivity(intent);
			return;
	     }
	}
}
