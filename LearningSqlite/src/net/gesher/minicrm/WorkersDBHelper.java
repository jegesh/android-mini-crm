package net.gesher.minicrm;

import net.gesher.minicrm.WorkersContract.Workers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WorkersDBHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "main.db";
	
	public WorkersDBHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL(WorkersContract.Workers.SQL_CREATE_TABLE);
		}catch(Exception e){
			Log.d("Creating table... : " + WorkersContract.Workers.TABLE_NAME, e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// needs to be in parallel thread, could take quite some time! (?)
	/*	try{
		db.execSQL("CREATE TABLE temp AS (SELECT * FROM "+WorkersContract.Workers.TABLE_NAME+")");
		db.execSQL(WorkersContract.Workers.SQL_DELETE_TABLE);
		db.execSQL("SELECT * INTO "+WorkersContract.Workers.TABLE_NAME+" FROM temp");
		db.execSQL("DROP TABLE IF EXISTS temp");
		}catch(Exception e){
			Log.d(WorkersContract.Workers.TABLE_NAME+" table", e.getMessage());
		}
		
		// fill table with saved data
*/
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// like onUpgrade
	//	db.execSQL(OrdersContract.Orders.SQL_DELETE_ORDERS);
	//	db.execSQL(OrdersContract.Orders.SQL_CREATE_ORDERS_TABLE);
		Log.d("WorkersTable","In onDowngrade");
	//	super.onDowngrade(db, oldVersion, newVersion);
	}

}
