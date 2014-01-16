package com.lnu.todomorrow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.lnu.todomorrow.dao.DAOException;
import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.dao.TaskDAO;
import com.lnu.todomorrow.utils.Goal;
import com.lnu.todomorrow.utils.Task;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class TaskForm extends Activity {
	private static final String TAG = TaskForm.class.getSimpleName();
	private Spinner goalSpin;
	private TimePicker tp;
	private String goalName;
	private Goal goal;

	private ArrayList<String> goalsStrings;
	private List<Goal> goals;
	private int value;
	private TextView val;

	private GoalDAO goalDAO;
	private TaskDAO taskDAO;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_form);

		val = (TextView) findViewById(R.id.value);
		SeekBar sb = (SeekBar) findViewById(R.id.add_value);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean bool) {

				String v = getResources().getString(R.string.task_value)
						+ Integer.toString(seekBar.getProgress());
				val = (TextView) findViewById(R.id.value);
				val.setText(v);
				value = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

		});

		taskDAO = new TaskDAO(this);
		goalDAO = new GoalDAO(this);

		if (getIntent().getExtras() != null) {
			goals = new ArrayList<Goal>();
			Goal activeGoal = (Goal) getIntent().getSerializableExtra("goal");
			goals.add(activeGoal);
		} else {
			goalDAO.open();

			goals = goalDAO.getAllGoals();

		}
		addItemsToSpinner();
		addSpinnerItemClickListener();

		tp = (TimePicker) findViewById(R.id.pick_deadline_time);
		tp.setIs24HourView(true);
		tp.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Log.d(TAG, "onCreate() finished");
	}

	@Override
	public void onDestroy() {
		if (goalDAO != null)
			goalDAO.close();
		if (taskDAO != null)
			taskDAO.close();
		super.onDestroy();
	}

	private void addItemsToSpinner() {
		goalSpin = (Spinner) findViewById(R.id.goal_spinner);
		goalsStrings = new ArrayList<String>();

		for (Goal go : goals) {
			goalsStrings.add(go.getName());
		}
		goalsStrings.add("New Goal");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, goalsStrings);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

		Calendar deadline = createCalendar(hour, minute, day, month, year);

		if (deadline.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
			Log.d(TAG, "deadline time vs curr time: " + deadline.getTimeInMillis() + " < "
					+ Calendar.getInstance().getTimeInMillis());
			createAlert(getResources().getString(R.string.alert_task_deadlinepast)).show();
			return;
		} else {
			Log.d(TAG,
					"Deadline correct - deadline time vs curr time: " + deadline.getTimeInMillis()
							+ " >= " + Calendar.getInstance().getTimeInMillis());

		}

		EditText et = (EditText) findViewById(R.id.taskname);

		String taskName = et.getText().toString().trim();

		if (taskName.isEmpty()) {
			createAlert(getResources().getString(R.string.alert_task_name_empty)).show();
			return;
		} else if (taskName.length() > 22) {
			createAlert(getResources().getString(R.string.alert_task_name_toolong)).show();
			return;
		}

		if (goal != null && goal.getDeadline() != null) {
			if (goal.getDeadline().before(deadline)) {
				createAlert(getResources().getString(R.string.alert_deadline_after_goal)).show();
				return;
			}
		}

		Task task = null;
		try {
			taskDAO.open();
			task = taskDAO.createTaskEntry(taskName, deadline, value, goal);
		} catch (DAOException e) {
			Log.e(TAG, "Couldn't create task: " + e.getMessage());
			createAlert("Couldn't create task").show();
			// e.printStackTrace();
			return;
		}
		Intent reply = new Intent();
		if (task == null) {
			setResult(RESULT_CANCELED, reply);
			finish();
		}

		reply.putExtra("task", task);
		setResult(RESULT_OK, reply);
		finish();
	}

	/**
	 * creates a Calendar instances
	 * 
	 * @param hour
	 * @param minute
	 * @param day
	 * @param month
	 * @param year
	 * @return
	 */
	private Calendar createCalendar(int hour, int minute, int day, int month, int year) {
		// Log.d(TAG, "DEBUG: " +hour + ":" + minute + ", " +day+ "/" +month +"/" + year);
		Calendar deadline = Calendar.getInstance();
		deadline.set(Calendar.HOUR_OF_DAY, hour);
		deadline.set(Calendar.MINUTE, minute);
		deadline.set(Calendar.SECOND, 0);

		deadline.set(Calendar.DAY_OF_MONTH, day);
		deadline.set(Calendar.MONTH, month);
		deadline.set(Calendar.YEAR, year);
		return deadline;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				addItemsToSpinner();
				goalSpin.setSelection(goalsStrings.size() - 2);
				goalName = goalsStrings.get(goalsStrings.size() - 2).toString();
				goal = (Goal) result.getSerializableExtra("goal");
			}
		}
	}

	private class CustomOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			goalName = arg0.getItemAtPosition(arg2).toString();
			if (goalName.equalsIgnoreCase("New Goal")) {
				Intent intent = new Intent();
				intent.setClass(TaskForm.this, GoalForm.class);
				TaskForm.this.startActivityForResult(intent, 0);
			} else {
				Toast.makeText(TaskForm.this, "Chosen Goal: " + goalName, Toast.LENGTH_SHORT)
						.show();

				goalDAO.open();
				goal = goalDAO.getGoal(goalName);

			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Toast.makeText(TaskForm.this, "Choose a Goal for this Task", Toast.LENGTH_SHORT).show();

		}

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
	 * creates an intent dynamically to send back the result to the calling activity
	 * (startActivityForResult)
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
			intent1 = new Intent(this, TaskList.class);
			intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		}
		return intent1;
	}

}
