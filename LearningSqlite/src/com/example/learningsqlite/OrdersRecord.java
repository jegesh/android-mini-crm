package com.example.learningsqlite;

import java.util.LinkedList;

import android.content.Context;
import android.database.Cursor;

import com.example.learningsqlite.OrdersContract.Orders;

public class OrdersRecord extends DatabaseRecord {
	public static final String TAG = "OrdersRecord";
	public String recordId;
	private String custId;
	public CustomersRecord customer;
	public LinkedList<WorkersRecord> workers;
	public LinkedList<ProductsRecord> products;
		
	public OrdersRecord(String id, Context activity){
		this(activity);
		recordId=id;
		super.setValues();
//		Log.d(TAG, "Title: "+ super.values[0]);
		if(custId!=null){
			try{
				customer = new CustomersRecord(custId, activity);
			}catch(Exception e){
				e.printStackTrace();
			}
		}/*
		Cursor c2 = db.query(JobWorkers.TABLE_NAME, DbConstants.JOBS_WORKERS_COLUMNS, DbConstants.JOBS_WORKERS_COLUMNS[1]+" = ?", new String[]{recordId}, null, null, null);
		if(c2.moveToFirst()){
			workers = new WorkersRecord[c2.getCount()];
			for(int i=0;i<c2.getCount();i++){
				try{
					workers[i] = new WorkersRecord(c2.getString(2), activity);
				}catch(Exception e){
					
				}
				c2.moveToNext();
			}
			c2.close();
		}
		Cursor c3 = db.query(JobsProducts.TABLE_NAME, DbConstants.JOBS_PRODUCTS_COLUMNS, DbConstants.JOBS_PRODUCTS_COLUMNS[1]+" = ?", new String[]{recordId}, null, null, null);
		if(c3.moveToFirst()){
			products = new ProductsRecord[c3.getCount()];
			for(int i=0;i<c3.getCount();i++){
				try{
					products[i] = new ProductsRecord(c3.getString(2), activity);
				}catch(Exception e){
					
				}
				c3.moveToNext();
			}
			c3.close();
		}
		*/
	}
	
	public OrdersRecord(Context activity){
		super(Orders.TABLE_NAME,R.layout.activity_orders_new_record,R.layout.orders_display_record, R.string.title_orders_main);
		db = new OrdersDBHelper(activity).getWritableDatabase();
	}
	
	@Override
	public void setValues(){
		Cursor c = super.db.query(super.tableName, null, "_ID = ?", new String[]{super.recordId}, null, null, null);
		
		if(c.moveToFirst()){
			super.columns = c.getColumnNames();
			int cols = c.getColumnCount()-1;
			values = new String[cols-1];
			int j = 0;
			for(int i = 0;i<cols;i++){
				int k = i + j;
				if(i==3){
					custId = c.getString(i+1);
					j=-1;
				}
				else
					values[k]=c.getString(i+1);
			}
		}
		//super.values = values;
		c.close();
	}
	
	/*
	
	public void update(ContentValues values) {
		try{
			super.updateRecord(DbConstants.ORDERS_COLUMNS, values);
		}catch(Exception e){
		//	Toast.makeText(InputFormActivity.this, text, duration)
			Log.d(InputFormActivity.TAG, e.getMessage());
		}
	}*/
	
	

	
	
	
}
