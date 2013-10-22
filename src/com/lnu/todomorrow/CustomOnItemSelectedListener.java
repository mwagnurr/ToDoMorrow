package com.lnu.todomorrow;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class CustomOnItemSelectedListener implements OnItemSelectedListener{

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		String goal = arg0.getItemAtPosition(arg2).toString();
		if(goal.equalsIgnoreCase("New Goal")){
			// create new Goal object
		} else {
			// add task to selected goal
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
