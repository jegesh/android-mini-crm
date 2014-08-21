package com.example.learningsqlite;

import java.util.ArrayList;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learningsqlite.ConfirmDeletionDialog.NoticeDialogListener;
import com.example.learningsqlite.ProductsContract.Products;
import com.example.learningsqlite.WorkersContract.Workers;

public class ViewDbActivity extends ListActivity implements NoticeDialogListener {
	private static final String UNAVAILABLE_FEATURE = "unavailable";
	public static final String RECORD_ID = "Record ID";
	static final String DOMAIN = "domain";
	private static final String TAG = "ViewDbActivity";
	public static final String MATCH_STRING_FOR_SEARCH = "searchString";
	
	private ArrayList<String> checkedRecordsById;
	private String[] projection;
	private String tableName;
	private String orderByField;
	private SQLiteOpenHelper currentDBHelper;
	private SQLiteDatabase currentDb;
	private String[] fields;
	private SimpleCursorAdapter mAdapter;
	private Cursor c;
	private String title;
	private int listItemLayout;
	private int[] txtViews;
	private int searchFieldIndex;
	private String matchString;
	private ArrayList<Integer> checkedItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_db);
		checkedItems = new ArrayList<>();
		
	}
	
	@Override
	protected void onResume() {
		String domain = "";
		if(getIntent().hasExtra(MainActivity.DOMAIN)){
			Bundle info = getIntent().getExtras();
			domain = info.getString(MainActivity.DOMAIN);
			DbConstants.currentDomain = domain;
		}else{
			domain = DbConstants.currentDomain;
		}
	//	Log.d(TAG, "In onCreate");
		
		
		setVars(domain);
		if(getIntent().hasExtra(MATCH_STRING_FOR_SEARCH)){
			makeSearchQuery();
		}
		else
			makeGeneralQuery();
		refreshDisplay(this);
//		prepareAutoComplete();
		
		/*
		// Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);
		
		*/
		super.onResume();/*
		if(	currentDb.isDatabaseIntegrityOk() && !currentDb.isDbLockedByCurrentThread() && !currentDb.inTransaction())
				Toast.makeText(this, "Everything's OK", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this	,"Problem!"		, Toast.LENGTH_SHORT).show();
			*/
	}
	
	
	
	private void setVars(String domain){
		switch (domain) {
		case DbConstants.ORDERS:
			fields  = new String[3];
			currentDBHelper = new OrdersDBHelper(this);
	//		currentDb = currentDBHelper.getWritableDatabase();
			fields[0] = OrdersContract.Orders._ID;
			fields[1] = OrdersContract.Orders.COLUMN_NAME_TITLE;
			fields[2] = OrdersContract.Orders.COLUMN_NAME_ORDER_DATE;
			tableName = OrdersContract.Orders.TABLE_NAME;
			orderByField = OrdersContract.Orders.COLUMN_NAME_ORDER_DATE;
			title = getString(R.string.title_orders_main);
			listItemLayout = R.layout.main_screen_listview_item_1;
			txtViews = DbConstants.listItemFields1;
			searchFieldIndex = 1;
			break;
		case DbConstants.CUSTOMERS:
			fields  = new String[4];
			currentDBHelper = new CustomersDBHelper(this);
//			currentDb = currentDBHelper.getWritableDatabase();
			fields[0] = CustomersContract.Customers._ID;
			fields[1] = CustomersContract.Customers.COLUMN_NAME_FIRST_NAME;
			fields[2] = CustomersContract.Customers.COLUMN_NAME_LAST_NAME;
			fields[3] = CustomersContract.Customers.COLUMN_NAME_ADDRESS;
			tableName = CustomersContract.Customers.TABLE_NAME;
			orderByField = CustomersContract.Customers.COLUMN_NAME_LAST_NAME;
			title = getString(R.string.title_customers_main);
			listItemLayout = R.layout.main_screen_listview_item_2;
			txtViews = DbConstants.listItemFields2;
			searchFieldIndex = 2;
			break;
		case DbConstants.WORKERS:
			fields = new String[4];
			currentDBHelper = new WorkersDBHelper(this);
			fields[0] = Workers._ID;
			fields[1] = Workers.COLUMN_NAME_FIRST_NAME;
			fields[2] = Workers.COLUMN_NAME_LAST_NAME;
			fields[3] = Workers.COLUMN_NAME_OCCUPATION;
			tableName = Workers.TABLE_NAME;
			orderByField = Workers.COLUMN_NAME_LAST_NAME;
			title = getString(R.string.title_workers_main);
			listItemLayout = R.layout.main_screen_listview_item_2;
			txtViews = DbConstants.listItemFields2;
			searchFieldIndex = 2;
			break;
		case DbConstants.PRODUCTS:
			fields  = new String[4];
			currentDBHelper = new ProductsDBHelper(this);
			fields[0] = Products._ID;
			fields[1] = Products.COLUMN_NAME_TITLE;
			fields[2] = Products.COLUMN_NAME_SUBTITLE;
			fields[3] = Products.COLUMN_NAME_PRICE_PER_UNIT;
			tableName = Products.TABLE_NAME;
			orderByField = Products.COLUMN_NAME_TITLE;
			title = getString(R.string.title_products_main);
			listItemLayout = R.layout.main_screen_listview_item_1;
			txtViews = DbConstants.listItemFields1;
			searchFieldIndex = 1;
			break;
		
		default:
			fields = new String[1];
			break;
		}
		currentDb = currentDBHelper.getReadableDatabase();
	//	currentDBHelper.close();

	}
	
	@Override
	protected void onPause() {
		currentDb.close();
		if(c!=null && !c.isClosed())
			c.close();
		if(currentDBHelper!=null)
			currentDBHelper.close();
		super.onPause();
	}
	
	private void prepareAutoComplete(){
		String[] searchArray = new String[mAdapter.getCursor().getCount()];
		mAdapter.getCursor().moveToFirst();
		for(int i = 0;i<searchArray.length;i++){
			searchArray[i] = mAdapter.getCursor().getString(searchFieldIndex);
			mAdapter.getCursor().moveToNext();
		}
		mAdapter.getCursor().close();
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,searchArray );
		AutoCompleteTextView auto = (AutoCompleteTextView)findViewById(R.id.search_box);
		auto.setThreshold(1);
		auto.setAdapter(aa);
		auto.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				parent.getAdapter();
				TextView txt = (TextView)view;
				matchString = txt.getText().toString();
				Intent intent = new Intent(view.getContext(), ViewDbActivity.class);
				intent.putExtra(MATCH_STRING_FOR_SEARCH, matchString);
				startActivity(intent);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_db, menu);
		return true;
	}
	
	private void makeGeneralQuery(){
		try {
			c = currentDb.query(tableName, fields, null, null, null, null, orderByField );
			Log.d(TAG, "No of columns: "+Integer.toString(c.getColumnCount()));
			Log.d(TAG, "No of rows: "+Integer.toString(c.getCount()));
	/*		for(int i = 0;i<c.getColumnCount();i++)
				Log.d(TAG, "Col no "+i+ " is "+c.getColumnName(i));*/
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			c=null;
			
		}
	}
	
	private void makeSearchQuery(){
		try {
			c = currentDb.query(tableName, fields, fields[searchFieldIndex]+"=?", new String[]{getIntent().getExtras().getString(MATCH_STRING_FOR_SEARCH)}, null, null, null );
			Log.d(TAG, "No of columns: "+Integer.toString(c.getColumnCount()));
			Log.d(TAG, "No of rows: "+Integer.toString(c.getCount()));
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			c=null;
			
		}
	}
		
	
	private boolean getDbCursor(){
		
		String[] fromColumns = new String[fields.length-1];
		for(int i=0;i<fromColumns.length;i++)
			fromColumns[i] = fields[i+1];
        int[] toViews = new int[fields.length-1];
        for(int i=0;i<fromColumns.length;i++)
			toViews[i] = txtViews[i];
	
		Log.d(TAG, "1: "+ fields[0]+", 2: "+fields[1]+", 3: "+fields[2]);
		
		mAdapter = new SimpleCursorAdapter(this,listItemLayout, c, fromColumns, toViews, 0);
		if(mAdapter.getCount()<1)
			return false;
		return true;
	}
	
	private void renderLayout(){
		if(getDbCursor()){
			Log.d(TAG, "Cursor initialized");
		ListView mainListView = getListView();
		mainListView.setAdapter(mAdapter);
		mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mainListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// turn all selected items back to default color
				for(int i = 0;i<checkedItems.size();i++)
					getListView().getChildAt(checkedItems.get(i)).setBackgroundColor(getResources().getColor(android.R.color.white));
				checkedItems.clear();
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.cab_menu, menu);
		        return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menu_item_archive_record:
					UnavailableFeatureDialog un = new UnavailableFeatureDialog();
					un.show(getFragmentManager(), UNAVAILABLE_FEATURE);
					return true;
					
				case R.id.menu_item_delete_record:
					ConfirmDeletionDialog dialog = new ConfirmDeletionDialog();
					dialog.show(getFragmentManager(), DisplayRecordActivity.DELETION_DIALOG_FRAG_TAG);
					return true;
				default:
					break;
				}
				return false;
				
				
			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,long id, boolean checked) {
				String selected = Integer.toString( getListView().getCheckedItemCount());
				Log.d(TAG, "no of items: "+selected);
				mode.setTitle(selected + " "+ getString(R.string.deletion_menu_title));
				if(checked){
					checkedItems.add(position);
					getListView().getChildAt(position).setBackgroundColor(Color.LTGRAY);
				}
				else{
					getListView().getChildAt(position).setBackgroundColor(getResources().getColor(android.R.color.white));
					checkedItems.remove(checkedItems.indexOf(position));
				}
					
			/*	View clickedItem = null;
				try{
				clickedItem = getListAdapter().getView(position, getCurrentFocus(), getListView());
				}catch(Exception e){
					Log.d(TAG, e.getCause().toString());
				}
				clickedItem.setBackgroundColor(color.holo_green_light);*/
			}
		});
		
		// onclick function for single short click on list item
		mainListView.setOnItemClickListener(new OnItemClickListener() { 

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				c.moveToPosition(position);
				String recId = c.getString(0);
				c.close();
				Intent intent = new Intent(view.getContext(), DisplayRecordActivity.class);
				intent.putExtra(DOMAIN, DbConstants.currentDomain);
				intent.putExtra(RECORD_ID, recId);
				startActivity(intent);
			//	finish();
			}
		});

	        	
		Log.d(TAG, "After...");
		}
	}
	/*
	public void selectRecord(View view){
		CheckBox ch = (CheckBox)view;

	
	}*/
	
	
	private void refreshDisplay(Activity act){
		
		this.setTitle(title);
		Log.d(TAG, "In refresh method");
		checkedRecordsById = new ArrayList<>();
		renderLayout();
		if(!getIntent().hasExtra(MATCH_STRING_FOR_SEARCH) && mAdapter.getCount()>0)
			Toast.makeText(this, getString(R.string.list_view_selecting_hint), Toast.LENGTH_SHORT).show();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			return true;
		case R.id.menu_item_sort_order:
			UnavailableFeatureDialog un = new UnavailableFeatureDialog();
			un.show(getFragmentManager(), UNAVAILABLE_FEATURE);
			return true;
		case R.id.add_record:
			Intent intent = new Intent(this, InputFormActivity.class);
			intent.putExtra(DOMAIN, DbConstants.currentDomain);
			startActivity(intent);
				return true;
		case R.id.search_for_record:
			View search = findViewById(R.id.search_box); 
			if(findViewById(R.id.search_box).getVisibility()==View.INVISIBLE)
				search.setVisibility(View.VISIBLE);
			
			else
				search.setVisibility(View.INVISIBLE);
			getActionBar().setCustomView(search);
			return true;

		default:
			break;
		}		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
	
	@Override
	protected void onStop() {
		//currentDb.close();
		/*
		if(currentDBHelper!=null)
			currentDBHelper.close();
		if(c!=null && !c.isClosed())
			c.close();
		*/
		super.onStop();
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		try {
			// TODO redo following code using new checkedItems data field
			SparseBooleanArray a = (getListView().getCheckedItemPositions());
			for(int i = 0; i < checkedItems.size(); i++){
					mAdapter.getCursor().moveToPosition(checkedItems.get(i));
					String recId = mAdapter.getCursor().getString(0);
				//	currentDb.beginTransaction();
					   try {
						   String[] params = {recId};
							 int rowsDeleted = currentDb.delete(tableName,  fields[0]+" = ?", params);
							 Log.d(TAG,Integer.toString(rowsDeleted));
					//	     currentDb.setTransactionSuccessful();
					   }catch(Exception e){
						   Log.d(TAG, e.getMessage());
						   
					   }
					   finally {
			//		     currentDb.endTransaction();
					   }
					   mAdapter.getCursor().close();
				}	
			
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
		// probably not the right way to do this, but works for now
		Intent intent = new Intent(getBaseContext(), ViewDbActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// do nothing
	}

}
