package com.whayer.stream.service.netty.rtsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whayer.stream.service.rtsp.SessionHandler;
import com.whayer.stream.service.rtsp.SessionHandlerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;
import io.netty.util.CharsetUtil;

public class RtspChannelHandler extends SimpleChannelInboundHandler<DefaultHttpRequest> {

	private final static Logger logger = LoggerFactory.getLogger(RtspChannelHandler.class);

	@Override
	public void channelRead0(ChannelHandlerContext ctx, DefaultHttpRequest request) throws Exception {
		// TODO Auto-generated method stub
		if (request.method().equals(RtspMethods.OPTIONS)) {
			options(ctx, request);
		} else if (request.method().equals(RtspMethods.DESCRIBE)) {
			describe(ctx, request);
		} else if (request.method().equals(RtspMethods.SETUP)) {
			setup(ctx, request);
		} else if (request.method().equals(RtspMethods.PLAY)) {
			play(ctx, request);
		} else if (request.method().equals(RtspMethods.TEARDOWN)) {
			teardown(ctx, request);
		} else if (request.method().equals(RtspMethods.PAUSE)) {
			pause(ctx, request);
		} else {
			logger.error("unsupport rtsp method!");
			unsupport(ctx, request);
		}
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

	public void options(ChannelHandlerContext ctx, DefaultHttpRequest request) {
		// TODO Auto-generated method stub
		SessionHandler sessionHandler = SessionHandlerFactory.sessionHandler(RtspMethods.OPTIONS.name());
		writeResponseWithFuture(ctx, request, sessionHandler.excute(ctx, request));
	}

	public void describe(ChannelHandlerContext ctx, DefaultHttpRequest request) {
		// TODO Auto-generated method stub
		SessionHandler sessionHandler = SessionHandlerFactory.sessionHandler(RtspMethods.DESCRIBE.name());
		writeResponseWithFuture(ctx, request, sessionHandler.excute(ctx, request));
	}

	public void setup(ChannelHandlerContext ctx, DefaultHttpRequest request) {
		// TODO Auto-generated method stub
		SessionHandler sessionHandler = SessionHandlerFactory.sessionHandler(RtspMethods.SETUP.name());
		writeResponseWithFuture(ctx, request, sessionHandler.excute(ctx, request));
	}

	public void play(ChannelHandlerContext ctx, DefaultHttpRequest request) {
		// TODO Auto-generated method stub
		SessionHandler sessionHandler = SessionHandlerFactory.sessionHandler(RtspMethods.PLAY.name());
		writeResponseWithFuture(ctx, request, sessionHandler.excute(ctx, request));
	}

	public void teardown(ChannelHandlerContext ctx, DefaultHttpRequest request) {
		// TODO Auto-generated method stub
		SessionHandler sessionHandler = SessionHandlerFactory.sessionHandler(RtspMethods.TEARDOWN.name());
		writeResponseWithFutureClose(ctx, request, sessionHandler.excute(ctx, request));
	}

	public void pause(ChannelHandlerContext ctx, DefaultHttpRequest request) {
		// TODO Auto-generated method stub
		SessionHandler sessionHandler = SessionHandlerFactory.sessionHandler(RtspMethods.PAUSE.name());
		writeResponseWithFuture(ctx, request, sessionHandler.excute(ctx, request));
	}

	public void unsupport(ChannelHandlerContext ctx, DefaultHttpRequest request) {
		// TODO Auto-generated method stub
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0,
				RtspResponseStatuses.METHOD_NOT_ALLOWED);
		writeResponseWithFuture(ctx, request, response);
	}

	public void writeResponseWithFuture(ChannelHandlerContext ctx, DefaultHttpRequest request,
			DefaultFullHttpResponse response) {
		if (!response.status().equals(RtspResponseStatuses.OK)) {
			ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
			response.content().writeBytes(buf);
			buf.release();
			ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			// 服务端向客户端发送数据，如果不是长连接，则在注册事件：在刷完response后关闭当前连接
			ChannelFuture future = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request)) {
				System.out.println("关闭连接");
				future.addListener(ChannelFutureListener.CLOSE);
			}
		}
	}

	public void writeResponseWithFutureClose(ChannelHandlerContext ctx, DefaultHttpRequest request,
			DefaultFullHttpResponse response) {
		if (!response.status().equals(RtspResponseStatuses.OK)) {
			ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
			response.content().writeBytes(buf);
			buf.release();
		}
		// 服务端向客户端发送数据，注册事件：在刷完response后关闭当前连接
		ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
