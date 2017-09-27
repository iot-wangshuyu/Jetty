package com.delta.rest.devicedriver;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import com.delta.bean.DeviceDriver;
import com.delta.configuration.Configuration;
import com.delta.dao.DeviceDriverDao;
import com.delta.dao.impl.DaoImplFactory;
import com.delta.jdbc.JdbcUtil;
import com.google.gson.Gson;

public class Upload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final DeviceDriverDao deviceDriverDaoInst = DaoImplFactory.getDeviceDriverDaoInst();
	private static Connection conn = null;
	private static final String fileRoot;
	private final String ipPath = "http://" + Configuration.ip + ":" + Configuration.port + "/DeviceDriver/";
	private static Logger logger = Logger.getLogger(Upload.class); // 使用Logger的静态方法获得当前类

	static {
		fileRoot = "D:\\\\SmartHome\\\\DeviceDriver";
		File file = new File(fileRoot);
		if (!file.exists() || !file.isDirectory()) {
			file.delete();
			file.mkdirs();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpServletResponse res = (HttpServletResponse) response;

		res.setHeader("Access-Control-Allow-Origin", "*");

		res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS,PUT");
		
		res.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Authorization, X-Requested-With, Origin");

		res.setHeader("Access-Control-Max-Age", "3600");
		
		String value = getIpAddr(request);// 访问者ip
		logger.info("ip:" + value);

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> items = new ArrayList<>();
		DeviceDriver deviceDriver = null;
		String fileName = null;
		InputStream fileInputStream = null;

		try {
			items = upload.parseRequest(request);
			Iterator<FileItem> it = items.iterator();// 得到所有的文件
			logger.info("Get all the files！");
			while (it.hasNext()) {
				FileItem fItem = (FileItem) it.next();
				if (fItem.isFormField()) { // 普通文本框的值
					String fValue = fItem.getString("UTF-8");
					Gson gson = new Gson(); // 使用Gson获取json数据
					try {
						deviceDriver = gson.fromJson(fValue, DeviceDriver.class); // 将http请求中的Json转换成DeviceDriver对象
						logger.info("The upload device driver describe data is:" + fValue.trim());

					} catch (Exception e) {
						// TODO: handle exception
						logger.info(e.toString());
					}

				} else {
					fileName = fItem.getName(); // 获取上传文件的数据
					fileInputStream = fItem.getInputStream();
					logger.info("The upload device driver file name is:" + fileName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.toString());
		}

		if (deviceDriver == null) {
			response.setContentType("text/plain;charset=utf-8");
			response.sendError(400, "The device driver describe data is missing!");
			logger.info("The device driver describe data is missing!");
			return;
		}

		if (fileName == null || fileInputStream == null) {
			response.setContentType("text/plain;charset=utf-8");
			response.sendError(400, "The device driver file data is missing!");
			logger.info("The device driver file data is missing!");
			return;
		}

		/* 保存驱动文件到本地磁盘 */
		fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		String filePath = fileRoot + File.separator + File.separator + fileName;
		FileOutputStream fos = new FileOutputStream(filePath);
		logger.info("Create a new file stream！");

		try {
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = fileInputStream.read(bytes)) != -1) {
				fos.write(bytes, 0, read);
				// logger.info("File is being transferred！");
			}
		} catch (IOException ie) {
			logger.info(ie.toString());
		} finally {
			logger.info("The device driver file uploaded successfully! The save path is:D:\\SmartHome\\DeviceDriver");
			fos.flush();
			fos.close();
			logger.info("Close the file stream！");
		}

		/* 保存驱动描述信息到数据库中 */
		String path = ipPath + fileName;
		deviceDriver.setAddress(path);
		deviceDriver.setUpdateTime(new Timestamp(System.currentTimeMillis()));

		try {
			conn = JdbcUtil.getConnection();
			logger.info("Database connection is successful!");
			conn.setAutoCommit(false);
			deviceDriverDaoInst.create(conn, deviceDriver);
			conn.commit();
			logger.info("The device driver describe data was successfully uploaded to the database!");
		} catch (SQLException ex) {
			logger.info(ex.toString());
			try {
				conn.rollback();
				logger.info("Database rollback!");
				throw new RuntimeException(ex.getMessage());
			} catch (SQLException e) {
				logger.info(e.toString());
				throw new RuntimeException(e.getMessage());
			}
		} finally {
			try {
				conn.setAutoCommit(true);
				conn.close();
				logger.info("Database close!");
			} catch (SQLException e) {
				logger.info(e.toString());
				e.printStackTrace();
			}
		}
		logger.info("The device drive upload successful!");
		response.setStatus(204);
	}

	public String getIpAddr(HttpServletRequest request) {// 获取真实的ip
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("PRoxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;

	}
}
