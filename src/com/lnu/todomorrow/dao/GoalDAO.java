package com.lnu.todomorrow.dao;

import java.util.ArrayList;
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
		Goal goal = new Goal();
		goal.setId(cursor.getInt(0));
		goal.setName(cursor.getString(1));
		goal.setScore((cursor.getInt(2)));
		goal.setDeadline((cursor.getString(3)));
		return goal;
	}
}
