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
	private String[] columnsTask = { DbHelper.TASKS_C_ID,
			DbHelper.TASKS_C_NAME, DbHelper.TASKS_C_DEADLINE,
			DbHelper.TASKS_C_GOAL, DbHelper.TASKS_C_FINISH,
			DbHelper.TASKS_C_VALUE };

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
	public Task createTaskEntry(String name, Calendar deadline, Goal goal) {
		ContentValues values = new ContentValues();
		values.put(DbHelper.TASKS_C_NAME, name);
		values.put(DbHelper.TASKS_C_DEADLINE, deadline.getTimeInMillis());
		values.put(DbHelper.TASKS_C_GOAL, goal.getId());
		long insertId = database.insert(DbHelper.TABLE_TASKS, null, values);
		Cursor cursor = database.query(DbHelper.TABLE_TASKS, columnsTask,
				DbHelper.TASKS_C_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Task task = cursorToTask(cursor);
		cursor.close();
		return task;
	}

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

	public void deleteTaskEntry(Task t) {
		long id = t.getId();
		database.delete(DbHelper.TABLE_TASKS, DbHelper.TASKS_C_ID + "=" + id,
				null);
	}

	public Task getTask(long id) {
		String restrict = DbHelper.TASKS_C_ID + "=" + id;
		Cursor cursor = database.query(true, DbHelper.TABLE_TASKS, columnsTask,
				restrict, null, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			Task t = cursorToTask(cursor);
			return t;
		}
		// Make sure to close the cursor
		cursor.close();
		return null;
	}

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

	private Task cursorToTask(Cursor cursor) {
		Task task = new Task();
		task.setName(cursor.getString(1));
		task.setId(cursor.getInt(0));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(cursor.getLong(2));
		task.setDeadline(cal);

		// System.out.println(cursor.getString(1) + "hat Goal: " +
		// cursor.getInt(3));
		// task.setGoal(cursor.getInt(3));

		return task;
	}

	public boolean updateTask(long id, String name, Calendar deadline,
			boolean finished, int value) {
		ContentValues args = new ContentValues();
		args.put(DbHelper.TASKS_C_NAME, name);
		args.put(DbHelper.TASKS_C_DEADLINE, deadline.getTimeInMillis());
		args.put(DbHelper.TASKS_C_FINISH, finished ? 1 : 0);
		args.put(DbHelper.TASKS_C_VALUE, value);

		String restrict = DbHelper.TASKS_C_ID + "=" + id;
		return database.update(DbHelper.TABLE_TASKS, args, restrict, null) > 0;
	}

}
