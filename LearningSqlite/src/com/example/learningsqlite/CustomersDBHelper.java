package com.example.learningsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CustomersDBHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 7;
	public static final String DATABASE_NAME = "main.db";
	
	public CustomersDBHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CustomersContract.Customers.SQL_CREATE_CUSTOMERS_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// copy data
		db.execSQL(CustomersContract.Customers.SQL_DELETE_CUSTOMERS);
		db.execSQL(CustomersContract.Customers.SQL_CREATE_CUSTOMERS_TABLE);
		// fill table with saved data

	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// like onUpgrade
//		super.onDowngrade(db, oldVersion, newVersion);
	}

}
