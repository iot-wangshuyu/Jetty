package com.delta.bean;



public class QrCodeInfo {
	/** 设备Mac地址 */
	public String mac;
	/** 设备类型 */
	public String type;
	/** 设备提供商 */
	public String vendor;
	/** 设备通信协议 */
	public String protocol;
	
	
	public String getMac() {
		return mac;
	}
	
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getVendor() {
		return vendor;
	}
	
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
