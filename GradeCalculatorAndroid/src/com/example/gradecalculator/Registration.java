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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//Class to register an account.
public class Registration extends ActionBarActivity{
	EditText username;
	EditText password;
	EditText confirmPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		Button signUp = (Button) findViewById(R.id.signUp);
		
		
		//Adds a click listener to the sign up button.
		signUp.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				registerAttempt(v);
			}
		});
		

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
		return super.onOptionsItemSelected(item);
	}
	
	//Function to make sure the input fields are set properly.
	public void registerAttempt(View view){
	   	 username = (EditText)findViewById(R.id.username_placeholder);
	   	 password = (EditText)findViewById(R.id.password_placeholder);
	   	 confirmPassword = (EditText)findViewById(R.id.confirm_placeholder);
	   	 
	   	 
	   	  String newUsername=username.getText().toString();
	   	  String newPassword=password.getText().toString();
	   	  String passwordCheck=confirmPassword.getText().toString();
	   	 
	   	  //TODO valadation goes here
	   	if(newUsername.length() == 0){
	   		 username.setError("Enter a username.");
			 return;
		 }

		 else if(newPassword.length() < 8){
			 password.setError("The password must be at least 8 characters long.");
			 return;
		 }
		 else if(passwordCheck.length() < 8){
			 confirmPassword.setError("The password must be at least 8 characters long.");
			 return;
		 }
		 else if(!newPassword.equals(passwordCheck)){
			 confirmPassword.setError("The two passwords must match.");
			 return;
		 }
	   	 
	   	//Calls the async task if everything is set properly.
	   	  new registerRequest(this).execute(newUsername, newPassword);
	   	
	   }
		
		
		//Async task to create an account on the database.
		class registerRequest extends AsyncTask<String, Void, String>{
	    	Context context;
	        private registerRequest(Context context) {
	            this.context = context.getApplicationContext();
	        }
	    	
			@Override
			protected String doInBackground(String... params) {
				
				
				
				HttpResponse response = null;
				HttpClient client= new DefaultHttpClient();
				HttpPost post = new HttpPost("http://54.200.94.110/GradeCalculator/api/index.php/Users");
				
			    
				
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("username", params[0]));
				pairs.add(new BasicNameValuePair("password", params[1]));
				
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
				//Returns an error if the username if already used.
				if(response.equals("error_username")){
					username.setError("This username already exists.");
					return;				
				}else{
					//Tells you to login if successful.
					text = "Success! Login to continue";
					int duration = Toast.LENGTH_LONG;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
					return;
					
				}
				
				
		     }
	    }
}
