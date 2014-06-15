package com.example.learningsqlite;

import com.example.learningsqlite.ProductsContract.Products;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProductsRecord extends DatabaseRecord {
	public String recordId;
	
	
	public ProductsRecord( Context activity){
		super(ProductsContract.Products.TABLE_NAME,0,0,0); // fill in values! (once they exist)
		db = new ProductsDBHelper(activity).getWritableDatabase();
	}
	
	public ProductsRecord(String id, Context activity){
		this(activity);
		recordId = id;
		setValues();
	}
	
}
