package com.whayer.stream.service.netty.rtcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleStateEvent;

public class RtcpChannelHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	private final static Logger logger = LoggerFactory.getLogger(RtcpChannelHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext context, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent stateEvent = (IdleStateEvent) evt;
			switch (stateEvent.state()) {
			case READER_IDLE:
				break;
			case WRITER_IDLE:
				break;
			case ALL_IDLE:
				break;
			}
		}
	}
}
