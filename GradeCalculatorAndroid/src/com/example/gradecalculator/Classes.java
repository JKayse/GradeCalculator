package com.example.gradecalculator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.ListView;
import android.widget.TextView;

public class Classes extends ActionBarActivity{
	String mainURL = "http://54.200.94.110/GradeCalculator/api/index.php/Classes/";
	DownloadJson task = new DownloadJson(this);
	ListView list;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classes);
		task.execute(mainURL);
		list = (ListView) findViewById(R.id.classList);
		
		TextView addClass = (TextView) findViewById(R.id.classTitle);
		
		addClass.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				addClass(v);
			}
		});
	}
	
	public void addClass(View view){
    	
    	Intent newClass = new Intent(this,AddClass.class );
    	startActivity(newClass);
    	
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
			return true;
		}
		if(id == R.id.signOut) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class DownloadJson extends AsyncTask<String, Void, ClassItem[]>{
		
		//The list of variables used for this class.
		ClassItem[] classes;
		ClassesAdapter adapter;
		HttpURLConnection connection;
		URL requestURL;
		InputStream stream;
		Scanner scanner;
		Context context;
		String jsonresults;

		String className;
		String professor;
		String classId;
		

		
		//Constructor for the subclass to take in the proper context.
		public DownloadJson(Context context){
			this.context = context;	
		}
		
		protected ClassItem[] doInBackground(String... urls){
			
			//Opens up a connection.
			try {
				UserInfo temp = UserInfo.getInstance();
				Integer id = temp.getUserId();
				String userid = id.toString();
				requestURL = new URL(urls[0] + userid);
				connection = (HttpURLConnection) requestURL.openConnection();
				connection.setReadTimeout(10000);
				connection.setConnectTimeout(15000);
				int status;
				status = connection.getResponseCode();
				
				if(status == HttpURLConnection.HTTP_UNAUTHORIZED){
					Log.d("JLK", "No authorization");
				}
				else if(status != HttpURLConnection.HTTP_OK){
					Log.d("JLK", "Status code: " + status);
				}
				else{
					//Gets all the data from the connection and saves it in a string.
					stream = connection.getInputStream();
					scanner = new Scanner(stream);
					
					jsonresults = scanner.useDelimiter("\\A").next();
					scanner.close();
					
					JSONObject json = new JSONObject(jsonresults);
					JSONArray values = json.getJSONArray("Classes");
					classes = new ClassItem[values.length()];
					for(int i = 0; i < values.length(); i++){
						JSONObject classItem = values.getJSONObject(i);
						className = classItem.getString("className");
						professor = classItem.getString("professor");
						classId = classItem.getString("classId");
						classes[i] = new ClassItem(className, professor, classId);
					}
				}
			} catch (IOException | JSONException e) {
				Log.e("JLK", "URL is bad");
				e.printStackTrace();
			}

			return classes;
		}
		
		
		protected void onPostExecute(ClassItem[] result){
			//Sets the adapter to the list view in the activity_main file.
			//Also makes the progress bar disappear.
			//progress.setVisibility(View.GONE);
			adapter = new ClassesAdapter(context, classes);
			list.setAdapter(adapter);
		}

	}
}
