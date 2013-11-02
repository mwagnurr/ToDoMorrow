package com.lnu.todomorrow.utils;

import com.lnu.todomorrow.GoalList;
import com.lnu.todomorrow.R;
import com.lnu.todomorrow.TaskList;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {

	Intent in;
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("Received Broadcast");
		in = new Intent (context, TaskList.class);
		showNotification(context);
		
	}

	private void showNotification(Context context) {		
		Notification.Builder notBuilder =
	            new Notification.Builder(context)
	            .setSmallIcon(R.drawable.ic_launcher)
	            .setContentTitle("My notification")
	            .setContentText("Hello World!");
	    NotificationManager mNotificationManager =
	        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    mNotificationManager.notify(1, notBuilder.build());
	   
		
	}

}
