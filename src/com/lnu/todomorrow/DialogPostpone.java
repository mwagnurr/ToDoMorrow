package com.lnu.todomorrow;

import java.util.Calendar;

import com.lnu.todomorrow.dao.TaskDAO;
import com.lnu.todomorrow.utils.Task;

import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class DialogPostpone extends Activity {
	private static final String TAG = DialogPostpone.class.getSimpleName();

	private TimePicker tp;
	private DatePicker dp;

	private TaskDAO taskDB;

	private long taskId;
	private int notifId;

	private NotificationAlarmManager alarmMan = new NotificationAlarmManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			taskId = extras.getLong("task_id", -1);
			getIntent().removeExtra("task_id");

			notifId = extras.getInt("notif_id", -1);
			getIntent().removeExtra("notif_id");

			if (taskId == -1) {
				Log.e(TAG, "didn't receive correct task_id");
			}
			if (notifId == -1) {
				Log.e(TAG, "didn't receive correct notif_id");
			}

		} else {
			Log.e(TAG, "no extras in the intent");
		}
		Log.d(TAG, "id in dialogPostone: " + taskId);

		setContentView(R.layout.activity_dialog_postpone);
		tp = (TimePicker) findViewById(R.id.pick_deadline_time);
		tp.setIs24HourView(true);
		// important because Android sucks
		tp.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
		dp = (DatePicker) findViewById(R.id.pick_deadline);

		taskDB = new TaskDAO(getApplicationContext());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dialog_postpone, menu);
		return true;
	}

	public void dialogOK(View view) {

		Log.d(TAG, "pressed dialog OK");
		int hour = tp.getCurrentHour();
		int minute = tp.getCurrentMinute();

		int day = dp.getDayOfMonth();
		int month = dp.getMonth();
		int year = dp.getYear();

		Log.d(TAG, "DEBUG: " + hour + ":" + minute + ", " + day + "/" + month + "/" + year);

		Calendar changedDeadline = Calendar.getInstance();
		changedDeadline.set(Calendar.HOUR_OF_DAY, hour);
		changedDeadline.set(Calendar.MINUTE, minute);
		changedDeadline.set(Calendar.SECOND, 0);

		changedDeadline.set(Calendar.DAY_OF_MONTH, day);
		changedDeadline.set(Calendar.MONTH, month);
		changedDeadline.set(Calendar.YEAR, year);

		taskDB.open();
		Task task = taskDB.getTask(taskId);
		task.setDeadline(changedDeadline);
		taskDB.updateTask(task);
		Toast.makeText(getApplicationContext(), "Postpone ok", Toast.LENGTH_SHORT).show();

		alarmMan.changeAlarmForTask(getApplicationContext(), task, changedDeadline);
		// alarmMan.removeAlarmForTask(getApplicationContext(), task);

		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notifId);

		finish();
	}

	public void dialogCancel(View view) {
		finish();
	}

}
