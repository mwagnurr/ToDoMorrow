package com.lnu.todomorrow;

import java.util.Calendar;
import com.lnu.todomorrow.dao.*;
import com.lnu.todomorrow.utils.Goal;
import com.lnu.todomorrow.utils.MyBroadcastReceiver;
import com.lnu.todomorrow.utils.Task;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class TaskList extends Activity {
	private static final String TAG = TaskList.class.getSimpleName();
	private static TaskDAO dataTasks;
	private static GoalDAO dataGoals;
	private TaskListFragment list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_list);

		list = (TaskListFragment) getFragmentManager().findFragmentById(
				R.id.task_list_fragment);

		dataTasks = new TaskDAO(this);

		dataGoals = new GoalDAO(this);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public void onDestroy() {
		dataTasks.close();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent1 = new Intent(this, GoalList.class);
			intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent1);
			return true;
		case R.id.filter_by_goal:
			// code for filtering by goal
		case R.id.show_goalList:
			Intent intent = new Intent(this, GoalList.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 0);
			return true;
		case R.id.sort_by_deadline:
			list.sortListByDeadline();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void addTask(View view) {
		Intent intent = new Intent(this, TaskForm.class);
		this.startActivityForResult(intent, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				int y = result.getIntExtra("dead_y", 2013);
				int m = result.getIntExtra("dead_m", 1);
				int d = result.getIntExtra("dead_d", 1);

				int h = result.getIntExtra("dead_h", 12);
				int min = result.getIntExtra("dead_min", 00);

				int val = result.getIntExtra("value", 0);

				String name = result.getStringExtra("task_name");

				String g = result.getStringExtra("goal");
				dataGoals.open();
				Goal goal = dataGoals.getGoal(g);
				System.out.println(goal.getName());

				Calendar cal = Calendar.getInstance();

				cal.set(Calendar.HOUR_OF_DAY, h);
				cal.set(Calendar.MINUTE, min);
				cal.set(Calendar.SECOND, 0);

				cal.set(Calendar.DAY_OF_MONTH, d);
				cal.set(Calendar.MONTH, m);
				cal.set(Calendar.YEAR, y);

				dataTasks.open();
				Task task = dataTasks.createTaskEntry(name, cal, val, goal);
//				task.setGoal(goal);
//				dataTasks.updateTask(task);

				Intent intent = new Intent(TaskList.this,
						MyBroadcastReceiver.class);
				PendingIntent pi = PendingIntent.getBroadcast(TaskList.this, 0,
						intent, 0);

				// calculation of time difference between now and
				// deadline of taks
				long deadTimeMs = task.getDeadline().getTimeInMillis();
				Calendar currTime = Calendar.getInstance();
				long currTimeMs = currTime.getTimeInMillis();
				long timeDiff = deadTimeMs - currTimeMs;

				// setting alarm
				AlarmManager alarmMan = (AlarmManager) getSystemService(ALARM_SERVICE);
				alarmMan.set(AlarmManager.RTC_WAKEUP, timeDiff, pi);

				Log.d(TAG, "created task: " + task);
				list.addTask(task);
			}
		}
	}
}
