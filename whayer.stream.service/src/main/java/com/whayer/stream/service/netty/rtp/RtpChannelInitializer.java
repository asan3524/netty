package com.whayer.stream.service.netty.rtp;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.stream.ChunkedWriteHandler;

public class RtpChannelInitializer extends ChannelInitializer<DatagramChannel> {

	public RtpChannelInitializer(InetSocketAddress inetSocketAddress) {
		// TODO Auto-generated constructor stub
		this.inetSocketAddress = inetSocketAddress;
	}
	private InetSocketAddress inetSocketAddress;
	
	@Override
	protected void initChannel(DatagramChannel e) throws Exception {
		ChannelPipeline pipeline = e.pipeline();
//		pipeline.addLast(new RtpDataEncoder(inetSocketAddress));
//		pipeline.addLast(new ChunkedWriteHandler());
		pipeline.addLast(new RtpChannelHandler());
	}

}
