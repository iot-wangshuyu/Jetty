package com.delta.jdbc;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.log4j.Logger;


public class BeanHandler implements ResultSetHandler {
	private Class<?> clazz;
	private static Logger logger = Logger.getLogger(BeanHandler.class); // 使用Logger的静态方法获得当前类
	public BeanHandler(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public Object handler(ResultSet rs) {
		try {
			if (!rs.next()) {
				return null;
			}
			Object bean = clazz.newInstance();
			ResultSetMetaData metadata = rs.getMetaData();
			// 获取结果集的列数
			logger.info("Get number of columns in the result set!");
			int columnCount = metadata.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				String columnName = metadata.getColumnName(i + 1);
				Object columnData = rs.getObject(i + 1);
				Field f = bean.getClass().getDeclaredField(columnName);
				f.setAccessible(true);
				f.set(bean, columnData);
			}
			return bean;
		}
		catch (Exception e) {
			logger.info(e.toString());
			throw new RuntimeException(e);			
		}
	}
}
