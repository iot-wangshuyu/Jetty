package com.delta.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.delta.bean.DeviceDriver;
import com.delta.dao.DeviceDriverDao;
import com.delta.jdbc.BeanListHandler;
import com.delta.jdbc.CrudFrame;
import com.delta.jdbc.JdbcUtil;


public class DeviceDriverDaoImpl implements DeviceDriverDao {
	private String tableName = "t_device_driver";
	
	@Override
	public void create(Connection conn, DeviceDriver deviceDriver) throws SQLException {
//		String sql = "insert into " + tableName + " values (?, ?, ?)";
//		Object[] params = {person.getId(), person.getName(), person.getId()};
		
		String sql = CrudFrame.insert(tableName, deviceDriver);
		Object[] params = { };
		JdbcUtil.update(conn, sql, params);
	}
	
//	@Override
//	public Download getSingle(Integer id) {
//		String sql = "select * from " + tableName + " where id = ?";
//		Object[] params = {id};
//		return (Download) JdbcUtil.query(sql, params, new BeanHandler(Download.class));
//	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DeviceDriver> getList(DeviceDriver deviceDriver) {
		// 两种方式：
		// 方式1：
		// 可以自定义查询语句的内容
//		String sql = "select * from " + tableName + " where deviceType = ? and deviceVendor = ? and deviceProtocol = ?";
//		Object[] params = {qrCodeInfo.getDeviceType(), qrCodeInfo.getDeviceVendor(), qrCodeInfo.getDeviceProtocol()};
//		return (List<DeviceDriver>) JdbcUtil.query(sql, params, new BeanListHandler(DeviceDriver.class));
		
		// 方式2：
		// 根据deviceDriver的各个非Null字段到数据库中做匹配
		String sql = CrudFrame.get(tableName, deviceDriver);
		Object[] params = { };
		return (List<DeviceDriver>) JdbcUtil.query(sql, params, new BeanListHandler(DeviceDriver.class));
		
	}
	
//	@Override
//	public void update(Connection conn, Integer id, Download person) throws SQLException {
//		String sql = "update " + tableName + " set name = ?, age = ? where id = ?";
//		Object[] params = {person.getName(), person.getId(), id};
//		JdbcUtil.update(conn, sql, params);
//	}
}
