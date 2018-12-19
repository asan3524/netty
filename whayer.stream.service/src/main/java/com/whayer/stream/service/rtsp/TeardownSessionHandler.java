package com.whayer.stream.service.rtsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whayer.stream.service.netty.NettyConfig;
import com.whayer.stream.service.netty.rtcp.RtcpHandler;
import com.whayer.stream.service.netty.rtp.RtpHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;

public class TeardownSessionHandler extends SessionHandler {

	private final static Logger logger = LoggerFactory.getLogger(TeardownSessionHandler.class);

	@Override
	public DefaultFullHttpResponse excute(ChannelHandlerContext ctx, DefaultHttpRequest request) {
		// TODO Auto-generated method stub
		logger.info(request.toString());

		String cseq = request.headers().get(RtspHeaderNames.CSEQ);
		if (null == cseq) {
			DefaultFullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0,
					RtspResponseStatuses.BAD_REQUEST);
			return response;
		}

		Session session = ctx.channel().attr(NettyConfig.Session).get();

		if (null == session) {
			DefaultFullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0,
					RtspResponseStatuses.BAD_REQUEST);
			return response;
		}

		RtpHandler rtpHandler = ctx.channel().attr(NettyConfig.RtpHandler).get();

		if (null != rtpHandler) {
			rtpHandler.close();
			ctx.channel().attr(NettyConfig.RtpHandler).set(null);
		}

		RtcpHandler rtcpHandler = ctx.channel().attr(NettyConfig.RtcpHandler).get();

		if (null != rtcpHandler) {
			rtcpHandler.close();
			ctx.channel().attr(NettyConfig.RtcpHandler).set(null);
		}

		DefaultFullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
		response.headers().set(RtspHeaderNames.CSEQ, cseq);
		response.headers().set(RtspHeaderNames.SESSION, session.getId());

		ctx.channel().attr(NettyConfig.Session).set(null);
		return response;
	}
}
