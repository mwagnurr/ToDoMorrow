package com.lnu.todomorrow;

import java.util.List;

import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.dao.TaskDAO;
import com.lnu.todomorrow.utils.Goal;

import com.lnu.todomorrow.utils.TestDataCreator;
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
	private static GoalDAO goalDAO;
	private static TaskDAO taskDAO;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goal_list);

		goalDAO = new GoalDAO(this);
		goalDAO.open();

		taskDAO = new TaskDAO(this);
		taskDAO.open();

		listView = (ListView) findViewById(R.id.goal_list);

		adapter = new GoalAdapter(this, R.layout.row_layout, goalDAO.getAllGoals());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnGoalItemClick());

		Log.d(TAG, "onCreate()");
	}

	@Override
	public void onDestroy() {
		if (goalDAO != null)
			goalDAO.close();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.goal_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.show_taskList:
			Intent intent = new Intent(this, TaskList.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.add_testdata:
			TestDataCreator data = new TestDataCreator(this);
			data.createTestData();
			this.recreate();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void addGoal(View view) {
		Intent intent = new Intent(this, GoalForm.class);
		startActivityForResult(intent, 0);
	}

	private class OnGoalItemClick implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			Goal sel = adapter.getItem(position);

			Log.d(TAG, "clicked goal " + sel);

			Intent intent = new Intent(GoalList.this, GoalOverview.class);
			intent.putExtra("goal", sel);
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

			String n = currGoal.getName();
			String nShort = n;
			if (n.length() > 10) {
				nShort = n.replace(" ", "\n");
			}
			name.setText(nShort);
			score.setText(String.valueOf(currGoal.getScore()));
			if (currGoal.getDeadline() != null) {
				deadline.setText(TimeUtil.getFormattedDate(currGoal.getDeadline()));
			} else {
				TextView labelDead = (TextView) row.findViewById(R.id.goalrow_deadline_label);
				deadline.setVisibility(View.GONE);
				labelDead.setVisibility(View.GONE);
			}

			tasks.setText(String.valueOf(taskDAO.getAllTasksByGoal(currGoal).size()));

			return row;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
		if (resultCode == RESULT_OK) {

			Goal goal = (Goal) result.getSerializableExtra("goal");
			adapter.add(goal);
			adapter.notifyDataSetChanged();

			Log.d(TAG, "received result: " + goal);
		} else {
			Log.e(TAG, "receveid bad result");
		}
	}

}
