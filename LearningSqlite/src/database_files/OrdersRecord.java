package database_files;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import database_files.JobWorkersContract.JobWorkers;
import database_files.JobsProductsContract.JobsProducts;
import database_files.OrdersContract.Orders;
import database_files.ProductsContract.Products;

import net.gesher.minicrm.R;
import net.gesher.minicrm.R.id;
import net.gesher.minicrm.R.layout;
import net.gesher.minicrm.R.string;



import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;


public class OrdersRecord extends DatabaseRecord {
	public static final String TAG = "OrdersRecord";
//	public String recordId;
//	private String custId;
	public CustomersRecord customer;
	public LinkedList<WorkersRecord> workers;
	public LinkedList<ProductsRecord> products;
	public LinkedList<WorkersRecord> removedWorkers;
	public LinkedList<ProductsRecord> removedProducts;
		
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
		removedProducts = new LinkedList<>();
		removedWorkers = new LinkedList<>();
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
		// if customer member present
		if(customer!=null){
			customer.saveDataToObject(activity);
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
				((WorkersRecord)workers.get(i)).saveDataToObject(singleContainer);
			}
		}
	}
	
	@Override
	public boolean insertNewRecord() {
		/* save to db, checking first if it should be an insert or update
		 * 1. save cust record
		 * 2. save orders record
		 * 3. save associated products/workers
		 * 4. save appropriate data to jobsWorkers and jobsProducts tables 
		 * 5. remove disassociated records from database
		 */
		if(!valueMap.values().isEmpty()){
			try {
				//TODO handle nested record saving errors, e.g. if the custRec doesn't save successfully but the order does, display a message
				
				// step 1: customer record
				updateCustomer();
				Log.d(TAG, "step 1");
				
				// step 2: orders record
				ContentValues cv = DatabaseRecord.mapValuesToCV(valueMap);
				recordId = Integer.toString((int)db.insert(tableName, null, cv));
				Log.d(TAG, "step 2");
				// step 3: update member records
				updateMembers();
			} catch (Exception e) {
				Log.d(TAG, "Error inserting new record: "+e.getMessage());
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false; // if orders record contains no data
	}

	void updateCustomer() {
		if(customer != null){
			if(customer.recordId!=null)
				customer.updateRecord(customer.valueMap);// update in case it was changed from the orders input screen
			else
				customer.insertNewRecord();
			valueMap.put(Orders.COLUMN_NAME_CUSTOMER_ID, customer.recordId);

		}
	}

	void updateMembers() {
		// step 3a: workers
		if(workers.size()>0)
			for(int i = 0;i<workers.size();i++){
				WorkersRecord w = (WorkersRecord)workers.get(i);
				if(w.recordId!=null){
					Log.d(TAG, "Worker update, id: "+w.recordId);
					w.updateRecord(w.valueMap);
				}
				else{
					w.insertNewRecord();
					Log.d(TAG, "Worker insertion, id: "+w.recordId);
				}
			}
		// step 3b: products
		if(products.size()>0)
			for(int i = 0;i<products.size();i++){
				ProductsRecord p = products.get(i);
				if(p.recordId!=null)
					p.updateRecord(p.valueMap);
				else
					p.insertNewRecord();
			}
		
		// step 4: commit associations to db
		for(int i = 0;i<workers.size();i++){
			if(((WorkersRecord)workers.get(i)).newlyAdded){
				ContentValues wCV = new ContentValues();
				wCV.put(JobWorkers.COLUMN_NAME_ORDERS_ID, this.recordId);
				wCV.put(JobWorkers.COLUMN_NAME_WORKERS_ID, workers.get(i).recordId);
				db.insert(JobWorkersContract.JobWorkers.TABLE_NAME, null, wCV);
			}
		}
		for(int i = 0;i<products.size();i++){
			if(products.get(i).newlyAdded){
				ContentValues pCV = new ContentValues();
				pCV.put(JobsProducts.COLUMN_NAME_ORDERS_ID, this.recordId);
				pCV.put(JobsProducts.COLUMN_NAME_PRODUCTS_ID, products.get(i).recordId);
				pCV.put(JobsProducts.COLUMN_NAME_AMOUNT, products.get(i).amount);
			}
		}
		
		// step 5: remove disassociated subrecords from db
		removeDisassociatedMembers();
	}
	
	private void removeDisassociatedMembers(){
		if(removedProducts.size()>0){
			for(int i = 0;i<removedProducts.size();i++){
				String deletionId = removedProducts.get(i).recordId;
				db.delete(Products.TABLE_NAME, Products._ID + " = ?", new String[]{deletionId});
			}
		}
		if(removedWorkers.size()>0){
			for(int i = 0;i<removedWorkers.size();i++){
				String deletionId = removedWorkers.get(i).recordId;
				Log.d(TAG, "Worker id: " + deletionId);
				db.delete(JobWorkers.TABLE_NAME, JobWorkers.COLUMN_NAME_WORKERS_ID + " = ? AND "+ JobWorkers.COLUMN_NAME_ORDERS_ID + " = ?", new String[]{deletionId,this.recordId});
			}
		}
	}
	
	@Override
	public boolean updateRecord(Map<String, String> values) {
		try{
			updateCustomer();
			super.updateRecord(values);
			updateMembers();
			removeDisassociatedMembers();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
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
		/*
		displayIdsToColumns.put(R.id.content_order_date_ordered, Orders.COLUMN_NAME_ORDER_DATE);
		displayIdsToColumns.put(R.id.content_order_target_date, Orders.COLUMN_NAME_FILL_BY_DATE);
		displayIdsToColumns.put(R.id.content_orders_address, Orders.COLUMN_NAME_ADDRESS);
		*/
	}
	
	/*
	
	public void update(ContentValues values) {
		try{
			super.updateRecord(DbConstants.ORDERS_COLUMNS, values);
		}catch(Exception e){
		//	Toast.makeText(NewRecordFormActivity.this, text, duration)
			Log.d(NewRecordFormActivity.TAG, e.getMessage());
		}
	}*/
	
	

	
	
	
}
