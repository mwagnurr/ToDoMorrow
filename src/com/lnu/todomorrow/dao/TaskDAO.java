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
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskDAO {

	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	
	private String[] columnsTask = { DbHelper.TASKS_C_ID, DbHelper.TASKS_C_NAME,
			DbHelper.TASKS_C_DEADLINE, DbHelper.TASKS_C_GOAL, DbHelper.TASKS_C_VALUE,
			DbHelper.TASKS_C_FINISHED, DbHelper.TASKS_C_FINISHED_AT };

	private static final String TAG = TaskDAO.class.getSimpleName();

	public TaskDAO(Context context) {
		dbHelper = new DbHelper(context);
	}

	/**
	 * opens database connection
	 * 
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		Log.d(TAG, "opened Databaseconnection");
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * closes database connection
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * creates a new task and an entry in the database
	 * 
	 * @param name
	 * @param deadline
	 * @param goal
	 * @return
	 */
	public Task createTaskEntry(String name, Calendar deadline, int value, Goal goal) {
		ContentValues values = new ContentValues();
		values.put(DbHelper.TASKS_C_NAME, name);
		values.put(DbHelper.TASKS_C_DEADLINE, deadline.getTimeInMillis());
		values.put(DbHelper.TASKS_C_GOAL, goal.getId());
		values.put(DbHelper.TASKS_C_VALUE, value);
		long insertId = database.insert(DbHelper.TABLE_TASKS, null, values);
		Cursor cursor = database.query(DbHelper.TABLE_TASKS, columnsTask,
				DbHelper.TASKS_C_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Task task = cursorToTask(cursor);
		cursor.close();
		return task;
	}

	/**
	 * returns a list of all tasks in the database
	 * @return
	 */
	public List<Task> getAllTasks() {
		List<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(DbHelper.TABLE_TASKS, columnsTask, null,
				null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task t = cursorToTask(cursor);
			tasks.add(t);
			cursor.moveToNext();
		}
		cursor.close();
		return tasks;

	}
	
	/**
	 * returns the task with the given id
	 * @param id
	 * @return
	 */
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
	 * @param goal
	 * @return
	 */
	public List<Task> getAllTasksByGoal(Goal goal) {
		List<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(DbHelper.TABLE_TASKS, columnsTask,
				"where " + DbHelper.TASKS_C_GOAL + " = " + goal.getId(), null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task t = cursorToTask(cursor);
			tasks.add(t);
			cursor.moveToNext();
		}
		cursor.close();
		return tasks;

	}
	
	/**
	 * deletes given task from database
	 * @param t
	 */
	public void deleteTaskEntry(Task t) {
		long id = t.getId();
		database.delete(DbHelper.TABLE_TASKS, DbHelper.TASKS_C_ID + "=" + id,
				null);
	}

	/**
	 * updates given task
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
		} else if (fin == 0)
			task.setFinished(false);

		// Log.d(TAG, "tasks fin=" + fin);

		Log.d(TAG, "DEBUG: converted cursor to: " + task);
		// TODO setGoal
		// String restrict = DbHelper.GOALS_C_ID + "=" + cursor.getInt(3);
		// Cursor goalCursor = database.query(true, DbHelper.TABLE_GOALS, GoalDAO.columnsGoal,
		// restrict,
		// null, null, null, null, null);
		// if (goalCursor != null && goalCursor.getCount() > 0) {
		// goalCursor.moveToFirst();
		// Goal g = cursorToGoal(cursor);
		// return g;
		// }

		// task.setGoal(cursor.getInt(3));

		return task;
	}

}
