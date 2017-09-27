package com.delta.configuration;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;


public class Configuration {
	private static Logger logger = Logger.getLogger(Configuration.class); // 使用Logger的静态方法获得当前类
	public static String port;//端口
	public static String ip;//计算机全名或者ip
	static {
	Properties prop = new Properties();
	try {
		InputStream in = new FileInputStream("computer.ini");
		prop.load(in);
		 ip = prop.getProperty("ip");	
		 port = prop.getProperty("port");
		 if(ip==null||ip.length()==0||port==null||port.length()==0){
			 logger.error("IP and port value cannot be empty!");
		 }else {
			 logger.info("Computer IP is:"+ip+";port is:"+port);
		}		
	} catch (Exception e) {
		logger.info(e.toString());
	}
	
	}


}
