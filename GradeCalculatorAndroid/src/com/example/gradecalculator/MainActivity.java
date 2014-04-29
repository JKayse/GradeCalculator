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

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
			return true;
		}
		if(id == R.id.signOut) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void registerNew(View view){
    	
    	Intent moveToRegister = new Intent(this,Registration.class );
    	startActivity(moveToRegister);
    	
    }
	
	public void loginAttempt(View view){
	   	 EditText usernameField = (EditText)findViewById(R.id.username_placeholder);
	   	 EditText passwordField = (EditText)findViewById(R.id.password_placeholder);
	   	 
	   	 String username = usernameField.getText().toString();
	   	 String password = passwordField.getText().toString();
	   	 if(username.length() == 0){
	   		 Toast toast = Toast.makeText(this, "Enter a username.", 5000);
				 toast.show();
				 return;
	   	 }
	   	 else if(password.length() < 8){
	   		 Toast toast = Toast.makeText(this, "The password must be at least 8 characters long.", 5000);
				 toast.show();
				 return;
	   	 }
	   	
	   	new loginRequest(getApplicationContext()).execute(username, password);
   }
	
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
			HttpPost post = new HttpPost("http://54.200.94.110/GradeCalculator/api/Login");
			
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

			if(response.contentEquals("error_username_doesnt_exists")){
				
				CharSequence text = "The username entered does not exist. Try Again.";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			} else if(response.contentEquals("null")){
				CharSequence text = "The password entered was not correct. Try Again.";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
			else {

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
		        
		        //Intent toMain = new Intent(context, Application_main.class);
				//startActivity(toMain);
			}
		}
    }
	
}


