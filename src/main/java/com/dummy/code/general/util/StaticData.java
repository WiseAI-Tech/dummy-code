package com.dummy.code.general.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dummy.code.properties.GeneralProperties;

@WebListener
public class StaticData implements ServletContextListener {

	@Autowired
	GeneralProperties generalProperties;

	public static boolean IS_LOGGER_DEBUG;
	public static String LOG_FILE_BASE_NAME;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		AutowireCapableBeanFactory autowireCapableBeanFactory = WebApplicationContextUtils
				.getRequiredWebApplicationContext(sce.getServletContext()).getAutowireCapableBeanFactory();
		autowireCapableBeanFactory.autowireBean(this);
		
		IS_LOGGER_DEBUG = generalProperties.isDebugLog();
		LOG_FILE_BASE_NAME = generalProperties.getLogFileBaseName();
	}
}