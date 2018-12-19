package com.whayer.stream.client.netty.rtsp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.rtsp.RtspDecoder;
import io.netty.handler.codec.rtsp.RtspEncoder;

public class RtspChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel e) throws Exception {
		ChannelPipeline pipeline = e.pipeline();
		pipeline.addLast("decoder", new RtspDecoder());
		pipeline.addLast("encoder", new RtspEncoder());
//		pipeline.addLast("requestHandler", new RtspRequestHandler());
		pipeline.addLast("channelHandler", new RtspChannelHandler());
	}
}
