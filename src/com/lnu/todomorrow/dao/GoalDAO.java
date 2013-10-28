package com.lnu.todomorrow.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.lnu.todomorrow.utils.Goal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GoalDAO {
	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	private String[] columnsGoal = { DbHelper.GOALS_C_ID, DbHelper.GOALS_C_NAME,
			DbHelper.GOALS_C_SCORE, DbHelper.GOALS_C_DEADLINE };

	private static final String TAG = GoalDAO.class.getSimpleName();

	public GoalDAO(Context context) {
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

	public Goal createGoalEntry(String name, String score) {
		ContentValues values = new ContentValues();
		values.put(DbHelper.GOALS_C_NAME, name);
		values.put(DbHelper.GOALS_C_SCORE, score);
		long insertId = database.insert(DbHelper.TABLE_GOALS, null, values);
		Cursor cursor = database.query(DbHelper.TABLE_GOALS, columnsGoal, DbHelper.GOALS_C_ID
				+ " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Goal goal = cursorToGoal(cursor);
		cursor.close();
		return goal;
	}

	public Goal createGoalEntry(String name, Calendar deadline) {
		ContentValues values = new ContentValues();
		values.put(DbHelper.GOALS_C_NAME, name);
		values.put(DbHelper.GOALS_C_SCORE, 0);

		if (deadline != null) {
			Date dead = deadline.getTime();
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			// String strDead = sdf.format(dead);
			// values.put(DbHelper.GOALS_C_DEADLINE, strDead);

			values.put(DbHelper.GOALS_C_DEADLINE, dead.getTime());

			// Log.d(TAG, "inserting goal with deadline: " + strDead);
		}

		long insertId = database.insert(DbHelper.TABLE_GOALS, null, values);
		Cursor cursor = database.query(DbHelper.TABLE_GOALS, columnsGoal, DbHelper.GOALS_C_ID
				+ " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Goal goal = cursorToGoal(cursor);
		cursor.close();
		return goal;
	}

	public List<Goal> getAllGoals() {
		List<Goal> goals = new ArrayList<Goal>();
		Cursor cursor = database.query(DbHelper.TABLE_GOALS, columnsGoal, null, null, null, null,
				null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Goal g = cursorToGoal(cursor);
			goals.add(g);
			cursor.moveToNext();
		}
		cursor.close();
		return goals;

	}

	public boolean updateGoal(Goal goal) {

		ContentValues args = new ContentValues();
		args.put(DbHelper.GOALS_C_NAME, goal.getName());
		args.put(DbHelper.GOALS_C_SCORE, goal.getScore());
		return database.update(DbHelper.TABLE_GOALS, args,
				DbHelper.GOALS_C_ID + "=" + goal.getId(), null) > 0;
	}

	public void deleteGoal(Goal goal) {

		// TODO delete tasks of goals

		database.delete(DbHelper.TABLE_GOALS, DbHelper.GOALS_C_ID + " = " + goal.getId(), null);
	}

	private Goal cursorToGoal(Cursor cursor) {

		int id = cursor.getInt(0);
		String name = cursor.getString(1);
		int score = cursor.getInt(2);
		Goal goal = new Goal(id, name, score);

		if (cursor.getLong(3) != 0) {
			Log.e(TAG, "goal " + name + " has deadline " + cursor.getLong(3));
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(cursor.getLong(3));
			goal.setDeadline(cal);
		}
		
		return goal;
	}
}
