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
	private PendingIntent pendInt;
	private String name;
	private String goalName;
	private String value;
	private long id;

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("Received Broadcast");
		name = intent.getStringExtra("name");
		goalName = intent.getStringExtra("goal");
		value = String.valueOf(intent.getIntExtra("value", 10));

		in = new Intent(context, com.lnu.todomorrow.TaskList.class);
		pendInt = PendingIntent.getActivity(context, 0, in, 0);

		System.out.println(intent.getExtras());
		System.out.println("id in broadCastReceiver: " + intent.getLongExtra("id", 0));
		id = intent.getLongExtra("id", 0);

		intPostpone = new Intent(context, com.lnu.todomorrow.DialogPostpone.class);
		intPostpone.putExtra("id", id);
		System.out.println(id);

		showNotification(context);

	}

	private void showNotification(Context context) {
		NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(name)
				.setContentText(
						goalName + "    "
								+ context.getResources().getText(R.string.notificationText) + " "
								+ value)
				.setContentIntent(pendInt)
				.setContentIntent(pendInt)
				.addAction(
						R.drawable.ic_launcher,
						"Finished",
						PendingIntent.getActivity(context, 0, new Intent(context,
								com.lnu.todomorrow.GoalOverview.class), 0))
				.addAction(R.drawable.ic_launcher, "Postpone",

				PendingIntent.getActivity(context, 0, intPostpone, 0));
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, notBuilder.build());

	}

}
