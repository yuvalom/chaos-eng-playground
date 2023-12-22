package com.lombard.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.lombard.application.DeviceService;
import com.lombard.controllers.DeviceController;

@Configuration
@PropertySource("application.properties")
public class ApplicationConfig {
	@Autowired
	Environment env;
	@Autowired
	PersistentConfig persistenceConfig;
	
	@Bean
	public DeviceController deviceController() throws Exception {
		DeviceService deviceService = deviceService();
		DeviceController deviceController = new DeviceController(deviceService);
		return deviceController;
	}
	
	@Bean
	@Lazy
	public DeviceService deviceService() throws Exception {
		DeviceService deviceService = new DeviceService(persistenceConfig.deviceRepository());
		return deviceService;
	}
}
