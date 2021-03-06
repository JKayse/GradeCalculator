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
import android.widget.Toast;

//Main activity for signing in.
public class MainActivity extends ActionBarActivity {
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	EditText usernameField;
	EditText passwordField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = sharedPref.edit();
		Button signIn = (Button) findViewById(R.id.signIn);
		Button signUp = (Button) findViewById(R.id.signUp);
		
		
		//Click listener to go to the sign up page.
		signUp.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				registerNew(v);
			}
		});
		
		//Click listener to sign in.
		signIn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				loginAttempt(v);
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
	public void onBackPressed(){
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
	
	//Goes to the register page.
	public void registerNew(View view){
    	
    	Intent moveToRegister = new Intent(this,Registration.class );
    	startActivity(moveToRegister);
    	
    }
	
	//Checks for valid input. Then calls an async task.
	public void loginAttempt(View view){
	   	 usernameField = (EditText)findViewById(R.id.username_placeholder);
	   	 passwordField = (EditText)findViewById(R.id.password_placeholder);
	   	 
	   	 String username = usernameField.getText().toString();
	   	 String password = passwordField.getText().toString();
	   	 if(username.length() == 0){
	   		 usernameField.setError("Enter a username.");
			 return;
	   	 }
	   	 else if(password.length() < 8){
	   		 passwordField.setError("The password must be at least 8 characters long.");
			 return;
	   	 }
	   	
	   	new loginRequest(getApplicationContext()).execute(username, password);
   }
	
	//Async task to login.
	class loginRequest extends AsyncTask<String, Void, String>{
    	Context context;
        private loginRequest(Context context) {
            this.context = context.getApplicationContext();
        }
    	
		@Override
		protected String doInBackground(String... params) {
			Log.d("username",params[0]);
			Log.d("password", params[1]);
			
			
			HttpResponse response = null;
			HttpClient client= new DefaultHttpClient();
			HttpPost post = new HttpPost("http://54.200.94.110/GradeCalculator/api/index.php/Login");
			
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
				
			}
			String responseString = "";
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

			//If any information is wrong it returns with an error.
			if(response.contentEquals("error_username_doesnt_exists")){
				usernameField.setError("The username entered does not exist.");
				return;
			} else if(response.contentEquals("null")){
				passwordField.setError("The password entered was not correct.");
				return;
			}
			else {
				//If the login ingormation is correct, it goes to the classes page.
				//Also submits the user id to shared preferences to hold the logged in state.
				JSONObject back = null;
				try {
					back = new JSONObject(response);
				} catch (JSONException e1) {

				}
				String username = null;
				try {
					username = (String) back.get("Username");
				} catch (JSONException e) {

				}
				int ID = -8;
				try {
					ID  = (Integer) back.get("ID");
				} catch (JSONException e) {

				}
				
				UserInfo temp = UserInfo.getInstance();
		        temp.setUsername(username);
		        temp.setUserId(ID);
		        editor.putString("user_id", Integer.toString(ID));
		        editor.commit();
		        Intent toMain = new Intent(context, Classes.class);
				startActivity(toMain);
			}
		}
    }
	
}


