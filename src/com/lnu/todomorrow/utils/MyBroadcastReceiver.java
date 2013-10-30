package com.lnu.todomorrow.utils;

import com.lnu.todomorrow.R;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("Received Broadcast");
		showNotification(context);
		
	}

	private void showNotification(Context context) {
		NotificationCompat.Builder notBuilder =
	            new NotificationCompat.Builder(context)
	            .setSmallIcon(R.drawable.ic_launcher)
	            .setContentTitle("My notification")
	            .setContentText("Hello World!");
	    NotificationManager mNotificationManager =
	        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    mNotificationManager.notify(1, notBuilder.build());
		
	}

}
