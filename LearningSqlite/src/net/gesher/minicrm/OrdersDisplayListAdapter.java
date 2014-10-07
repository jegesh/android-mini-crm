package net.gesher.minicrm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class OrdersDisplayListAdapter extends BaseExpandableListAdapter {
	private static final int GROUP_ORDER_DETAILS = 0;
	private static final int GROUP_CUSTOMER = 1;
	private static final int GROUP_PRODUCTS = 2;
	private static final int GROUP_WORKERS = 3;
	private Activity activity;
	private Integer[] orderContentIds;
	private Integer[] customerContentIds;
	private HashMap<Integer, String> orderContentIdsToColumns;
	private HashMap<Integer, String> customerContentIdsToColumns;

	private List<? extends Map<String, ?>> mGroupData;
	private int mExpandedGroupLayout;
	private int mCollapsedGroupLayout;
	private String[] mGroupFrom;
	private int[] mGroupTo;

	private List<? extends List<? extends Map<String, ?>>> mChildData;
	private int mChildLayout;
	private int mLastChildLayout;
	private String[] mChildFrom;
	private int[] mChildTo;

	private LayoutInflater mInflater;


	public OrdersDisplayListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int groupLayout,
					String[] groupFrom, int[] groupTo,
					List<? extends List<? extends Map<String, ?>>> childData,
							int childLayout, String[] childFrom, int[] childTo) {
		this(context, groupData, groupLayout, groupLayout, groupFrom, groupTo, childData,
				childLayout, childLayout, childFrom, childTo);
	}


	public OrdersDisplayListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
					int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
					List<? extends List<? extends Map<String, ?>>> childData,
							int childLayout, String[] childFrom, int[] childTo) {
		this(context, groupData, expandedGroupLayout, collapsedGroupLayout,
				groupFrom, groupTo, childData, childLayout, childLayout,
				childFrom, childTo);
	}

	public OrdersDisplayListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
					int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
					List<? extends List<? extends Map<String, ?>>> childData,
							int childLayout, int lastChildLayout, String[] childFrom,
							int[] childTo) {
		mGroupData = groupData;
		mExpandedGroupLayout = expandedGroupLayout;
		mCollapsedGroupLayout = collapsedGroupLayout;
		mGroupFrom = groupFrom;
		mGroupTo = groupTo;

		mChildData = childData;
		mChildLayout = childLayout;
		mLastChildLayout = lastChildLayout;
		mChildFrom = childFrom;
		mChildTo = childTo;

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public OrdersDisplayListAdapter(Context context,List<? extends Map<String, ?>> groupData,  String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo, 
					Map<Integer, String> orderContentMap, Map<Integer, String> custContentMap) {
		this(context, groupData, R.layout.orders_display_members_header, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
		this.orderContentIds = new Integer[orderContentMap.size()];
		this.customerContentIds = new Integer[custContentMap.size()];
		orderContentMap.keySet().toArray(this.orderContentIds);
		custContentMap.keySet().toArray(this.customerContentIds);
		this.orderContentIdsToColumns = (HashMap<Integer, String>) orderContentMap;
		this.customerContentIdsToColumns = (HashMap<Integer, String>) custContentMap;
		
	}

	public Object getChild(int groupPosition, int childPosition) {
		return mChildData.get(groupPosition).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		View v = null;
		switch (groupPosition) {
		case GROUP_ORDER_DETAILS:
			v = mInflater.inflate(R.layout.orders_display_expandable_list_child, null);
			break;
		case GROUP_CUSTOMER:
			v = mInflater.inflate(R.layout.customer_display_list_child, null);
			break;

		case GROUP_PRODUCTS:
			v = mInflater.inflate(R.layout.listview_item_3, null);
			break;

		case GROUP_WORKERS:
			v = mInflater.inflate(R.layout.listview_item_3, null);
			break;
		}
		if(v == null)
			Log.d("Adapter class", "View is null");
		bindView(v, mChildData.get(groupPosition).get(childPosition), mChildFrom, mChildTo, groupPosition);
		
		return v;
	}


	public View newChildView(boolean isLastChild, ViewGroup parent) {
		return mInflater.inflate((isLastChild) ? mLastChildLayout : mChildLayout, parent, false);
	}

	private void bindView(View view, Map<String, ?> data, String[] from, int[] to, int groupPosition) {
	//	XMLLayoutParser parser = new XMLLayoutParser(activity);
		
		switch (groupPosition) {
		case GROUP_ORDER_DETAILS:
			for(Entry<Integer,String> e:orderContentIdsToColumns.entrySet()){
				TextView txtView = (TextView)view.findViewById((int) e.getKey());
				if(txtView != null)
					txtView.setText( (CharSequence) data.get(e.getValue()));
			}
			break;
		case GROUP_CUSTOMER:
			for(Entry<Integer, String> e:customerContentIdsToColumns.entrySet()){
				TextView txtView = (TextView)view.findViewById((int) e.getKey());
				if(txtView != null)
					txtView.setText( (CharSequence) data.get(e.getValue()));
			}
			break;

		case GROUP_PRODUCTS:
			bindViewDefault(view, data, from, to);
			break;

		case GROUP_WORKERS:
			bindViewDefault(view, data, from, to);
			break;
		}
		
	}

	public int getChildrenCount(int groupPosition) {
		return mChildData.get(groupPosition).size();
	}

	public Object getGroup(int groupPosition) {
		return mGroupData.get(groupPosition);
	}

	public int getGroupCount() {
		return mGroupData.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {
		View v;
		if (convertView == null) {
			v = newGroupView(isExpanded, parent);
		} else {
			v = convertView;
		}
		bindViewDefault(v, mGroupData.get(groupPosition), mGroupFrom, mGroupTo);
		return v;
	}


	private void bindViewDefault(View view, Map<String, ?> data, String[] from, int[] to) {
		int len = to.length;
		for (int i = 0; i < len; i++) {
			TextView v = (TextView)view.findViewById(to[i]);
			if (v != null) {
				v.setText((String)data.get(from[i]));
			}
		}
		
	}


	public View newGroupView(boolean isExpanded, ViewGroup parent) {
		return mInflater.inflate((isExpanded) ? mExpandedGroupLayout : mCollapsedGroupLayout,
				parent, false);
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

	
}
