package database_files;

import android.provider.BaseColumns;

public class ProductsContract {
	public static abstract class Products implements BaseColumns{
		public static final String TABLE_NAME = "products";
		public static final String COLUMN_NAME_TITLE = "title";
		public static final String COLUMN_NAME_SUBTITLE = "subtitle";
		public static final String COLUMN_NAME_SELL_BY_UNIT = "unit";
		public static final String COLUMN_NAME_PRICE_PER_UNIT = "price_per_unit";
		public static final String COLUMN_NAME_NOTES = "notes";
		public static final String _ID = "_id";
		private static final String TEXT_TYPE = "TEXT";
		private static final String COMMA_SEPARATOR = ",";
		public static final String SQL_CREATE_TABLE = 
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY, "+ 
						COLUMN_NAME_TITLE +	" " + TEXT_TYPE + COMMA_SEPARATOR + 
						COLUMN_NAME_SUBTITLE +" " +TEXT_TYPE+COMMA_SEPARATOR+
						COLUMN_NAME_SELL_BY_UNIT +" " +TEXT_TYPE+COMMA_SEPARATOR+
						COLUMN_NAME_PRICE_PER_UNIT +" " +TEXT_TYPE+COMMA_SEPARATOR+
						COLUMN_NAME_NOTES +" " +TEXT_TYPE+");";
		public static final String  SQL_DELETE_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;
	}
}
