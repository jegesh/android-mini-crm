package com.example.learningsqlite;

import com.example.learningsqlite.ProductsContract.Products;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProductsRecord extends DatabaseRecord {
	public String recordId;
	private String tableName;
	public static SQLiteDatabase db;
	public String title;
	public String subtitle;
	public String unit;
	public String pricePer;
	public String notes;
	
	public ProductsRecord(String id, Context activity){
		tableName = Products.TABLE_NAME;
		db = new ProductsDBHelper(activity).getWritableDatabase();
		Cursor c = db.query(tableName, DbConstants.PRODUCTS_COLUMNS, "_ID = ?", new String[]{id}, null, null, null);
		c.moveToFirst();
		recordId = id;
		title = c.getString(1);
		subtitle = c.getString(2);
		unit = c.getString(3);
		pricePer = c.getString(4);
		notes = c.getString(5);
		c.close();
		
	}
	/*
	public static void createNew(String[] values){
		ContentValues cv = new ContentValues();
		for(int i = 0;i<cv.size();i++)
			cv.put(DbConstants.PRODUCTS_COLUMNS[i+1], values[i]);
		db.insert(Products.TABLE_NAME, null, cv);
	};


	public void update(ContentValues values){
		try{
			super.updateRecord(DbConstants.PRODUCTS_COLUMNS, values);
		}catch(Exception e){
		//	Toast.makeText(InputFormActivity.this, text, duration)
			Log.d(InputFormActivity.TAG, e.getMessage());
		}
	}
	
	public void delete(){
		super.deleteRecord();
	}
*/
}
