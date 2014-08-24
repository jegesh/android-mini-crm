package net.gesher.minicrm;

import net.gesher.minicrm.ProductsContract.Products;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProductsDBHelper  extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "main.db";
	
	public ProductsDBHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL(ProductsContract.Products.SQL_CREATE_TABLE);
		}catch(Exception e){
			Log.d("Creating table... : " + ProductsContract.Products.TABLE_NAME, e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// needs to be in parallel thread, could take quite some time! (?)
		/*
		try{
		db.execSQL("CREATE TABLE temp AS (SELECT * FROM "+ProductsContract.Products.TABLE_NAME+")");
		db.execSQL(ProductsContract.Products.SQL_DELETE_TABLE);
		db.execSQL("SELECT * INTO "+ ProductsContract.Products.TABLE_NAME+" FROM temp");
		db.execSQL("DROP TABLE IF EXISTS temp");
		}catch(Exception e){
			Log.d(ProductsContract.Products.TABLE_NAME+" table", e.getMessage());
		}*/
		
		// fill table with saved data
		
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// like onUpgrade
	//	db.execSQL(OrdersContract.Orders.SQL_DELETE_ORDERS);
	//	db.execSQL(OrdersContract.Orders.SQL_CREATE_ORDERS_TABLE);
		Log.d("ProductsTable","In onDowngrade");
	//	super.onDowngrade(db, oldVersion, newVersion);
	}

}
