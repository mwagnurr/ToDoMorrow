package com.lnu.todomorrow;

import java.util.Calendar;

import android.util.Log;

import com.lnu.todomorrow.utils.Task;

public class ScoreManager {

	private static final String TAG = ScoreManager.class.getSimpleName();

	public ScoreManager() {

	}

	public int calculateScore(Task task) {

		if (!task.isFinished()) {
			Log.e(TAG, "Task is not finished, can't calculate score yet");
			return 0;
		}

		// TODO fancy math

		int score = 0;
		int value = task.getValue();
		Calendar deadline = task.getDeadline();

		long deadTimeMs = deadline.getTimeInMillis();
		Calendar currTime = Calendar.getInstance();
		long currTimeMs = currTime.getTimeInMillis();

		long timeDiff = deadTimeMs - currTimeMs;

		if (timeDiff < 0) {
			Log.d(TAG, "calculating after deadline");
			score += 1;
		} else {
			Log.d(TAG, "calculating before deadline");
			score += value;
		}

		return score;

	}
}
