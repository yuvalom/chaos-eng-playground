package com.lombard.application.utils;

import com.lombard.application.dto.DeviceDTO;
import com.lombard.application.dto.DeviceDTO.DeviceAttributesDTO;
import com.lombard.application.dto.DeviceDTO.NetworkProtocolDetailsDTO;
import com.lombard.logic.Device;

public class DeviceUtils {
	public static DeviceDTO toDTO(Device device) {
		DeviceDTO deviceDTO = new DeviceDTO(device.getUuid(), device.getPlatformMgmtServerId(),
				device.getPlatformMgmtDeviceId(), device.getPlatformMgmtDeviceName(),
				new NetworkProtocolDetailsDTO(device.getNetworkProtocolDetails().getHost(),
						device.getNetworkProtocolDetails().getPort(), device.getNetworkProtocolDetails().getScheme()),
				new DeviceAttributesDTO(device.getDeviceAttributes().getManufacturer(),
						device.getDeviceAttributes().getModel(), device.getDeviceAttributes().getFirmwareVersion(),
						device.getDeviceAttributes().getCpuArchitecture(),
						device.getDeviceAttributes().getMacAddress()),
				null);

		return deviceDTO;
	}
	
	public static Device toDevice(DeviceDTO deviceDTO) {
		Device device = new Device(deviceDTO.getUuid(),
				
		Device device1 = new Device(deviceDTO.getUuid(), deviceDTO.getPlatformMgmtServerId(),
				deviceDTO.getPlatformMgmtDeviceId(), deviceDTO.getPlatformMgmtDeviceName(),
				new NetworkProtocolDetailsDTO(deviceDTO.getNetworkProtocolDetails().getHost(),
						deviceDTO.getNetworkProtocolDetails().getPort(), deviceDTO.getNetworkProtocolDetails().getScheme()),
				new DeviceAttributesDTO(deviceDTO.getDeviceAttributes().getManufacturer(),
						deviceDTO.getDeviceAttributes().getModel(), deviceDTO.getDeviceAttributes().getFirmwareVersion(),
						deviceDTO.getDeviceAttributes().getCpuArchitecture(),
						deviceDTO.getDeviceAttributes().getMacAddress()),
				null);

		return device;
	}
}
