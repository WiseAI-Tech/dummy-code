package com.dummy.code.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.dummy.code.general.util.StaticData;

public class Logger {

	public static final String LOGGER_PATH = Paths
			.get(System.getProperty("catalina.base"), "logger", StaticData.LOG_FILE_BASE_NAME).toString();

	private final String logName;
	private StringBuffer sb = new StringBuffer();

	public Logger(String logName) {
		this.logName = logName;
	}

	public String getLogName() {
		return logName;
	}

	public void writeLog(String log) {
		if (StaticData.IS_LOGGER_DEBUG) {
			System.out.println(log);
		}
		sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()).concat(": "));
		sb.append(log);
		sb.append(System.lineSeparator());
	}

	public void flushLog() {
		try {
			File file = Paths.get(LOGGER_PATH, logName).toFile();
			if (!file.exists()) {
				file.mkdirs();
			}

			file = Paths.get(file.getAbsolutePath(),
					new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()).concat("_act.txt"))
					.toFile();

			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			out.write(sb.toString());
			out.flush();
			out.close();

			sb.setLength(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void writeError(Throwable throwable) {
		writeError(convertStackTraceToString(throwable));
	}

	public void writeError(String errorLog) {
		try {
			File file = Paths.get(LOGGER_PATH, logName).toFile();
			if (!file.exists()) {
				file.mkdirs();
			}

			file = Paths.get(file.getAbsolutePath(),
					new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()).concat("_err.txt"))
					.toFile();

			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			out.write(
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()).concat(": "));
			out.write(errorLog);
			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String convertStackTraceToString(Throwable throwable) {
		String convertedString = null;
		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
			throwable.printStackTrace(pw);
			convertedString = sw.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return convertedString;
	}
}
