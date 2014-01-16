package com.lnu.todomorrow;

import java.util.ArrayList;
import java.util.List;

import com.lnu.todomorrow.GoalFilterDialogFragment.GoalFilterDialogListener;
import com.lnu.todomorrow.TaskListFragment.TaskDataChangedListener;
import com.lnu.todomorrow.utils.Goal;
import com.lnu.todomorrow.utils.Task;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class TaskList extends Activity implements GoalFilterDialogListener, TaskDataChangedListener {
	private static final String TAG = TaskList.class.getSimpleName();
	// private static TaskDAO dataTasks;
	private TaskListFragment list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_list);

		list = (TaskListFragment) getFragmentManager().findFragmentById(R.id.task_list_fragment);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "destroying activity");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void showGoalFilterDialog() {
		DialogFragment dialog = new GoalFilterDialogFragment();
		dialog.show(getFragmentManager(), "FilterGoalDialogFragment");
		Log.d(TAG, "showing filter dialog");
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
			showGoalFilterDialog();
			return true;
		case R.id.show_goalList:
			Intent intent = new Intent(this, GoalList.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 0);
			return true;
		case R.id.sort_by_deadline:
			list.sortListByDeadline();
			return true;
		case R.id.delete_finished_tasks:
			list.deleteFinishedTasks();
			return true;
		case R.id.refresh:
			recreate();
			return true;
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
		Log.d(TAG, "received result");
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {

				Task task = (Task) result.getSerializableExtra("task");

				if (task == null) {
					Log.e(TAG, "received as task null");
					return;
				}

				Log.d(TAG, "created task: " + task);
				list.addTask(task);
			}
		}
	}

	@Override
	public void onGoalFilterDialogSubmit(List<Goal> filteredGoals) {
		Log.d(TAG, "Receveid selected filter goals");
		list.setFilterGoalList((ArrayList<Goal>) filteredGoals);
	}

	@Override
	public void onTaskChanged() {
		// we don't care!
	}

}
