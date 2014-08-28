package database_files;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class JobsProductsDBHelper  extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "main.db";
	
	public JobsProductsDBHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL(JobsProductsContract.JobsProducts.SQL_CREATE_TABLE);
		}catch(Exception e){
			Log.d("Creating table... : " + JobsProductsContract.JobsProducts.TABLE_NAME, e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// needs to be in parallel thread, could take quite some time! (?) 
		/*
		int ver = oldVersion;
		while(ver<newVersion){
		switch (ver) {
		case 1:
			db.execSQL("ALTER TABLE "+DATABASE_NAME+"."+JobsProductsContract.JobsProducts.TABLE_NAME+" ADD COLUMN amount TEXT");
			ver++;
			break;

		default:
			break;
		} */
		}/*
		try{
		db.execSQL("CREATE TABLE temp AS (SELECT * FROM "+ JobsProductsContract.JobsProducts.TABLE_NAME+")");
		db.execSQL(JobsProductsContract.JobsProducts.SQL_DELETE_TABLE);
		db.execSQL("SELECT * INTO "+ JobsProductsContract.JobsProducts.TABLE_NAME+" FROM temp");
		db.execSQL("DROP TABLE IF EXISTS temp");
		}catch(Exception e){
			Log.d(JobsProductsContract.JobsProducts.TABLE_NAME+" table", e.getMessage());
		}
		
		// fill table with saved data
	*/
//	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// like onUpgrade
	//	db.execSQL(OrdersContract.Orders.SQL_DELETE_ORDERS);
	//	db.execSQL(OrdersContract.Orders.SQL_CREATE_ORDERS_TABLE);
		Log.d("JobsProductsTable","In onDowngrade");
	//	super.onDowngrade(db, oldVersion, newVersion);
	}

}
