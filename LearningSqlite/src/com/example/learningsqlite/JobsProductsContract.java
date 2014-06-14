package com.example.learningsqlite;

import android.provider.BaseColumns;

public class JobsProductsContract {
	public static abstract class JobsProducts implements BaseColumns{
		public static final String TABLE_NAME = "job_products";
		public static final String COLUMN_NAME_ORDERS_ID = "orders_id";
		public static final String COLUMN_NAME_PRODUCTS_ID = "products_id";
		public static final String COLUMN_NAME_AMOUNT = "amount";
		public static final String _ID = "_id";
		private static final String TEXT_TYPE = "TEXT";
		private static final String COMMA_SEPARATOR = ",";
		public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY, "+ COLUMN_NAME_ORDERS_ID + 
				" " + TEXT_TYPE + COMMA_SEPARATOR + COLUMN_NAME_PRODUCTS_ID +" " +TEXT_TYPE+");";
		public static final String  SQL_DELETE_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;
	}

}
