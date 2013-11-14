package com.lnu.todomorrow;

import java.util.Calendar;

import com.lnu.todomorrow.dao.DAOException;
import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.utils.Goal;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	public void addDeadlineChecked(View view) {
		Log.d(TAG, "addDeadlineChecked()");

		LinearLayout lay = (LinearLayout) findViewById(R.id.goal_form_linearlayout);

		if (!deadlineChecked) {

			TextView dateText = new TextView(this);
			dateText.setText("Date of Deadline: ");
			lay.addView(dateText);

			//TODO change date picker view
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

	public void addGoalButtonClick(View v) {
		Log.d(TAG, "addGoal button clicked");
		EditText nameReader = (EditText) findViewById(R.id.goal_form_name_reader);
		String goalName = nameReader.getText().toString();

		if (goalName.isEmpty()) {
			Log.e(TAG, "goal name is empty");
			createAlert(getResources().getString(R.string.alert_goal_name_empty)).show();
			return;
		} else if (goalName.length() > 14) {
			// TODO change goal length with layout change; maybe - change layout to support longer
			createAlert(getResources().getString(R.string.alert_goal_name_toolong)).show();
			return;
		}

		Calendar deadline = null;

		if (deadlineChecked) {

			int day = deadDate.getDayOfMonth();
			int month = deadDate.getMonth();
			int year = deadDate.getYear();

			int hour = deadTime.getCurrentHour();
			int min = deadTime.getCurrentMinute();

			deadline = createCalendar(day, month, year, hour, min);

			if (deadline.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
				Log.e(TAG, "deadline is in the past");
				createAlert(getResources().getString(R.string.alert_goal_deadlinepast)).show();
				return;
			}

		}

		GoalDAO dao = new GoalDAO(this);
		dao.open();

		Intent reply = new Intent();

		Goal goal = null;
		try {
			goal = dao.createGoalEntry(goalName, deadline);

		} catch (DAOException e) {

			Log.e(TAG, "Error in goal creation: " + e.getMessage());
			// e.printStackTrace();
			createAlert(getResources().getString(R.string.alert_goal_notunique)).show();

			return;
		}

		reply.putExtra("goal", goal);
		setResult(RESULT_OK, reply);
		finish();
	}

	/**
	 * @param day
	 * @param month
	 * @param year
	 * @param hour
	 * @param min
	 * @return
	 */
	private Calendar createCalendar(int day, int month, int year, int hour, int min) {
		Calendar calendar;
		calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, min);
		calendar.set(Calendar.SECOND, 0);

		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		return calendar;
	}

	private AlertDialog createAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setMessage(R.string.alert_msg);

		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton(getResources().getString(android.R.string.ok),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		return builder.create();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent1 = createBackIntent();
			startActivity(intent1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * creates dynamically an intent to send back to the calling activity (startActivityForResult)
	 * 
	 * @return
	 */
	private Intent createBackIntent() {
		Intent intent1;
		// determine which activity called the form and return correspondingly
		ComponentName caller = getCallingActivity();
		if (caller != null) {
			Log.d(TAG, "bla. " + caller.getClassName());
			Class<?> cls = null;
			try {
				cls = Class.forName(caller.getClassName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			intent1 = new Intent(this, cls);
			intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		} else {
			intent1 = new Intent(this, GoalList.class);
			intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		}
		return intent1;
	}

}
