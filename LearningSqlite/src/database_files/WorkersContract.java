package database_files;

import android.provider.BaseColumns;

public class WorkersContract {
	public static abstract class Workers implements BaseColumns{
		public static final String TABLE_NAME = "workers";
		public static final String COLUMN_NAME_FIRST_NAME = "first_name";
		public static final String COLUMN_NAME_LAST_NAME = "last_name";
		public static final String COLUMN_NAME_PHONE1 = "phone1";
		public static final String COLUMN_NAME_PHONE2 = "phone2";
		public static final String COLUMN_NAME_EMAIL = "email";
		public static final String COLUMN_NAME_ADDRESS = "address";
		public static final String COLUMN_NAME_OCCUPATION = "occupation";
		public static final String COLUMN_NAME_RELATIONSHIP = "relationship";
		public static final String _ID = "_id";
		private static final String TEXT_TYPE = "TEXT";
		private static final String COMMA_SEPARATOR = ",";
		public static final String SQL_CREATE_TABLE = 
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY, "+ 
		COLUMN_NAME_FIRST_NAME + " " + TEXT_TYPE + COMMA_SEPARATOR + 
		COLUMN_NAME_LAST_NAME +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_PHONE1 +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_PHONE2 +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_EMAIL +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_ADDRESS +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_OCCUPATION +" " +TEXT_TYPE+COMMA_SEPARATOR+
		COLUMN_NAME_RELATIONSHIP +" " +TEXT_TYPE+");";
		public static final String  SQL_DELETE_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;
	}

}
