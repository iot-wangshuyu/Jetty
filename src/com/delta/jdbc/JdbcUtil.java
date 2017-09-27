package com.delta.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;




public class JdbcUtil {
	private static Logger logger = Logger.getLogger(JdbcUtil.class); // 使用Logger的静态方法获得当前类
	private static JdbcPool pool = new JdbcPool();	
	public static Connection getConnection() throws SQLException {
		return pool.getConnection();
	}
	
	public static Object query(String sql, Object[] params, ResultSetHandler rsh) {
		
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				st.setObject(i + 1, params[i]);
			}
			rs = st.executeQuery();			
			return rsh.handler(rs);
		}
		catch (SQLException e) {
			e.printStackTrace();
			logger.info(e.toString());
			throw new RuntimeException(e.getMessage());
		}
		finally {
			queryRelease(conn, st, rs);
		}
	}
	
	public static void update(Connection conn, String sql, Object[] params) throws SQLException {
		PreparedStatement st = conn.prepareStatement(sql);
		for (int i = 0; i < params.length; i++) {
			st.setObject(i + 1, params[i]);
		}
		st.executeUpdate();
		
		if (st != null) {
			st.close();
		}
	}
	
	private static void queryRelease(Connection conn, Statement st, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			}
			catch (SQLException e) {
				logger.info(e.toString());
				e.printStackTrace();
			}
			rs = null;
		}
		if (st != null) {
			try {
				st.close();
			}
			catch (SQLException e) {
				logger.info(e.toString());
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				// conn并没有关闭，而是返回到数据库连接池中
				conn.close();
			}
			catch (SQLException e) {
				logger.info(e.toString());
				e.printStackTrace();
			}
		}
	}
}
