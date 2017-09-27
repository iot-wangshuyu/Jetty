package com.delta.jdbc;

import java.sql.ResultSet;

public interface ResultSetHandler {
	public Object handler(ResultSet rs);
}
