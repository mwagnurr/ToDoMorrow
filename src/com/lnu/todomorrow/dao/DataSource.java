package com.lnu.todomorrow.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataSource {

	  // Database fields
	  private SQLiteDatabase database;
	  private DbHelper dbHelper;

	  private static final String TAG = DataSource.class.getSimpleName();

	  public DataSource(Context context) {
	    dbHelper = new DbHelper(context);
	  }

	  /**
	   * opens database connection
	   * @throws SQLException
	   */
	  public void open() throws SQLException {
		Log.d(TAG, "opened Databaseconnection");
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }
	  
	} 
