package net.gesher.minicrm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.gesher.minicrm.ConfirmDeletionDialog.NoticeDialogListener;
import net.gesher.minicrm.PhonePickerFragment.PhoneDialogListener;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import database_files.CustomersContract.Customers;
import database_files.CustomersRecord;
import database_files.DatabaseRecord;
import database_files.DbConstants;
import database_files.OrdersRecord;
import database_files.ProductsRecord;
import database_files.WorkersRecord;
import database_files.JobsProductsContract.JobsProducts;
import database_files.ProductsContract.Products;
import database_files.WorkersContract.Workers;


public class DisplayRecordActivity extends Activity implements NoticeDialogListener,PhoneDialogListener {
	private static final String KEY_CHILD_SUBTITLE2 = "subtitle2";
	private static final String KEY_CHILD_SUBTITLE1 = "subtitle1";
	private static final String KEY_CHILD_TITLE2 = "title2";
	private static final String KEY_CHILD_TITLE1 = "title1";
	private static final String KEY_MEMBER_TITLE = "Title";
	private static final String KEY_MEMBER_COUNT = "Count";
	public static final String DELETION_DIALOG_FRAG_TAG = "deletion dialog";
	public static final String TAG = "DisplayRecord";
	
	//private SQLiteDatabase db;
	public DatabaseRecord currentRecord;
	public int[] phoneFields;
	public String[] phoneLabels;
	int selectedPhoneIndex;
	private int[] contentIds;
	int emailAddressId;
	private String phoneString;
	//private String tableName;
	//private String[] fields;
	//private Cursor c;
	private LinearLayout custFrame;
	private LinearLayout ordersFormContainer;
	private int workersHeaderId;
	private int productsHeaderId;
	private int workersListItemLayout;
	private int productsListItemLayout;
	int groupHeaderHeight;
	private ExpandableListView lView;
	private boolean firstTime;
	protected int initialListHeight;
	protected OrdersDisplayListAdapter mListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		workersListItemLayout = R.layout.main_screen_listview_item_2;
		productsListItemLayout = R.layout.main_screen_listview_item_1;
		
	}
	
	@Override
	protected void onResume() {
	//	Log.d(TAG, getIntent().getStringExtra(ViewDbActivity.RECORD_ID));
		switch (getIntent().getStringExtra(ViewDbActivity.DOMAIN)) {
		case DbConstants.ORDERS:
			currentRecord = new OrdersRecord(getIntent().getStringExtra(ViewDbActivity.RECORD_ID), this);
			phoneFields = new int[3];
			phoneLabels = new String[3];
			emailAddressId = R.id.customers_email;
			break;
		case DbConstants.CUSTOMERS:
			phoneFields = new int[3];
			phoneLabels = new String[3];
			currentRecord = new CustomersRecord(getIntent().getStringExtra(ViewDbActivity.RECORD_ID), this);
			emailAddressId = R.id.customers_email;
			break;
		case DbConstants.WORKERS:
			phoneFields = new int[2];
			phoneLabels = new String[2];
			currentRecord = new WorkersRecord(getIntent().getStringExtra(ViewDbActivity.RECORD_ID), this);
			emailAddressId = R.id.workers_email;
			break;
		case DbConstants.PRODUCTS:
			currentRecord = new ProductsRecord(getIntent().getStringExtra(ViewDbActivity.RECORD_ID), this);
			break;
		default:
			break;
		}
	//	Log.d(TAG, "layout id: "+currentRecord.layoutId);
		setContentView(currentRecord.displayLayoutId);
		if(currentRecord instanceof OrdersRecord){
			lView = (ExpandableListView)findViewById(R.id.orders_display_members_list);
			
	//		lView.setOnGroupExpandListener(getExpandListener());
	//		lView.setOnGroupCollapseListener(getCollapseListener());
	//		firstTime = false;
	/*		lView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				
				@Override
				public void onGlobalLayout() {
					if(!firstTime){
					groupHeaderHeight = lView.getHeight() + 2;
					setInitialListHeight(lView);
					firstTime = true;
					}
				}
			});
			*/
		}
		this.setTitle(getString(currentRecord.titleId));
		XMLLayoutParser par = new XMLLayoutParser(this);
		try {
			contentIds = par.getElementIds(currentRecord.displayLayoutId, XMLLayoutParser.TEXT_VIEW);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fillFields();
		super.onResume();
	//	Log.d(TAG, "Height of list: "+lView.getHeight());
	}
	
	public void editRecord(String requestedDomain, String recordId){
		Intent intent = new Intent(this, InputFormActivity.class);
		intent.putExtra(ViewDbActivity.DOMAIN, requestedDomain);
		intent.putExtra(ViewDbActivity.RECORD_ID, recordId);
		startActivity(intent);
	}
	
	public void editCustomer(View v){
		editRecord(DbConstants.CUSTOMERS, ((OrdersRecord)currentRecord).customer.recordId);
	}
	
	private void fillFields(View context, DatabaseRecord memberRec){
		Integer[] displayIds = new Integer[memberRec.displayIdsToColumns.size()];
		memberRec.displayIdsToColumns.keySet().toArray(displayIds);
		for(int i = 0; i<memberRec.displayIdsToColumns.size();i++){
			TextView txtView = (TextView)context.findViewById(displayIds[i]);
			if(txtView != null)
				txtView.setText(memberRec.valueMap.get(memberRec.displayIdsToColumns.get(displayIds[i])));
		}
	}
		
	private void fillFields(){
		for(int i = 0; i<contentIds.length;i++){
			TextView txtView = (TextView)findViewById(contentIds[i]);
			if(txtView != null)
				txtView.setText(currentRecord.valueMap.get(currentRecord.displayIdsToColumns.get(contentIds[i])));
		}
		LinearLayout container = (LinearLayout) findViewById(R.id.orders_display_container);
		if(currentRecord instanceof OrdersRecord){
			
			OrdersRecord oRecord = (OrdersRecord)currentRecord;
//			Toast.makeText(this, "Cust name: "+oRecord.customer.valueMap.get(Customers.COLUMN_NAME_FIRST_NAME), Toast.LENGTH_SHORT).show();
		//	ExpandableListView lView = (ExpandableListView)findViewById(R.id.orders_display_members_list);
			ArrayList<HashMap<String,String>> headerMapList = new ArrayList<>();
			ArrayList<ArrayList<HashMap<String, String>>> childDataList = new ArrayList<>();
			
			HashMap<String, String> orderDetailsMap = new HashMap<>();
			orderDetailsMap.put(KEY_MEMBER_TITLE, getString(R.string.label_order_details));
			orderDetailsMap.put(KEY_MEMBER_COUNT, null);
			headerMapList.add(orderDetailsMap);
			childDataList.add(getMemberList(DbConstants.ORDERS));
			
			// code to handle customer info
			if(oRecord.customer!=null){
				HashMap<String, String> custHeaderMap = new HashMap<>();
				custHeaderMap.put(KEY_MEMBER_TITLE, getString(R.string.label_customer_info));
				custHeaderMap.put(KEY_MEMBER_COUNT, null);
				headerMapList.add(custHeaderMap);
				
				// needs to be fixed to display all customer info
				childDataList.add(getMemberList(DbConstants.CUSTOMERS));
				
				/*
				custFrame = (LinearLayout)findViewById(R.id.orders_customer_fragment_container);
				getLayoutInflater().inflate(R.layout.customer_display, custFrame);
				LinearLayout custHeader = new LinearLayout(this);
				custHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				custHeader.setOrientation(LinearLayout.HORIZONTAL);
				TextView frameTitle = new TextView(this);
				LinearLayout.LayoutParams titleLp =new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
				titleLp.weight = 1;
				frameTitle.setLayoutParams(titleLp);
				frameTitle.setText(R.string.label_customer_info);
				frameTitle.setTextSize(24);
				frameTitle.setTextColor(Color.BLUE);
				int addinIndex = container.indexOfChild(custFrame);
				custHeader.addView(frameTitle);
				Button toggler = getToggleButton();
				toggler.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(custFrame.getVisibility()==View.VISIBLE){
							custFrame.setVisibility(View.GONE);
							((Button)v).setText(R.string.btn_show_orders_member);
						}else{
							custFrame.setVisibility(View.VISIBLE);
							((Button)v).setText(R.string.btn_hide_orders_member);
						}
						
					}
				});
				custHeader.addView(toggler);
				container.addView(custHeader, addinIndex);
				fillFields(custFrame, oRecord.customer);
				findViewById(R.id.orders_customer_details_divider).setVisibility(View.VISIBLE);
				custFrame.setVisibility(View.GONE);
				*/
			}
			
			// code to handle display of products
			if(oRecord.products.size()>0){
				HashMap<String, String> prodHeaderMap = new HashMap<>();
				prodHeaderMap.put(KEY_MEMBER_TITLE, getString(R.string.label_product_info));
				prodHeaderMap.put(KEY_MEMBER_COUNT, Integer.toString(oRecord.products.size()));
				headerMapList.add(prodHeaderMap);
				
				childDataList.add(getMemberList(DbConstants.PRODUCTS));
			}
			
			// code to handle display of workers on job
			if(oRecord.workers.size()>0){
				HashMap<String, String> workersHeaderMap = new HashMap<>();
				workersHeaderMap.put(KEY_MEMBER_TITLE, getString(R.string.label_workers_info));
				workersHeaderMap.put(KEY_MEMBER_COUNT, Integer.toString(oRecord.workers.size()));
				headerMapList.add(workersHeaderMap);
				
				childDataList.add(getMemberList(DbConstants.WORKERS));
				/*
				LinearLayout workersSuperFrame = (LinearLayout)findViewById(R.id.orders_workers_fragment_container);
				LinearLayout workersHeader = getMemberGroupHeader(workersSuperFrame, R.string.label_workers_info);
				
				container = (LinearLayout)findViewById(R.id.orders_display_container);
				container.addView(workersHeader, container.indexOfChild(workersSuperFrame));
				String[] cols = {Workers.COLUMN_NAME_FIRST_NAME,Workers.COLUMN_NAME_LAST_NAME,Workers.COLUMN_NAME_OCCUPATION};
				workersSuperFrame.addView(getMemberList(oRecord.workers, workersListItemLayout,cols,DbConstants.listItemFields2));
				workersSuperFrame.setVisibility(View.GONE);
				findViewById(R.id.orders_workers_details_divider).setVisibility(View.VISIBLE); */
			}
			int[] toFields = {R.id.field_title1,R.id.field_title2};
			String[] fromKeys = {KEY_MEMBER_TITLE,KEY_MEMBER_COUNT};
			String[] fromChildrenKeys = {KEY_CHILD_TITLE1,KEY_CHILD_TITLE2,KEY_CHILD_SUBTITLE1,KEY_CHILD_SUBTITLE2};
			int[] toChildFields = {R.id.field_title1,R.id.field_title2,R.id.field_subtitle1,R.id.field_subtitle2};
			OrdersDisplayListAdapter adapter = new OrdersDisplayListAdapter(this, headerMapList, fromKeys, toFields, 
					childDataList, R.layout.listview_item_3, fromChildrenKeys, toChildFields, oRecord.displayIdsToColumns,
					oRecord.customer.displayIdsToColumns);
			mListAdapter = adapter;
			lView.setAdapter(adapter);
			lView.setDividerHeight(3);
			lView.expandGroup(0);
	//		lView.setFocusable(false);
		}
	}
	
	private void setInitialListHeight(ExpandableListView listView){
		LayoutParams lp = listView.getLayoutParams();
		int groups = listView.getExpandableListAdapter().getGroupCount();
		Log.d(TAG, "No of groups: "+groups+", Height of list: "+ listView.getHeight());
		lp.height = groupHeaderHeight * groups;
		listView.setLayoutParams(lp);
		initialListHeight = lp.height;
//		listView.refreshDrawableState(); // don't know if this is necessary
	}
	
	private OnGroupCollapseListener getCollapseListener(){
		OnGroupCollapseListener listener = new OnGroupCollapseListener() {
			
			@Override
			public void onGroupCollapse(int groupPosition) {
				LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) lView.getLayoutParams();
				int collapsedChildren = mListAdapter.getChildrenCount(groupPosition);
			    int itemHeight = mListAdapter.getChildView(groupPosition, 0, false, null, null).getHeight();
				param.height = initialListHeight - (collapsedChildren * 290); //TODO find height dynamically
			    lView.setLayoutParams(param);
				initialListHeight = param.height;
			}
		};
		return listener;
	}
	
	private OnGroupExpandListener getExpandListener(){
		OnGroupExpandListener listener = new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
	//			ExpandableListView expListView = (ExpandableListView)findViewById(R.id.orders_display_members_list);
	//			ScrollView scrollView = (ScrollView)findViewById(R.id.orders_display_scroll_container);
				LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) lView.getLayoutParams();
				int expandedChildren = mListAdapter.getChildrenCount(groupPosition);
			    int itemHeight = mListAdapter.getChildView(groupPosition, 0, false, null, null).getHeight();
				param.height = initialListHeight + (expandedChildren * 290); //TODO find height dynamically
			    lView.setLayoutParams(param);
	//		    lView.refreshDrawableState();
	//		    scrollView.refreshDrawableState();
				initialListHeight = param.height;
			}
		};
		return listener;
	}

	// returns a disappeared list view containing all the member records for a given type -- NOT
	//NOTE: constructing the adapter may take some time -- consider doing asynchronously -- NOT
	private ArrayList<HashMap<String, String>> getMemberList(String memberType){
//		ListView lView = new ListView(this);
		
		// this block necessary to allow scrolling of listview that's a descendant of a scrollView element
		// it has been commented that this only works if all list items are the same height
		// if problems are encountered, see here: http://stackoverflow.com/questions/6210895/listview-inside-scrollview-is-not-scrolling-on-android/14577399#14577399
	/*
		lView.setOnTouchListener(new ListView.OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            int action = event.getAction();
	            switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                // Disallow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(true);
	                break;

	            case MotionEvent.ACTION_UP:
	                // Allow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(false);
	                break;
	            }

	            // Handle ListView touch events.
	            v.onTouchEvent(event);
	            return true;
	        }
	    });
		
		LayoutParams lvLayparams = new LayoutParams(LayoutParams.MATCH_PARENT, 600);
		lView.setLayoutParams(lvLayparams);
		*/
		ArrayList<HashMap<String, String>> memberList = new ArrayList<>();
		OrdersRecord order = (OrdersRecord)currentRecord;
		
		// construct list of value hashes for display in expandable list
		// key is meaningless, value is value that will be displayed therein
		switch (memberType) {
		case DbConstants.ORDERS:
			memberList.add((HashMap<String, String>) order.valueMap);
			break;
		case DbConstants.PRODUCTS:
			for(ProductsRecord rec:order.products){
				HashMap<String, String> childData = new HashMap<>();
				childData.put(KEY_CHILD_TITLE1, rec.valueMap.get(Products.COLUMN_NAME_TITLE));
				childData.put(KEY_CHILD_TITLE2, rec.valueMap.get(Products.COLUMN_NAME_SUBTITLE));
				childData.put(KEY_CHILD_SUBTITLE1, rec.valueMap.get(Products.COLUMN_NAME_PRICE_PER_UNIT));
				childData.put(KEY_CHILD_SUBTITLE2, rec.valueMap.get(JobsProducts.COLUMN_NAME_AMOUNT));
				memberList.add(childData);
			}
			break;
		case DbConstants.WORKERS:
			for(WorkersRecord rec:order.workers){
				HashMap<String, String> childData = new HashMap<>();
				childData.put(KEY_CHILD_TITLE1, rec.valueMap.get(Workers.COLUMN_NAME_FIRST_NAME));
				childData.put(KEY_CHILD_TITLE2, rec.valueMap.get(Workers.COLUMN_NAME_LAST_NAME));
				childData.put(KEY_CHILD_SUBTITLE1, rec.valueMap.get(Workers.COLUMN_NAME_OCCUPATION));
				childData.put(KEY_CHILD_SUBTITLE2, null);
				memberList.add(childData);
			}
			break;
		case DbConstants.CUSTOMERS:
			/*
			HashMap<String, String> childData = new HashMap<>();
			childData.put(KEY_CHILD_TITLE1, order.customer.valueMap.get(Customers.COLUMN_NAME_FIRST_NAME));
			childData.put(KEY_CHILD_TITLE2, order.customer.valueMap.get(Customers.COLUMN_NAME_LAST_NAME));
			childData.put(KEY_CHILD_SUBTITLE1, order.customer.valueMap.get(Customers.COLUMN_NAME_ADDRESS));
			childData.put(KEY_CHILD_SUBTITLE2, null);
			*/
			memberList.add((HashMap<String, String>) order.customer.valueMap);
			break;

		default:
			break;
		}
		/*
		for(Object rec:members.toArray()){
			DatabaseRecord memberRec = (DatabaseRecord)rec;
			memberList.add(memberRec.valueMap);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this,memberList , listItemLayoutId, fromColumns, toFields);
		lView.setAdapter(adapter);
		lView.setOnItemClickListener(new MemberOpenListener());
//		lView.setVisibility(View.GONE); 
		 */
		return memberList;
	}
	
	class MemberOpenListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long index) {
			// TODO figure out a better way to do this
			// current implementation makes too many assumptions, and depends on current configuration not changing
			
			String domain = "";
			DatabaseRecord member = null;
			if(((View)parent.getParent()).getId() == R.id.orders_workers_fragment_container){
				domain = DbConstants.WORKERS;
				member = ((OrdersRecord)currentRecord).workers.get(position);
			}else{
				domain = DbConstants.PRODUCTS;
				member = ((OrdersRecord)currentRecord).products.get(position);
			}
			Intent intent = new Intent(getBaseContext(), DisplayRecordActivity.class);
			intent.putExtra(ViewDbActivity.DOMAIN, domain);
			intent.putExtra(ViewDbActivity.RECORD_ID, member.recordId);
			startActivity(intent);
		}

		
	}

	private LinearLayout getMemberGroupHeader(final ViewGroup parent, int titleId) {
		LinearLayout groupHeader = new LinearLayout(this);
		groupHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		groupHeader.setOrientation(LinearLayout.HORIZONTAL);
		TextView frameTitle = new TextView(this);
		LinearLayout.LayoutParams titleLp =new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
		titleLp.weight = 1;
		frameTitle.setLayoutParams(titleLp);
		frameTitle.setText(titleId);
		frameTitle.setTextSize(24);
		frameTitle.setTextColor(Color.BLUE);
		groupHeader.addView(frameTitle);
		Button toggler = getToggleButton();
		toggler.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(parent.getVisibility()==View.VISIBLE){
					parent.setVisibility(View.GONE);
					toggleHideButtonText((Button)v);
				}else{
					parent.setVisibility(View.VISIBLE);
					toggleHideButtonText((Button)v);
				}

			}
		});
		groupHeader.addView(toggler);
		return groupHeader;
	}

	void toggleHideButtonText(Button b){
		if(b.getText().toString().equals(getString(R.string.btn_hide_orders_member)))
			b.setText(R.string.btn_show_orders_member);
		else
			b.setText(R.string.btn_hide_orders_member);
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

		// Inflate the menu; this adds items to the action bar if it is present.
		if(getIntent().getStringExtra(ViewDbActivity.DOMAIN).equals(DbConstants.PRODUCTS))
			getMenuInflater().inflate(R.menu.display_product_menu, menu);
		else
			getMenuInflater().inflate(R.menu.display_record, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. 
		int id = item.getItemId();
		switch (id) {
		
		case R.id.menu_item_edit_record:
			String recID = currentRecord.recordId;
			String d = getIntent().getExtras().getString(ViewDbActivity.DOMAIN);
			editRecord(d, recID);
			return true;
		case R.id.menu_item_delete_record:
			ConfirmDeletionDialog dialog = new ConfirmDeletionDialog();
			dialog.show(getFragmentManager(), DELETION_DIALOG_FRAG_TAG);
		//	if(deletionAffirmed)
		
			return true;
		case R.id.menu_item_call:
			// workaround for problem of TextViews being null when ExpandableListView group containing them is not open
			if(currentRecord instanceof OrdersRecord){
				OrdersRecord record = (OrdersRecord)currentRecord;
				if(record.customer == null)
					return true;
				else{
					AsyncTask<String, Integer, Integer> task = new AsyncTask<String, Integer, Integer>() {
						
						@Override
						protected Integer doInBackground(String... params) {
							
							return null;
						}
						
						@Override
						protected void onPreExecute() {
							lView.expandGroup(1);
							super.onPreExecute();
						}
						
						@Override
						protected void onPostExecute(Integer result) {
							phoneFields[0] = R.id.customers_phone1;
							phoneFields[1] = R.id.customers_phone2;
							phoneFields[2] = R.id.customers_contact_phone;
							
							phoneLabels[0] = getString(R.string.customer_phone1_hint)+": "+((TextView)findViewById(phoneFields[0])).getText();
							phoneLabels[1] = getString(R.string.customer_phone2_hint)+": "+((TextView)findViewById(phoneFields[1])).getText();
							phoneLabels[2] = getString(R.string.customer_contact_phone_hint)+": "+((TextView)findViewById(phoneFields[2])).getText();
							
							phoneString = "";
							ArrayList<String> phones = new ArrayList<>();
							for(int phoneId:phoneFields){
								TextView tView = (TextView)findViewById(phoneId);
								if(tView!=null && tView.getText().length()>=9)
									phones.add(tView.getText().toString());
							}
							if(phones.size()==1){
								phoneString = phones.get(0);
								// strip off dashes and other unwanted characters
								StringBuilder phoneNum = new StringBuilder();
								for(int i = 0;i<phoneString.length();i++){
									if((byte)phoneString.substring(i, i+1).toCharArray()[0]<58 && (byte)phoneString.substring(i, i+1).toCharArray()[0]>46)
										phoneNum.append(phoneString.substring(i, i+1));
								}

								// check availability of phone app and open intent
								PackageManager packageManager = getPackageManager();
								Uri number = Uri.parse("tel:"+ phoneNum);
								Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
								List<ResolveInfo> activities = packageManager.queryIntentActivities(callIntent, 0);
								boolean isIntentSafe = activities.size() > 0;
								if(isIntentSafe){
									startActivity(callIntent);

								}
							}
							else if(phones.size()==0){
								Toast.makeText(getBaseContext(), getString(R.string.message_no_phone_number), Toast.LENGTH_SHORT).show();
								return ;
							}else{
								DialogFragment newFragment = new PhonePickerFragment();
				    			newFragment.show(getFragmentManager(), "phone nums");
							}
							super.onPostExecute(result);
							
						}
					};
					task.execute(new String[]{});
					
					return true;
					// end stupid workaround
				}
			}
			switch (getIntent().getStringExtra(ViewDbActivity.DOMAIN)) {
			case DbConstants.CUSTOMERS:
				phoneFields[0] = R.id.customers_phone1;
				phoneFields[1] = R.id.customers_phone2;
				phoneFields[2] = R.id.customers_contact_phone;
				phoneLabels[0] = getString(R.string.customer_phone1_hint)+": "+((TextView)findViewById(phoneFields[0])).getText();
				phoneLabels[1] = getString(R.string.customer_phone2_hint)+": "+((TextView)findViewById(phoneFields[1])).getText();
				phoneLabels[2] = getString(R.string.customer_contact_phone_hint)+": "+((TextView)findViewById(phoneFields[2])).getText();
				break;
			case DbConstants.ORDERS:
				
				break;
			case DbConstants.WORKERS:
				phoneFields[0] = R.id.workers_phone1;
				phoneFields[1] = R.id.workers_phone2;
				phoneLabels[0] = getString(R.string.workers_phone1_hint)+": "+((TextView)findViewById(phoneFields[0])).getText();
				phoneLabels[1] = getString(R.string.workers_phone2_hint)+": "+((TextView)findViewById(phoneFields[1])).getText();
				break;
			case DbConstants.PRODUCTS:
				// nothing to do
				break;

			default:
				break;
			}
			
			phoneString = "";
			ArrayList<String> phones = new ArrayList<>();
			for(int phoneId:phoneFields){
				TextView tView = (TextView)findViewById(phoneId);
				if(tView!=null && tView.getText().length()>=9)
					phones.add(tView.getText().toString());
			}
			if(phones.size()==1){
				phoneString = phones.get(0);
				return true;
			}
			else if(phones.size()==0){
				Toast.makeText(this, getString(R.string.message_no_phone_number), Toast.LENGTH_SHORT).show();
				return false;
			}else{
				DialogFragment newFragment = new PhonePickerFragment();
    			newFragment.show(getFragmentManager(), "phone nums");
			}
			return true;
		case R.id.menu_item_email:
			// ridiculous workaround for orders records
			if(currentRecord instanceof OrdersRecord && ((OrdersRecord)currentRecord).customer != null){
				AsyncTask<String, Integer, Integer> task = new AsyncTask<String, Integer, Integer>() {
					
					@Override
					protected void onPreExecute() {
						lView.expandGroup(1);
						super.onPreExecute();
					}
					
					@Override
					protected Integer doInBackground(String... params) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					protected void onPostExecute(Integer result) {
						TextView emailTxt = (TextView)findViewById(emailAddressId);
						if(emailTxt != null){
							String emailAddress = emailTxt.getText().toString();
							// TODO use email regex
							if(emailAddress.length()>9 && emailAddress.contains("@")){
								Intent emailIntent = new Intent(Intent.ACTION_SEND);
								emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
								emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailAddress});
								startActivity(emailIntent);
								return ;
							}
						}else{
							Toast.makeText(getBaseContext(), R.string.message_no_email, Toast.LENGTH_SHORT).show();
						}

						super.onPostExecute(result);
					}
				};
				task.execute(new String[]{});
				return true;
				// end of workaround
			}
			TextView emailTxt = (TextView)findViewById(emailAddressId);
			if(emailTxt != null){
				String emailAddress = emailTxt.getText().toString();
				// TODO use email regex
				if(emailAddress.length()>9 && emailAddress.contains("@")){
					Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
					emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailAddress});
					startActivity(emailIntent);
					return true;
				}
			}else{
				Toast.makeText(this, R.string.message_no_email, Toast.LENGTH_SHORT).show();
			}
			return false;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPhoneDialogPositiveClick(PhonePickerFragment dialog) {
		selectedPhoneIndex = dialog.clickedPhoneIndex;

		TextView tView = (TextView)findViewById(phoneFields[selectedPhoneIndex]);
		phoneString = tView.getText().toString();


		// strip off dashes and other unwanted characters
		StringBuilder phoneNum = new StringBuilder();
		for(int i = 0;i<phoneString.length();i++){
			if((byte)phoneString.substring(i, i+1).toCharArray()[0]<58 && (byte)phoneString.substring(i, i+1).toCharArray()[0]>46)
				phoneNum.append(phoneString.substring(i, i+1));
		}

		// check availability of phone app and open intent
		PackageManager packageManager = getPackageManager();
		Uri number = Uri.parse("tel:"+ phoneNum);
		Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
		List<ResolveInfo> activities = packageManager.queryIntentActivities(callIntent, 0);
		boolean isIntentSafe = activities.size() > 0;
		if(isIntentSafe){
			startActivity(callIntent);

		}

	}	

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		Log.d(TAG, "Yes clicked");
		currentRecord.deleteRecord();
			

		Log.d(TAG, "Before refresh");
		Intent intent2 = new Intent(this, ViewDbActivity.class);
		startActivity(intent2);
		
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
	
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onStop() {
		currentRecord.db.close();
		super.onStop();
	}

}
