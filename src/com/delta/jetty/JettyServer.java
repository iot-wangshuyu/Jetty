package com.delta.jetty;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import com.delta.configuration.Configuration;



public class JettyServer {
	private static Logger logger = Logger.getLogger(JettyServer.class); // 使用Logger的静态方法获得当前类
	
	public static void start() {
		
		int port= Integer.parseInt(Configuration.port);	//获取端口		
		Server server = new Server(port);	
		
		ResourceHandler resourceHandler = new ResourceHandler();    
		try {			
	        resourceHandler.setDirectoriesListed(true);    
	        resourceHandler.setResourceBase("D:/SmartHome");    
	        resourceHandler.setStylesheet("");			
		} catch (Exception e) {
			// TODO: handle exception
			logger.info(e);
		}
			       
		ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContext.setContextPath("/SmartHomeCloudService");// 设置项目路径
		logger.info("ServletContextHandler{/SmartHomeCloudService}");
		// 下载接口
		ServletHolder deviceDriverDownloadHolder = new ServletHolder(com.delta.rest.devicedriver.Download.class);
		servletContext.addServlet(deviceDriverDownloadHolder, "/DeviceDriver/Download");		
		// 上传接口
		ServletHolder deviceDriverUploadHolder = new ServletHolder(com.delta.rest.devicedriver.Upload.class);
		servletContext.addServlet(deviceDriverUploadHolder, "/DeviceDriver/Upload");
		
		 
		 HandlerList handlerList = new HandlerList();
	     handlerList.setHandlers(new Handler[]{servletContext,resourceHandler});
			
		 server.setHandler(handlerList);
		
		try {
			server.start();
			logger.info("Server starts successfully!");
			server.join();			
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.info(e.toString());
		}
	}
}
