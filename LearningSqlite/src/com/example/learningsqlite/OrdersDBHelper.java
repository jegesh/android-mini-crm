package com.example.learningsqlite;

import com.example.learningsqlite.OrdersContract.Orders;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class OrdersDBHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "main.db";
	
	public OrdersDBHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL(OrdersContract.Orders.SQL_CREATE_ORDERS_TABLE);
			Log.d("OrdersDBHelper", "In onCreate");
		}catch(Exception e){
			Log.d("Creating table... : " + OrdersContract.Orders.TABLE_NAME, e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// needs to be in parallel thread, could take quite some time! (?)
		Log.d("OrdersDBHelper", "In onUpgrade");
		/*
		try{
		db.execSQL("CREATE TABLE temp AS (SELECT * FROM "+OrdersContract.Orders.TABLE_NAME+")");
		db.execSQL(OrdersContract.Orders.SQL_DELETE_ORDERS);
		if(oldVersion==6 && newVersion==7)
			db.execSQL("");// convert column name
		else
			db.execSQL(OrdersContract.Orders.SQL_CREATE_ORDERS_TABLE);
		db.execSQL("SELECT * INTO "+OrdersContract.Orders.TABLE_NAME+" FROM temp");
		db.execSQL("DROP TABLE IF EXISTS temp");
		}catch(Exception e){
			Log.d(OrdersContract.Orders.TABLE_NAME+" table", e.getMessage());
		}
		*/
//		db.execSQL(OrdersContract.Orders.SQL_DELETE_ORDERS);
//		db.execSQL(OrdersContract.Orders.SQL_CREATE_ORDERS_TABLE);
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
