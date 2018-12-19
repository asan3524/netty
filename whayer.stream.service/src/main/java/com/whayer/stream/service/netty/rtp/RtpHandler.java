package com.whayer.stream.service.netty.rtp;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whayer.stream.service.netty.rtcp.RtcpHandler;
import com.whayer.stream.service.rtp.RtpSender;
import com.whayer.stream.service.rtp.RtpSenderFactory;
import com.whayer.stream.service.rtsp.Session;
import com.whayer.stream.service.util.OsUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class RtpHandler {

	private final static Logger logger = LoggerFactory.getLogger(RtpHandler.class);

	private Bootstrap bootstrap = null;
	private ChannelHandlerContext ctx = null;
	private Session session = null;
	private ChannelFuture future = null;
	private RtpSender rtpSender = null;

	public RtpHandler(ChannelHandlerContext ctx, Session session) {
		this.ctx = ctx;
		this.session = session;
		this.bootstrap = new Bootstrap();
	}

	public RtpHandler handler() {

		if (OsUtil.system() == OsUtil.Os.LINUX) {
			bootstrap.channel(EpollDatagramChannel.class);
		} else {
			bootstrap.channel(NioDatagramChannel.class);
		}
		bootstrap.handler(new RtpChannelInitializer(new InetSocketAddress(session.getLocalAddress(), session.getClientRtpPort()))).group(ctx.channel().eventLoop())
				.option(ChannelOption.SO_BROADCAST, true);
		future = bootstrap.bind(session.getServerRtpPort());
		rtpSender = RtpSenderFactory.rtpSender("RECORD");
		return this;
	}

	public RtpHandler listener(RtcpHandler rtcpHandler) {
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				// TODO Auto-generated method stub
				if (future.isSuccess()) {
					rtpSender.send(future.channel(), session);
					logger.info("rtp data sending :" + session.getId() + " - " + session.getLocalAddress() + " - "
							+ session.getClientRtpPort());
				} else {
					logger.error("rtp bind error : " + session.getId() + " - " + session.getLocalAddress() + " - "
							+ session.getClientRtpPort());
					logger.error(f.cause().getMessage());
					rtcpHandler.close();
				}
			}
		});
		return this;
	}

	public ChannelFuture close() {
		logger.info("rtp close");
		rtpSender.close();
		return future.addListener(ChannelFutureListener.CLOSE);
	}

	public boolean isSuccess() {
		return future.isSuccess();
	}
}
