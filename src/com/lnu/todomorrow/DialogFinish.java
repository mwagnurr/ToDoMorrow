package com.lnu.todomorrow;

import java.util.Calendar;

import com.lnu.todomorrow.dao.TaskDAO;
import com.lnu.todomorrow.utils.Task;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class DialogFinish extends Activity {

	private long id;
	private TaskDAO taskDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent in = getIntent();
		id = in.getLongExtra("id", 0);
		System.out.println("id in dialogFinish: " + id);

		setContentView(R.layout.activity_dialog_finish);

		taskDB = new TaskDAO(getApplicationContext());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dialog_postpone, menu);
		return true;
	}

	public void dialogOK(View view) {

		taskDB.open();
		Task t = taskDB.getTask(id);
		t.setFinishedAt(Calendar.getInstance());
		t.setFinished(true);
		taskDB.updateTask(t);
		Toast.makeText(getApplicationContext(), "Task finished", Toast.LENGTH_SHORT).show();
		finish();
	}

	public void dialogCancel(View view) {
		finish();
	}

}
