package com.example.gradecalculator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

//An activity called in the beginning. Decides if you're logged in or not.
public class StartActivity extends Activity{
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_app);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = sharedPref.edit();
		
		Thread wait = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(2500);
                } catch (Exception e) {

                } finally {
                	Intent intent;
                	//If your're logged in go to the class list.
                	if (sharedPref.contains("user_id")) {
                		UserInfo temp = UserInfo.getInstance();
                		Integer id = Integer.parseInt(sharedPref.getString("user_id", ""));
        		        temp.setUserId(id);
                		intent = new Intent(StartActivity.this, Classes.class);
                		
            		} else {
            			//Else go to the page to login.
            			intent = new Intent(StartActivity.this, MainActivity.class);
            		}
                    startActivity(intent);
                    finish();
                }
            }
        };
        wait.start();
		
		
		
	}

}
