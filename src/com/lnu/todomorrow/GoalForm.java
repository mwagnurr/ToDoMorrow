package com.lnu.todomorrow;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class GoalForm extends Activity {

	private static final String TAG = GoalForm.class.getSimpleName();

	private DatePicker deadDate;
	private TimePicker deadTime;
	private boolean deadlineChecked = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goal_form);

	}

	public void addDeadlineChecked(View view) {
		Log.d(TAG, "addDeadlineChecked()");

		LinearLayout lay = (LinearLayout) findViewById(R.id.goal_form_linearlayout);

		if (!deadlineChecked) {

			TextView dateText = new TextView(this);
			dateText.setText("Date of Deadline: ");
			lay.addView(dateText);

			deadDate = new DatePicker(this);
			lay.addView(deadDate);
			TextView timeText = new TextView(this);
			timeText.setText("Time of Deadline: ");
			lay.addView(timeText);

			deadTime = new TimePicker(this);
			lay.addView(deadTime);

			deadlineChecked = true;

			Log.d(TAG, "added dynamic layout content for deadline");
		} else {
			
			deadDate = null;
			deadTime = null;
			lay.removeAllViews();
			Log.d(TAG, "destroyed dynamic layout content for deadline");

			deadlineChecked = false;
		}

	}
	
	public void addGoalButtonClick(View v){
		Log.d(TAG, "addGoal button clicked");
		EditText nameReader = (EditText) findViewById(R.id.goal_form_name_reader);
		String name = nameReader.getText().toString();

		
		Intent reply = new Intent();
		reply.putExtra("goal_name", name);
		
		if(deadlineChecked){
			
			int day = deadDate.getDayOfMonth();
			int month = deadDate.getMonth();
			int year = deadDate.getYear();

			
			int hour = deadTime.getCurrentHour();
			int min = deadTime.getCurrentMinute();

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, min);
			calendar.set(Calendar.SECOND, 0);
			
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.YEAR, year);
			
			Log.d(TAG, "created calendar: " + calendar);
			
			reply.putExtra("goal_deadline", calendar);	
		}
		

		setResult(RESULT_OK, reply);
		finish();
	}
}
