package com.whayer.stream.service.rtp;

public abstract class RtpSenderFactory {
	public static RtpSender rtpSender(String method) {
		switch (method) {
		case "RECORD":
			return new RecordRtpSender();
		default:
			return null;
		}
	}
}
