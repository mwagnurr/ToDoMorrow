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

		int score = 0;
		int value = task.getValue();
		Calendar deadline = task.getDeadline();

		long deadTimeMs = deadline.getTimeInMillis();
		Calendar currTime = Calendar.getInstance();
		long currTimeMs = currTime.getTimeInMillis();

		long timeDiff = deadTimeMs - currTimeMs;

		// if more than one minute after deadline
		if (timeDiff < 60000) {
			score += value * 0.5;
			Log.d(TAG, "calculating after deadline, score 50%: " + score);
		} else {
			score += value;
			Log.d(TAG, "calculating before deadline, score: " + score);
		}

		return score;

	}
}
