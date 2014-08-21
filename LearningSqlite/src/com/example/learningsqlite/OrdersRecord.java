package com.example.learningsqlite;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.learningsqlite.JobWorkersContract.JobWorkers;
import com.example.learningsqlite.JobsProductsContract.JobsProducts;
import com.example.learningsqlite.OrdersContract.Orders;

public class OrdersRecord extends DatabaseRecord {
	public static final String TAG = "OrdersRecord";
//	public String recordId;
//	private String custId;
	public CustomersRecord customer;
	public LinkedList<WorkersRecord> workers;
	public LinkedList<ProductsRecord> products;
		
	public OrdersRecord(String id, Context activity){
		this(activity);
		recordId=id;
		setValues((Activity)activity);
//		Log.d(TAG, "Title: "+ super.values[0]);
		
	}
	
	public OrdersRecord(Context activity){
		super(Orders.TABLE_NAME,R.layout.activity_orders_new_record,R.layout.orders_display_record, R.string.title_orders_main);
		dbHelper = new OrdersDBHelper(activity);
		db = dbHelper.getWritableDatabase();
		inputIdsToColumns = new HashMap<Integer, String>();
		displayIdsToColumns = new HashMap<Integer, String>();
		setInputIdsToColumns();
		setDisplayIdsToColumns();
		
		workers = new LinkedList<>();
		products = new LinkedList<>();
	//	Toast.makeText(activity, "Done constructing", Toast.LENGTH_SHORT).show();
	}
	
	
	public void setValues(Activity activity){
		super.setValues();
		if(valueMap.get(Orders.COLUMN_NAME_CUSTOMER_ID)!=null && valueMap.get(Orders.COLUMN_NAME_CUSTOMER_ID).length()>0){
			try{
				customer = new CustomersRecord(valueMap.get(Orders.COLUMN_NAME_CUSTOMER_ID), activity);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		Cursor workersCursor = db.query(JobWorkers.TABLE_NAME, null, JobWorkers.COLUMN_NAME_ORDERS_ID+" = ?", new String[]{recordId}, null, null, null);
		if(workersCursor.moveToFirst()){
			for(int i=0;i<workersCursor.getCount();i++){
				try{
					workers.add(new WorkersRecord(workersCursor.getString(2), activity));
					
				}catch(Exception e){
					e.printStackTrace();
				}
				workersCursor.moveToNext();
			}
			workersCursor.close();
		}
		
		Cursor productsCursor = db.query(JobsProducts.TABLE_NAME, null, JobsProducts.COLUMN_NAME_ORDERS_ID+" = ?", new String[]{recordId}, null, null, null);
		if(productsCursor.moveToFirst()){
			for(int i = 0;i<productsCursor.getCount();i++){
			try{
				products.add(new ProductsRecord(productsCursor.getString(2), activity));
			}catch(Exception e){
				e.printStackTrace();
			}
			productsCursor.moveToNext();
		}
		productsCursor.close();
		}
	}
	
	public void saveBasicFieldsToObject(Activity activity){
		super.saveDataToObject(activity);
	}
	
	@Override
	public void saveDataToObject(Activity activity) {
		super.saveDataToObject(activity);
		// if customer member is an existant customer, and its data needs to be updated
		if(customer!=null){
			customer.saveDataToObject(activity);
			if(customer.recordId!=null)
				valueMap.put(Orders.COLUMN_NAME_CUSTOMER_ID, customer.recordId);
		}
		if(products.size()>0){
			LinearLayout productsContainer = (LinearLayout) activity.findViewById(R.id.orders_products_fragment_container);
			for(int i = 0;i<productsContainer.getChildCount();i++){
				LinearLayout singleContainer = (LinearLayout)productsContainer.getChildAt(i);
				products.get(i).saveDataToObject(singleContainer);
			}
		}
		if(workers.size()>0){
			LinearLayout workersContainer = (LinearLayout)activity.findViewById(R.id.orders_workers_fragment_container);
			for(int i = 0;i<workersContainer.getChildCount();i++){
				LinearLayout singleContainer = (LinearLayout)workersContainer.getChildAt(i);
				workers.get(i).saveDataToObject(singleContainer);
			}
		}
	}
	
	boolean insertCust(){
		boolean custSaved = false;
		
			// check that map contains some data
    		if(customer != null){
    			if(customer.recordId != null)
    				customer.updateRecord(customer.valueMap);
    			else{
    				custSaved = customer.insertNewRecord();
    				if(!custSaved)
    					return false; // TODO preferable to make a toast here, but at the moment no context is accessible
    			//	cv.put(Orders.COLUMN_NAME_CUSTOMER_ID, customer.recordId);
    			}
    		}
    		return true;
    	
    	
	}
	
	boolean insertProds(){
		if(products.size()>0){
		// check multiple record for each order and insert or update
		}
		return true;
	}
	
	boolean insertWorkers(){
		if(workers.size()>0){
			
		}
		return true;
	}
	
	@Override
	public boolean insertNewRecord() {
		// save to db, checking first if it should be an insert or update
		if(!valueMap.values().isEmpty()){
		try {
			if(insertCust()){
				if(insertProds()){
					if(insertWorkers()){
						ContentValues cv = DatabaseRecord.mapValuesToCV(valueMap);
						recordId = Integer.toString((int)db.insert(tableName, null, cv));
					}else
						return false;
				}else
					return false;
			}else
				return false;
        	
		} catch (Exception e) {
			Log.d(TAG, "Error inserting new record: "+e.getMessage());
			return false;
		}
		return true;
		}
		return false;
    	
	}
	
	@Override
	public boolean updateRecord(Map<String, String> values) {
		// TODO Auto-generated method stub
		return super.updateRecord(values);
	}

	@Override
	protected void setInputIdsToColumns() {
		inputIdsToColumns.put(R.id.order_new_record_title, Orders.COLUMN_NAME_TITLE);
		inputIdsToColumns.put(R.id.orders_new_record_order_date_field, Orders.COLUMN_NAME_ORDER_DATE);
		inputIdsToColumns.put(R.id.orders_new_record_fill_by_date_field, Orders.COLUMN_NAME_FILL_BY_DATE);
		inputIdsToColumns.put(R.id.order_new_record_address, Orders.COLUMN_NAME_ADDRESS);
	}

	@Override
	protected void setDisplayIdsToColumns() {
		displayIdsToColumns.put(R.id.header_orders_display, Orders.COLUMN_NAME_TITLE);
		displayIdsToColumns.put(R.id.content_order_date_ordered, Orders.COLUMN_NAME_ORDER_DATE);
		displayIdsToColumns.put(R.id.content_order_target_date, Orders.COLUMN_NAME_FILL_BY_DATE);
		displayIdsToColumns.put(R.id.content_orders_address, Orders.COLUMN_NAME_ADDRESS);
		
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
