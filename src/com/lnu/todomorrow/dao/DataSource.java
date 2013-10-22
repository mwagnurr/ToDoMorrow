package com.lnu.todomorrow.dao;

import java.util.ArrayList;
import java.util.List;

import com.lnu.todomorrow.utils.Task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataSource {

	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	private String[] columnsTask = { DbHelper.TASKS_C_ID,
			DbHelper.TASKS_C_NAME, DbHelper.TASKS_C_DEADLINE };
	

	private static final String TAG = DataSource.class.getSimpleName();

	public DataSource(Context context) {
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

	public void close() {
		dbHelper.close();
	}

	public Task createTaskEntry(String name, String deadline) {
		ContentValues values = new ContentValues();
		values.put(DbHelper.TASKS_C_NAME, name);
		values.put(DbHelper.TASKS_C_DEADLINE, deadline);
		long insertId = database.insert(DbHelper.TABLE_TASKS, null, values);
		Cursor cursor = database.query(DbHelper.TABLE_TASKS, columnsTask,
				DbHelper.TASKS_C_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Task task = cursorToTask(cursor);
		cursor.close();
		return task;
	}

	public List<Task> getAllTasks(){
		List<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(DbHelper.TABLE_TASKS, columnsTask, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
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
		task.setDeadline(cursor.getString(2));
		return task;
	}

}
