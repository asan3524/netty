package com.whayer.stream.service.rtsp;

public abstract class SessionHandlerFactory {
	public static SessionHandler sessionHandler(String method) {
		switch (method) {
		case "OPTIONS":
			return new OptionsSessionHandler();
		case "DESCRIBE":
			return new DescribeSessionHandler();
		case "SETUP":
			return new SetupSessionHandler();
		case "PLAY":
			return new PlaySessionHandler();
		case "PAUSE":
			return new PauseSessionHandler();
		case "TEARDOWN":
			return new TeardownSessionHandler();
		default:
			return null;
		}
	}
}
