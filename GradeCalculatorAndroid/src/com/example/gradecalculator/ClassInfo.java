package com.example.gradecalculator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
import android.widget.ExpandableListView;
import android.widget.TextView;

public class ClassInfo extends ActionBarActivity{
	String mainURL = "http://54.200.94.110/GradeCalculator/api/index.php/Class/";
	DownloadJsonList task = new DownloadJsonList(this);
	String classId;
	Integer current;
	ExpandableListView categoriesList;
	
	public static final String CLASS_KEY = "ClassKey";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class);
		Intent intent = getIntent();
		ArrayList<Integer> classItem = intent.getIntegerArrayListExtra(CLASS_KEY);
		current = classItem.get(0);
		
		TextView className = (TextView) findViewById(R.id.className);
		TextView professor = (TextView) findViewById(R.id.professor);
		categoriesList = (ExpandableListView) findViewById(R.id.scrollGrading);
		
		
		className.setText(ClassesAdapter.classes[current].getClassName());
		professor.setText("Professor: " + ClassesAdapter.classes[current].getProfessor());
		classId = ClassesAdapter.classes[current].getClassId();
		task.execute(mainURL);	
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
	
public class DownloadJsonList extends AsyncTask<String, Void, Category[]>{
		
		//The list of variables used for this class.
		Category[] categories;
		CategoriesAdapter adapter;
		HttpURLConnection connection;
		URL requestURL;
		InputStream stream;
		Scanner scanner;
		Context context;
		String jsonresults;


		

		
		//Constructor for the subclass to take in the proper context.
		public DownloadJsonList(Context context){
			this.context = context;	
		}
		
		protected Category[] doInBackground(String... urls){
			
			//Opens up a connection.
			try {
				String newUrl = urls[0] + classId;
				requestURL = new URL(newUrl);
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
					JSONArray values = json.getJSONArray("Categories");
					categories = new Category[values.length()];
					for(int i = 0; i < values.length(); i++){
						JSONObject categoryItem = values.getJSONObject(i);
						String categoryId = categoryItem.getString("categoryId");
						String categoryName = categoryItem.getString("categoryName");
						String categoryPercentage = categoryItem.getString("categoryPercentage");
						categories[i] = new Category();
						categories[i].setCategoryId(categoryId);
						categories[i].setCategoryName(categoryName);
						Log.d("JLK", categoryName);
						categories[i].setPercentage(categoryPercentage);
						JSONArray gradeItems = categoryItem.getJSONArray("grades");
						Grade[] grades = new Grade[gradeItems.length()];
						for(int j = 0; j < gradeItems.length(); j++){
							JSONObject gradeItem = gradeItems.getJSONObject(j);
							String gradeName = gradeItem.getString("gradeName");
							String gradePercentage = gradeItem.getString("percentage");
							String gradeId = gradeItem.getString("gradeId");
							grades[j] = new Grade();
							grades[j].setGradeName(gradeName);
							grades[j].setPercentage(gradePercentage);
							grades[j].setGradeId(gradeId);
						}
						categories[i].setGrades(grades);
						
					}
				}
			} catch (IOException | JSONException  e) {
				Log.e("JLK", "URL is bad");
				e.printStackTrace();
			}

			return categories;
		}
		
		
		protected void onPostExecute(Category[] result){
			//Sets the adapter to the list view in the activity_main file.
			//Also makes the progress bar disappear.
			//progress.setVisibility(View.GONE);
			adapter = new CategoriesAdapter(context, result);
			categoriesList.setAdapter(adapter);
			super.onPostExecute(result);
		}

	}

}
