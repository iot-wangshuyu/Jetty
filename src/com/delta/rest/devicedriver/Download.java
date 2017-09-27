package com.delta.rest.devicedriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import com.delta.bean.DeviceDriver;
import com.delta.bean.QrCodeInfo;
import com.delta.dao.DeviceDriverDao;
import com.delta.dao.impl.DaoImplFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final DeviceDriverDao deviceDriverDaoInst = DaoImplFactory.getDeviceDriverDaoInst();
	private static Logger logger = Logger.getLogger(Download.class); // 使用Logger的静态方法获得当前类

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpServletResponse res = (HttpServletResponse) response;//跨域访问设置
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		res.setHeader("Access-Control-Max-Age", "3600"); 

		String value = getIpAddr(request);// 访问者ip
		logger.info("ip:" + value);

		if (request.getContentLength() <= 0) {
			res.setContentType("text/plain;charset=utf-8");
			res.sendError(400, "The input data is empty!");
			logger.info("The input data is empty!");// 打印日志
			return;
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = null;
		QrCodeInfo qrCodeInfo = null;
		try {
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			logger.info("Request data is:" + sb.toString());
			Gson gson = new Gson(); // 使用Gson获取json数据
			qrCodeInfo = gson.fromJson(sb.toString(), QrCodeInfo.class);// 将http请求中的Json转换成QrCodeInfo对象
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.toString());
		}
		if (qrCodeInfo.getMac() == null && qrCodeInfo.getType() == null && qrCodeInfo.getVendor() == null
				&& qrCodeInfo.getProtocol() == null) {
			res.setContentType("text/plain;charset=utf-8");
			res.sendError(400, "The input data is invalid!");
			logger.info("The input data is invalid!");
			return;
		}

		DeviceDriver deviceDriver = new DeviceDriver(); // 由qrCodeInfo映射到deviceDriver
		deviceDriver.setDeviceType(qrCodeInfo.getType());
		deviceDriver.setDeviceVendor(qrCodeInfo.getVendor());
		deviceDriver.setDeviceProtocol(qrCodeInfo.getProtocol());
		logger.info("From the database to find the corresponding results list!");
		List<DeviceDriver> resultList = deviceDriverDaoInst.getList(deviceDriver); // 从数据库中查找对应的结果列表

		// JsonConfig jsonConfig = new JsonConfig();// 注册jsonConfig注释
		// jsonConfig.registerJsonValueProcessor(Timestamp.class, new
		// DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss.S"));// 时间格式转换
		// JSONArray json = JSONArray.fromObject(resultList, jsonConfig);//
		// 把list<>转换成jsonArray

		Gson jso = new GsonBuilder().setDateFormat("yyyy-MM-dd  HH:mm:ss.S").create();// Gson，执行toJson方法，怎么指定实体里date的格式
		String str = jso.toJson(resultList);
		str=str.replace("[","");
		str=str.replace("]","");
		
		if (resultList == null || resultList.size() == 0) { // 判断resultList是否有值
			logger.info("No matching drivers!");
		} else {
			logger.info("The data returned is:" + str);
		}

		try {
			// 若没有匹配驱动，返回空值
			res.setContentType("application/json;charset=utf-8");
			PrintWriter pw = response.getWriter();
			pw.write(str);
			pw.flush();
		} catch (Exception e) {
			// TODO: handle exception
			logger.info(e.toString());
		}

	}

	public String getIpAddr(HttpServletRequest request) { // 获取真实的ip
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
