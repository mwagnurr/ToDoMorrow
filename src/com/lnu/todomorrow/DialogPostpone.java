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

public class DialogPostpone extends Activity {

	private TimePicker tp;
	private DatePicker dp;
	private long id;
	private TaskDAO taskDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//tt
		Intent in = getIntent();
		id = in.getLongExtra("id", 0);
		System.out.println("id in dialogPostone: " + id);

		setContentView(R.layout.activity_dialog_postpone);
		tp = (TimePicker) findViewById(R.id.pick_deadline_time);
		tp.setIs24HourView(true);
		dp = (DatePicker) findViewById(R.id.pick_deadline);

		taskDB = new TaskDAO(getApplicationContext());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dialog_postpone, menu);
		return true;
	}

	public void dialogOK(View view) {

		int hour = tp.getCurrentHour();
		int minute = tp.getCurrentMinute();

		int day = dp.getDayOfMonth();
		int month = dp.getMonth();
		int year = dp.getYear();

		Calendar deadline = Calendar.getInstance();
		deadline.set(Calendar.HOUR_OF_DAY, hour);
		deadline.set(Calendar.MINUTE, minute);
		deadline.set(Calendar.SECOND, 0);

		deadline.set(Calendar.DAY_OF_MONTH, day);
		deadline.set(Calendar.MONTH, month);
		deadline.set(Calendar.YEAR, year);

		taskDB.open();
		Task t = taskDB.getTask(id);
		t.setDeadline(deadline);
		taskDB.updateTask(t);
		Toast.makeText(getApplicationContext(), "Postpone ok", Toast.LENGTH_SHORT).show();
		finish();
	}

	public void dialogCancel(View view) {
		finish();
	}

}
