package com.lnu.todomorrow.utils;

import java.util.Calendar;

import android.content.Context;
import android.util.Log;

import com.lnu.todomorrow.dao.DAOException;
import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.dao.TaskDAO;

/**
 * debug class for easy test data generation
 *
 */
public class TestDataCreator {
	private static final String TAG = TestDataCreator.class.getSimpleName();

	private GoalDAO goalDAO;
	private TaskDAO taskDAO;
	private static int count = 0;

	public TestDataCreator(Context context) {
		goalDAO = new GoalDAO(context);
		taskDAO = new TaskDAO(context);
	}
	/**
	 * method for convenience
	 */
	public void createTestData() {
		++count;

		goalDAO.open();
		taskDAO.open();
		try {
			String goalName = "TestDeadline" + count;
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 15);
			calendar.set(Calendar.MINUTE, 40);
			calendar.set(Calendar.SECOND, 0);

			calendar.set(Calendar.DAY_OF_MONTH, 15);
			calendar.set(Calendar.MONTH, 11);
			calendar.set(Calendar.YEAR, 2013);

			Goal goal1;

			goal1 = goalDAO.createGoalEntry(goalName, calendar);

			Log.d(TAG, "TESTDATA created: " + goal1);
			goalName = "TestSecDeadline" + count;

			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, 3);
			calendar.set(Calendar.YEAR, 2014);

			Goal goal2 = goalDAO.createGoalEntry(goalName, calendar);

			Log.d(TAG, "TESTDATA created: " + goal2);

			goalName = "TestNoDeadline" + count;

			calendar = null;
			Goal goal3 = goalDAO.createGoalEntry(goalName, calendar);

			Log.d(TAG, "TESTDATA created: " + goal3);

			// ! NEW TASK

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

			// ! NEW TASK

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

			// ! NEW TASK

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

			// ! NEW TASK

			taskName = "Task4";

			calendarTask.set(Calendar.HOUR_OF_DAY, 15);
			calendarTask.set(Calendar.MINUTE, 40);
			calendarTask.set(Calendar.SECOND, 0);

			calendarTask.set(Calendar.DAY_OF_MONTH, 7);
			calendarTask.set(Calendar.MONTH, 11);
			calendarTask.set(Calendar.YEAR, 2013);

			value = 50;

			Task task4 = taskDAO.createTaskEntry(taskName, calendarTask, value, goal1);
			
			task4.setFinished(true);
			task4.setFinishedAt(Calendar.getInstance());
			
			taskDAO.updateTask(task4);

			Log.d(TAG, "TESTDATA created: " + task4);

			// ! NEW TASK

			taskName = "Taskcomp1";

			calendarTask.set(Calendar.HOUR_OF_DAY, 15);
			calendarTask.set(Calendar.MINUTE, 40);
			calendarTask.set(Calendar.SECOND, 0);

			calendarTask.set(Calendar.DAY_OF_MONTH, 7);
			calendarTask.set(Calendar.MONTH, 11);
			calendarTask.set(Calendar.YEAR, 2013);

			value = 50;

			Task taskc1 = taskDAO.createTaskEntry(taskName, calendarTask, value, goal1);

			taskc1.setFinished(true);
			
			Calendar finished = Calendar.getInstance();
			
			finished.set(Calendar.DAY_OF_MONTH, 10);
			finished.set(Calendar.MONTH, 8);
			finished.set(Calendar.YEAR, 2013); 
			Log.e(TAG, "D finished: " + TimeUtil.getFormattedDate(calendarTask));
			
			Log.v(TAG, "is month: " + finished.get(Calendar.MONTH) );
			
			taskc1.setFinishedAt(finished);
			
			taskDAO.updateTask(taskc1);

			Log.d(TAG, "TESTDATA created: " + taskc1);

			// ! NEW TASK

			taskName = "Taskcomp2";

			calendarTask.set(Calendar.HOUR_OF_DAY, 15);
			calendarTask.set(Calendar.MINUTE, 40);
			calendarTask.set(Calendar.SECOND, 0);

			calendarTask.set(Calendar.DAY_OF_MONTH, 7);
			calendarTask.set(Calendar.MONTH, 11);
			calendarTask.set(Calendar.YEAR, 2013);

			value = 50;

			Task taskc2 = taskDAO.createTaskEntry(taskName, calendarTask, value, goal1);

			taskc2.setFinished(true);
			
			finished = Calendar.getInstance();
			
			finished.set(Calendar.DAY_OF_MONTH, 10);
			finished.set(Calendar.MONTH, 9);
			finished.set(Calendar.YEAR, 2013);
			
			taskc2.setFinishedAt(finished);
			
			taskDAO.updateTask(taskc2);

			Log.d(TAG, "TESTDATA created: " + taskc2);

			// ! NEW TASK

			taskName = "Taska";

			calendarTask.set(Calendar.HOUR_OF_DAY, 15);
			calendarTask.set(Calendar.MINUTE, 40);
			calendarTask.set(Calendar.SECOND, 0);

			calendarTask.set(Calendar.DAY_OF_MONTH, 7);
			calendarTask.set(Calendar.MONTH, 11);
			calendarTask.set(Calendar.YEAR, 2013);

			value = 50;

			Task taska = taskDAO.createTaskEntry(taskName, calendarTask, value, goal3);

			Log.d(TAG, "TESTDATA created: " + taska);

			// ! NEW TASK

			taskName = "TaskA1";

			calendarTask.set(Calendar.HOUR_OF_DAY, 15);
			calendarTask.set(Calendar.MINUTE, 40);
			calendarTask.set(Calendar.SECOND, 0);

			calendarTask.set(Calendar.DAY_OF_MONTH, 9);
			calendarTask.set(Calendar.MONTH, 11);
			calendarTask.set(Calendar.YEAR, 2013);

			value = 100;

			Task taskA1 = taskDAO.createTaskEntry(taskName, calendarTask, value, goal2);

			Log.d(TAG, "TESTDATA created: " + taskA1);

			// ! NEW TASK

			taskName = "Taskb";

			calendarTask.set(Calendar.HOUR_OF_DAY, 15);
			calendarTask.set(Calendar.MINUTE, 40);
			calendarTask.set(Calendar.SECOND, 0);

			calendarTask.set(Calendar.DAY_OF_MONTH, 9);
			calendarTask.set(Calendar.MONTH, 11);
			calendarTask.set(Calendar.YEAR, 2013);

			value = 50;

			Task taskb = taskDAO.createTaskEntry(taskName, calendarTask, value, goal3);

			Log.d(TAG, "TESTDATA created: " + taskb);

			// ! NEW TASK

			taskName = "Taskc";

			calendarTask.set(Calendar.HOUR_OF_DAY, 15);
			calendarTask.set(Calendar.MINUTE, 40);
			calendarTask.set(Calendar.SECOND, 0);

			calendarTask.set(Calendar.DAY_OF_MONTH, 9);
			calendarTask.set(Calendar.MONTH, 11);
			calendarTask.set(Calendar.YEAR, 2013);

			value = 50;

			Task taskc = taskDAO.createTaskEntry(taskName, calendarTask, value, goal3);

			Log.d(TAG, "TESTDATA created: " + taskc);

		} catch (DAOException e) {
			e.printStackTrace();
		}

		goalDAO.close();
		taskDAO.close();
	}
}
