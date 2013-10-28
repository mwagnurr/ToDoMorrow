package com.lnu.todomorrow;

import java.util.Calendar;
import java.util.List;

import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.utils.Goal;
import com.lnu.todomorrow.utils.TimeUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GoalList extends Activity {
	private static final String TAG = GoalList.class.getSimpleName();
	private ListView listView;
	private GoalAdapter adapter;
	private static GoalDAO datasource;
	private List<Goal> goals;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goal_list);

		datasource = new GoalDAO(this);
		datasource.open();

		listView = (ListView) findViewById(R.id.goal_list);

		adapter = new GoalAdapter(this, R.layout.row_layout, datasource.getAllGoals());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnGoalItemClick());
		
		Log.d(TAG, "onCreate()");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.goal_list, menu);	
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_goal:
			Intent intent = new Intent(this, GoalForm.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private class OnGoalItemClick implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			Goal sel = adapter.getItem(position);
			
			Log.d(TAG, "clicked goal " + sel);
			
			Intent intent = new Intent(GoalList.this, TodoList.class);
			GoalList.this.startActivity(intent);
		}
	}

	private class GoalAdapter extends ArrayAdapter<Goal> {

		public GoalAdapter(Context context, int resource, List<Goal> objects) {
			super(context, resource, objects);
		}

		@Override
		// Called when updating the ListView
		public View getView(int position, View convertView, ViewGroup parent) {
			View row;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row_goal_list, parent, false);
			} else {
				row = convertView;
			}

			Goal currGoal = getItem(position);

			TextView name = (TextView) row.findViewById(R.id.goalrow_name);
			TextView score = (TextView) row.findViewById(R.id.goalrow_score);
			TextView tasks = (TextView) row.findViewById(R.id.goalrow_tasks);
			TextView deadline = (TextView) row.findViewById(R.id.goalrow_deadline);

			name.setText(currGoal.getName());
			score.setText(String.valueOf(currGoal.getScore()));
			deadline.setText(TimeUtil.getFormattedDate(currGoal.getDeadline()));
			tasks.setText("TODO");

			return row;
		}

	}

	/** Called when the activity receives a results. */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
		if (resultCode == RESULT_OK) {
			Log.d(TAG, "received result");
			
			String goalName =  result.getStringExtra("goal_name");
			
			Calendar cal = (Calendar) result.getSerializableExtra("goal_deadline");
			
			datasource.open();
			Goal goal = datasource.createGoalEntry(goalName, cal);
			
			Log.d(TAG, "created goal: " + goal);
			
			adapter.add(goal);
			adapter.notifyDataSetChanged();
		}
	}
}
