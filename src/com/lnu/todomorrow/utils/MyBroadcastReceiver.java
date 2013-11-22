package com.lnu.todomorrow.utils;

import com.lnu.todomorrow.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {

	private Intent in;
	private Intent intPostpone;
	private Intent intFinish;
	private PendingIntent pendInt;
	private String name;
	private String goalName;
	private int value;
	private long id;
	private static int notificationID = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		name = intent.getStringExtra("name");
		goalName = intent.getStringExtra("goal");
		value = intent.getIntExtra("value", 10);
		id = intent.getLongExtra("id", 0);

		in = new Intent(context, com.lnu.todomorrow.TaskList.class);
		pendInt = PendingIntent.getActivity(context, 0, in, 0);

		System.out.println("id in broadCastReceiver: " + intent.getLongExtra("id", 0));
		

		intPostpone = new Intent(context, com.lnu.todomorrow.DialogPostpone.class);
		intPostpone.putExtra("id", id);
		
		intFinish= new Intent(context, com.lnu.todomorrow.DialogFinish.class);
		intFinish.putExtra("id",id);

		showNotification(context);

	}

	private void showNotification(Context context) {
		NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(name)
				.setContentText(
						goalName + "    "
								+ context.getResources().getText(R.string.notificationText) + " "
								+ String.valueOf(value))
				.setContentIntent(pendInt)
				.addAction(
						R.drawable.ic_launcher, "Finished",
						PendingIntent.getActivity(context, notificationID, intFinish, 0))
				.addAction(R.drawable.ic_launcher, "Postpone",
						PendingIntent.getActivity(context, notificationID, intPostpone, 0));
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notificationID, notBuilder.build());
		notificationID++;

	}

}
