package com.chj.datasource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

	private static String defaultDataSource;

	/*
	 * 管理所有的数据源id; 主要是为了判断数据源是否存在;
	 */
	public static List<String> dataSourceList = new ArrayList<String>();

	/*
	 * 代码中的determineCurrentLookupKey方法取得一个字符串， 该字符串将与配置文件中的相应字符串进行匹配以定位数据源，配置文件，即applicationContext.xml文件中需要要如下代码：(non-Javadoc)
	 * @see org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource#determineCurrentLookupKey()
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		/*
		 * DynamicDataSourceContextHolder代码中使用setDataSourceType 设置当前的数据源，在路由类中使用getDataSourceType进行获取， 交给AbstractRoutingDataSource进行注入使用。
		 */
		String dataSource = DynamicDataSourceContextHolder.getDataSource();
		if (StringUtils.isEmpty(dataSource)) {
			dataSource = defaultDataSource;
		}
		if (!dataSourceList.contains(dataSource)) {
			throw new RuntimeException("dataSource: [" + dataSource + "] not found!!!");
		}
		logger.info("get current thread dataSource : " + dataSource);
		return dataSource;
	}

	public static void setDefaultDataSource(String defaultDataSource) {
		DynamicDataSource.defaultDataSource = defaultDataSource;
	}

	public static String getDefaultDataSource() {
		return DynamicDataSource.defaultDataSource;
	}

	public static void addDataSource(String dataSource) {
		if (dataSourceList == null) {
			dataSourceList = new ArrayList<String>();
		}
		if (StringUtils.isNotEmpty(dataSource)) {
			if (!dataSourceList.contains(dataSource)) {
				dataSourceList.add(dataSource);
			}
		}
	}

}
