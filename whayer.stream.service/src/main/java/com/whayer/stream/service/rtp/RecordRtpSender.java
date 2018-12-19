package com.whayer.stream.service.rtp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.whayer.stream.service.rtsp.Session;

import io.netty.channel.Channel;

public class RecordRtpSender extends RtpSender {

	private volatile boolean send = true;
	private ExecutorService executorService = Executors.newFixedThreadPool(2);

	@Override
	public Channel send(Channel channel, Session session) {
		// TODO Auto-generated method stub

		System.out.println("==========开始发送数据=========");
		executorService.execute(PacketizerFactory.packetizer(executorService, channel, session));
		
//		executorService.execute(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				System.out.println("==========开始发送数据=========");
//				try {
//					channel.writeAndFlush(new ChunkedNioFile(FileChannel.open(Paths.get(session.getUri()), StandardOpenOption.READ)));
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});
		
		return channel;
	}

	@Override
	public synchronized boolean close() {
		// TODO Auto-generated method stub
		System.out.println("==========数据通道关闭=========");
		send = false;
		if (null != executorService && !executorService.isShutdown()) {
			executorService.shutdown();
		}
		return send;
	}

}
