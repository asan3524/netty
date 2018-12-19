package com.whayer.stream.service.netty.rtcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

public class RtcpChannelInitializer extends ChannelInitializer<DatagramChannel> {

	@Override
	protected void initChannel(DatagramChannel e) throws Exception {
		ChannelPipeline pipeline = e.pipeline();
		pipeline.addLast(new RtcpChannelHandler());
//		pipeline.addLast(new IdleStateHandler());
//		pipeline.addLast(new DatagramPacketDecoder(null));
//		pipeline.addLast(new RtpWriteChannelHandler());
	}

}
