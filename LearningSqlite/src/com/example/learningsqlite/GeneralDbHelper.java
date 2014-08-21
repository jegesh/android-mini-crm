package com.example.learningsqlite;

import com.example.learningsqlite.JobWorkersContract.JobWorkers;
import com.example.learningsqlite.JobsProductsContract.JobsProducts;
import com.example.learningsqlite.OrdersContract.Orders;
import com.example.learningsqlite.ProductsContract.Products;
import com.example.learningsqlite.WorkersContract.Workers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GeneralDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "main.db";
	
	public GeneralDbHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CustomersContract.Customers.SQL_CREATE_CUSTOMERS_TABLE);
		db.execSQL(Orders.SQL_CREATE_ORDERS_TABLE);
		db.execSQL(Products.SQL_CREATE_TABLE);
		db.execSQL(Workers.SQL_CREATE_TABLE);
		db.execSQL(JobsProducts.SQL_CREATE_TABLE);
		db.execSQL(JobWorkers.SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// copy data
		
		// fill table with saved data

	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// like onUpgrade
//		super.onDowngrade(db, oldVersion, newVersion);
	}

}
