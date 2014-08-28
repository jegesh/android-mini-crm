package database_files;

import database_files.CustomersContract.Customers;
import database_files.JobWorkersContract.JobWorkers;
import database_files.JobsProductsContract.JobsProducts;
import database_files.OrdersContract.Orders;
import database_files.ProductsContract.Products;
import database_files.WorkersContract.Workers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GeneralDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "main.db";
	
	public GeneralDbHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Customers.SQL_CREATE_CUSTOMERS_TABLE);
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
