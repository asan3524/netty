package com.whayer.stream.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.whayer.stream.client.netty.rtsp.Client;
import com.whayer.stream.client.netty.rtsp.RtspClient;

@Configuration
@Component
public class Start {

	private final static Logger logger = LoggerFactory.getLogger(Start.class);

	@Value("${netty.client.workNum}")
	private Integer workNum = 10;

	@Value("${netty.client.total}")
	private Integer total = 100;

	@Autowired
	private RtspClient rtspClient;

	private ThreadPoolExecutor pool = null;
	private ScheduledExecutorService executors = null;
	private ArrayBlockingQueue<Runnable> workQueue = null;

	@PreDestroy
	public void destory() {
		if (null != pool) {
			pool.shutdownNow();
			pool = null;
		}
		if (null != executors) {
			executors.shutdownNow();
			executors = null;
		}
		if (null != workQueue) {
			workQueue.clear();
			workQueue = null;
		}
		if (null != rtspClient) {
			rtspClient.destory();
			rtspClient = null;
		}
	}

	@PostConstruct
	public void init() {

		logger.info("开始分派任务....");
		long start = System.nanoTime();

		workQueue = new ArrayBlockingQueue<Runnable>(total);
		pool = new ThreadPoolExecutor(workNum, workNum, 10, TimeUnit.SECONDS, workQueue);
		executors = Executors.newScheduledThreadPool(1);

		executors.schedule(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < total; i++) {
					pool.execute(new Client(rtspClient));
				}

				while (true) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					ResultSet rs = new ResultSet();
//					long num = rs.getStep();
					if (Client.num.get() == total) {
						logger.info("完成" + total + "个调用,开始统计结果.........");

						logger.info("测试结果打印:\n" + new ResultSet().math().toString());

						break;
					} else {
						logger.info("完成" + Client.num.get() + "个调用.........");
					}
				}
				rtspClient.destory();
			}
		}, 1, TimeUnit.SECONDS);

		long end = System.nanoTime();
		logger.info("分派任务结束，耗时：" + (end - start) / 1000 + "微秒");
	}
}
