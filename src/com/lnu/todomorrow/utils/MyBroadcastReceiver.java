package com.lnu.todomorrow.utils;

import com.lnu.todomorrow.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {

	Intent in;
	PendingIntent pendInt;
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("Received Broadcast");
		in = new Intent(context, com.lnu.todomorrow.TaskList.class);
		pendInt = PendingIntent.getActivity(context, 0, intent, 0);
		System.out.println(intent.getExtras());
		
		showNotification(context);
		
	}

	private void showNotification(Context context) {		
		NotificationCompat.Builder notBuilder =
	            new NotificationCompat.Builder(context)
	            .setSmallIcon(R.drawable.ic_launcher)
	            .setContentTitle("My notification")
	            .setContentText("Hello World!")
	            .setContentIntent(pendInt);
	    NotificationManager mNotificationManager =
	        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    mNotificationManager.notify(1, notBuilder.build());
	   
		
	}

}
