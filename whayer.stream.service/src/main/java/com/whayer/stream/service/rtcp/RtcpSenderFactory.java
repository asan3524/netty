package com.whayer.stream.service.rtcp;

public abstract class RtcpSenderFactory {
	public static RtcpSender rtcpSender(String method) {
		switch (method) {
		case "RR":
			return new RRRtpSender();
		case "SR":
			return new SRRtpSender();
		default:
			return null;
		}
	}
}
