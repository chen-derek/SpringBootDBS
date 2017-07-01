package com.chj.common;

public class Constants {

	public static final String REST_APPLICATION_JSON = "application/json; charset=utf-8";
	public static final String DATA_SOURCE_PREFERENCE_JNDI = "JNDI";
	
	public static class DB{
		public static final String APP_DB_INIT = "app.ds.init";
		public static final String APP_DS_DEFAULT = "app.ds.default";
		
		public static final String DB_JNDI = "{0}.jndi.name";
		public static final String DB_DRIVER = "{0}.datasource.driver";
		public static final String DB_URL = "{0}.datasource.url";
		public static final String DB_USERNAME = "{0}.datasource.username";
		public static final String DB_PASSWORD = "{0}.datasource.password";
	} 

}
