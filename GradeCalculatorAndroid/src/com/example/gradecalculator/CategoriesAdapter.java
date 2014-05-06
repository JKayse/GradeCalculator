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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoriesAdapter extends BaseExpandableListAdapter{

	LayoutInflater inflater;
	Context context;
	Category[] categories;

	CategoriesAdapter(Context context, Category[] data){
		super();
		this.context = context;
		categories = data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public Grade getChild(int groupPosition, int childPosition) {
        return categories[groupPosition].getGrade(childPosition);
    }
	
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
		deleteGrade.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				deleteGrade(currentGrade.getGradeId());
			}
		});
		editGrade.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				editGrade(v);
			}
		});
        
        gradeName.setText(currentGrade.getGradeName());
        gradePercentage.setText(currentGrade.getPercentage()+"%");
        
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

	@Override
	public int getGroupCount() {
		return categories.length;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return categories[groupPosition].getGrades().length;
	}

	@Override
	public Category getGroup(int groupPosition) {
		return categories[groupPosition];
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	
	public void deleteGrade(String gradeId){
		new deleteGradeRequest(context).execute(gradeId);
	}
	
	public void editGrade(View view){
		Log.d("JLK", "test edit grade button");
	}
	
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

	
			Intent intent = new Intent();
			intent.setAction("com.example.gradecalculator.REFRESH");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ArrayList<Integer> classNumber = new ArrayList(1);
			classNumber.add(ClassInfo.current);	
			intent.putIntegerArrayListExtra(ClassInfo.CLASS_KEY, classNumber);
			Classes.activity.finish();
			context.startActivity(intent);
			//Eventually add check for repeated class name.
			/*if(response.equals("error_username")){
				
				text = "This username already exists.";
				
				
			}else if(response.equals("error_email")){
				text = "This email already exists.";
			}else{
				 text = "Success! Login to continue";
				
			}*/
			return;
	     }
	}

}
