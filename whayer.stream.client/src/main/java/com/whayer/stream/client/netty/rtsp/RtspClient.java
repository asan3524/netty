package com.whayer.stream.client.netty.rtsp;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

@Configuration
@Component
public class RtspClient {
	private final static Logger logger = LoggerFactory.getLogger(RtspClient.class);

	@Value("${netty.server.url}")
	private String url = "192.168.40.19";

	@Value("${netty.server.rtsp.port}")
	private Integer port = 554;

	@Value("${netty.client.workNum}")
	private Integer workNum = 0;

	private static Bootstrap bootstrap = new Bootstrap();
	private final NioEventLoopGroup work = new NioEventLoopGroup(workNum);

	public void destory() {
		logger.info("rtsp client resource cleaning!");
		work.shutdownGracefully().syncUninterruptibly();
	}

	@PostConstruct
	public void init() {
		bootstrap.remoteAddress(url, port);
		bootstrap.group(work).channel(NioSocketChannel.class).handler(new RtspChannelInitializer())
				.option(ChannelOption.SO_KEEPALIVE, true);
		logger.info("rtsp client init success!");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				destory();
			}
		});
	}

	public Bootstrap clone() {
		return bootstrap.clone();
	}
}
