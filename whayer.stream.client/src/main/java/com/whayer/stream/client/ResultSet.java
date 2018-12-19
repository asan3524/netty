package com.whayer.stream.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ResultSet {
	public static ConcurrentMap<String, Result> results = new ConcurrentHashMap<String, Result>();

	private int success = 0;
	private int total = 0;

	private long connectTime = 0;
	private long optionsTime = 0;
	private long describeTime = 0;
	private long setupTime = 0;
	private long playTime = 0;
	private long reciveDataTime = 0;
	private long teardownTime = 0;

	private long playDoneTime = 0;
	private long reciveTime = 0;

	private long totalTime = 0;

	public ResultSet math() {
		total = results.size();

		if (total > 0) {
			for (Result result : results.values()) {
				if (null != result) {
					if (result.isSuccess()) {
						success++;

						long c = result.getConnectTime();
						long o = result.getOptionsTime_e() - result.getOptionsTime();
						long d = result.getDescribeTime_e() - result.getDescribeTime();
						long s = result.getSetupTime_e() - result.getSetupTime();
						long p = result.getPlayTime_e() - result.getPlayTime();
						long r = result.getReciveDataTime_e() - result.getReciveDataTime();
						long t = result.getTeardownTime_e() - result.getTeardownTime();

						c = c < 0 ? 0 : c;
						o = o < 0 ? 0 : o;
						d = d < 0 ? 0 : d;
						s = s < 0 ? 0 : s;
						p = p < 0 ? 0 : p;
						r = r < 0 ? 0 : r;
						t = t < 0 ? 0 : t;

						long pd = 0;
						if (p > 0) {
							pd = c + o + d + s + p;
						}

						long rt = 0;
						if (r > 0) {
							rt = c + o + d + s + p + r;
						}

						long tt = c + o + d + s + p + r + t;

						connectTime += c;
						optionsTime += o;
						describeTime += d;
						setupTime += s;
						playTime += p;
						reciveDataTime += r;
						teardownTime += t;
						playDoneTime += pd;
						reciveTime += rt;

						totalTime += tt;
					}
				}
			}

			connectTime /= success;
			optionsTime /= success;
			describeTime /= success;
			setupTime /= success;
			playTime /= success;
			reciveDataTime /= success;
			teardownTime /= success;
			playDoneTime /= success;
			reciveTime /= success;

			totalTime /= success;
		}

		return this;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		int length = 20;
		sb.append(format("AVG", length));
		sb.append(format("AVG_TIME", length));
		sb.append(format("AVG_RECIVE_TIME", length));
		sb.append(format("AVG_PLAYDONE_TIME", length));
		sb.append(format("AVG_TEARDOWN_TIME", length));
		sb.append(format("AVG_RECIVEDATA_TIME", length));
		sb.append(format("AVG_PLAY_TIME", length));
		sb.append(format("AVG_SETUP_TIME", length));
		sb.append(format("AVG_DESCRIBE_TIME", length));
		sb.append(format("AVG_OPTIONS_TIME", length));
		sb.append(format("AVG_CONNECT_TTIME", length) + "\n");

		sb.append(format(success + "/" + total, length));
		sb.append(format(totalTime / 1000000 + "", length));
		sb.append(format(reciveTime / 1000000 + "", length));
		sb.append(format(playDoneTime / 1000000 + "", length));
		sb.append(format(teardownTime / 1000000 + "", length));
		sb.append(format(reciveDataTime / 1000000 + "", length));
		sb.append(format(playTime / 1000000 + "", length));
		sb.append(format(setupTime / 1000000 + "", length));
		sb.append(format(describeTime / 1000000 + "", length));
		sb.append(format(optionsTime / 1000000 + "", length));
		sb.append(format(connectTime / 1000000 + "", length));
		return sb.toString();
	}

	private String format(String str, int length) {
		if (str.length() >= length) {
			return str;
		} else {
			int len = length - str.length();
			StringBuilder sb = new StringBuilder();
			sb.append(str);
			while(len > 0) {
				sb.append("-");
				len--;
			}
			return sb.toString();
		}
	}

	public long getStep() {
		long c = 0;
		long o = 0;
		long d = 0;
		long s = 0;
		long p = 0;
		long t = 0;
		long close = 0;

		for (Result result : results.values()) {
			if (null != result) {
				if (result.getStepOver().equals("CONNECT")) {
					c++;
				} else if (result.getStepOver().equals("OPTIONS")) {
					o++;
				} else if (result.getStepOver().equals("DESCRIBE")) {
					d++;
				} else if (result.getStepOver().equals("SETUP")) {
					s++;
				} else if (result.getStepOver().equals("PLAY")) {
					p++;
				} else if (result.getStepOver().equals("TEARDOWNTIME")) {
					t++;
				}

				if (result.getStep().equals("CLOSE")) {
					close++;
				}
			}
		}

		System.out.println(c + "--" + o + "--" + d + "--" + s + "--" + p + "--" + t + "--" + close);
		return close;
	}
}
