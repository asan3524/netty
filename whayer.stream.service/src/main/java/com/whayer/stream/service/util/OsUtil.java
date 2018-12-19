package com.whayer.stream.service.util;

public class OsUtil {

	public static enum Os {
		WINDOWS, LINUX
	}

	private static Os os = Os.WINDOWS;

	static {
		String osname = System.getProperty("os.name").toLowerCase();
		if (osname.contains("windows")) {
			os = Os.WINDOWS;
		} else if (osname.contains("linux")) {
			os = Os.LINUX;
		}
	}

	public static Os system() {
		return os;
	}
}
