package com.lnu.todomorrow;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.lnu.todomorrow.dao.*;
import com.lnu.todomorrow.utils.Goal;
import com.lnu.todomorrow.utils.MyBroadcastReceiver;
import com.lnu.todomorrow.utils.Task;
import com.lnu.todomorrow.utils.TimeUtil;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskList extends Activity {
	private static final String TAG = TaskList.class.getSimpleName();
	private ListView lv;
	private MyAdapter adapter;
	private static TaskDAO datasource;
	private List<Task> tasks;
	private List<Goal> goals;
	private ScoreManager scoreMan;
	private static GoalDAO dataGoals;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_list);
		lv = (ListView) findViewById(R.id.list);
		scoreMan = new ScoreManager();
		datasource = new TaskDAO(this);
		datasource.open();
		tasks = datasource.getAllTasks();

		dataGoals = new GoalDAO(this);
		dataGoals.open();
		goals = dataGoals.getAllGoals();
		
		adapter = new MyAdapter(this, R.layout.row_layout, tasks);
		lv.setAdapter(adapter);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public void onDestroy() {
		datasource.close();
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
			Collections.sort(tasks, new DateComparator());
			if(adapter == null){
				adapter = new MyAdapter(this, R.layout.row_layout, tasks);
			}
			adapter.notifyDataSetChanged();
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

				String name = result.getStringExtra("task_name");

				int g = result.getIntExtra("goal" , 0);
				Goal goal = goals.get(g);
				

				Calendar cal = Calendar.getInstance();

				cal.set(Calendar.HOUR_OF_DAY, h);
				cal.set(Calendar.MINUTE, min);
				cal.set(Calendar.SECOND, 0);

				cal.set(Calendar.DAY_OF_MONTH, d);
				cal.set(Calendar.MONTH, m);
				cal.set(Calendar.YEAR, y);

				datasource.open();
				Task task = datasource.createTaskEntry(name, cal, goal);

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
				adapter.add(task);
				adapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * New Adapter Class for row-layout in WeatherApp
	 * 
	 * @author Lia
	 * 
	 */
	class MyAdapter extends ArrayAdapter<Task> {
		Context context;

		public MyAdapter(Context context, int resource, List<Task> objects) {
			super(context, resource, objects);
		}

		@Override
		// Called when updating the ListView
		public View getView(int position, View convertView, ViewGroup parent) {
			View row;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row_layout, parent, false);
			} else {
				row = convertView;
			}

			Collections.sort(tasks, new BooleanComparator());
			TextView name = (TextView) row.findViewById(R.id.show_task);
			TextView dead = (TextView) row.findViewById(R.id.show_deadline);
			TextView goal = (TextView) row.findViewById(R.id.show_goal);

			CheckBox check = (CheckBox) row.findViewById(R.id.check);
			check.setTag(position);
			check.setChecked(tasks.get(position).isFinished());
			check.setOnClickListener(new CheckListener());
			if(tasks.get(position).isFinished()){
				check.setEnabled(false);
			}

			name.setText(tasks.get(position).getName());
			dead.setText(TimeUtil.getFormattedDate(tasks.get(position)
					.getDeadline()));
			goal.setText("Goal");

			return row;
		}

	}

	public class CheckListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			CheckBox check = (CheckBox) arg0;
			int pos = (Integer) check.getTag();
			if (check.isChecked()) {
				Task t = tasks.get(pos);
				t.setFinished(true);
				datasource.updateTask(t.getName(), t.getDeadline(), t.isFinished(), t.getValue());
//				int score = scoreMan.calculateScore(t);
//				Goal g = t.getGoal();
//				g.addScore(score);

				Toast.makeText(TaskList.this,
						"Task: " + t.getName() + "Checked: " + t.isFinished(),
						Toast.LENGTH_LONG).show();
			}
			
			if (adapter == null) {
				adapter = new MyAdapter(TaskList.this, R.layout.row_layout,
						tasks);
			}
			adapter.notifyDataSetChanged();
		}

	}

	public class BooleanComparator implements Comparator<Task> {

		@Override
		public int compare(Task lhs, Task rhs) {
			if (lhs.isFinished())
				return 1;
			if (rhs.isFinished())
				return -1;
			return 0;
		}

	}
	
	public class DateComparator implements Comparator<Task> {

		@Override
		public int compare(Task lhs, Task rhs) {
			if(lhs.getDeadline().getTimeInMillis() < rhs.getDeadline().getTimeInMillis())
				return -1;
			if(lhs.getDeadline().getTimeInMillis() > rhs.getDeadline().getTimeInMillis())
				return 1;
			return 0;
		}
		
	}

}
