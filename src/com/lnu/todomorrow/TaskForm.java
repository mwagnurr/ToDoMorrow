package com.lnu.todomorrow;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class TaskForm extends Activity {
	private static final String TAG = TaskForm.class.getSimpleName();
	private Spinner goalSpin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_form);

		addItemsToSpinner();
		addSpinnerItemClickListener();
		Log.d(TAG, "onCreate()");
	}

	private void addItemsToSpinner() {
		goalSpin = (Spinner) findViewById(R.id.goal_spinner);
		// retrieve all available goals from database
		ArrayList<String> goals = new ArrayList<String>();
		// run through List of Goals, add names to ArrayList
		goals.add("New Goal");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, goals);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		goalSpin.setAdapter(dataAdapter);

	}
	
	private void addSpinnerItemClickListener() {
		goalSpin = (Spinner) findViewById(R.id.goal_spinner);
		goalSpin.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		
	}
}
