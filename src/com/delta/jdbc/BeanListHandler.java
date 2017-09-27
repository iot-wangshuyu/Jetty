package com.delta.jdbc;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class BeanListHandler implements ResultSetHandler {
	private Class<?> clazz;
	private static Logger logger = Logger.getLogger(BeanListHandler.class); // 使用Logger的静态方法获得当前类
	public BeanListHandler(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public Object handler(ResultSet rs) {
		try {
			List<Object> list = new ArrayList<Object>();
			while (rs.next()) {
				Object bean = clazz.newInstance();
				ResultSetMetaData metadata = rs.getMetaData();
				logger.info("Get number of columns in the result set!");
				int columnCount = metadata.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					String columnName = metadata.getColumnName(i + 1);
					Object columnData = rs.getObject(i + 1);
					Field f = bean.getClass().getDeclaredField(columnName);
					f.setAccessible(true);
					f.set(bean, columnData);
				}
				list.add(bean);
			}
			return list;
		}
		catch (Exception e) {
			logger.info(e.toString());
			throw new RuntimeException(e);
		}
	}
}
