package com.lnu.todomorrow.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class GoalDAO {
	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	private TaskDAO taskDb;
	private Context context;
	protected static String[] columnsGoal = { DbHelper.GOALS_C_ID, DbHelper.GOALS_C_NAME,
			DbHelper.GOALS_C_SCORE, DbHelper.GOALS_C_DEADLINE };

	private static final String TAG = GoalDAO.class.getSimpleName();

	public GoalDAO(Context context) {
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

	public void close() {
		// Log.d(TAG, "closed Database connection");
		dbHelper.close();
	}

	public Goal createGoalEntry(String name, Calendar deadline) throws DAOException {

		if (name == null || name.isEmpty()) {
			throw new DAOException("goal name is empty");
		}
		ContentValues values = new ContentValues();
		values.put(DbHelper.GOALS_C_NAME, name);
		values.put(DbHelper.GOALS_C_SCORE, 0);

		if (deadline != null) {
			Date dead = deadline.getTime();

			// inserting date as UNIX time long
			values.put(DbHelper.GOALS_C_DEADLINE, dead.getTime());
		}
		long insertId = 0;
		try {

			insertId = database.insertOrThrow(DbHelper.TABLE_GOALS, null, values);

		} catch (SQLiteConstraintException se) {
			Log.e(TAG, "ConstraintException: " + se.getMessage());
			throw new DAOException(se.getMessage(), se);
		}

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

	public Goal getGoal(long id) {
		String restrict = DbHelper.GOALS_C_ID + "=" + id;
		Cursor cursor = database.query(true, DbHelper.TABLE_GOALS, columnsGoal, restrict, null,
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			Goal g = cursorToGoal(cursor);
			return g;
		}
		// Make sure to close the cursor
		cursor.close();
		return null;
	}

	public Goal getGoal(String name) {

		String restrict = DbHelper.GOALS_C_NAME + "=" + "'" + name + "'";
		Cursor cursor = database.query(true, DbHelper.TABLE_GOALS, columnsGoal, restrict, null,
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			Goal g = cursorToGoal(cursor);
			return g;
		}
		cursor.close();
		return null;
	}

	public boolean updateGoal(Goal goal) {

		ContentValues args = new ContentValues();
		args.put(DbHelper.GOALS_C_NAME, goal.getName());
		args.put(DbHelper.GOALS_C_SCORE, goal.getScore());
		return database.update(DbHelper.TABLE_GOALS, args,
				DbHelper.GOALS_C_ID + "=" + goal.getId(), null) > 0;
	}

	public void deleteGoal(Goal goal) {

		taskDb = new TaskDAO(context);
		taskDb.open();
		// TODO delete goal and delete tasks of goals
		List<Task> tasks = taskDb.getAllTasksByGoal(goal);
		for (Task t : tasks) {
			taskDb.deleteTaskEntry(t);
		}
		taskDb.close();

		database.delete(DbHelper.TABLE_GOALS, DbHelper.GOALS_C_ID + " = " + goal.getId(), null);

	}

	private Goal cursorToGoal(Cursor cursor) {
		if (cursor.getCount() == 0) {
			Log.e(TAG, "Cursor is empty!");
			return null;
		}
		int id = cursor.getInt(0);
		String name = cursor.getString(1);
		int score = cursor.getInt(2);
		Goal goal = new Goal(id, name, score);

		if (cursor.getLong(3) != 0) {
			// Log.e(TAG, "goal " + name + " has deadline " +
			// cursor.getLong(3));
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(cursor.getLong(3));
			goal.setDeadline(cal);
		} else {
			goal.setDeadline(null);
		}

		return goal;
	}
}
