package com.example.gradecalculator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

import com.example.gradecalculator.AddClass.classRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

//Class that holds all the information for a specific class.
public class ClassInfo extends ActionBarActivity{
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	String mainURL = "http://54.200.94.110/GradeCalculator/api/index.php/Class/";
	DownloadJsonList task = new DownloadJsonList(this);
	String classId;
	public static Integer current;
	ExpandableListView categoriesList;
	ArrayList<String> spinnerArray;
	Category[] categories;
	public static Activity activity;
	Context thisContext;
	
	public static final String CLASS_KEY = "ClassKey";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = sharedPref.edit();
		Intent intent = getIntent();
		ArrayList<Integer> classItem = intent.getIntegerArrayListExtra(CLASS_KEY);
		current = classItem.get(0);
		thisContext = this;
		activity = this;
		TextView className = (TextView) findViewById(R.id.className);
		TextView professor = (TextView) findViewById(R.id.professor);
		categoriesList = (ExpandableListView) findViewById(R.id.scrollGrading);
		Button addGrade = (Button) findViewById(R.id.addGrade);
		Button deleteClass = (Button) findViewById(R.id.deleteClass);
		
		//Sets a click listener to add a new grade.
		addGrade.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				addGradeShow(v);
			}
		});
		
		//Click listener to delete a class.
		deleteClass.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				deleteClass(v);
			}
		});
		
		
		className.setText(ClassesAdapter.classes[current].getClassName());
		professor.setText("Professor: " + ClassesAdapter.classes[current].getProfessor());
		classId = ClassesAdapter.classes[current].getClassId();
		//Starts a task to get all the information for the specific class.
		task.execute(mainURL);	
	}
	
	//Shows the alert dialog for adding a grade.
	public void addGradeShow(View view){
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		
		LayoutInflater inflater = getLayoutInflater();
		View inflateThis = inflater.inflate(R.layout.add_grade,null);
		dialog.setView(inflateThis);
		
		final Spinner values = (Spinner) inflateThis.findViewById(R.id.addCategory);
		final EditText gradeName = (EditText) inflateThis.findViewById(R.id.addGradeName);
		final EditText startScore = (EditText) inflateThis.findViewById(R.id.topNumber);
		final EditText endScore = (EditText) inflateThis.findViewById(R.id.bottomNumber);
		final Button submitGrade = (Button) inflateThis.findViewById(R.id.addGrade);
		final Button cancelSubmit = (Button) inflateThis.findViewById(R.id.cancelAddGrade);
		ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categories);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		values.setAdapter(adapter);
		final AlertDialog dialogFinal = dialog.create();
		dialogFinal.show();
		
		//Adds a click listener to close the dialog.
		cancelSubmit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialogFinal.dismiss();
			}
		});
		
		//Adds a click listener to submit the new grade. Then closes the dialog.
		submitGrade.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String newGradeName = gradeName.getText().toString();
				String newStartScore = startScore.getText().toString();
				String newEndScore = endScore.getText().toString();
				
				//Checks for proper values.
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
					String categoryId = ((Category) values.getSelectedItem()).getCategoryId();
		        	
		        	
		        	
		        	Integer startGrade = Integer.parseInt(newStartScore);
		        	Integer endGrade = Integer.parseInt(newEndScore);
		        	Double finalScore = (double)(startGrade)/(double) (endGrade) * 100;
		        	String totalScore = String.valueOf(finalScore);
		        	Log.d("JLK", totalScore);
		        	//Sends the information to the database, closes the dialog, and refreshes the activity.
		        	new gradeRequest(thisContext).execute(categoryId, newGradeName, totalScore);
		        	Intent intent = getIntent();
					finish();
					startActivity(intent);
					dialogFinal.dismiss();
				}
			}
		});
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
		if(id == R.id.signOut) {
			editor.clear().commit();
			Intent intent = new Intent(this, MainActivity.class);
			finish();
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
//Downloads all the information for the specific class.	
public class DownloadJsonList extends AsyncTask<String, Void, Category[]>{
		
		//The list of variables used for this class.
		CategoriesAdapter secondAdapter;
		HttpURLConnection connection;
		URL requestURL;
		InputStream stream;
		Scanner scanner;
		Context context;
		String jsonresults;
		double totalGrade;

		//Constructor for the subclass to take in the proper context.
		public DownloadJsonList(Context context){
			this.context = context;	
		}
		//Parses all the json and sets values.
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
					spinnerArray =  new ArrayList<String>();
					double calcCategory =0;
					for(int i = 0; i < values.length(); i++){
						JSONObject categoryItem = values.getJSONObject(i);
						String categoryId = categoryItem.getString("categoryId");
						String categoryName = categoryItem.getString("categoryName");
						String categoryPercentage = categoryItem.getString("categoryPercentage");
						categories[i] = new Category();
						categories[i].setCategoryId(categoryId);
						categories[i].setCategoryName(categoryName);
						//Adds categories to the spinner.
						spinnerArray.add(categoryName);
						Log.d("JLK", categoryName);
						categories[i].setPercentage(categoryPercentage);
						JSONArray gradeItems = categoryItem.getJSONArray("grades");
						Grade[] grades = new Grade[gradeItems.length()];
						double totalGrade = 0;
						//Adds up all the percentages to calculate the grade.
						for(int j = 0; j < gradeItems.length(); j++){
							JSONObject gradeItem = gradeItems.getJSONObject(j);
							String gradeName = gradeItem.getString("gradeName");
							String gradePercentage = gradeItem.getString("percentage");
							String gradeId = gradeItem.getString("gradeId");
							grades[j] = new Grade();
							grades[j].setGradeName(gradeName);
							grades[j].setPercentage(gradePercentage);
							grades[j].setGradeId(gradeId);
							totalGrade += Double.parseDouble(gradePercentage);
						}
						if(gradeItems.length()>0){
							calcCategory += (double) (totalGrade/(gradeItems.length()*100)) * (double) Integer.parseInt(categoryPercentage);
						}
						
						categories[i].setGrades(grades);
						
					}
					totalGrade = calcCategory;
					
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

			//Calls the expandable list view with the information and sets all the known information.
			TextView currentGrade = (TextView) findViewById(R.id.currentPercentage);
			currentGrade.setText("Current Grade: " + String.format("%.2f", totalGrade) +"%");
			secondAdapter = new CategoriesAdapter(context, result);
			categoriesList.setAdapter(secondAdapter);
			super.onPostExecute(result);
		}

	}

	//An async task for adding a grade to the database.
	class gradeRequest extends AsyncTask<String, Void, String>{
		Context context;
	    private gradeRequest(Context context) {
	        this.context = context.getApplicationContext();
	    }
		
		@Override
		protected String doInBackground(String... params) {
			HttpResponse response = null;
			HttpClient client= new DefaultHttpClient();
			HttpPost post = new HttpPost("http://54.200.94.110/GradeCalculator/api/index.php/AddGrade");

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("categoryId", params[0]));
			pairs.add(new BasicNameValuePair("gradeName", params[1]));
			pairs.add(new BasicNameValuePair("percentage", params[2]));
			
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
	
	//Async task to delete the whole class.
	class deleteClassRequest extends AsyncTask<String, Void, String>{
		Context context;
	    private deleteClassRequest(Context context) {
	        this.context = context.getApplicationContext();
	    }
		
		@Override
		protected String doInBackground(String... params) {
			HttpResponse response = null;
			HttpClient client= new DefaultHttpClient();
			HttpPost post = new HttpPost("http://54.200.94.110/GradeCalculator/api/index.php/DeleteClass");

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("classId", params[0]));
			
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
		
		//Once the class is deleted it goes back to the list of classes.
		@Override
		protected void onPostExecute(String response) {
			CharSequence text = "";
			Log.d("JLK", response);
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK , intent);
			finish();
			return;
	     }
	}
	
	
	//Function to call the async task to delete the class.
	public void deleteClass(View view){
		new deleteClassRequest(thisContext).execute(classId);
	}
	

}
