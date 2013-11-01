package com.lnu.todomorrow;

import java.util.ArrayList;
import java.util.List;

import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.utils.Goal;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class TaskForm extends Activity {
	private static final String TAG = TaskForm.class.getSimpleName();
	private Spinner goalSpin;
	private TimePicker tp;
	private String goal;
	private GoalDAO goalDB;
	ArrayList<String> goals;
	List<Goal> g;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_form);

		goalDB = new GoalDAO(this);
		addItemsToSpinner();
		addSpinnerItemClickListener();

		tp = (TimePicker) findViewById(R.id.pick_deadline_time);
		tp.setIs24HourView(true);
		Log.d(TAG, "onCreate()");
	}

	private void addItemsToSpinner() {
		goalSpin = (Spinner) findViewById(R.id.goal_spinner);
		goalDB.open();

		g = goalDB.getAllGoals();
		goals = new ArrayList<String>();

		goals.add("Select Goal");
		goals.add("New Goal");
		for (Goal go : g) {
			goals.add(go.getName());
		}

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

	public void addTask(View view) {
		int hour = tp.getCurrentHour();
		int minute = tp.getCurrentMinute();

		DatePicker dp = (DatePicker) findViewById(R.id.pick_deadline);
		int day = dp.getDayOfMonth();
		int month = dp.getMonth();
		int year = dp.getYear();

		EditText et = (EditText) findViewById(R.id.taskname);
		String taskName = et.getText().toString().trim();

		Intent reply = new Intent();
		reply.putExtra("dead_h", hour);
		reply.putExtra("dead_min", minute);
		reply.putExtra("dead_d", day);
		reply.putExtra("dead_m", month);
		reply.putExtra("dead_y", year);
		reply.putExtra("task_name", taskName);
		reply.putExtra("goal", goal);
		setResult(RESULT_OK, reply);
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				goalDB = new GoalDAO(this);
				addItemsToSpinner();
				goalSpin.setSelection(goals.size() - 1);
				goal = goals.get(goals.size() - 1).toString();
			}
		}
	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			goal = arg0.getItemAtPosition(arg2).toString();
			if (goal.equalsIgnoreCase("Select Goal")) {

			} else if (goal.equalsIgnoreCase("New Goal")) {
				Intent intent = new Intent();
				intent.setClass(TaskForm.this, GoalForm.class);
				TaskForm.this.startActivityForResult(intent, 0);
			} else
				Toast.makeText(TaskForm.this, "Choosen Goal: " + goal,
						Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Toast.makeText(TaskForm.this, "Choose a Goal for this Task",
					Toast.LENGTH_SHORT).show();

		}

	}
}
