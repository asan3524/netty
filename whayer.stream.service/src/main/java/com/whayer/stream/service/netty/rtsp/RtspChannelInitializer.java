package com.whayer.stream.service.netty.rtsp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.rtsp.RtspDecoder;
import io.netty.handler.codec.rtsp.RtspEncoder;

public class RtspChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel e) throws Exception {
		ChannelPipeline pipeline = e.pipeline();
		pipeline.addLast(new RtspDecoder());
		pipeline.addLast(new RtspEncoder());
		pipeline.addLast(new RtspChannelHandler());
	}
}
