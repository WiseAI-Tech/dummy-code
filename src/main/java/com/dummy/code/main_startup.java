package com.dummy.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

@ServletComponentScan
@SpringBootApplication
public class main_startup extends SpringBootServletInitializer {

	@Profile("no_db_mode")
	@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
			DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
	static class WithoutDB {
		@Bean
		public DataSource dataSource() {
			DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
			return dataSourceBuilder.build();
		}
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct
	private void init() throws Exception {
		/* This is to check the integrity of locale properties file */
		String[] localeList = new ClassPathResource("locale").getFile().list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});

		for (String localeName : localeList) {
			System.out.println("Checking Locale: " + localeName);
			ArrayList<String> localeDataHeaderList = new ArrayList<String>();

			File mainLocaleFile = new ClassPathResource("locale/" + localeName + "/messages_en.properties").getFile();
			if (!mainLocaleFile.exists()) {
				throw new Exception("The Main Locale (" + localeName + ") 'messages_en.properties' Does Not Exist!");
			} else {
				BufferedReader reader = new BufferedReader(new FileReader(mainLocaleFile));
				String line = reader.readLine();
				while ((line = reader.readLine()) != null) {
					if (!(line.trim().isEmpty() || line.trim().startsWith("#"))) {
						localeDataHeaderList.add(line.trim().split("=")[0]);
					}
				}
				reader.close();
			}
			File localeFilePath = new ClassPathResource("locale/" + localeName).getFile();
			boolean isPassAll = true;
			for (File localeFile : localeFilePath.listFiles()) {
				if (!localeFile.getName().equals(mainLocaleFile.getName())) {
					ArrayList<String> tempLocaleDataHeaderList = new ArrayList<>(localeDataHeaderList);
					boolean isPass = true;
					System.out.println("Checking (" + localeName + ")" + localeFile.getName() + ":");
					BufferedReader reader = new BufferedReader(new FileReader(localeFile));
					String line = reader.readLine();
					while ((line = reader.readLine()) != null) {
						if (!(line.trim().isEmpty() || line.trim().startsWith("#"))) {
							if (!tempLocaleDataHeaderList.contains(line.trim().split("=")[0])) {
								isPass = false;
								isPassAll = false;
								System.out.println("Extra Locale Properties '" + line.trim().split("=")[0] + "'");
							} else {
								tempLocaleDataHeaderList.remove(line.trim().split("=")[0]);
							}
						}
					}
					if (tempLocaleDataHeaderList.size() != 0) {
						isPass = false;
						isPassAll = false;
						for (String localeDataHeader : tempLocaleDataHeaderList) {
							System.out.println(
									"Missing Locale Properties '" + localeDataHeader.trim().split("=")[0] + "'");
						}
					}
					if (isPass) {
						System.out.println(localeFile.getName() + " Passed Validation.");
					} else {
						System.out.println(localeFile.getName() + " Failed Validation.");
					}
					reader.close();
					System.out.println();
				}
			}
			if (!isPassAll) {
				throw new Exception("Locale File Fix Required. Please Refer to Error Log.");
			}
		}
	}
}
