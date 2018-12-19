package com.whayer.stream.client.netty.rtsp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.whayer.stream.client.Result;
import com.whayer.stream.client.ResultSet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspEncoder;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspVersions;

public class Client implements Runnable {

	public static AtomicInteger rtspPort = new AtomicInteger(20000);
	public static AtomicInteger rtpPort = new AtomicInteger(30000);
	public static AtomicInteger num = new AtomicInteger(0);
	
	private RtspClient rtspClient;
	private ExecutorService client = Executors.newSingleThreadExecutor();
	
	public Client(RtspClient rtspClient) {
		// TODO Auto-generated constructor stub
		this.rtspClient = rtspClient;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Result result = new Result();
		String id = result.getId();

		result.setStep("CONNECT");
		ResultSet.results.put(id, result);

		final long start = System.nanoTime();
		Bootstrap bootstrap = rtspClient.clone();
		
		ChannelFuture channelFuture = reConnect(bootstrap);
		Channel channel = channelFuture.channel();
		channel.attr(Result.Result).set(id);

		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				// TODO Auto-generated method stub
				if (future.isSuccess()) {
					long end = System.nanoTime();
					ResultSet.results.get(id).setConnectTime(end - start);
					ResultSet.results.get(id).setStepOver("CONNECT");

					long start = System.nanoTime();
					options(channel);
					ResultSet.results.get(id).setStep("OPTIONS");
					ResultSet.results.get(id).setOptionsTime(start);

					client.execute(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							while (!"CLOSE".equals(ResultSet.results.get(result.getId()).getStep())) {

								if (channel.isWritable()) {
									if ("OPTIONS".equals(ResultSet.results.get(id).getStep())
											&& "OPTIONS".equals(ResultSet.results.get(id).getStepOver())) {
										long s = System.nanoTime();
										describe(channel);
										ResultSet.results.get(id).setStep("DESCRIBE");
										ResultSet.results.get(id).setDescribeTime(s);
									} else if ("DESCRIBE".equals(ResultSet.results.get(id).getStep())
											&& "DESCRIBE".equals(ResultSet.results.get(id).getStepOver())) {
										long s = System.nanoTime();
										setup(channel);
										ResultSet.results.get(id).setStep("SETUP");
										ResultSet.results.get(id).setSetupTime(s);
									} else if ("SETUP".equals(ResultSet.results.get(id).getStep())
											&& "SETUP".equals(ResultSet.results.get(id).getStepOver())) {
										long s = System.nanoTime();
										play(channel, ResultSet.results.get(id).getSessionId());
										ResultSet.results.get(id).setStep("PLAY");
										ResultSet.results.get(id).setPlayTime(s);
									} else if ("PLAY".equals(ResultSet.results.get(id).getStep())
											&& "PLAY".equals(ResultSet.results.get(id).getStepOver())) {
										long s = System.nanoTime();
										teardown(channel);
										ResultSet.results.get(id).setStep("TEARDOWN");
										ResultSet.results.get(id).setTeardownTime(s);
									}
								}
								try {
									Thread.sleep(30);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							channelFuture.addListener(ChannelFutureListener.CLOSE);
						}
					});
				} else {
					ResultSet.results.get(id).setSuccess(false);
					ResultSet.results.get(id).setStep("CLOSE");
				}
			}
		});
	}

	private ChannelFuture reConnect(Bootstrap bootstrap) {
		
		bootstrap.localAddress(rtspPort.getAndAdd(1));
		ChannelFuture channelFuture = bootstrap.connect();
		try {
			channelFuture.syncUninterruptibly();
			return channelFuture;
		} catch (Exception e) {
			// TODO: handle exception
			return reConnect(bootstrap);
		}
		
	}
	
	private ChannelFuture options(Channel channel) {
		// TODO Auto-generated method stub
		HttpRequest request = new DefaultHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.OPTIONS,
				"rtsp:/" + channel.remoteAddress().toString());
		request.headers().set(RtspHeaderNames.CSEQ, 2);
		return writeRequest(channel, request);
	}

	private ChannelFuture describe(Channel channel) {
		// TODO Auto-generated method stub
		HttpRequest request = new DefaultHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.DESCRIBE,
				"rtsp:/" + channel.remoteAddress().toString());
		request.headers().set(RtspHeaderNames.CSEQ, 3);
		return writeRequest(channel, request);
	}

	private ChannelFuture setup(Channel channel) {
		// TODO Auto-generated method stub
		HttpRequest request = new DefaultHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.SETUP,
				"rtsp:/" + channel.remoteAddress().toString());
		request.headers().set(RtspHeaderNames.CSEQ, 4);
		request.headers().set(RtspHeaderNames.TRANSPORT,
				"RTP/AVP;unicast;client_port=" + rtpPort + "-" + rtpPort.getAndAdd(1));
		return writeRequest(channel, request);
	}

	private ChannelFuture play(Channel channel, String id) {
		// TODO Auto-generated method stub
		HttpRequest request = new DefaultHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.PLAY,
				"rtsp:/" + channel.remoteAddress().toString());
		request.headers().set(RtspHeaderNames.CSEQ, 5);
		request.headers().set(RtspHeaderNames.SESSION, id);
		request.headers().set(RtspHeaderNames.RANGE, "npt-0.00000");
		return writeRequest(channel, request);
	}

	private ChannelFuture teardown(Channel channel) {
		// TODO Auto-generated method stub
		HttpRequest request = new DefaultHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.TEARDOWN,
				"rtsp:/" + channel.remoteAddress().toString());
		request.headers().set(RtspHeaderNames.CSEQ, 6);
		return writeRequest(channel, request);
	}

	private ChannelFuture writeRequest(Channel channel, HttpRequest request) {
		channel.pipeline().addLast(new RtspEncoder());
		return channel.writeAndFlush(request);
	}
}
