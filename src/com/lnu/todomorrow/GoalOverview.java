package com.lnu.todomorrow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.lnu.todomorrow.dao.TaskDAO;
import com.lnu.todomorrow.utils.Goal;
import com.lnu.todomorrow.utils.Task;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GoalOverview extends Activity {
	private static final String TAG = GoalOverview.class.getSimpleName();

	private XYPlot plot;
	
	private static TaskDAO taskDAO;
	
	private Goal thisGoal;
	
	private List<Integer> valuesX;
	private List<Integer> valuesY;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goal_overview);
		
		thisGoal = (Goal) getIntent().getSerializableExtra("goal");
		taskDAO = new TaskDAO(this);
		TextView goalName = (TextView)findViewById(R.id.goal_overview_name);
		
		goalName.setText(thisGoal.getName());
		
		createTaskPlot();
        
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
        
		Log.d(TAG, "onCreate()");
	}


	/**
	 * 
	 */
	private void createTaskPlot() {
		plot = (XYPlot) findViewById(R.id.goal_overview_plot);
//		
//		// Create a couple arrays of y-values to plot:
        Number[] series1Numbers = {1, 3, 5, 2, 0, 4, 0};
     
        taskDAO.open();
        List<Task> tasks = taskDAO.getAllTasksByGoal(thisGoal);
        
        Calendar cal = Calendar.getInstance();
        
        
        int timeField = Calendar.DAY_OF_YEAR;
        
        cal.set(timeField, cal.get(timeField)+15);
        
        initGraphValues(tasks, cal, 31, timeField);
        
		XYSeries s1 = new SimpleXYSeries(valuesX,
				valuesY, "Tasks completed");
//		 XYSeries s1 = new SimpleXYSeries(
//               Arrays.asList(series1Numbers),        
//               SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, 
//               "Tasks completed");   


       // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_plf1);
        
        plot.addSeries(s1, series1Format);

//        // reduce the number of range labels
        plot.setTicksPerRangeLabel(5);
        plot.getGraphWidget().setDomainLabelOrientation(-45);

        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1);
        //for debug
        //plot.getLayoutManager().setMarkupEnabled(true);
	}



	private void initGraphValues(List<Task> tasks, Calendar pivotTimeX, int sumValuesX, int calendarField) {
		int maxValueX = pivotTimeX.get(calendarField);
        
        valuesX = new ArrayList<Integer>();
        //Integer[] valuesX = new Integer[sumValuesX];
        for(int i = 0; i<sumValuesX;i++){
        	valuesX.add((maxValueX - sumValuesX) +i);
        	
        	Log.d(TAG, "DEBUG: for i:"+i + " valueX=" + valuesX.get(i));
        }
        
        List<Task> finishedTasks = new ArrayList<Task>();
        List<Task> openTasks = new ArrayList<Task>();
        Integer[] tasksFinY = new Integer[valuesX.size()];
        for(int i = 0; i < tasks.size(); i++){
        	
        	Task curr = tasks.get(i);
        	if(curr.isFinished()){
        		Log.d(TAG, "added finished task: " +curr);
        		finishedTasks.add(curr);
        		int currValue = curr.getFinishedAt().get(calendarField);
        		for(int j = 0; j < valuesX.size(); j++){
        			
        			if(currValue==valuesX.get(j)){
        				tasksFinY[j]+=1;
        			}else{
        				tasksFinY[j]=0;
        			}
        		}
        	}else{
        		Log.d(TAG, "added unfinished task: " +curr);
        		
        		openTasks.add(curr);
        		
        		for(int j = 0; j < valuesX.size(); j++){
        			curr.getFinishedAt();
        		}
        	}
        }
        
        for(int d=0; d<tasksFinY.length; d++){
        	Log.d(TAG, "DEBUG: for d:"+d + " tasks=" + tasksFinY[d]);
        }
        
        valuesY = Arrays.asList(tasksFinY);
	}
	
}