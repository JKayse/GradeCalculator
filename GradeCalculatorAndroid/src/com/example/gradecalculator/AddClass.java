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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//Class to add a class to a user's account.
public class AddClass extends ActionBarActivity{
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_class);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = sharedPref.edit();
		
		//Click listener to add categories.
		TextView addCategory = (TextView) findViewById(R.id.addGrading);
		addCategory.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				addCategory(v);
			}
		});
		
		//Click listener for submitting the class.
		Button submitClass = (Button) findViewById(R.id.submitClass);
		submitClass.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				addClass(v);
			}
		});
		
		//Click listener for canceling adding a class. Just closes the activity.
		Button cancelClass = (Button) findViewById(R.id.cancelAdd);
		cancelClass.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	//Function that inflates another layout to add more categories.
	public void addCategory(View view){
		LinearLayout categoryList = (LinearLayout) findViewById(R.id.categories);
		LinearLayout categoryItem = (LinearLayout) getLayoutInflater().inflate(R.layout.category, null);
	    categoryList.addView(categoryItem);
    	
    }
	
	
	//Adds the class checking for the right fields.
	public void addClass(View view){
		EditText className = (EditText)findViewById(R.id.addClassName);
	   	EditText professor = (EditText)findViewById(R.id.addProfessor);
	   	LinearLayout categoryList = (LinearLayout) findViewById(R.id.categories);
	   	 
	   	 
		String newClassName=className.getText().toString();
		String newProfessor=professor.getText().toString();

		//Sets an error if the fields are empty.
	   	if(newClassName.length() == 0){
	   		className.setError("Enter a class name.");
			return;
		}
	   	else if(newProfessor.length() == 0){
	   		professor.setError("Enter a professor's name.");
			return;
		}
	   	
	   	//Gets all the categories and makes sure the total percentage is 100%.
	   	Integer totalPercentage = 0;
	   	JSONArray categoryValues = new JSONArray();
	   	for(int i=0; i< categoryList.getChildCount(); ++i) {
	   	    LinearLayout category = (LinearLayout) categoryList.getChildAt(i);
	   	    EditText categoryName = (EditText) category.getChildAt(0);
	   	    EditText categoryPercentage = (EditText) category.getChildAt(1);
	   	    
	   	    String newCategoryName = categoryName.getText().toString();
			String newCategoryPercentage = categoryPercentage.getText().toString();
			
			if(newCategoryName.length() == 0 && newCategoryPercentage.length() ==0){
				continue;
			}
			else if(newCategoryName.length() == 0){
		   		categoryName.setError("Enter a corresponding category name.");
				return;
			}
		   	else if(newCategoryPercentage.length() ==0){
		   		categoryPercentage.setError("Enter a corresponding percentage.");
				return;
			}
		   	else{
		   		JSONObject categoryPair = new JSONObject();
		   		try {
		   		    categoryPair.put("categoryName", newCategoryName);
		   		    categoryPair.put("percentage", newCategoryPercentage);
		   		    categoryValues.put(categoryPair);
		   		    totalPercentage += Integer.parseInt(newCategoryPercentage);
		   		} catch (JSONException e) {
		   		    // TODO Auto-generated catch block
		   		    e.printStackTrace();
		   		}
		   	}
	   	    
	   	}
	   	if(totalPercentage != 100){
	   		int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(this, "The percentages must add to 100.", duration);
			toast.show();
	   		return;
	   	}
	   	else{
	   		//Sends all the information as json to the database.
	   		JSONObject finalCategories = new JSONObject();
	   	    try {
				finalCategories.put("category", categoryValues);
				String newCategories = finalCategories.toString();
				Log.d("JLK", newCategories);
				Log.d("JLK", newClassName);
				Log.d("JLK", newProfessor);
				new classRequest(this).execute(newClassName, newProfessor, newCategories);
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK , intent);
				//Ends the activity after it's submitted.
				finish();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   	}

	   	
	   	
    	
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logged_in, menu);
		return true;
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.information) {
			Intent intent = new Intent(this, AboutPage.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.signOut) {
			editor.clear().commit();
			Intent intent = new Intent(this, MainActivity.class);
			finish();
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Sends the request to the database.
	class classRequest extends AsyncTask<String, Void, String>{
    	Context context;
        private classRequest(Context context) {
            this.context = context.getApplicationContext();
        }
    	
		@Override
		protected String doInBackground(String... params) {
			HttpResponse response = null;
			HttpClient client= new DefaultHttpClient();
			HttpPost post = new HttpPost("http://54.200.94.110/GradeCalculator/api/index.php/AddClass");
			
			UserInfo current = UserInfo.getInstance();
			Integer id = current.getUserId();
			String userid = id.toString();
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("userId", userid));
			pairs.add(new BasicNameValuePair("className", params[0]));
			pairs.add(new BasicNameValuePair("professor", params[1]));
			pairs.add(new BasicNameValuePair("categories", params[2]));
			
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
			return;
	     }
    }
}
