package com.proempresa.campaniamodule.util;

import proempresa.logutil.LogUtil;
import proempresa.logutil.dto.LogDto;
import proempresa.logutil.enums.LogLevel;

public class LoggerUtil {

	public static void printInfo(String description, Object... args) {
		LogUtil.printLog(LogDto.builder()
				.level(LogLevel.INFO)
				.serviceName("campanianotificar-entity")
				.description(description)
				.arguments(args)
				.build());
	}
	
	public static void printError(String description, Throwable ex) {
		LogUtil.printLog(LogDto.builder()
				.level(LogLevel.ERROR)
				.serviceName("campanianotificar-entity")
				.description(description)
				.exception(ex)
				.build());
	}
	
}
