package net.gesher.minicrm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.gesher.minicrm.AddMemberDialog.AddMemberDialogListener;
import net.gesher.minicrm.DatePickerInputDialog.NoticeDatePickerDialogListener;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import database_files.CustomersContract;
import database_files.CustomersContract.Customers;
import database_files.CustomersRecord;
import database_files.DatabaseRecord;
import database_files.DbConstants;
import database_files.OrdersContract;
import database_files.OrdersRecord;
import database_files.ProductsContract.Products;
import database_files.ProductsRecord;
import database_files.WorkersContract.Workers;
import database_files.WorkersRecord;


public class NewRecordFormActivity extends Activity implements NoticeDatePickerDialogListener, AddMemberDialogListener {
	private static final String KEY_ADDED_MEMBER_DOMAIN = "added member domain";
	private static final String RECORD_MEMBER_REQUESTED = "record member requested";
	private static final String KEY_GROUP_NAME = "group name";
	private static final String NEW_RECORD_ID = "New record id";
	private static final String REQUESTING_RECORD_ID = "requesting id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MEMBER_DOMAIN = "domain";
	public static final String KEY_MEMBER_ID = "member id";
	public static final String KEY_SUBTITLE = "subtitle";
	public static final String KEY_AMOUNT = "amount";
	public static final String TAG = "NewRecordFormActivity";
	public static final String CUSTOMER_ID = "customer ID";
	private DatabaseRecord record;
	private String TABLE_NAME;
	private String recId;
	private int layoutId;
	AddMemberDialog dialog;
	private int currentDateFieldId;
	public String addedComponentDomain;
	private ExpandableListView membersList;
	private ExpandableListAdapter eListAdapter;
	private LinkedList<HashMap<String, String>> productsList;
	private LinkedList<HashMap<String, String>> workersList;
	private LinkedList<HashMap<String, String>> groupsList;
	private LinkedList<List<HashMap<String, String>>> dataList;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
				
		switch (getIntent().getExtras().getString(ViewDbActivity.DOMAIN)) {
		case DbConstants.ORDERS:
			TABLE_NAME = OrdersContract.Orders.TABLE_NAME;
			layoutId = R.layout.activity_orders_new_record;
			if(getIntent().hasExtra(ViewDbActivity.RECORD_ID)){
				recId = getIntent().getExtras().getString(ViewDbActivity.RECORD_ID);
				if(((MyApp)getApplication()).tempOrdersRecord != null)
					record = ((MyApp)getApplication()).tempOrdersRecord;
				else
					record = new OrdersRecord(recId, this);
				setTitle(R.string.title_activity_order_edit);
			}else{
				setTitle(R.string.title_activity_orders_new_record);
				record = new OrdersRecord(this);
			}
			productsList = new LinkedList<HashMap<String,String>>();
			workersList = new LinkedList<HashMap<String,String>>();
			dataList = new LinkedList<>();
			break;
		case DbConstants.CUSTOMERS:
			TABLE_NAME = CustomersContract.Customers.TABLE_NAME;
			layoutId = R.layout.layout_customer_input;

			if(getIntent().hasExtra(ViewDbActivity.RECORD_ID)){
				setTitle(R.string.title_activity_customer_edit);
				recId = getIntent().getExtras().getString(ViewDbActivity.RECORD_ID);
				record = new CustomersRecord(recId, this);
			}else{
				setTitle(R.string.title_activity_customers_new_record_form);
				record = new CustomersRecord(this);
			}
			break;
		case DbConstants.WORKERS:
			TABLE_NAME = Workers.TABLE_NAME;
			layoutId = R.layout.workers_input_form;
			if(getIntent().hasExtra(ViewDbActivity.RECORD_ID)){
				recId = getIntent().getExtras().getString(ViewDbActivity.RECORD_ID);
				record = new WorkersRecord(recId, this);
				setTitle(R.string.title_workers_edit);
			}else{
				setTitle(R.string.title_workers_new_record);
				record = new WorkersRecord(this);
			}
			break;
		case DbConstants.PRODUCTS:
			TABLE_NAME = Products.TABLE_NAME;
			layoutId = R.layout.products_input_form;
			if(getIntent().hasExtra(ViewDbActivity.RECORD_ID)){
				recId = getIntent().getExtras().getString(ViewDbActivity.RECORD_ID);
				record = new ProductsRecord(recId, this);
				setTitle(R.string.title_products_edit);
			}else{
				setTitle(R.string.title_products_new_record);
				record = new ProductsRecord(this);
			}
			break;
		default:
			break;
		}
		
		setContentView(layoutId);
		
		membersList = (ExpandableListView) findViewById(R.id.members_list);
		if(membersList != null){
//			membersList.setOnChildClickListener(new OnChildClickListener() {
//				
//				@Override
//				public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//					String dom = ((TextView)v.findViewById(R.id.member_domain)).getText().toString();
//					String recId = ((TextView)v.findViewById(R.id.member_record_id)).getText().toString();
//					Intent intent = new Intent(NewRecordFormActivity.this, DisplayRecordActivity.class);
//					String d = "";
//					if(dom.equals(getString(R.string.title_customers_main))){
//						d = DbConstants.CUSTOMERS;
//					}
//					if(dom.equals(getString(R.string.title_products_main))){
//						d = DbConstants.PRODUCTS;
//					}
//					if(dom.equals(getString(R.string.title_workers_main))){
//						d = DbConstants.WORKERS;
//					}
//					return false;
//				}
//			});
		}
		populateFields();
		if(TABLE_NAME.equals(Products.TABLE_NAME) ){
			Spinner spinner = (Spinner)findViewById(R.id.products_input_sale_unit_spinner);
			populatePriceUnitSpinner(spinner, record, this);
			if(findViewById(R.id.products_amount_container) == null){
				Toast.makeText(this, "amount is null", Toast.LENGTH_SHORT).show();
			}
			if(getIntent().hasExtra(RECORD_MEMBER_REQUESTED))
				findViewById(R.id.products_amount_container).setVisibility(View.VISIBLE);
		}
		if(savedInstanceState != null){
			addedComponentDomain = savedInstanceState.getString(KEY_ADDED_MEMBER_DOMAIN);
		}

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_ADDED_MEMBER_DOMAIN, addedComponentDomain);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onResume() {
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if(((MyApp)getApplication()).newMember != null){
			// create new member record and add to parent record
			OrdersRecord or = (OrdersRecord) record;
			DatabaseRecord member = ((MyApp)getApplication()).newMember;
			if(member instanceof CustomersRecord){
				addMemberToParent(or, member.recordId, "");
			//	or.customer = (CustomersRecord) member;
			}
			if(member instanceof ProductsRecord){
				addMemberToParent(or, member.recordId, ((ProductsRecord)member).amount);
//				or.products.add((ProductsRecord) member);
			}
			if(member instanceof WorkersRecord){
				addMemberToParent(or, member.recordId, "");
//				or.workers.add((WorkersRecord) member);
			}
			((MyApp)getApplication()).newMember = null;
			populateFields();
		}
		super.onResume();
	}
	
	/*
	private ImageView getRemoveButton(){

		ImageView removeButton = new ImageView(this);
		LinearLayout.LayoutParams removalLayout = new LinearLayout.LayoutParams(60, 60);
		removalLayout.setMargins(0, 0, 12, 0);
		removalLayout.gravity = Gravity.RIGHT;
		removeButton.setLayoutParams(removalLayout);
		removeButton.setClickable(true);
		removeButton.setImageResource(R.drawable.red_x);
		return removeButton;
	}
	*/
	
	public void populateFields(){
		
	//	if(recId!=null){ // this activity is functioning as an 'edit record' form
			switch (TABLE_NAME) {
			case OrdersContract.Orders.TABLE_NAME:
				OrdersRecord ordersRec = (OrdersRecord)record;
				ordersRec.populateInputFields(this);
				if(groupsList == null)
					groupsList = new LinkedList<>();
				else
					groupsList.clear();
				// TODO is there a more efficient way to do this, rather than clearing and re-populating the lists each time?
				productsList.clear();
				workersList.clear();
				dataList.clear(); 
				
				// special code for handling special nested nature of orders records
				if(ordersRec.customer != null){
					HashMap<String, String> custGroup = new HashMap<>();
					custGroup.put(KEY_GROUP_NAME, getString(R.string.title_customers_main));
					groupsList.add(custGroup);
					HashMap<String, String> custMap = new HashMap<>();
					custMap.put(KEY_TITLE, ordersRec.customer.valueMap.get(Customers.COLUMN_NAME_FIRST_NAME) +
							" " + ordersRec.customer.valueMap.get(Customers.COLUMN_NAME_LAST_NAME));
					custMap.put(KEY_SUBTITLE, ordersRec.customer.valueMap.get(Customers.COLUMN_NAME_ADDRESS));
					custMap.put(KEY_AMOUNT, null);
					custMap.put(KEY_MEMBER_DOMAIN, getString(R.string.title_customers_main));
					custMap.put(KEY_MEMBER_ID, ordersRec.customer.recordId);
					LinkedList<HashMap<String, String>> custList = new LinkedList<>();
					custList.add(custMap);
					dataList.add(custList);
				}
				
				if(ordersRec.products.size()>0){
					HashMap<String, String> prodGroup = new HashMap<>();
					prodGroup.put(KEY_GROUP_NAME, getString(R.string.title_products_main));
					groupsList.add(prodGroup);
					for(int i=0;i<ordersRec.products.size();i++){
						HashMap<String, String> prod = new HashMap<String, String>();
						prod.put(KEY_TITLE, ordersRec.products.get(i).valueMap.get(Products.COLUMN_NAME_TITLE));
						prod.put(KEY_SUBTITLE, ordersRec.products.get(i).valueMap.get(Products.COLUMN_NAME_SUBTITLE));
						prod.put(KEY_AMOUNT, ordersRec.products.get(i).amount);
						prod.put(KEY_MEMBER_DOMAIN, getString(R.string.title_products_main));
						prod.put(KEY_MEMBER_ID, ordersRec.products.get(i).recordId);
						productsList.add(prod);
					}
					dataList.add(productsList);
				}
				if(ordersRec.workers.size()>0){
					HashMap<String, String> workersGroup = new HashMap<>();
					workersGroup.put(KEY_GROUP_NAME, getString(R.string.title_workers_main));
					groupsList.add(workersGroup);
					for(int i = 0;i<ordersRec.workers.size();i++){
						HashMap<String, String> w = new HashMap<String, String>();
						w.put(KEY_TITLE, ordersRec.workers.get(i).valueMap.get(Workers.COLUMN_NAME_FIRST_NAME)+" "
								+ordersRec.workers.get(i).valueMap.get(Workers.COLUMN_NAME_LAST_NAME));
						w.put(KEY_SUBTITLE, ordersRec.workers.get(i).valueMap.get(Workers.COLUMN_NAME_OCCUPATION));
						w.put(KEY_AMOUNT, null);
						w.put(KEY_MEMBER_DOMAIN, getString(R.string.title_workers_main));
						w.put(KEY_MEMBER_ID, ordersRec.workers.get(i).recordId);
						workersList.add(w);
					}
					dataList.add(workersList);
				}
				// TODO is it necessary to release the previous adapter from memory before reassignment?
				String[] fromFields = new String[]{KEY_TITLE,KEY_SUBTITLE,KEY_AMOUNT,KEY_MEMBER_DOMAIN,KEY_MEMBER_ID};
				int[] toFields = new int[]{R.id.title,R.id.subtitle,R.id.member_amount,R.id.member_domain,R.id.member_record_id};
				eListAdapter = new SimpleExpandableListAdapter(this, groupsList, R.layout.orders_display_members_header, new String[]{KEY_GROUP_NAME}, 
						new int[]{R.id.field_title1}, dataList, R.layout.orders_input_member_list_item, fromFields, toFields);
				membersList.setAdapter(eListAdapter);
				break;
			case "Bids":
				// ditto
				break;

			default:
				record.populateInputFields(this);
				break;
			}
	}

	public void addSaleUnit(View v){
		Spinner sp = (Spinner)findViewById(R.id.products_input_sale_unit_spinner);
		EditText saleUnitInput = (EditText)findViewById(R.id.products_input_new_sale_unit);
		if(sp.getVisibility()==View.VISIBLE){
			sp.setVisibility(View.GONE);
			sp.setSelected(false);
			saleUnitInput.setVisibility(View.VISIBLE);
		}else{
			saleUnitInput.setVisibility(View.GONE);
			sp.setVisibility(View.VISIBLE);
			sp.setSelected(true);
			sp.setSelection(0);
		}
	}
	
	private Button getToggleButton() {
		Button toggler = new Button(this);
		LinearLayout.LayoutParams togglerLayout = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	//	togglerLayout.gravity = Gravity.RIGHT;
		toggler.setLayoutParams(togglerLayout);
		toggler.setText(R.string.btn_show_orders_member);
		toggler.setTextSize(22);
		return toggler;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.orders_new_record_form, menu);
		if(getIntent().getExtras().getString(ViewDbActivity.DOMAIN).equals(DbConstants.ORDERS)){
			return true;	
		}
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add_customer) {
			addedComponentDomain = getString(R.string.domain_customers);
			dialog = new AddMemberDialog();
			dialog.show(getFragmentManager(), "dialog " + addedComponentDomain);
			return true;
		}
		if (id == R.id.action_add_product) {
			addedComponentDomain = getString(R.string.domain_products);
			dialog = new AddMemberDialog();
			dialog.show(getFragmentManager(), "dialog " + addedComponentDomain);
			return true;
		}
		if (id == R.id.action_add_worker) {
			addedComponentDomain = getString(R.string.domain_workers);
			dialog = new AddMemberDialog();
			dialog.show(getFragmentManager(), "dialog " + addedComponentDomain);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	public void addRecordMember(View v){
		//TODO turn it into a 'fragment' when adding cust/prod/worker from within other domain
		addedComponentDomain = (String) v.getTag();
		dialog = new AddMemberDialog();
		dialog.show(getFragmentManager(), "dialog " + addedComponentDomain);
		
	}
	*/
	
	// 'save' button click method -- for saving inputed data to database as a new record (or updating) 
	public void saveRecordToDb(View view) throws Exception {
		if(record instanceof OrdersRecord)
			record = (OrdersRecord)record; // necessary so that insertNewRecord() will be called from the override and not from the parent method
		Log.d(TAG, "table: "+TABLE_NAME);
		if(record.recordId==null){
			
			record.saveDataToObject(this);
			
			if(record.insertNewRecord()){ // if values were typed into fields
				Toast.makeText(this, getString(R.string.record_saved_success_message), Toast.LENGTH_SHORT).show();
	/*			if(getIntent().hasExtra(REQUESTING_RECORD_ID)){
					Intent intent = new Intent(this, NewRecordFormActivity.class);
					// send back id of record that the request came from 
					intent.putExtra(ViewDbActivity.RECORD_ID, getIntent().getStringExtra(REQUESTING_RECORD_ID));
					// send id of new 'child' record that was just created
					intent.putExtra(NEW_RECORD_ID, record.recordId);
					startActivity(intent);
					
				}
				else{*/
					if(record.db.isOpen())
						record.db.close();
					if(record.dbHelper!=null)
						record.dbHelper.close();
					if(getIntent().hasExtra(RECORD_MEMBER_REQUESTED))
						((MyApp)getApplication()).newMember = record;
					else
						((MyApp)getApplication()).newMember = null;
					finish();
			//	}
			}else{
				Toast.makeText(this, getString(R.string.record_saved_failure_message), Toast.LENGTH_SHORT).show();
			//	Toast.makeText(this, "Empty record", Toast.LENGTH_SHORT).show();
				finish();
			}

		}else{ // if record already exists in db
			record.saveDataToObject(this);
			if(record.updateRecord(record.valueMap)){
				Toast.makeText(this, getString(R.string.record_update_success_message), Toast.LENGTH_SHORT).show();	
			}else{
				Toast.makeText(this, getString(R.string.record_update_failure_message), Toast.LENGTH_SHORT).show();	
			}
			finish();
		}
	}
	
	protected static void populatePriceUnitSpinner(Spinner spinner, DatabaseRecord record, Activity activity){
		Cursor c = record.db.query(Products.TABLE_NAME, new String[]{Products.COLUMN_NAME_SELL_BY_UNIT,Products._ID}, 
				Products.COLUMN_NAME_SELL_BY_UNIT+" IS NOT NULL", null, Products.COLUMN_NAME_SELL_BY_UNIT, null, null); // new String[]{Products.COLUMN_NAME_SELL_BY_UNIT} 
		SimpleCursorAdapter priceUnitAdapter = new SimpleCursorAdapter(activity, android.R.layout.simple_spinner_item, c, new String[]{Products.COLUMN_NAME_SELL_BY_UNIT}, new int[]{android.R.id.text1}, 0);
		priceUnitAdapter.setDropDownViewResource(R.layout.add_member_spinner_item);
		spinner.setAdapter(priceUnitAdapter);
	}
	
	
	public void cancel(View view){
		((MyApp)getApplication()).newMember = null;
		finish();
	}
	
	@Override
	protected void onDestroy() {
		((MyApp)getApplication()).tempOrdersRecord = null;
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		if(record!=null && record.db.isOpen())
			record.db.close();
		if(record instanceof OrdersRecord)
			((MyApp)getApplication()).tempOrdersRecord = (OrdersRecord) record;
//		if(record!=null)
//			record.dbHelper.close();
		super.onStop();
	}
	
	public void openDatePicker(View v){
		if(v.getId()==R.id.orders_order_date_btn)
			currentDateFieldId = R.id.orders_new_record_order_date_field;
		else if(v.getId()==R.id.orders_target_date_btn)
			currentDateFieldId = R.id.orders_new_record_fill_by_date_field;
		DatePickerInputDialog dp = new DatePickerInputDialog();
		dp.show(getFragmentManager(), DbConstants.DATE_PICKER_DIALOG);
		
	}

	@Override
	public void onDateDialogPositiveClick(DialogFragment dialog) {
		int[] dateVals = DbConstants.lastPickedDate;
		EditText dateField = (EditText)findViewById(currentDateFieldId);
		dateField.setText(Integer.toString(dateVals[0])+"."+Integer.toString(dateVals[1])+"."+Integer.toString(dateVals[2]));
	}

	@Override
	public void onDateDialogNegativeClick(DialogFragment dialog) {
		//closes dialog, no date value saved
		
	}
	
	@Override
	public void onAddMemberAcceptClickListener() {
		OrdersRecord ordRec = (OrdersRecord)record;
		addMemberToParent(ordRec, dialog.addedMemberId, dialog.productAmount);
		
	}
	
	public void removeMember(View v){ // member list item 'x' onClick method
		String dom = ((TextView) ((ViewGroup)v.getParent()).findViewById(R.id.member_domain)).getText().toString();
		if(dom.equals(getString(R.string.title_customers_main))){
			((OrdersRecord)record).customer = null;
			populateFields();
		}
		if(dom.equals(getString(R.string.title_products_main))){
			String prodId = ((TextView) ((ViewGroup)v.getParent()).findViewById(R.id.member_record_id)).getText().toString();
			Log.d(TAG, "Removing product with id: "+prodId);
			OrdersRecord or = (OrdersRecord)record;
			for(int i=0;i<or.products.size();i++){
				ProductsRecord p = or.products.get(i);
				if(p.recordId.equals(prodId)){
					or.removedProducts.add(p);
					or.products.remove(i);
					break;
				}
			}
			populateFields();
		}
		if(dom.equals(getString(R.string.title_workers_main))){
			String wId = ((TextView) ((ViewGroup)v.getParent()).findViewById(R.id.member_record_id)).getText().toString();
			OrdersRecord or = (OrdersRecord)record;
			for(int i=0;i<or.workers.size();i++){
				WorkersRecord w = or.workers.get(i);
				if(w.recordId.equals(wId)){
					or.removedWorkers.add(w);
					or.workers.remove(i);
					break;
				}
			}
			populateFields();
		}
	}
	
	private void addMemberToParent(OrdersRecord parent, String memberId, String amount){
		
		switch (addedComponentDomain) {
		case "customers":
			CustomersRecord cust = new CustomersRecord(memberId, this);
			parent.customer = cust;
			Toast.makeText(this, getString(R.string.message_customer_added), Toast.LENGTH_SHORT).show();
			break;
		case "products":
			ProductsRecord prod = new ProductsRecord(memberId,this);
			prod.newlyAdded = true;
			prod.amount = amount;
			parent.products.add(prod);
			Toast.makeText(this, getString(R.string.message_product_added), Toast.LENGTH_SHORT).show();
			break;
		case "workers":
			WorkersRecord work = new WorkersRecord(memberId, this);
			work.newlyAdded = true;
			parent.workers.add(work);
			Toast.makeText(this, getString(R.string.message_worker_added), Toast.LENGTH_SHORT).show();
			break;
		}
//		ExpandableListAdapter ela = null;
//		membersList.setAdapter(ela);
		populateFields();
	}

	@Override
	public void onAddMemberNewClickListener() {
		Intent intent = new Intent(this, NewRecordFormActivity.class);
		switch (addedComponentDomain) {
		case "customers":
			intent.putExtra(ViewDbActivity.DOMAIN, DbConstants.CUSTOMERS);
			break;
		case "products":
			intent.putExtra(ViewDbActivity.DOMAIN, DbConstants.PRODUCTS);
			break;
		case "workers":
			intent.putExtra(ViewDbActivity.DOMAIN, DbConstants.WORKERS);
			break;
		}
		intent.putExtra(RECORD_MEMBER_REQUESTED, true);
		startActivity(intent);
	}

}
