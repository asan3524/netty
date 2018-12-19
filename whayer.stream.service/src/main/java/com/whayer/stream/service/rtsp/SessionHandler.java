package com.whayer.stream.service.rtsp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;

public abstract class SessionHandler {
	// public static ConcurrentHashMap<String, Session> session_pool = new
	// ConcurrentHashMap<String, Session>();
	//
	// public Session session(String id) {
	// return SessionHandler.session_pool.get(id);
	// }

	public abstract DefaultFullHttpResponse excute(ChannelHandlerContext ctx, DefaultHttpRequest request);
}
