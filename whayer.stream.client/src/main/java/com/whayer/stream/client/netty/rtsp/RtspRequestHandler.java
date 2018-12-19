package com.whayer.stream.client.netty.rtsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class RtspRequestHandler extends ChannelOutboundHandlerAdapter {

	private final static Logger logger = LoggerFactory.getLogger(RtspRequestHandler.class);

	@Override
	public void write(ChannelHandlerContext context, Object msg, ChannelPromise promise) throws Exception {

		context.writeAndFlush(msg).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {

				if (future.isSuccess()) {
					logger.info(context.channel().id() + "下发成功" + msg.toString());
					promise.setSuccess();
				} else {
					logger.info(context.channel().id() + "下发失败");
					future.cause().printStackTrace();
					future.channel().close();
				}
			}
		});
		// }
	}
}
