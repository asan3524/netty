package com.whayer.stream.service.rtsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whayer.stream.service.netty.NettyConfig;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;
import io.netty.util.CharsetUtil;

public class DescribeSessionHandler extends SessionHandler {

	private final static Logger logger = LoggerFactory.getLogger(DescribeSessionHandler.class);

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
		response.headers().set(RtspHeaderNames.CONTENT_BASE, request.uri());

		String local = ctx.channel().localAddress().toString();
		String remote = ctx.channel().remoteAddress().toString();

		Session session = describe(request.uri(), remote.substring(1, remote.indexOf(":")),
				local.substring(1, local.indexOf(":")));
		ctx.channel().attr(NettyConfig.Session).set(session);
		response.headers().set(RtspHeaderNames.CONTENT_TYPE, session.getContentType());
		response.headers().set(RtspHeaderNames.CONTENT_LENGTH, session.generateSdp().length());

		ByteBuf buffer = Unpooled.copiedBuffer(session.generateSdp(), CharsetUtil.UTF_8);
		response.content().writeBytes(buffer);
		buffer.release();

		return response;
	}

	private Session describe(String uri, String localAddress, String remoteAddress) {
		return new Session(uri, localAddress, remoteAddress);
	}
}
