package com.whayer.stream.service.rtcp;

import com.whayer.stream.service.rtsp.Session;

import io.netty.channel.Channel;

public abstract class RtcpSender {
	public abstract Channel send(Channel channel, Session session);
}
