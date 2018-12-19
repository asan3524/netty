package com.whayer.stream.service.rtp;

import com.whayer.stream.service.rtsp.Session;

import io.netty.channel.Channel;

public abstract class RtpSender {
	public abstract Channel send(Channel channel, Session session);
	public abstract boolean close();
}
