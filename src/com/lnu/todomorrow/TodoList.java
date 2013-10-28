package com.lnu.todomorrow;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Collections;

import com.lnu.todomorrow.dao.*;
import com.lnu.todomorrow.utils.Task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TodoList extends Activity {
	private static final String TAG = TodoList.class.getSimpleName();
	private ListView lv;
	private MyAdapter adapter;
	private static TaskDAO datasource;
	private List<Task> tasks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list);
		lv = (ListView) findViewById(R.id.list);
		datasource = new TaskDAO(this);
		datasource.open();
		tasks = datasource.getAllTasks();
		
		adapter = new MyAdapter(this, R.layout.row_layout, tasks);
		lv.setAdapter(adapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);
		return true;
	}
	
	public void addTask(View view){
		Intent intent = new Intent(this, TaskForm.class);
		this.startActivityForResult(intent, 0);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent result){
		switch(requestCode){
		case 0:
			if(resultCode == RESULT_OK){
				int y = result.getIntExtra("dead_y", 2013);
				int m = result.getIntExtra("dead_m", 1);
				int d = result.getIntExtra("dead_d", 1);
				
				int h = result.getIntExtra("dead_h", 12);
				int min = result.getIntExtra("dead_min", 00);
				
				String name = result.getStringExtra("task_name");
				int goal = result.getIntExtra("goal", 0);
				
				Date date = new Date(y-1900, m, d);
				date.setHours(h);
				date.setMinutes(min);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				String deadline = sdf.format(date);
				datasource.open();
				Task task = datasource.createTaskEntry(name, deadline, goal);
				
				Log.d(TAG, "created task: " + task);
				adapter.add(task);
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	public void checkboxChecked(View view){
		CheckBox check = (CheckBox) view;
		Collections.sort(tasks, new BooleanComparator());
		if(adapter == null){
			adapter = new MyAdapter(this, R.layout.row_layout, tasks);
		}
		if(check.isChecked()){
			// task.setFinished = true;
			// cross out task
			adapter.notifyDataSetChanged();
			Toast.makeText(this, "Checkbox Checked", Toast.LENGTH_LONG).show();
		} else {
			// set finished-field to false
			Toast.makeText(this, "Checkbox Unchecked", Toast.LENGTH_LONG).show();
		}
		
	}
	
	/**
	 * New Adapter Class for row-layout in WeatherApp
	 * 
	 * @author Lia
	 * 
	 */
	class MyAdapter extends ArrayAdapter<Task> {
		Context context;

		public MyAdapter(Context context, int resource, List<Task> objects) {
			super(context, resource, objects);
		}

		@Override
		// Called when updating the ListView
		public View getView(int position, View convertView, ViewGroup parent) {
			View row;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row_layout, parent, false);
			} else {
				row = convertView;
			}

			TextView name = (TextView) row.findViewById(R.id.show_task);
			TextView dead = (TextView) row.findViewById(R.id.show_deadline);
			TextView goal = (TextView) row.findViewById(R.id.show_goal);
			
			name.setText(tasks.get(position).getName());
			dead.setText(tasks.get(position).getDeadline());
			goal.setText("Goal");

			return row;
		}

	}
	
	public class BooleanComparator implements Comparator<Task> {

		@Override
		public int compare(Task lhs, Task rhs) {
			if(lhs.isFinished())
				return 1;
			if(rhs.isFinished())
				return -1;
			return 0;
		}

	}

}
