package com.lnu.todomorrow;

import java.util.Calendar;

import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.dao.TaskDAO;
import com.lnu.todomorrow.utils.Goal;
import com.lnu.todomorrow.utils.Task;
import com.lnu.todomorrow.utils.TimeUtil;

import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class DialogFinish extends Activity {
	private static final String TAG = DialogFinish.class.getSimpleName();

	private long taskId;
	private int notifId;
	private TaskDAO taskDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");
		setContentView(R.layout.activity_dialog_finish);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			taskId = extras.getLong("task_id", -1);
			getIntent().removeExtra("task_id");

			notifId = extras.getInt("notif_id", -1);
			getIntent().removeExtra("notif_id");

			if (taskId == -1) {
				Log.e(TAG, "didn't receive correct task_id");
			}
			if (notifId == -1) {
				Log.e(TAG, "didn't receive correct notif_id");
			}
		} else {
			Log.e(TAG, "no extras in the intent");
		}

		taskDAO = new TaskDAO(getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dialog_postpone, menu);
		return true;
	}

	public void dialogOK(View view) {

		Log.d(TAG, "pressed OK - taskId: " + taskId);
		taskDAO.open();
		Task t = taskDAO.getTask(taskId);

		t.setFinishedAt(Calendar.getInstance());
		t.setFinished(true);
		if (!taskDAO.updateTask(t)) {
			Log.e(TAG, "couldnt update Task");
		}

		Log.d(TAG,
				"task - " + t.getName() + " set isfinshed to: " + t.isFinished()
						+ TimeUtil.getFormattedDate(t.getFinishedAt()));

		ScoreManager scoreMan = new ScoreManager();
		int score = scoreMan.calculateScore(t);

		int id = t.getGoal().getId();

		GoalDAO goalDAO = new GoalDAO(getApplicationContext());
		goalDAO.open();

		Goal g = goalDAO.getGoal(id);
		g.addScore(score);
		t.setGoal(g);
		goalDAO.updateGoal(g);

		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notifId);

		Log.v(TAG, "task: " + t);
		Toast.makeText(getApplicationContext(), "Task finished", Toast.LENGTH_SHORT).show();

		finish();
	}

	public void dialogCancel(View view) {
		Log.d(TAG, "pressed CANCEL - taskId: " + taskId);
		finish();
	}

}
