package com.delta.jdbc;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;

/**
 * 提供SQL语句的拼接功能
 */
public class CrudFrame {
	private static Logger logger = Logger.getLogger(CrudFrame.class); // 使用Logger的静态方法获得当前类
	public static <T> String get(String table, T obj) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(table);
		sb.append(" where ");
		toExpressions(sb, obj, " and ");
		return sb.toString();
	}
	
	public static <T> String insert(String table, T obj) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ");
		sb.append(table);
		sb.append(" set ");
		toExpressions(sb, obj, ", ");
		return sb.toString();
	}
	
	public static <T> String update(String table, T obj, T where) {
		StringBuilder sb = new StringBuilder();
		sb.append("update ");
		sb.append(table);
		sb.append(" set ");
		toExpressions(sb, obj, ",");
		sb.append(" where ");
		toExpressions(sb, where, " and ");
		return sb.toString();
	}
	
	public static <T> String delete(String table, T obj) {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ");
		sb.append(table);
		sb.append(" where ");
		toExpressions(sb, obj, " and ");
		return sb.toString();
	}
	
	// 拼接SQL语句
	private static void toExpression(StringBuilder sb, String key, Object value) {
		sb.append(key);
		sb.append('=');
		if (value instanceof Number || value instanceof Boolean) {
			sb.append(value);
		} else {
			sb.append('\'');
			sb.append(value);
			sb.append('\'');
		}
	}
	
	// 拼接SQL语句
	private static void toExpressions(StringBuilder sb, Object obj, String conn) {
		boolean firstField = true;
		Field[] fields = obj.getClass().getFields();
		for (Field f : fields) {
			try {
				Object value = f.get(obj);
				if (value != null) {
					if (!firstField) {
						sb.append(conn);
					} else {
						firstField = false;
					}
					toExpression(sb, f.getName(), value);
				}
			} catch (Exception e) {
				logger.info(e.toString());
				e.printStackTrace();
			}
		}
	}
}
