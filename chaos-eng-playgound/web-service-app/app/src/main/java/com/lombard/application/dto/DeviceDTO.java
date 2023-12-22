package com.lombard.application.dto;

public class DeviceDTO {
	private String uuid;
	private int platformMgmtServerId;
	private String platformMgmtDeviceId;
	private String platformMgmtDeviceName;
	private NetworkProtocolDetailsDTO networkProtocolDetails;
	private DeviceAttributesDTO deviceAttributes;
	private String deviceType;

	public DeviceDTO(String uuid, int platformMgmtServerId, String platformMgmtDeviceId, String platformMgmtDeviceName,
			NetworkProtocolDetailsDTO networkProtocolDetails,
			DeviceAttributesDTO deviceAttributes, String deviceType) {
		this.uuid = uuid;
		this.platformMgmtServerId = platformMgmtServerId;
		this.platformMgmtDeviceId = platformMgmtDeviceId;
		this.platformMgmtDeviceName = platformMgmtDeviceName;
		this.networkProtocolDetails = networkProtocolDetails;
		this.deviceAttributes = deviceAttributes;
		this.deviceType = deviceType;
		
	}

	public String getUuid() {
		return uuid;
	}

	public int getPlatformMgmtServerId() {
		return platformMgmtServerId;
	}

	public String getPlatformMgmtDeviceId() {
		return platformMgmtDeviceId;
	}

	public String getPlatformMgmtDeviceName() {
		return platformMgmtDeviceName;
	}

	public NetworkProtocolDetailsDTO getNetworkProtocolDetails() {
		return networkProtocolDetails;
	}

	public DeviceAttributesDTO getDeviceAttributes() {
		return deviceAttributes;
	}

	public String getDeviceType() {
		return deviceType;
	}
	
	public static class NetworkProtocolDetailsDTO {
		private String host;
		private int port;
		private String scheme;
		
		public NetworkProtocolDetailsDTO(String host, int port, String scheme) {
			this.host = host;
			this.port = port;
			this.scheme = scheme;
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		public String getScheme() {
			return scheme;
		}
	}
	
	public static class DeviceAttributesDTO {
		private String manufacturer;
		private String model;
		private String firmwareVersion;
		private String cpuArchitecture;
		private String macAddress; 
		
		public DeviceAttributesDTO(String manufacturer, String model, String firmwareVersion, String cpuArchitecture, String macAddress) {
			this.manufacturer = manufacturer;
			this.model = model;
			this.firmwareVersion = firmwareVersion;
			this.cpuArchitecture = cpuArchitecture;
			this.macAddress = macAddress;
			
		}

		public String getManufacturer() {
			return manufacturer;
		}

		public String getModel() {
			return model;
		}

		public String getFirmwareVersion() {
			return firmwareVersion;
		}

		public String getCpuArchitecture() {
			return cpuArchitecture;
		}

		public String getMacAddress() {
			return macAddress;
		}
	}
}
