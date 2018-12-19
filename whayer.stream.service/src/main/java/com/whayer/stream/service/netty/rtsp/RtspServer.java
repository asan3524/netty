package com.whayer.stream.service.netty.rtsp;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.whayer.stream.service.util.OsUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Configuration
@Component
public class RtspServer {

	private final static Logger logger = LoggerFactory.getLogger(RtspServer.class);

	@Value("${netty.server.rtsp.port}")
	private Integer port = 554;

	@Value("${netty.server.enable}")
	private Integer enable = 0;

	@Value("${netty.server.bossNum}")
	private Integer bossNum = 1;

	@Value("${netty.server.workNum}")
	private Integer workNum = 0;

	private Channel channel = null;
	private ChannelFuture future = null;

	// 优化处理，bossGroup仅负责接收客户端连接，不做逻辑，取值越小越好
	private EventLoopGroup boss = null;

	// 优化处理，默认值为CPU线程数*2
	// 并发较少时1<=N<=CPU线程数；并发大时需要根据并发连接数动态调节
	// 如果是客户端的workGroup，评估客户端连接数，创建一个大的池所有客户端连接大量服务提供者共用
	// 或者创建workGroup数组，不同客户端通过hash散列值打散使用
	private EventLoopGroup work = null;

	private void start() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		// 在linux系统环境下，应该考虑是使用epoll模式
		// EpollEventLoopGroup+EpollServerSocketChannel

		if (OsUtil.system() == OsUtil.Os.LINUX) {
			boss = new EpollEventLoopGroup(bossNum);
			work = new EpollEventLoopGroup(workNum);
			bootstrap.group(boss, work).channel(EpollServerSocketChannel.class)
					.childHandler(new RtspChannelInitializer()).option(ChannelOption.SO_BACKLOG, 1024)
					.childOption(ChannelOption.SO_KEEPALIVE, false);
		} else {
			boss = new NioEventLoopGroup(bossNum);
			work = new NioEventLoopGroup(workNum);
			bootstrap.group(boss, work).channel(NioServerSocketChannel.class).childHandler(new RtspChannelInitializer())
					.option(ChannelOption.SO_BACKLOG, 1024).childOption(ChannelOption.SO_KEEPALIVE, true);
		}

		future = bootstrap.bind(port);
		future.syncUninterruptibly();
		channel = future.channel();
		logger.info("stream server start success on port:" + port);
	}

	@PreDestroy
	public void destory() {

		logger.info("stream server resource cleaning!");
		
//		future.channel().closeFuture().syncUninterruptibly();
		if (null != channel) {
			channel.closeFuture();
		}
		work.shutdownGracefully().syncUninterruptibly();
		boss.shutdownGracefully().syncUninterruptibly();
	}

	@PostConstruct
	public void init() {
		if (enable == 1) {
			start();
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				destory();
			}
		});
	}
}
