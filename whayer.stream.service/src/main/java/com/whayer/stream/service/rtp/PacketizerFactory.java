package com.whayer.stream.service.rtp;

import java.util.concurrent.ExecutorService;

import com.whayer.stream.service.rtp.h264.H264Packetizer;
import com.whayer.stream.service.rtsp.Session;

import io.netty.channel.Channel;

public class PacketizerFactory {
	public static AbstractPacketizer packetizer(ExecutorService executorService, Channel channel, Session session) {
		switch (session.getSdp().getM().getPayloads().get(0)) {
		case 96:
			return new H264Packetizer(executorService, channel, session);
		default:
			return null;
		}
	}
}
