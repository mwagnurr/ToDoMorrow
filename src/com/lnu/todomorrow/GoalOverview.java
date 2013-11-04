package com.lnu.todomorrow;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.androidplot.Plot;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class GoalOverview extends Activity {
	private static final String TAG = GoalOverview.class.getSimpleName();

	private XYPlot plot;

	private static TaskDAO taskDAO;

	private Goal thisGoal;

	private List<Integer> valuesX;
	private List<Integer> valuesY;
	
	private int graphBackColor = Color.WHITE;
	private int graphLabelColor = Color.BLACK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goal_overview);

		thisGoal = (Goal) getIntent().getSerializableExtra("goal");
		taskDAO = new TaskDAO(this);
		TextView goalName = (TextView) findViewById(R.id.goal_overview_name);

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

		taskDAO.open();
		List<Task> tasks = taskDAO.getAllTasksByGoal(thisGoal);

		int timeField = Calendar.MONTH;

		Calendar cal;
		if (thisGoal.getDeadline() == null) {
			cal = Calendar.getInstance();

			cal.set(timeField, cal.get(timeField) + 1);
		} else {
			cal = thisGoal.getDeadline();
		}

		initGraphValues(tasks, cal, 12, timeField);

		XYSeries s1 = new SimpleXYSeries(valuesX, valuesY, "Tasks completed");

		Log.d(TAG, "series 1 is: " + s1);
		// XYSeries s1 = new SimpleXYSeries(
		// Arrays.asList(series1Numbers),
		// SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
		// "Tasks completed");

		// Create a formatter to use for drawing a series using
		// LineAndPointRenderer
		// and configure it from xml:
		LineAndPointFormatter series1Format = new LineAndPointFormatter();
		series1Format.setPointLabelFormatter(new PointLabelFormatter());
		series1Format.configure(getApplicationContext(),
				R.xml.line_point_formatter_with_plf1);

		plot.addSeries(s1, series1Format);

		// // reduce the number of range labels
		//plot.setTicksPerRangeLabel(5);
		//plot.getGraphWidget().setDomainLabelOrientation(-45);
		
		
//		plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
//		plot.setPlotMargins(0, 0, 0, 0);
//	    plot.setPlotPadding(0, 0, 0, 0);
//	    plot.setGridPadding(0, 10, 5, 10);
	    
	    //plot.getDomainLabelWidget().setPaddingBottom(20);
	    
	    
		//background colors
	    plot.setBackgroundColor(graphBackColor);
	    plot.getGraphWidget().getBackgroundPaint().setColor(graphBackColor);
	    plot.getGraphWidget().getGridBackgroundPaint().setColor(graphBackColor);

	    //axis valuse color
	    plot.getGraphWidget().getDomainLabelPaint().setColor(graphLabelColor);
	    plot.getGraphWidget().getRangeLabelPaint().setColor(graphLabelColor);
	    plot.getGraphWidget().setDomainLabelVerticalOffset(2);

	    plot.getGraphWidget().getDomainOriginLabelPaint().setColor(graphLabelColor);
	    
	    //axis line color
//	    plot.getGraphWidget().getDomainOriginLinePaint().setColor(graphLabelColor);
//	    plot.getGraphWidget().getRangeOriginLinePaint().setColor(graphLabelColor);

	    
	    plot.setDomainLabel("Months");
	    plot.getDomainLabelWidget().getLabelPaint().setColor(graphLabelColor);
	    plot.getRangeLabelWidget().getLabelPaint().setColor(graphLabelColor);
	    
	    plot.getLegendWidget().getTextPaint().setColor(graphLabelColor);
	    
	    //Sizing and positioning of graph
//	    plot.getGraphWidget().setSize(new SizeMetrics(
//	            0.8f, SizeLayoutType.RELATIVE,
//	            0.8f, SizeLayoutType.RELATIVE));
	    
	    plot.getGraphWidget().setSize(new SizeMetrics(
	            0, SizeLayoutType.FILL,
	            0, SizeLayoutType.FILL));
	    
	    //graph positioning via margins
	    plot.getGraphWidget().setMargins(10, 30, 10, 30);
	    
	    //do not change:
	    plot.setPlotMargins(0, 0, 0, 0);
	    plot.setPlotPadding(0, 0, 0, 0);
	    plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
	    
	    
	    //plot.getLayoutManager().getMarginPaint().setColor(Color.BLUE);
	    //plot.getLayoutManager().getPaddingPaint().setColor(Color.RED);
	    
	    //grid scaling
	    plot.getGraphWidget().setGridPaddingRight(10);
	    plot.getGraphWidget().setGridPaddingTop(10);

		//domain configuration
		plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, valuesX.size());     
		plot.setDomainValueFormat(new DecimalFormat("0"));
	    plot.setDomainStepValue(1);
		

	    //range configuration
	    plot.setRangeValueFormat(new DecimalFormat("0"));
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, valuesY.size());
		plot.setRangeStepValue(1);
		
		// for debug
		//plot.getLayoutManager().setMarkupEnabled(true);

		Log.d(TAG, "created graph plot!");
	}

	private void initGraphValues(List<Task> tasks, Calendar pivotTimeX,
			int sumValuesX, int calendarField) {
		int maxValueX = pivotTimeX.get(calendarField);

		valuesX = new ArrayList<Integer>();
		// Integer[] valuesX = new Integer[sumValuesX];
		for (int i = 0; i < sumValuesX; i++) {
			valuesX.add((maxValueX - sumValuesX) + i);

			Log.d(TAG, "DEBUG: for i:" + i + " valueX=" + valuesX.get(i));
		}

		List<Task> finishedTasks = new ArrayList<Task>();
		List<Task> openTasks = new ArrayList<Task>();
		Integer[] tasksFinY = new Integer[valuesX.size()];

		// init
		for (int x = 0; x < tasksFinY.length; x++) {
			tasksFinY[x] = 0;
		}

		for (int i = 0; i < tasks.size(); i++) {

			Task curr = tasks.get(i);
			if (curr.isFinished()) {
				Log.d(TAG, "added finished task: " + curr);
				finishedTasks.add(curr);
				int currValue = curr.getFinishedAt().get(calendarField);
				for (int j = 0; j < valuesX.size(); j++) {

					if (currValue == valuesX.get(j)) {
						tasksFinY[j] += 1;
					}
				}
			} else {
				Log.d(TAG, "added unfinished task: " + curr);

				openTasks.add(curr);

			}
		}

		for (int d = 0; d < tasksFinY.length; d++) {
			Log.d(TAG, "DEBUG: for d:" + d + " tasks=" + tasksFinY[d]);
		}

		valuesY = Arrays.asList(tasksFinY);
	}

}