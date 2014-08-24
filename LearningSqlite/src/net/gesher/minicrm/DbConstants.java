package net.gesher.minicrm;

public final class DbConstants {
	public static final String[] ORDERS_COLUMNS = new String[]{OrdersContract.Orders._ID,OrdersContract.Orders.COLUMN_NAME_TITLE,
		OrdersContract.Orders.COLUMN_NAME_ORDER_DATE,OrdersContract.Orders.COLUMN_NAME_FILL_BY_DATE,OrdersContract.Orders.COLUMN_NAME_CUSTOMER_ID,
		OrdersContract.Orders.COLUMN_NAME_ADDRESS};
	public static final String[] JOBS_WORKERS_COLUMNS = new String[]{JobWorkersContract.JobWorkers._ID,JobWorkersContract.JobWorkers.COLUMN_NAME_ORDERS_ID,JobWorkersContract.JobWorkers.COLUMN_NAME_WORKERS_ID};
	public static final String[] JOBS_PRODUCTS_COLUMNS = new String[]{JobsProductsContract.JobsProducts._ID,
		JobsProductsContract.JobsProducts.COLUMN_NAME_ORDERS_ID, JobsProductsContract.JobsProducts.COLUMN_NAME_PRODUCTS_ID, JobsProductsContract.JobsProducts.COLUMN_NAME_AMOUNT};
	public static final String[] WORKERS_COLUMNS = new String[]{WorkersContract.Workers._ID,WorkersContract.Workers.COLUMN_NAME_FIRST_NAME,
		WorkersContract.Workers.COLUMN_NAME_LAST_NAME,WorkersContract.Workers.COLUMN_NAME_PHONE1,WorkersContract.Workers.COLUMN_NAME_PHONE2,
		WorkersContract.Workers.COLUMN_NAME_EMAIL,WorkersContract.Workers.COLUMN_NAME_ADDRESS,WorkersContract.Workers.COLUMN_NAME_OCCUPATION,
		WorkersContract.Workers.COLUMN_NAME_RELATIONSHIP};
	public static final String[] PRODUCTS_COLUMNS = new String[]{ProductsContract.Products._ID,ProductsContract.Products.COLUMN_NAME_TITLE,
		ProductsContract.Products.COLUMN_NAME_SUBTITLE,ProductsContract.Products.COLUMN_NAME_SELL_BY_UNIT,ProductsContract.Products.COLUMN_NAME_PRICE_PER_UNIT,
		ProductsContract.Products.COLUMN_NAME_NOTES};
	public static final String[] CUSTOMERS_COLUMNS = new String[]{CustomersContract.Customers._ID,CustomersContract.Customers.COLUMN_NAME_FIRST_NAME,
		CustomersContract.Customers.COLUMN_NAME_LAST_NAME,CustomersContract.Customers.COLUMN_NAME_PHONE1,CustomersContract.Customers.COLUMN_NAME_PHONE2, 
		CustomersContract.Customers.COLUMN_NAME_EMAIL,CustomersContract.Customers.COLUMN_NAME_ADDRESS,CustomersContract.Customers.COLUMN_NAME_CUSTOMER_CONTACT,
		CustomersContract.Customers.COLUMN_NAME_CONTACT_PHONE};
	
	public static final String ORDERS = "Orders";
	public static final String CUSTOMERS = "Customers";
	public static final String WORKERS = "Workers";
	public static final String PRODUCTS = "Products";
	
	public static final String DATE_PICKER_DIALOG = "date picker dialog";
	public static int[] lastPickedDate = {1,1,2012};
	public static String currentDomain;
	
	
	public static final int[] listItemFields1 = {R.id.field_title,R.id.field_subtitle1,R.id.field_subtitle2};
	public static final String CONTACTS = "Workers";
	public static int[] listItemFields2 = {R.id.field_title1,R.id.field_title2,R.id.field_subtitle};
	public static int[] listItemFields3 = {R.id.field_title, R.id.field_subtitle};
	
	
}


