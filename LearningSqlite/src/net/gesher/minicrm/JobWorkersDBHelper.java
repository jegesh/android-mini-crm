package net.gesher.minicrm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class JobWorkersDBHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "main.db";
	
	public JobWorkersDBHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL(JobWorkersContract.JobWorkers.SQL_CREATE_TABLE);
		}catch(Exception e){
			Log.d("Creating table... : " + JobWorkersContract.JobWorkers.TABLE_NAME, e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// needs to be in parallel thread, could take quite some time! (?)
	/*	try{
		db.execSQL("CREATE TABLE temp AS (SELECT * FROM "+JobWorkersContract.JobWorkers.TABLE_NAME+")");
		db.execSQL(JobWorkersContract.JobWorkers.SQL_DELETE_TABLE);
		if(oldVersion==6 && newVersion==7)
			db.execSQL("");// convert column name
		else
			db.execSQL(JobWorkersContract.JobWorkers.SQL_CREATE_TABLE);
		db.execSQL("SELECT * INTO "+ JobWorkersContract.JobWorkers.TABLE_NAME+" FROM temp");
		db.execSQL("DROP TABLE IF EXISTS temp");
		}catch(Exception e){
			Log.d(JobWorkersContract.JobWorkers.TABLE_NAME+" table", e.getMessage());
		}
		*/
		// fill table with saved data

	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// like onUpgrade
	//	db.execSQL(OrdersContract.Orders.SQL_DELETE_ORDERS);
	//	db.execSQL(OrdersContract.Orders.SQL_CREATE_ORDERS_TABLE);
		Log.d("OrdersDb","In onDowngrade");
	//	super.onDowngrade(db, oldVersion, newVersion);
	}

}
