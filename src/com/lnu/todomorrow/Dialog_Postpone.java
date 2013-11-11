package com.lnu.todomorrow;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class Dialog_Postpone extends Activity {

	private TimePicker tp;
	private DatePicker dp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog_postpone);
		tp = (TimePicker) findViewById(R.id.pick_deadline_time);
		dp = (DatePicker) findViewById(R.id.pick_deadline);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dialog_postpone, menu);
		return true;
	}

	public void dialogOK(View view) {
		Toast.makeText(getApplicationContext(), "Postpone ok", Toast.LENGTH_SHORT).show();
		finish();
	}
	
	public void dialogCancel(View view) {
		finish();
	}

}
