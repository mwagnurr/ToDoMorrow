package com.lnu.todomorrow;

import java.util.Arrays;
import java.util.Calendar;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class GoalOverview extends Activity {
	private static final String TAG = GoalOverview.class.getSimpleName();

	private XYPlot plot;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goal_overview);
		
		plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
//		
//		// Create a couple arrays of y-values to plot:
        Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
        Number[] years = {
                978307200,  // 2001
                1009843200, // 2002
                1041379200, // 2003
                1383320640559l,
                1072915200, // 2004
                1104537600  // 2005
        };
        
        Calendar cal = Calendar.getInstance();
        cal.get(Calendar.DAY_OF_YEAR);
// 
//        // Turn the above arrays into XYSeries':
//        XYSeries series1 = new SimpleXYSeries(
//                Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
//                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
//                "Series1");                             // Set the display title of the series
// 
//        // same as above
//        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");
// 
//        // Create a formatter to use for drawing a series using LineAndPointRenderer
//        // and configure it from xml:
//        LineAndPointFormatter series1Format = new LineAndPointFormatter();
//        series1Format.setPointLabelFormatter(new PointLabelFormatter());
//        series1Format.configure(getApplicationContext(),
//                R.xml.line_point_formatter_with_plf1);
// 
//        // add a new series' to the xyplot:
//        plot.addSeries(series1, series1Format);
// 
//        // same as above:
//        LineAndPointFormatter series2Format = new LineAndPointFormatter();
//        series2Format.setPointLabelFormatter(new PointLabelFormatter());
//        series2Format.configure(getApplicationContext(),
//                R.xml.line_point_formatter_with_plf1);
//        plot.addSeries(series2, series2Format);
// 
//        // reduce the number of range labels
//        plot.setTicksPerRangeLabel(3);
//        plot.getGraphWidget().setDomainLabelOrientation(-45);
//        
        
		Log.d(TAG, "onCreate()");
	}
}
