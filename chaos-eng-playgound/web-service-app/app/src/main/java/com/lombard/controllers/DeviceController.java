package com.lombard.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lombard.application.DeviceService;
import com.lombard.application.dto.DeviceDTO;

@Controller
@RequestMapping("api/v1")
public class DeviceController {
	
	private DeviceService deviceService;
	
	public DeviceController(DeviceService deviceService) {
		this.deviceService = deviceService;
	}
	
	@PostMapping("device")
	public ResponseEntity<DeviceDTO> add(@RequestBody DeviceDTO device) {
		try {
			deviceService.addDevice(device);
			return ResponseEntity.ok(device);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
	
	@PutMapping("device")
	public ResponseEntity<DeviceDTO> get(@RequestBody DeviceDTO device) {
		try {
			deviceService.addDevice(device);
			return ResponseEntity.ok(device);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
	
	@GetMapping("server/{serverId}/device/{deviceId}")
	public ResponseEntity<DeviceDTO> get(@PathVariable int serverId, @PathVariable String deviceId) {
		try {
			DeviceDTO device = deviceService.fetchDevice(serverId, deviceId);
			return ResponseEntity.ok(device);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}


}
