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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditTask extends Activity {
	private static final String TAG = EditTask.class.getSimpleName();
	private Spinner goalSpin;
	private TimePicker tp;
	private DatePicker dp;
	private String goalName;
	private Goal goal;
	private Goal currentGoal;
	private EditText et;
	private Button bt;

	private ArrayList<String> goalsStrings;
	private List<Goal> goals;
	private int value;
	private TextView val;

	private GoalDAO goalDAO;
	private TaskDAO taskDAO;

	// TODO change date picker view

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_form);

		Intent in = getIntent();
		Task t = (Task) in.getSerializableExtra("task");
		currentGoal = t.getGoal();
		taskDAO = new TaskDAO(this);
		goalDAO = new GoalDAO(this);

		taskDAO.open();
		taskDAO.deleteTaskEntry(t);

		tp = (TimePicker) findViewById(R.id.pick_deadline_time);
		dp = (DatePicker) findViewById(R.id.pick_deadline);
		et = (EditText) findViewById(R.id.taskname);
		bt = (Button) findViewById(R.id.add_task);

		bt.setText("Finished Editing");
		et.setText(t.getName());

		Calendar cal = t.getDeadline();
		tp.setCurrentHour(cal.HOUR_OF_DAY);
		tp.setCurrentMinute(cal.MINUTE);
		dp.updateDate(cal.YEAR, cal.MONTH, cal.DAY_OF_MONTH);

		val = (TextView) findViewById(R.id.value);
		val.setText(t.getValue());

		SeekBar sb = (SeekBar) findViewById(R.id.add_value);
		sb.setProgress(t.getValue());
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean bool) {

				String v = "Value: " + Integer.toString(seekBar.getProgress());
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

		goalDAO.open();

		goals = goalDAO.getAllGoals();

		addItemsToSpinner();
		addSpinnerItemClickListener();

		tp = (TimePicker) findViewById(R.id.pick_deadline_time);
		tp.setIs24HourView(true);

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
		int pos = goalsStrings.indexOf(currentGoal.getName());
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, goalsStrings);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		goalSpin.setSelection(pos);
		goalSpin.setAdapter(dataAdapter);

	}

	private void addSpinnerItemClickListener() {
		goalSpin = (Spinner) findViewById(R.id.goal_spinner);
		goalSpin.setOnItemSelectedListener(new CustomOnItemSelectedListener());

	}

	public void addTask(View view) {
		int hour = tp.getCurrentHour();
		int minute = tp.getCurrentMinute();

		int day = dp.getDayOfMonth();
		int month = dp.getMonth();
		int year = dp.getYear();

		Calendar deadline = createCalendar(hour, minute, day, month, year);

		if (deadline.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
			createAlert(getResources().getString(R.string.alert_task_deadlinepast)).show();
			return;
		}

		et = (EditText) findViewById(R.id.taskname);

		String taskName = et.getText().toString().trim();

		if (taskName.isEmpty()) {
			createAlert(getResources().getString(R.string.alert_task_name_empty)).show();
			return;
		} else if (taskName.length() > 30) {
			// TODO change task length with layout change;
			createAlert(getResources().getString(R.string.alert_task_name_toolong)).show();
			return;
		}

		if (goal.getDeadline() != null) {
			if (goal.getDeadline().before(deadline)) {
				createAlert(getResources().getString(R.string.alert_deadline_before_goal)).show();
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
				intent.setClass(EditTask.this, GoalForm.class);
				EditTask.this.startActivityForResult(intent, 0);
			} else {
				Toast.makeText(EditTask.this, "Choosen Goal: " + goalName, Toast.LENGTH_SHORT)
						.show();

				goalDAO.open();
				goal = goalDAO.getGoal(goalName);

			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Toast.makeText(EditTask.this, "Choose a Goal for this Task", Toast.LENGTH_SHORT).show();

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
