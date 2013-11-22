package com.lnu.todomorrow.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.lnu.todomorrow.utils.Goal;
import com.lnu.todomorrow.utils.Task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskDAO {

	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	private GoalDAO goalDAO;
	private Context context;

	private String[] columnsTask = { DbHelper.TASKS_C_ID, DbHelper.TASKS_C_NAME,
			DbHelper.TASKS_C_DEADLINE, DbHelper.TASKS_C_GOAL, DbHelper.TASKS_C_VALUE,
			DbHelper.TASKS_C_FINISHED, DbHelper.TASKS_C_FINISHED_AT };

	private static final String TAG = TaskDAO.class.getSimpleName();

	public TaskDAO(Context context) {
		dbHelper = new DbHelper(context);
		this.context = context;
	}

	/**
	 * opens database connection
	 * 
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		// Log.d(TAG, "opened Database connection");
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * closes database connection
	 */
	public void close() {
		// Log.d(TAG, "closed Database connection");
		dbHelper.close();
	}

	/**
	 * creates a new task and an entry in the database
	 * 
	 * @param name
	 * @param deadline
	 * @param goal
	 * @return
	 * @throws DAOException
	 */
	public Task createTaskEntry(String name, Calendar deadline, int value, Goal goal)
			throws DAOException {

		if (name == null || name.isEmpty()) {
			throw new DAOException("task name is empty");
		} else if (goal == null) {
			throw new DAOException("goal is null");
		}
		ContentValues values = new ContentValues();
		values.put(DbHelper.TASKS_C_NAME, name);
		values.put(DbHelper.TASKS_C_DEADLINE, deadline.getTimeInMillis());
		values.put(DbHelper.TASKS_C_GOAL, goal.getId());
		values.put(DbHelper.TASKS_C_VALUE, value);
		long insertId = 0;
		try {
			insertId = database.insertOrThrow(DbHelper.TABLE_TASKS, null, values);

		} catch (SQLiteConstraintException se) {
			Log.e(TAG, "ConstraintException: " + se.getMessage());
			throw new DAOException(se.getMessage(), se);
		}
		Cursor cursor = database.query(DbHelper.TABLE_TASKS, columnsTask, DbHelper.TASKS_C_ID
				+ " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Task task = cursorToTask(cursor);
		cursor.close();
		return task;
	}

	/**
	 * returns a list of all tasks in the database
	 * 
	 * @return
	 */
	public List<Task> getAllTasks() {
		List<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(DbHelper.TABLE_TASKS, columnsTask, null, null, null, null,
				null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task t = cursorToTask(cursor);
			tasks.add(t);
			cursor.moveToNext();
		}
		cursor.close();
		return tasks;

	}

	public List<Task> getAllTasksByGoal(Goal goal) {
		List<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(DbHelper.TABLE_TASKS, columnsTask, DbHelper.TASKS_C_GOAL
				+ " = " + goal.getId(), null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task t = cursorToTask(cursor);
			tasks.add(t);
			cursor.moveToNext();
		}
		cursor.close();
		return tasks;
	}

	public List<Task> getAllTasksFilteredByGoals(List<Goal> goals) {

		if (goals.size() <= 0) {
			Log.d(TAG, "goal list to filter is empty - get all tasks instead");
			return getAllTasks();
		}
		String selection = "";

		String orStr = " OR ";

		for (int i = 0; i < goals.size(); i++) {
			selection += DbHelper.TASKS_C_GOAL + " = " + goals.get(i).getId() + orStr;
		}

		selection = selection.substring(0, selection.length() - orStr.length());

		Log.d(TAG, "DEBUG: selection string: " + selection);

		List<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(DbHelper.TABLE_TASKS, columnsTask, selection, null, null,
				null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task t = cursorToTask(cursor);
			tasks.add(t);
			cursor.moveToNext();
		}
		cursor.close();
		return tasks;

	}

	public List<Task> getAllTasks(boolean allFinishedTasks) {

		List<Task> tasks = new ArrayList<Task>();

		String selection = "";

		if (allFinishedTasks) {
			selection = DbHelper.TASKS_C_FINISHED + "= 1";
		} else {
			selection = DbHelper.TASKS_C_FINISHED + "= 0";
		}
		Cursor cursor = database.query(DbHelper.TABLE_TASKS, columnsTask, selection, null, null,
				null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task t = cursorToTask(cursor);
			tasks.add(t);
			cursor.moveToNext();
			Log.d(TAG, "DEBUG: getAllTasks(" + allFinishedTasks + ") retrieved: " + t);
		}
		cursor.close();
		return tasks;

	}

	public Task getTask(long id) {
		String restrict = DbHelper.TASKS_C_ID + "=" + id;
		Cursor cursor = database.query(true, DbHelper.TABLE_TASKS, columnsTask, restrict, null,
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			Task t = cursorToTask(cursor);
			return t;
		}
		// Make sure to close the cursor
		cursor.close();
		return null;
	}

	/**
	 * returns a list of all tasks with given goal
	 * 
	 * @param goal
	 * @return
	 */

	/**
	 * deletes given task from database
	 * 
	 * @param t
	 */
	public void deleteTaskEntry(Task t) {
		long id = t.getId();
		database.delete(DbHelper.TABLE_TASKS, DbHelper.TASKS_C_ID + "=" + id, null);
	}

	/**
	 * updates given task
	 * 
	 * @param task
	 * @return
	 */
	public boolean updateTask(Task task) {
		ContentValues args = new ContentValues();
		args.put(DbHelper.TASKS_C_NAME, task.getName());
		args.put(DbHelper.TASKS_C_DEADLINE, task.getDeadline().getTimeInMillis());
		args.put(DbHelper.TASKS_C_VALUE, task.getValue());
		args.put(DbHelper.TASKS_C_FINISHED, task.isFinished() ? 1 : 0);
		if (task.getFinishedAt() != null)
			args.put(DbHelper.TASKS_C_FINISHED_AT, task.getFinishedAt().getTimeInMillis());
		if (task.getGoal() != null)
			args.put(DbHelper.TASKS_C_GOAL, task.getGoal().getId());

		return database.update(DbHelper.TABLE_TASKS, args,
				DbHelper.TASKS_C_ID + "=" + task.getId(), null) > 0;

	}

	private Task cursorToTask(Cursor cursor) {
		Task task = new Task();
		task.setName(cursor.getString(1));
		task.setId(cursor.getInt(0));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(cursor.getLong(2));
		task.setDeadline(cal);
		// task.setFinished(cursor.getInt(4)? 1 : 0);

		task.setValue(cursor.getInt(4));

		int fin = cursor.getInt(5);
		if (fin == 1) {
			task.setFinished(true);
			Calendar finCal = Calendar.getInstance();
			finCal.setTimeInMillis(cursor.getLong(6));
			task.setFinishedAt(finCal);
		} else if (fin == 0) {
			task.setFinished(false);
			task.setFinishedAt(null);
		}

		goalDAO = new GoalDAO(context);
		goalDAO.open();
		Goal g = goalDAO.getGoal(cursor.getInt(3));
		task.setGoal(g);
		// Log.d(TAG, "tasks fin=" + fin);

		// Log.d(TAG, "DEBUG: converted cursor to: " + task);
		goalDAO.close();
		return task;
	}

	public void updateTasksForGoal(Goal g) {
		List<Task> taskList = getAllTasksByGoal(g);

		if(goalDAO == null){
			goalDAO = new GoalDAO(context);
		}
		goalDAO.open();
		for (Task t : taskList) {
			t.setGoal(goalDAO.getGoal(g.getId()));
			System.out.println("Goal Name: " + g.getName() + "Score: " + g.getScore());
			updateTask(t);
		}
		goalDAO.close();

	}

}
