package com.chj.datasource;


/**
 * 动态数据源上下文.
 * 
 * @author haijchen
 * 
 */
public class DynamicDataSourceContextHolder {

	/*
	 * 当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本， 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
	 */
	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

	/**
	 * 使用setDataSourceType设置当前的
	 * 
	 * @param dataSourceType
	 */
	public static void setDataSource(String dataSource) {
		contextHolder.set(dataSource);
	}

	public static String getDataSource() {
		return contextHolder.get();
	}

	public static void clearDataSource() {
		contextHolder.remove();
	}

}
