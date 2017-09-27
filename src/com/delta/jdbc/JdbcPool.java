package com.delta.jdbc;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

public class JdbcPool implements DataSource {
	private static Logger logger = Logger.getLogger(JdbcPool.class); // 使用Logger的静态方法获得当前类
	private static LinkedList<Connection> listConnections = new LinkedList<Connection>();

	static {
		Properties prop = new Properties();
		try {
			InputStream in = new FileInputStream("mysql.ini");
			prop.load(in);
			String driver = prop.getProperty("driver");
			String url = prop.getProperty("url");
			String username = prop.getProperty("username");
			String password = prop.getProperty("password");
			int jdbcPoolInitSize = Integer.parseInt(prop.getProperty("jdbcPoolInitSize"));
			Class.forName(driver);

			if (driver == null || driver.length() == 0 || url == null || url.length() == 0 || username == null
					|| username.length() == 0 || password == null || password.length() == 0 ) {
				logger.error("Database configuration parameters can not be empty!");
			}
			for (int i = 0; i < jdbcPoolInitSize; i++) {
				Connection conn = DriverManager.getConnection(url, username, password);
				listConnections.add(conn);
			}
		} catch (Exception e) {
			logger.info(e.toString());
			throw new ExceptionInInitializerError(e);
		}
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {

	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (listConnections.size() > 0) {
			final Connection conn = listConnections.removeFirst();
			return (Connection) Proxy.newProxyInstance(JdbcPool.class.getClassLoader(),
					new Class[] { Connection.class }, new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							if (!method.getName().equals("close")) {
								return method.invoke(conn, args);
							} else {
								listConnections.add(conn);
								return null;
							}
						}
					});
		} else {
			logger.info("Server is busy!");
			throw new RuntimeException("服务器忙");
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return null;
	}
}
