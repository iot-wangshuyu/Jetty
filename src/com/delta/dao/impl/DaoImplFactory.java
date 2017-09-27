package com.delta.dao.impl;

import com.delta.dao.DeviceDriverDao;

public class DaoImplFactory {
	private static final DeviceDriverDao deviceDriverDaoInst = new DeviceDriverDaoImpl();
	
	public static DeviceDriverDao getDeviceDriverDaoInst() {
		return deviceDriverDaoInst;
	}
}
