package com.whayer.stream.service.rtsp;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whayer.stream.service.netty.NettyConfig;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;

public class SetupSessionHandler extends SessionHandler {

	public static AtomicInteger rtpPort = new AtomicInteger(50000);
	private final static Logger logger = LoggerFactory.getLogger(SetupSessionHandler.class);

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

		String transport = request.headers().get(RtspHeaderNames.TRANSPORT);
		if (null == transport || transport.indexOf("client_port") < 0) {
			DefaultFullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0,
					RtspResponseStatuses.BAD_REQUEST);
			return response;
		}

		Session session = ctx.channel().attr(NettyConfig.Session).get();

		String local = ctx.channel().localAddress().toString();
		String remote = ctx.channel().remoteAddress().toString();

		if (null == session) {
			session = new Session(request.uri(), remote.substring(1, remote.indexOf(":")),
					local.substring(1, local.indexOf(":")));
		}
		session.initTransport(transport);
		session.setServerRtpPort(rtpPort.getAndAdd(1));
		session.setServerRtcpPort(rtpPort.getAndAdd(1));

		DefaultFullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
		response.headers().set(RtspHeaderNames.CSEQ, cseq);
		response.headers().set(RtspHeaderNames.CACHE_CONTROL, "no-cache");
		response.headers().set(RtspHeaderNames.TRANSPORT, session.transport());
		response.headers().set(RtspHeaderNames.SESSION, session.getId());

		return response;
	}
}
