package com.lnu.todomorrow;

/**
 * @author Julia Bergmayr, Michael Wagner
 * @version 1.0
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.dao.TaskDAO;
import com.lnu.todomorrow.utils.Goal;
import com.lnu.todomorrow.utils.Task;
import com.lnu.todomorrow.utils.TimeUtil;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class TaskListFragment extends ListFragment {
	private static final String TAG = TaskListFragment.class.getSimpleName();

	private TaskDAO taskDAO;
	private TaskListAdapter adapter;
	private ScoreManager scoreMan;
	private GoalDAO goalDAO;

	private ArrayList<Goal> filterGoalList;

	private TaskDataChangedListener taskChangeListener;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		taskDAO = new TaskDAO(getActivity());
		goalDAO = new GoalDAO(getActivity());
		scoreMan = new ScoreManager();

		if (savedInstanceState != null) {
			filterGoalList = (ArrayList<Goal>) savedInstanceState.getSerializable("filterGoalList");
		} else {
			filterGoalList = new ArrayList<Goal>();
		}

		fetchFilteredTasks();

	}

	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		registerForContextMenu(getListView());

	}

	/**
	 * Creates Context Menu to Delete and Update Tasks
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		Log.d(TAG, "onCreateContextMenu");
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle("" + adapter.getItem(info.position).getName());
		menu.add(0, 0, 0, "Update");
		menu.add(0, 1, 1, "Delete");
	}

	/**
	 * Defines Activity for Context Menu
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		Log.d(TAG, " context item selected " + item.getItemId());
		if (item.getItemId() == 0) {
			Task t = adapter.getItem(info.position);
			Intent intent = new Intent(getActivity(), EditTask.class);
			intent.putExtra("task", t);
			this.startActivityForResult(intent, 0);

		} else if (item.getItemId() == 1) { // deleteTask
			Task t = adapter.getItem(info.position);
			taskDAO.deleteTaskEntry(t);
			adapter.remove(t);
		}
		return true;
	}

	/**
	 * Defines actions after activity result from Edit Task
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		switch (requestCode) {
		case 0:
			// Task t = (Task) result.getSerializableExtra("task");
			// adapter.add(t);
			// adapter.notifyDataSetChanged();
			fetchFilteredTasks();
			Log.d(TAG, "received result");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putSerializable("filterGoalList", filterGoalList);

		Log.d(TAG, "Saving Fragment state");

	}

	/**
	 * stores goal that should be displayed in list
	 * @param filterGoals
	 */
	public void setFilterGoalList(ArrayList<Goal> filterGoals) {
		Log.d(TAG, "filtering task fragment by goals");

		this.filterGoalList = filterGoals;
		fetchFilteredTasks();

	}

	/**
	 * preparing for filtering list by goals
	 * @param goal
	 */
	public void filterByGoal(Goal goal) {
		Log.d(TAG, "filtering task fragment by goal: " + goal.getName());

		if (filterListContainsGoal(goal)) {
			Log.d(TAG, "goal: " + goal.getName() + " already filtered");
			return;
		}

		filterGoalList.add(goal);
		fetchFilteredTasks();
	}

	/**
	 * loading tasks for specified goals
	 */
	private void fetchFilteredTasks() {
		taskDAO.open();
		List<Task> tasks = taskDAO.getAllTasksFilteredByGoals(filterGoalList);

		debugPrint(tasks);

		if (adapter == null) {
			adapter = new TaskListAdapter(getActivity(), R.layout.row_layout, tasks);
			setListAdapter(adapter);
		} else {
			adapter.clear();
			adapter.addAll(tasks);
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * @param tasks
	 */
	private void debugPrint(List<Task> tasks) {
		Log.v(TAG, "fetched tasks:");
		for (Task curr : tasks) {

			Log.v(TAG, curr.toString());
		}
	}

	/**
	 * checks if list contains specified goal
	 * @param goal
	 * @return boolean
	 */
	private boolean filterListContainsGoal(Goal goal) {
		for (int i = 0; i < filterGoalList.size(); i++) {
			if (filterGoalList.get(i).getId() == goal.getId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * adds task to adapter
	 * @param task
	 */
	public void addTask(Task task) {
		Log.d(TAG, "adding task to TaskListFragment adapter - " + task.getName());

		adapter.add(task);
		adapter.notifyDataSetChanged();
	}

	/**
	 * sorts tasklist entries by date of deadline
	 */
	public void sortListByDeadline() {

		Log.d(TAG, "sorting by deadline");

		if (adapter == null) {
			Log.d(TAG, "adapter was null");
			List<Task> tasks = taskDAO.getAllTasksFilteredByGoals(filterGoalList);
			adapter = new TaskListAdapter(getActivity(), R.layout.row_layout, tasks);
		}

		adapter.sort(new DateComparator());

		adapter.notifyDataSetChanged();
	}

	/**
	 * Adapter for TaskList entries
	 * @author Julia Bergmayr, Michael Wagner
	 *
	 */
	private class TaskListAdapter extends ArrayAdapter<Task> {
		private List<Task> tasks;

		public TaskListAdapter(Context context, int resource, List<Task> objects) {
			super(context, resource, objects);
			tasks = objects;
		}

		@Override
		// Called when updating the ListView
		public View getView(int position, View convertView, ViewGroup parent) {
			View row;
			if (convertView == null) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				row = inflater.inflate(R.layout.row_layout, parent, false);
			} else {
				row = convertView;
			}

			Collections.sort(tasks, new BooleanComparator());
			TextView name = (TextView) row.findViewById(R.id.show_task);
			TextView dead = (TextView) row.findViewById(R.id.show_deadline);
			TextView goal = (TextView) row.findViewById(R.id.show_goal);
			TextView value = (TextView) row.findViewById(R.id.show_value);

			CheckBox check = (CheckBox) row.findViewById(R.id.check);
			check.setTag(position);
			check.setChecked(tasks.get(position).isFinished());
			check.setOnClickListener(new CheckListener());
			check.setFocusable(false);
			if (tasks.get(position).isFinished()) {
				check.setEnabled(false);
				// Log.d(TAG, "disabling checkbox because task: " +
				// tasks.get(position));
			} else
				check.setEnabled(true);

			name.setText(tasks.get(position).getName());
			dead.setText(TimeUtil.getFormattedDate(tasks.get(position).getDeadline()));
			goal.setText(tasks.get(position).getGoal().getName());
			value.setText(String.valueOf(tasks.get(position).getValue()));

			return row;
		}

	}

	/**
	 * Comparator for sorting finished tasks on end of list
	 * @author Julia Bergmayr, Michael Wanger
	 *
	 */
	private class BooleanComparator implements Comparator<Task> {

		@Override
		public int compare(Task lhs, Task rhs) {
			if (lhs.isFinished())
				return 1;
			if (rhs.isFinished())
				return -1;
			return 0;
		}

	}

	/**
	 * Comparator to compare dates
	 * @author Julia Bergmayr, Michael Wagner
	 *
	 */
	private class DateComparator implements Comparator<Task> {

		@Override
		public int compare(Task lhs, Task rhs) {
			if (lhs.getDeadline().getTimeInMillis() < rhs.getDeadline().getTimeInMillis())
				return -1;
			if (lhs.getDeadline().getTimeInMillis() > rhs.getDeadline().getTimeInMillis())
				return 1;
			return 0;
		}

	}

	/**
	 * Listener for checking Checkboxes
	 * @author Julia Bergmayr, Michael Wagner
	 *
	 */
	private class CheckListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			CheckBox check = (CheckBox) arg0;
			Log.d(TAG, "check box clicked");
			int pos = (Integer) check.getTag();
			if (check.isChecked()) {
				Task t = adapter.getItem(pos);
				finishTask(t);

				Toast.makeText(getActivity(),
						"Task: " + t.getName() + "Checked: " + t.isFinished(), Toast.LENGTH_LONG)
						.show();
			}

			adapter.notifyDataSetChanged();

			// callback for implementing activities
			taskChangeListener.onTaskChanged();
		}

		/**
		 * called when task is finished (checkbox is checked)
		 * @param t
		 */
		private void finishTask(Task t) {
			t.setFinished(true);
			t.setFinishedAt(Calendar.getInstance());
			if (!taskDAO.updateTask(t)) {
				Log.e(TAG, "couldnt update Task");
			}
			Log.d(TAG,
					"task - " + t.getName() + " set isfinshed to: " + t.isFinished()
							+ TimeUtil.getFormattedDate(t.getFinishedAt()));

			int score = scoreMan.calculateScore(t);

			int id = t.getGoal().getId();
			if (goalDAO == null) {
				goalDAO = new GoalDAO(getActivity());
			}
			goalDAO.open();
			Goal g = goalDAO.getGoal(id);
			g.addScore(score);
			t.setGoal(g);
			goalDAO.updateGoal(g);

			NotificationAlarmManager alarmMan = new NotificationAlarmManager();

			alarmMan.removeAlarmForTask(getActivity(), t);

		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {

			taskChangeListener = (TaskDataChangedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement TaskDataChangedListener");
		}
	}

	public interface TaskDataChangedListener {
		public void onTaskChanged();
	}

	/**
	 * deletes all finished tasks from list and database
	 */
	public void deleteFinishedTasks() {
		taskDAO.open();
		List<Task> tasks = taskDAO.getAllTasks();
		for (Task t : tasks) {
			if (t.isFinished()) {
				taskDAO.deleteTaskEntry(t);
				adapter.remove(t);
			}
		}

		// TODO change deletion to not actually delete
		taskDAO.close();
		adapter.notifyDataSetChanged();

	}

}
