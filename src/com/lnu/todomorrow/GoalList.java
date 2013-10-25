package com.lnu.todomorrow;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class GoalList extends Activity {
	private static final String TAG = GoalList.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goal_list);
		
		Log.d(TAG,"onCreate()");
	}
	
}
