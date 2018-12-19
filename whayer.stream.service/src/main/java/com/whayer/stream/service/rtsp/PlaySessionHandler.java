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

public class PlaySessionHandler extends SessionHandler {

	private final static Logger logger = LoggerFactory.getLogger(PlaySessionHandler.class);

	@Override
	public DefaultFullHttpResponse excute(ChannelHandlerContext ctx, DefaultHttpRequest request) {
		// TODO Auto-generated method stub
		logger.info(request.toString());
		Session session = ctx.channel().attr(NettyConfig.Session).get();

		if (null == session) {
			DefaultFullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0,
					RtspResponseStatuses.BAD_REQUEST);
			return response;
		}
		
		session.setUri("D:/download/1118.mp4");
		RtpHandler rtpHandler = new RtpHandler(ctx, session).handler();
		RtcpHandler rtcpHandler = new RtcpHandler(ctx, session).handler();

		rtpHandler.listener(rtcpHandler);
		rtcpHandler.listener(rtpHandler);

		ctx.channel().attr(NettyConfig.RtpHandler).set(rtpHandler);
		ctx.channel().attr(NettyConfig.RtcpHandler).set(rtcpHandler);

		DefaultFullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
		response.headers().set(RtspHeaderNames.CSEQ, request.headers().get(RtspHeaderNames.CSEQ));
		response.headers().set(RtspHeaderNames.SESSION, session.getId());
		response.headers().set(RtspHeaderNames.SPEED, "0.00000");
		response.headers().set(RtspHeaderNames.RANGE, "npt=0.00000-3221.00000");
		response.headers().set(RtspHeaderNames.RTP_INFO,
				"url=rtsp://" + ctx.channel().remoteAddress() + "/;seq=0;rtptime=0 ");

		return response;
	}
}
