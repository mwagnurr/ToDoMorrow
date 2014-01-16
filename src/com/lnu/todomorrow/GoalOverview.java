package com.lnu.todomorrow;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.androidplot.Plot;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;
import com.androidplot.xy.XYStepMode;
import com.lnu.todomorrow.TaskListFragment.TaskDataChangedListener;
import com.lnu.todomorrow.dao.GoalDAO;
import com.lnu.todomorrow.dao.TaskDAO;
import com.lnu.todomorrow.utils.Goal;
import com.lnu.todomorrow.utils.MyBroadcastReceiver;
import com.lnu.todomorrow.utils.Task;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class GoalOverview extends Activity implements TaskDataChangedListener {
	private static final String TAG = GoalOverview.class.getSimpleName();

	private XYPlot plot;

	private static TaskDAO taskDAO;
	private GoalDAO goalDAO;

	private TaskListFragment listFragment;

	private Goal thisGoal;

	private int graphBackColor = Color.BLACK;
	private int graphLabelColor = Color.WHITE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goal_overview);

		taskDAO = new TaskDAO(this);
		goalDAO = new GoalDAO(this);

		Goal goal = (Goal) getIntent().getSerializableExtra("goal");
		int goalId;

		if (goal != null) {

			goalId = goal.getId();
			storeGoalIdSettings(goal.getId());

			Log.d(TAG, "goal info in intent - goalId: " + goalId + ", storing to settings");

		} else {
			goalId = getGoalIdSettings();
			Log.d(TAG, "no intent info - getting last goalId from settings: " + goalId);

			if (goalId == -1) {
				Log.e(TAG, "no goal ID is stored");
				goalId = 1;
			}
		}

		goalDAO.open();
		thisGoal = goalDAO.getGoal(goalId);

		TextView goalName = (TextView) findViewById(R.id.goal_overview_name);

		goalName.setText(thisGoal.getName());

		Log.d(TAG, "thisGoal: " + thisGoal);

		// ExampleFragment fragment = (ExampleFragment)
		// getFragmentManager().findFragmentById(R.id.example_fragment);
		listFragment = (TaskListFragment) getFragmentManager().findFragmentById(
				R.id.goal_overview_task_list_fragment);

		listFragment.filterByGoal(thisGoal);

		createTaskPlot();

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// TODO add sorting options for taskfragment list

		Log.d(TAG, "onCreate() finished");
	}

	/**
	 * @return
	 */
	private int getGoalIdSettings() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int goalId = prefs.getInt("thisGoalId", -1);
		return goalId;
	}

	/**
	 * @param goalId
	 */
	private void storeGoalIdSettings(int goalId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();

		editor.putInt("thisGoalId", goalId);
		editor.apply();
	}

	@Override
	public void onDestroy() {
		if (taskDAO != null)
			taskDAO.close();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent1 = new Intent(this, GoalList.class);
			intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * fetches data and creates graph with current settings
	 */
	@SuppressLint("SimpleDateFormat")
	private void createTaskPlot() {
		plot = (XYPlot) findViewById(R.id.goal_overview_plot);

		taskDAO.open();
		List<Task> tasks = taskDAO.getAllTasksByGoal(thisGoal);

		for (Task curr : tasks) {
			Log.v(TAG, "- " + curr);
		}

		int timeField = Calendar.MONTH;

		// definition of how far the graph should draw on X axis - either to deadline or to
		// curr month + 1
		Calendar cal;
		if (thisGoal.getDeadline() == null) {
			cal = Calendar.getInstance();

			cal.set(timeField, cal.get(timeField) + 1);

			Log.d(TAG, "goal has no deadline - display current time + 1");
		} else {
			cal = thisGoal.getDeadline();

			Calendar currTime = Calendar.getInstance();
			if (currTime.after(cal)) {
				Log.d(TAG, "goal has deadline and it is older than today - display current time");
				cal = currTime;
			} else {
				Log.d(TAG, "goal has deadline and it is not older than today - display deadline");
			}
			// Log.d(TAG, "goal has deadline - " + thisGoal + " / cal: " + cal);
		}

		SimpleDateFormat dateComparisonFormat = new SimpleDateFormat("MM/yy");

		Log.e(TAG, "cal is: " + cal.getTimeInMillis());
		XYSeries s1 = initGraphSeries(tasks, cal, 12, timeField, dateComparisonFormat);

		// TODO another plot series for vertical deadline line

		// Formatter for graph line and point, partly defined in XML
		LineAndPointFormatter series1Format = new LineAndPointFormatter();
		// hide point label text
		PointLabelFormatter pointLabel = new PointLabelFormatter();
		pointLabel.getTextPaint().setColor(Color.TRANSPARENT);
		series1Format.setPointLabelFormatter(pointLabel);
		series1Format
				.configure(getApplicationContext(), R.xml.line_point_formatter_tasks_completed);

		plot.addSeries(s1, series1Format);

		configureGraph();

		Log.d(TAG, "created graph plot!");
	}

	/**
	 * configures graph display (background/line/text colors, layout etc)
	 * 
	 * @param s1
	 */
	private void configureGraph() {

		// testTutorialCode(series1Format);

		plot.getGraphWidget().setDomainLabelOrientation(-45);

		// background colors
		plot.setBackgroundColor(graphBackColor);
		plot.getGraphWidget().getBackgroundPaint().setColor(graphBackColor);
		plot.getGraphWidget().getGridBackgroundPaint().setColor(graphBackColor);

		// axis values color
		plot.getGraphWidget().getDomainLabelPaint().setColor(graphLabelColor);
		plot.getGraphWidget().getRangeLabelPaint().setColor(graphLabelColor);
		plot.getGraphWidget().setDomainLabelVerticalOffset(1);

		// plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.TRANSPARENT);

		// axis line color
		plot.getGraphWidget().getDomainOriginLinePaint().setColor(graphLabelColor);
		plot.getGraphWidget().getRangeOriginLinePaint().setColor(graphLabelColor);

		plot.setDomainLabel("Months");
		plot.getDomainLabelWidget().getLabelPaint().setColor(graphLabelColor);
		plot.getRangeLabelWidget().getLabelPaint().setColor(graphLabelColor);

		plot.getLegendWidget().getTextPaint().setColor(graphLabelColor);

		plot.getGraphWidget().setSize(
				new SizeMetrics(0, SizeLayoutType.FILL, 0, SizeLayoutType.FILL));

		// graph positioning via margins
		plot.getGraphWidget().setMargins(10, 30, 10, 30);

		// do not change:
		plot.setPlotMargins(0, 0, 0, 0);
		plot.setPlotPadding(0, 0, 0, 0);
		plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);

		// grid scaling
		plot.getGraphWidget().setGridPaddingRight(10);
		plot.getGraphWidget().setGridPaddingTop(10);

		// for debug
		// plot.getLayoutManager().setMarkupEnabled(true);

		plot.getGraphWidget().setDomainValueFormat(new GraphXLabelFormat());
	}

	private XYSeries initGraphSeries(List<Task> tasks, Calendar pivotTimeX, int sumValuesX,
			int calendarField, DateFormat dateFormat) {

		Log.d(TAG, "initGraphSeries()");

		// setup
		int decrement = -1;
		Integer[] valXInt = new Integer[sumValuesX];
		Integer[] valYInt = new Integer[sumValuesX];
		for (int y = 0; y < sumValuesX; y++) {
			valYInt[y] = 0; // init array
		}

		// Laufzeit: L( sumValuesX * tasks.size() + C)
		for (int i = 0; i < sumValuesX; i++) {

			long currLong = pivotTimeX.getTimeInMillis();
			int curr = (int) (currLong / 1000);
			valXInt[i] = curr;

			String strCurrPivotTime = dateFormat.format(pivotTimeX.getTime());

			for (int j = 0; j < tasks.size(); j++) {
				Task currTask = tasks.get(j);

				if (currTask.isFinished()) {

					String strCurrFinishedAt = dateFormat
							.format(currTask.getFinishedAt().getTime());

					// Log.e(TAG, "" + strCurrFinishedAt + " vs " + strCurrPivotTime);
					if (strCurrPivotTime.equals(strCurrFinishedAt)) {
						valYInt[i] += 1;
						Log.d(TAG, "task " + currTask.getName() + " was completed "
								+ strCurrFinishedAt + " and therefore counted");
					}

				}
			}

			// decrement calendar
			pivotTimeX.add(calendarField, decrement);

		}

		// List<Integer> valuesX = Arrays.asList(valXInt);
		// List<Integer> valuesY = Arrays.asList(valYInt);

		XYSeries series = new SimpleXYSeries(Arrays.asList(valXInt), Arrays.asList(valYInt),
				"Tasks completed");

		// plot.getGraphWidget().addDomainAxisValueLabelRegion(valXInt[0], valXInt[1],
		// new AxisValueLabelFormatter(Color.BLUE));

		// YValueMarker marker = new YValueMarker(valXInt[1], "test");
		// Paint bla = new Paint();
		// bla.setColor(Color.GREEN);
		// marker.setLinePaint(bla);
		// plot.addMarker(marker);

		// domain configuration
		plot.setDomainStep(XYStepMode.SUBDIVIDE, valXInt.length);
		// plot.setDomainValueFormat(new DecimalFormat("0"));
		// plot.setDomainStepValue(1);

		// range configuration
		plot.setRangeValueFormat(new DecimalFormat("0"));
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, valYInt.length);
		plot.setRangeStepValue(1);

		// for check
		Log.d(TAG, "Successfully initialized graph values.");
		return series;
	}

	private class GraphXLabelFormat extends Format {
		private static final long serialVersionUID = 1L;

		@SuppressLint("SimpleDateFormat")
		private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");

		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

			// because our timestamps are in seconds and SimpleDateFormat expects milliseconds
			// we multiply our timestamp by 1000:
			long timestamp = ((Number) obj).longValue() * 1000;
			Date date = new Date(timestamp);
			return dateFormat.format(date, toAppendTo, pos);
		}

		@Override
		public Object parseObject(String source, ParsePosition pos) {
			return null;

		}
	}

	public void addTask(View view) {
		Intent intent = new Intent(this, TaskForm.class);
		intent.putExtra("goal", thisGoal);
		this.startActivityForResult(intent, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		Log.d(TAG, "received result");
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {

				Task task = (Task) result.getSerializableExtra("task");

				if (task == null) {
					Log.e(TAG, "received as task null");
					return;
				}

				// creating intent for alarmManager
				Intent intent = new Intent(GoalOverview.this, MyBroadcastReceiver.class);
				intent.putExtra("name", task.getName());
				intent.putExtra("goal", task.getGoal().getName());
				intent.putExtra("value", task.getValue());
				intent.putExtra("id", task.getId());
				PendingIntent pi = PendingIntent.getBroadcast(GoalOverview.this, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);

				// setting alarm
				AlarmManager alarmMan = (AlarmManager) getSystemService(ALARM_SERVICE);
				alarmMan.set(AlarmManager.RTC_WAKEUP, task.getDeadline().getTimeInMillis(), pi);

				Log.d(TAG, "created task: " + task);
				if (listFragment == null) {
					listFragment = (TaskListFragment) getFragmentManager().findFragmentById(
							R.id.goal_overview_task_list_fragment);
				}
				listFragment.addTask(task);
			}
		}
	}

	/**
	 * for debugging - do not look at it!
	 * 
	 * @param series1Format
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	private void testTutorialCode(XYSeriesFormatter series1Format) {
		Number[] numSightings = { 5, 8, 9, 2, 5 };
		// Number[] years = {
		// 978307200, // 2001
		// 1009843200, // 2002
		// 1041379200, // 2003
		// 1072915200, // 2004
		// 1104537600 // 2005
		// };
		Number[] years = { 90, // 2001
				100, // 2002
				110, // 2003
				120, // 2004
				130 // 2005
		};
		// create our series from our array of nums:
		XYSeries series2 = new SimpleXYSeries(Arrays.asList(years), Arrays.asList(numSightings),
				"Sightings in USA");

		plot.setDomainStep(XYStepMode.SUBDIVIDE, years.length);
		plot.addSeries(series2, series1Format);
	}

	@Override
	public void onTaskChanged() {
		Log.d(TAG,
				"receiveid onTaskChanged - the taskListFragment data changed, we should redraw graph!");
		// plot.clear();
		// createTaskPlot();
		// plot.redraw();

		recreate();
	}
}