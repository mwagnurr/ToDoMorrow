package com.lnu.todomorrow.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "todomorrow.db";
	private static final int DATABASE_VERSION = 1;

	//GOALS table columns
	public static final String TABLE_GOALS = "goals";
	public static final String GOALS_C_ID = "_id";
	public static final String GOALS_C_NAME = "name";
	public static final String GOALS_C_SCORE = "score";
	public static final String GOALS_C_DEADLINE = "deadline";
	
	//TASKS table columns
	public static final String TABLE_TASKS = "tasks";
	public static final String TASKS_C_ID = "_id";
	public static final String TASKS_C_NAME = "name";
	public static final String TASKS_C_FINISH = "finished";
	public static final String TASKS_C_DEADLINE = "deadline";
	public static final String TASKS_C_VALUE = "value";
	/**
	 * Goal foreign key - nullable
	 */
	public static final String TASKS_C_GOAL = "goal";
	

    private static final String DATABASE_CREATE_GOALS = "create table " + TABLE_GOALS 
    		+ " (" + GOALS_C_ID + " integer primary key autoincrement, "
    		+ GOALS_C_NAME + " text not null, "
    		+ GOALS_C_SCORE + " integer, "
    		+ GOALS_C_DEADLINE + " datetime);";
    
    private static final String DATABASE_CREATE_TASKS = "create table " + TABLE_TASKS 
    		+ " (" + TASKS_C_ID + " integer primary key autoincrement, "
    		+ TASKS_C_NAME + " text not null, "
            + TASKS_C_DEADLINE + " datetime, " 
            + TASKS_C_VALUE + " integer, "
            + TASKS_C_GOAL + " integer, "
    		+ "foreign key(" + TASKS_C_GOAL +") references " +TABLE_GOALS +"(" +GOALS_C_ID+")"+");";
    

    
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_GOALS);
		db.execSQL(DATABASE_CREATE_TASKS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(DbHelper.class.getName(), "Upgrading database from version " 
	    		+ oldVersion + " to " + newVersion 
	    		+ ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS );
        onCreate(db);
	}

}
