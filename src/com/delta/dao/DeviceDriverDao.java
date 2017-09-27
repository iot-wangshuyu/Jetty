package com.delta.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.delta.bean.DeviceDriver;

public interface DeviceDriverDao {
	void create(Connection conn, DeviceDriver deviceDriver) throws SQLException;
	
//	Download getSingle(Integer id);
	
	List<DeviceDriver> getList(DeviceDriver deviceDriver);
	
//	void update(Connection conn, Integer id, Download person) throws SQLException;
}
