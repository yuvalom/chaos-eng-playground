package com.lombard.storage.models;

import java.io.Serializable;

public class DeviceEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private int platformMgmtServerId;
	private String platformMgmtDeviceId;
	private String platformMgmtDeviceName;
	private String platformMgmtDeviceUsername;
	private String host;
	private int port;
	private String type;
	private String manufacturer;
	private String model;
	private String firmwareVersion;
	private String cpuArchitecture;
	private String macAddress;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getPlatformMgmtServerId() {
		return platformMgmtServerId;
	}
	public void setPlatformMgmtServerId(int platformMgmtServerId) {
		this.platformMgmtServerId = platformMgmtServerId;
	}
	public String getPlatformMgmtDeviceId() {
		return platformMgmtDeviceId;
	}
	public void setPlatformMgmtDeviceId(String platformMgmtDeviceId) {
		this.platformMgmtDeviceId = platformMgmtDeviceId;
	}
	public String getPlatformMgmtDeviceName() {
		return platformMgmtDeviceName;
	}
	public void setPlatformMgmtDeviceName(String platformMgmtDeviceName) {
		this.platformMgmtDeviceName = platformMgmtDeviceName;
	}
	public String getPlatformMgmtDeviceUsername() {
		return platformMgmtDeviceUsername;
	}
	public void setPlatformMgmtDeviceUsername(String platformMgmtDeviceUsername) {
		this.platformMgmtDeviceUsername = platformMgmtDeviceUsername;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getFirmwareVersion() {
		return firmwareVersion;
	}
	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}
	public String getCpuArchitecture() {
		return cpuArchitecture;
	}
	public void setCpuArchitecture(String cpuArchitecture) {
		this.cpuArchitecture = cpuArchitecture;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
}
