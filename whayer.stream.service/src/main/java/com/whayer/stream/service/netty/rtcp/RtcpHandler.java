package com.whayer.stream.service.netty.rtcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whayer.stream.service.netty.rtp.RtpHandler;
import com.whayer.stream.service.rtcp.RtcpSender;
import com.whayer.stream.service.rtcp.RtcpSenderFactory;
import com.whayer.stream.service.rtsp.Session;
import com.whayer.stream.service.util.OsUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class RtcpHandler {

	private final static Logger logger = LoggerFactory.getLogger(RtcpHandler.class);

	private Bootstrap bootstrap = null;
	private ChannelHandlerContext ctx = null;
	private Session session = null;
	private ChannelFuture future = null;
	private RtcpSender rtcpSender = null;
	
	public RtcpHandler(ChannelHandlerContext ctx, Session session) {
		this.ctx = ctx;
		this.session = session;
		this.bootstrap = new Bootstrap();
	}

	public RtcpHandler handler() {

		if (OsUtil.system() == OsUtil.Os.LINUX) {
			bootstrap.channel(EpollDatagramChannel.class);
		} else {
			bootstrap.channel(NioDatagramChannel.class);
		}
		bootstrap.handler(new RtcpChannelInitializer()).group(ctx.channel().eventLoop());
		future = bootstrap.bind(session.getServerRtcpPort());
		rtcpSender = RtcpSenderFactory.rtcpSender("SR");

		return this;
	}

	public RtcpHandler listener(RtpHandler rtpHandler) {
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				// TODO Auto-generated method stub
				if (future.isSuccess()) {
					rtcpSender.send(future.channel(), session);
					logger.info("rctp SR package send completed");
				} else {
					logger.error("rctp bind error : " + session.getId() + " - " + session.getLocalAddress() + " - "
							+ session.getClientRtpPort());
					logger.error(f.cause().getMessage());
					rtpHandler.close();
				}
//				close();
			}
		});

		return this;
	}

	public ChannelFuture close() {
		logger.info("rtcp close");
		return future.addListener(ChannelFutureListener.CLOSE);
	}

	public boolean isSuccess() {
		return future.isSuccess();
	}
}
