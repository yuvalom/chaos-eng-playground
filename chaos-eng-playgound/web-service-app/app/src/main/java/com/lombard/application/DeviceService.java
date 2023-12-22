package com.lombard.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lombard.application.dto.DeviceDTO;
import com.lombard.application.utils.DeviceUtils;
import com.lombard.logic.Device;
import com.lombard.logic.DeviceRepository;

public class DeviceService {
	private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

	private DeviceRepository deviceRepository;

	public DeviceService(DeviceRepository deviceRepository) {
		this.deviceRepository = deviceRepository;
	}
	
	public void addDevice(DeviceDTO deviceDTO) throws Exception {
		logger.debug("Trying to add new device [{}]", deviceDTO.getUuid());
		if (deviceDTO == null) {
			throw new Exception("Device cannot be null");
		}
		
		Device device = DeviceUtils.toDevice(deviceDTO);
		deviceRepository.add(device);
		logger.info("Successfully added new device", device.getUuid());
	}
	
	public void updateDevice(DeviceDTO deviceDTO) throws Exception {
		logger.debug("Trying to update device [{}]", deviceDTO.getUuid());
		if (deviceDTO == null) {
			throw new Exception("Device cannot be null");
		}
		
		Device device = DeviceUtils.toDevice(deviceDTO);
		deviceRepository.update(device);
		logger.info("Successfully updated device", device.getUuid());
	}
	
	public DeviceDTO fetchDevice(int serverId, String deviceId) throws Exception {
		try {
			logger.debug("Trying to fetch server's [{}] device [{}]", serverId, deviceId);
			Device device = deviceRepository.fetchOne(serverId, deviceId);
			if (device == null) {
				return null;
			}
			return DeviceUtils.toDevice(device);
		} catch (Exception e) {
			logger.error("Failed to fetch server's [{}] device [{}]",serverId, deviceId, e);
			throw e;
		}

	}
}
