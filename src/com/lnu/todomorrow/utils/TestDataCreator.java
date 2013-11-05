package com.lnu.todomorrow.utils;

import java.util.Calendar;

import android.content.Context;
import android.util.Log;

import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.dao.TaskDAO;

public class TestDataCreator {
	private static final String TAG = TestDataCreator.class.getSimpleName();

	private GoalDAO goalDAO;
	private TaskDAO taskDAO;

	public TestDataCreator(Context context) {
		goalDAO = new GoalDAO(context);
		taskDAO = new TaskDAO(context);
	}

	/**
	 * method for convenience
	 */
	public void createTestData() {

		goalDAO.open();
		taskDAO.open();

		String goalName = "TestDeadline1";
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 15);
		calendar.set(Calendar.MINUTE, 40);
		calendar.set(Calendar.SECOND, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 15);
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.YEAR, 2013);

		Goal goal1 = goalDAO.createGoalEntry(goalName, calendar);

		Log.d(TAG, "TESTDATA created: " + goal1);
		goalName = "TestDeadline2";

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 3);
		calendar.set(Calendar.YEAR, 2014);

		Goal goal2 = goalDAO.createGoalEntry(goalName, calendar);

		Log.d(TAG, "TESTDATA created: " + goal2);

		goalName = "TestNoDeadline1";

		calendar = null;
		Goal goal3 = goalDAO.createGoalEntry(goalName, calendar);

		Log.d(TAG, "TESTDATA created: " + goal3);

		String taskName = "Task1";

		Calendar calendarTask = Calendar.getInstance();
		calendarTask.set(Calendar.HOUR_OF_DAY, 15);
		calendarTask.set(Calendar.MINUTE, 40);
		calendarTask.set(Calendar.SECOND, 0);

		calendarTask.set(Calendar.DAY_OF_MONTH, 15);
		calendarTask.set(Calendar.MONTH, 11);
		calendarTask.set(Calendar.YEAR, 2013);

		int value = 50;

		Task task1 = taskDAO.createTaskEntry(taskName, calendarTask, value, goal1);

		Log.d(TAG, "TESTDATA created: " + task1);

		taskName = "Task2";

		calendarTask.set(Calendar.HOUR_OF_DAY, 15);
		calendarTask.set(Calendar.MINUTE, 40);
		calendarTask.set(Calendar.SECOND, 0);

		calendarTask.set(Calendar.DAY_OF_MONTH, 15);
		calendarTask.set(Calendar.MONTH, 11);
		calendarTask.set(Calendar.YEAR, 2013);

		value = 50;

		Task task2 = taskDAO.createTaskEntry(taskName, calendarTask, value, goal1);

		Log.d(TAG, "TESTDATA created: " + task2);

		taskName = "Task3";

		calendarTask.set(Calendar.HOUR_OF_DAY, 15);
		calendarTask.set(Calendar.MINUTE, 40);
		calendarTask.set(Calendar.SECOND, 0);

		calendarTask.set(Calendar.DAY_OF_MONTH, 19);
		calendarTask.set(Calendar.MONTH, 11);
		calendarTask.set(Calendar.YEAR, 2013);

		value = 50;

		Task task3 = taskDAO.createTaskEntry(taskName, calendarTask, value, goal1);

		Log.d(TAG, "TESTDATA created: " + task3);

		taskName = "Task4";

		calendarTask.set(Calendar.HOUR_OF_DAY, 15);
		calendarTask.set(Calendar.MINUTE, 40);
		calendarTask.set(Calendar.SECOND, 0);

		calendarTask.set(Calendar.DAY_OF_MONTH, 7);
		calendarTask.set(Calendar.MONTH, 11);
		calendarTask.set(Calendar.YEAR, 2013);

		value = 50;

		Task task4 = taskDAO.createTaskEntry(taskName, calendarTask, value, goal1);

		Log.d(TAG, "TESTDATA created: " + task4);

		goalDAO.close();
		taskDAO.close();
	}
}
