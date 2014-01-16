package com.lnu.todomorrow;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lnu.todomorrow.utils.NotificationReceiver;
import com.lnu.todomorrow.utils.Task;

public class NotificationAlarmManager {
	private static final String TAG = TaskList.class.getSimpleName();

	public void removeAlarmForTask(Context context, Task task) {
		Log.d(TAG, "removed alarm for task: " + task.getId());
		PendingIntent pi = createAlarmPendingIntent(context, task);
		AlarmManager alarmMan = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMan.cancel(pi);
	}

	public void changeAlarmForTask(Context context, Task task, Calendar changedTime) {

		PendingIntent pi = createAlarmPendingIntent(context, task);
		AlarmManager alarmMan = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMan.cancel(pi);

		alarmMan.set(AlarmManager.RTC_WAKEUP, changedTime.getTimeInMillis(), pi);
		Log.d(TAG, "changed alarm to: " + changedTime.getTimeInMillis() + " vs currTime: "
				+ Calendar.getInstance().getTimeInMillis());
	}

	public void setAlarmForTask(Context context, Task task) {
		PendingIntent pi = createAlarmPendingIntent(context, task);

		// Log.d(TAG, "creating alarm for task: " + task + ", alarmID: " + alarmID);

		// setting alarm
		AlarmManager alarmMan = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMan.set(AlarmManager.RTC_WAKEUP, task.getDeadline().getTimeInMillis(), pi);

		Log.d(TAG, "set alarm to: " + task.getDeadline().getTimeInMillis() + " vs currTime: "
				+ Calendar.getInstance().getTimeInMillis());
	}

	/**
	 * taskId = alarmId
	 * 
	 * @param context
	 * @param task
	 * @return
	 */
	private PendingIntent createAlarmPendingIntent(Context context, Task task) {
		Intent intent = new Intent(context, NotificationReceiver.class);
		intent.putExtra("task", task);

		int alarmID = (int) task.getId();
		PendingIntent pi = PendingIntent.getBroadcast(context, alarmID, intent,
				PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
		return pi;
	}

}
