package com.lombard.storage.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lombard.logic.Device;
import com.lombard.logic.DeviceAttributes;
import com.lombard.logic.DeviceRepository;
import com.lombard.logic.NetworkProtocolDetails;
import com.lombard.storage.models.DeviceEntity;

public class DeviceDao implements DeviceRepository {
	private static final Logger logger = LoggerFactory.getLogger(DeviceDao.class);
	private SessionFactory sessionFactory;

	public DeviceDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void add(Device device) throws Exception {
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			DeviceEntity entity = toDeviceEntity(device);
			session.save(entity);
		} finally {
			if (session != null)
				session.close();
		}
	}
	
	@Override
	public void update(Device device) throws Exception {
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			DeviceEntity entity = toDeviceEntity(device);
			session.update(entity);
		} finally {
			if (session != null)
				session.close();
		}
	}
	
	@Override
	public List<Device> fetch(int serverId) {
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Query<DeviceEntity> query = session.createQuery("from devices where server_id = " + serverId);
			List<DeviceEntity> list = query.list();
			return toDeviceList(list);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to list server %s devices, reason: %s", e.getMessage());
			logger.error(errorMsg, e);
			return null;
		} finally {
			if (session != null)
				session.close();
		}
	}

	
	@Override
	public Device fetchOne(int serverId, String uuid) {
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			@SuppressWarnings("unchecked")
			Query<DeviceEntity> query = session.createQuery("from DeviceEntity where platformMgmtServerId = " + serverId + " and id = '" + uuid + "'");
			DeviceEntity result = query.getSingleResult();
			if (result == null)
				return null;
			
			return toDevice(result);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to fetch device %s of server %s, reason: %s", uuid, serverId, e.getMessage());
			logger.error(errorMsg, e);
			return null;
		} finally {
			if (session != null)
				session.close();
		}
	}
	
	private List<Device> toDeviceList(List<DeviceEntity> deviceEtities) {
		List<Device> devices = new ArrayList<>();
		for (DeviceEntity entity : deviceEtities) {
			Device d = toDevice(entity);
			devices.add(d);
		}
		return devices;
	}
	
	private Device toDevice(DeviceEntity deviceEntity) {
		Device device = new Device(deviceEntity.getId(), deviceEntity.getPlatformMgmtServerId(),
				deviceEntity.getPlatformMgmtDeviceId(), deviceEntity.getPlatformMgmtDeviceName(),
				deviceEntity.getPlatformMgmtDeviceUsername(),
				new NetworkProtocolDetails(deviceEntity.getHost(), deviceEntity.getPort(), null),
				new DeviceAttributes(deviceEntity.getManufacturer(), deviceEntity.getModel(),
						deviceEntity.getFirmwareVersion(), deviceEntity.getCpuArchitecture(),
						deviceEntity.getMacAddress()),
				null);
		
		return device;
	}
	
	private DeviceEntity toDeviceEntity(Device device) {
		DeviceEntity deviceEntity = new DeviceEntity();
		deviceEntity.setId(device.getUuid());
		deviceEntity.setPlatformMgmtServerId(device.getPlatformMgmtServerId());
		deviceEntity.setPlatformMgmtDeviceId(device.getPlatformMgmtDeviceId());
		deviceEntity.setPlatformMgmtDeviceName(device.getPlatformMgmtDeviceName());
		deviceEntity.setPlatformMgmtDeviceUsername(device.getPlatformMgmtDeviceUsername());
		deviceEntity.setHost(device.getNetworkProtocolDetails() != null ? device.getNetworkProtocolDetails().getHost() : null);
		deviceEntity.setPort(device.getNetworkProtocolDetails() != null ? device.getNetworkProtocolDetails().getPort() : 0);
		deviceEntity.setManufacturer(device.getDeviceAttributes() != null ? device.getDeviceAttributes().getManufacturer() : null);
		deviceEntity.setModel(device.getDeviceAttributes() != null ? device.getDeviceAttributes().getModel() : null);
		deviceEntity.setFirmwareVersion(device.getDeviceAttributes() != null ? device.getDeviceAttributes().getFirmwareVersion() : null);
		deviceEntity.setCpuArchitecture(device.getDeviceAttributes() != null ? device.getDeviceAttributes().getCpuArchitecture() : null);
		deviceEntity.setMacAddress(device.getDeviceAttributes() != null ? device.getDeviceAttributes().getMacAddress() : null);
		deviceEntity.setType(device.getDeviceType().getType());

		return deviceEntity;
	}

}
