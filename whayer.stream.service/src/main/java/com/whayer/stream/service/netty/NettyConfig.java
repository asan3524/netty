package com.whayer.stream.service.netty;

import com.whayer.stream.service.netty.rtcp.RtcpHandler;
import com.whayer.stream.service.netty.rtp.RtpHandler;
import com.whayer.stream.service.rtsp.Session;

import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.util.AttributeKey;

public class NettyConfig {

	public static final AttributeKey<Session> Session = AttributeKey.newInstance("Session");
	public static final AttributeKey<RtpHandler> RtpHandler = AttributeKey.newInstance("RtpHandler");
	public static final AttributeKey<RtcpHandler> RtcpHandler = AttributeKey.newInstance("RtcpHandler");

	public static String OPTIONS = null;

	static {
		StringBuffer sb = new StringBuffer();
		sb.append(RtspMethods.OPTIONS).append(",").append(RtspMethods.DESCRIBE).append(",").append(RtspMethods.SETUP)
				.append(",").append(RtspMethods.PLAY).append(",").append(RtspMethods.TEARDOWN).append(",")
				.append(RtspMethods.PAUSE).append(",").append(RtspMethods.ANNOUNCE).append(",")
				.append(RtspMethods.GET_PARAMETER);
		OPTIONS = sb.toString();
	}
}
