package com.whayer.stream.client.netty.rtsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whayer.stream.client.Result;
import com.whayer.stream.client.ResultSet;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;

@Sharable
public class RtspChannelHandler extends SimpleChannelInboundHandler<DefaultHttpResponse> {

	private final static Logger logger = LoggerFactory.getLogger(RtspChannelHandler.class);

	@Override
	public void channelRead0(ChannelHandlerContext ctx, DefaultHttpResponse response) throws Exception {
		// TODO Auto-generated method stub

		String id = ctx.channel().attr(Result.Result).get();

		if (response.status().equals(RtspResponseStatuses.OK)) {
			String cseq = response.headers().get(RtspHeaderNames.CSEQ);

			long end = System.nanoTime();
			if ("2".equals(cseq)) {
				ResultSet.results.get(id).setStepOver("OPTIONS");
				ResultSet.results.get(id).setOptionsTime_e(end);
			} else if ("3".equals(cseq)) {
				ResultSet.results.get(id).setStepOver("DESCRIBE");
				ResultSet.results.get(id).setDescribeTime_e(end);
			} else if ("4".equals(cseq)) {
				ResultSet.results.get(id).setStepOver("SETUP");
				ResultSet.results.get(id).setSetupTime_e(end);
				ResultSet.results.get(id).setSessionId(response.headers().get(RtspHeaderNames.SESSION));
			} else if ("5".equals(cseq)) {
				ResultSet.results.get(id).setStepOver("PLAY");
				ResultSet.results.get(id).setPlayTime_e(end);
			} else if ("6".equals(cseq)) {
				ResultSet.results.get(id).setSuccess(true);
				ResultSet.results.get(id).setStepOver("TEARDOWNTIME");
				ResultSet.results.get(id).setStep("CLOSE");
				ResultSet.results.get(id).setTeardownTime_e(end);
				ctx.close();
			}
		} else {
			logger.error("response error : " + response.status());
			ResultSet.results.get(id).setStep("CLOSE");
			ResultSet.results.get(id).setSuccess(false);
			ctx.close();
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		if (null != ctx.channel().attr(Result.Result).get()) {
			Client.num.addAndGet(1);
		}
		super.channelUnregistered(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
