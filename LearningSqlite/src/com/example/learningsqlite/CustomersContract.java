package com.example.learningsqlite;

import android.provider.BaseColumns;


public final class CustomersContract {
	public static abstract class Customers implements BaseColumns{
		public static final String TABLE_NAME = "customers";
		public static final String COLUMN_NAME_FIRST_NAME = "firstname";
		public static final String COLUMN_NAME_LAST_NAME = "lastname";
		public static final String COLUMN_NAME_PHONE1 = "phone1";
		public static final String COLUMN_NAME_PHONE2 = "phone2";
		public static final String COLUMN_NAME_EMAIL = "email";
		public static final String COLUMN_NAME_ADDRESS = "address";
		public static final String COLUMN_NAME_CUSTOMER_CONTACT = "contact_name";
		public static final String COLUMN_NAME_CONTACT_PHONE = "contact_phone";
		public static final String _ID = "_id";
		private static final String TEXT_TYPE = "TEXT";
		private static final String COMMA_SEPARATOR = ",";
		public static final String SQL_CREATE_CUSTOMERS_TABLE = 
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY, "+ 
		COLUMN_NAME_FIRST_NAME +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_LAST_NAME +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_PHONE1+" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_PHONE2+" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_EMAIL +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_ADDRESS+" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_CUSTOMER_CONTACT+" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_CONTACT_PHONE+" " +TEXT_TYPE+");";
		public static final String  SQL_DELETE_CUSTOMERS = "DROP TABLE IF EXISTS "+ TABLE_NAME;
	}

}
