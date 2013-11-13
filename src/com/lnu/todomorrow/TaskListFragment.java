package com.lnu.todomorrow;

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

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class TaskListFragment extends ListFragment {
	private static final String TAG = TaskListFragment.class.getSimpleName();

	private TaskDAO taskDAO;
	private TaskListAdapter adapter;
	private List<Task> tasks;
	private ScoreManager scoreMan;
	private GoalDAO goalDAO;

	private ArrayList<Goal> filterGoalList;

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

		setListAdapter(adapter);

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putSerializable("filterGoalList", filterGoalList);

		Log.d(TAG, "Saving Fragment state");

	}

	public void setFilterGoalList(ArrayList<Goal> filterGoals) {
		Log.d(TAG, "filtering task fragment by goals");

		this.filterGoalList = filterGoals;
		fetchFilteredTasks();

	}

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
	 * 
	 */
	private void fetchFilteredTasks() {
		taskDAO.open();
		tasks = taskDAO.getAllTasksFilteredByGoals(filterGoalList);
		if (adapter == null) {
			adapter = new TaskListAdapter(getActivity(), R.layout.row_layout, tasks);
			// setListAdapter(adapter);
		} else {
			adapter.clear();
			adapter.addAll(tasks);
			adapter.notifyDataSetChanged();
		}
	}

	private boolean filterListContainsGoal(Goal goal) {
		for (int i = 0; i < filterGoalList.size(); i++) {
			if (filterGoalList.get(i).getId() == goal.getId()) {
				return true;
			}
			// if(filterGoalList.contains(goal))
		}
		return false;
	}

	public void addTask(Task task) {
		Log.d(TAG, "adding task to TaskListFragment adapter - " + task.getName());

		adapter.add(task);
		adapter.notifyDataSetChanged();
	}

	public void sortListByDeadline() {

		Collections.sort(tasks, new DateComparator());

		if (adapter == null) {
			adapter = new TaskListAdapter(getActivity(), R.layout.row_layout, tasks);
		}
		adapter.notifyDataSetChanged();
	}

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

			CheckBox check = (CheckBox) row.findViewById(R.id.check);
			check.setTag(position);
			check.setChecked(tasks.get(position).isFinished());
			check.setOnClickListener(new CheckListener());
			if (tasks.get(position).isFinished()) {
				check.setEnabled(false);
				// Log.d(TAG, "disabling checkbox because task: " +
				// tasks.get(position));
			} else
				check.setEnabled(true);

			name.setText(tasks.get(position).getName());
			dead.setText(TimeUtil.getFormattedDate(tasks.get(position).getDeadline()));
			goal.setText(tasks.get(position).getGoal().getName());

			return row;
		}

	}

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

	private class CheckListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			CheckBox check = (CheckBox) arg0;
			int pos = (Integer) check.getTag();
			if (check.isChecked()) {
				Task t = tasks.get(pos);
				finishTask(t);

				Toast.makeText(getActivity(),
						"Task: " + t.getName() + "Checked: " + t.isFinished(), Toast.LENGTH_LONG)
						.show();
			}

			if (adapter == null) {
				adapter = new TaskListAdapter(getActivity(), R.layout.row_layout, tasks);
			}
			adapter.notifyDataSetChanged();
		}

		/**
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
			System.out.println(g.getScore());
			g.addScore(score);
			t.setGoal(g);
			goalDAO.updateGoal(g);
			System.out.println(g.getScore());

		}

	}

	public interface OnTaskChangedListener {
		// TODO let activities implement
		public void onTaskChanged();
	}
}
