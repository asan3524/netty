package com.whayer.stream.service.rtsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whayer.stream.service.netty.NettyConfig;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;

public class OptionsSessionHandler extends SessionHandler {

	private final static Logger logger = LoggerFactory.getLogger(OptionsSessionHandler.class);

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
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
		response.headers().set(RtspHeaderNames.CSEQ, cseq);
		response.headers().set(RtspHeaderNames.PUBLIC, NettyConfig.OPTIONS);
		return response;
	}
}
