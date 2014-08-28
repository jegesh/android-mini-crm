package database_files;

import android.provider.BaseColumns;

public class OrdersContract {
	public static abstract class Orders implements BaseColumns{
		public static final String TABLE_NAME = "orders";
		public static final String COLUMN_NAME_TITLE = "title";
		public static final String COLUMN_NAME_CUSTOMER_ID = "customer_id";
		public static final String COLUMN_NAME_ORDER_DATE = "order_date";
		public static final String COLUMN_NAME_FILL_BY_DATE = "fill_by_date";
		public static final String COLUMN_NAME_ADDRESS = "address";
		public static final String _ID = "_id";
		private static final String TEXT_TYPE = "TEXT";
		private static final String COMMA_SEPARATOR = ",";
		public static final String SQL_CREATE_ORDERS_TABLE = 
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY, "+ 
		COLUMN_NAME_TITLE + " " + TEXT_TYPE + COMMA_SEPARATOR + 
		COLUMN_NAME_CUSTOMER_ID +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_ORDER_DATE +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_FILL_BY_DATE +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_ADDRESS +" " +TEXT_TYPE+");";
		public static final String  SQL_DELETE_ORDERS = "DROP TABLE IF EXISTS "+ TABLE_NAME;
	}
}
