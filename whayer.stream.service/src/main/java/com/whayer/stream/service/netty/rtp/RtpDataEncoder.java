package com.whayer.stream.service.netty.rtp;

import java.net.InetSocketAddress;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

public class RtpDataEncoder extends MessageToMessageEncoder<ByteBuf> {

	public RtpDataEncoder(InetSocketAddress inetSocketAddress) {
		// TODO Auto-generated constructor stub
		this.inetSocketAddress = inetSocketAddress;
	}
	private InetSocketAddress inetSocketAddress;
	
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		out.add(new DatagramPacket(msg.retain(), inetSocketAddress));
	}

}
