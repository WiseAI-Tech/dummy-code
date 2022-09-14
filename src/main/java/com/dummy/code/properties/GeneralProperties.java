package com.dummy.code.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "general")
public class GeneralProperties {

	private String type;
	private boolean debugLog;
	private String logFileBaseName;
	private boolean allowPwa;
	private int pwaVersion;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isDebugLog() {
		return debugLog;
	}

	public void setDebugLog(boolean debugLog) {
		this.debugLog = debugLog;
	}

	public String getLogFileBaseName() {
		return logFileBaseName;
	}

	public void setLogFileBaseName(String logFileBaseName) {
		this.logFileBaseName = logFileBaseName;
	}

	public boolean isAllowPwa() {
		return allowPwa;
	}

	public void setAllowPwa(boolean allowPwa) {
		this.allowPwa = allowPwa;
	}

	public int getPwaVersion() {
		return pwaVersion;
	}

	public void setPwaVersion(int pwaVersion) {
		this.pwaVersion = pwaVersion;
	}
}