package com.lombard.app.config;

import java.io.File;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.http.client.utils.URIBuilder;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.lombard.logic.DeviceRepository;
import com.lombard.storage.dao.DeviceDao;

@Configuration
@EnableTransactionManagement
@PropertySource("db.properties")
public class PersistentConfig {
	private static final Logger logger = LoggerFactory.getLogger(PersistentConfig.class);
	@Autowired
	Environment env;
	
	
	private static final String ENV_DB_SCHEMA = "db.schema";
	private static final String ENV_DB_HOST = "db.host";
	private static final String ENV_DB_PORT = "db.port";
	private static final String ENV_DB_USER = "db.user";
	private static final String ENV_DB_PASS = "db.pass";
	private static final String ENV_DB_VALIDATE_CERT = "sql.validate.cert";
	private static final String ENV_DB_VALIDATE_CN = "sql.validate.cn";
	private static final String ENV_DB_CA_FILE_NAME = "sql.ca.fileName";
	private static final String ENV_DB_CA_PATH = "sql.ca.path";
	private static final String ENV_DB_USE_TLS = "sql.use.tls";
	private static final String DEFAULT_DB_CA_FILE_NAME = "mysql_ca.pem";
	
	@Bean
	public DeviceRepository deviceRepository() throws Exception {
		LocalSessionFactoryBean sessionFactory = sessionFactory();
		DeviceDao deviceDao = new DeviceDao(sessionFactory.getObject());
		return deviceDao;
	}
	
	@Bean
	public LocalSessionFactoryBean sessionFactory() throws Exception {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setHibernateProperties(hibernateProperties());
		sessionFactory.setMappingLocations(createMapperLocations());
		return sessionFactory;
	}

	@Bean
	public DataSource dataSource() throws Exception {

		String dbDriver = env.getProperty("db.driver");
		String dbConnectionUrl = buildDbConnectionURL();
		String dbUsername = env.getProperty(ENV_DB_USER);
		String dbPass = env.getProperty(ENV_DB_PASS);
		// this is the pooled data source
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(dbDriver);

		logger.info("Connecting to {}", dbConnectionUrl);
		dataSource.setUrl(dbConnectionUrl);
		dataSource.setUsername(dbUsername);
		dataSource.setPassword(dbPass);
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager hibernateTransactionManager() throws Exception {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory().getObject());
		return transactionManager;
	}

	private final Properties hibernateProperties() {
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty(env.getProperty("hibernate.hbm2ddl.auto"), "validate");
		hibernateProperties.setProperty(env.getProperty("hibernate.dialect"), "org.hibernate.dialect.MySQLDialect");
		hibernateProperties.setProperty(env.getProperty("hibernate.show_sql"), "false");
		return hibernateProperties;
	}
	
	private String buildDbConnectionURL() throws Exception {
		String host = env.getProperty(ENV_DB_HOST);
		String port = env.getProperty(ENV_DB_PORT);
		String db = env.getProperty(ENV_DB_SCHEMA);
		String useTLS = env.getProperty(ENV_DB_USE_TLS);
		String caPath = env.getProperty(ENV_DB_CA_PATH);
		String caFileName = env.getProperty(ENV_DB_CA_FILE_NAME, DEFAULT_DB_CA_FILE_NAME);
		String validateCert = env.getProperty(ENV_DB_VALIDATE_CERT);
		String validateCN = env.getProperty(ENV_DB_VALIDATE_CN);


		URIBuilder uriBuilder = new URIBuilder().setScheme("jdbc:mysql");
		uriBuilder.setHost(host).setPort(Integer.parseInt(port));
		uriBuilder.setPath(db);
		uriBuilder.setParameter("autoReconnect", "true");
		//uriBuilder.setParameter("allowPublicKeyRetrieval", "true");
		// if ENV_DB_USE_TLS was explicitly set to false
		if ("false".equalsIgnoreCase(useTLS)) {
			uriBuilder.addParameter("useSSL", "false");
		} else {
			// otherwise, if it was not set or set to true set useSSL to true
			uriBuilder.addParameter("useSSL", "true");

			if ("true".equalsIgnoreCase(validateCert)) {
				File caFile = new File(caPath, caFileName);

				if (!caFile.canRead())
					throw new Exception("could not read sql CA file [" + caFile.getAbsolutePath() + "]");

				uriBuilder.addParameter("serverSslCert", caFile.getAbsolutePath());

				if (validateCN != null) {
					uriBuilder.addParameter("disableSslHostnameVerification",
							"false".equalsIgnoreCase(validateCN) ? "true" : "false");
				}
			} else {
				uriBuilder.addParameter("trustServerCertificate", "true");
			}
		}

		String uri = uriBuilder.build().toString();

		// mysql doen't treat the escaped colon correctly so we need to decode it
		uri = uri.replace("%3A", ":");
		// maria driver doen't treat the escaped backslash correctly so we need to
		// decode it
		uri = uri.replace("%5C", "\\");
		return uri;
	}
	
	private Resource[] createMapperLocations() {
		Resource[] mapperLocations = new Resource[] {
				new ClassPathResource("com/lombard/storage/mappers/Device.hbm.xml")
		};
		return mapperLocations;
	}
}
