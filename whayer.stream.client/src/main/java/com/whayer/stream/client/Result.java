package com.whayer.stream.client;

import java.util.UUID;

import io.netty.util.AttributeKey;

public class Result {

	public static final AttributeKey<String> Result = AttributeKey.newInstance("Result");
	
	public Result() {
		// TODO Auto-generated constructor stub
		this.id = UUID.randomUUID().toString();
	}
	
	private String sessionId;
	
	private String id;
	private boolean success;
	private String step;
	private String stepOver;

	private long connectTime = 0;
	private long optionsTime = 0;
	private long describeTime = 0;
	private long setupTime = 0;
	private long playTime = 0;
	private long reciveDataTime = 0;
	private long teardownTime = 0;

	private long connectTime_e = 0;
	private long optionsTime_e = 0;
	private long describeTime_e = 0;
	private long setupTime_e = 0;
	private long playTime_e = 0;
	private long reciveDataTime_e = 0;
	private long teardownTime_e = 0;
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getStepOver() {
		return stepOver;
	}

	public void setStepOver(String stepOver) {
		this.stepOver = stepOver;
	}

	public long getConnectTime() {
		return connectTime;
	}

	public void setConnectTime(long connectTime) {
		this.connectTime = connectTime;
	}

	public long getOptionsTime() {
		return optionsTime;
	}

	public void setOptionsTime(long optionsTime) {
		this.optionsTime = optionsTime;
	}

	public long getDescribeTime() {
		return describeTime;
	}

	public void setDescribeTime(long describeTime) {
		this.describeTime = describeTime;
	}

	public long getSetupTime() {
		return setupTime;
	}

	public void setSetupTime(long setupTime) {
		this.setupTime = setupTime;
	}

	public long getPlayTime() {
		return playTime;
	}

	public void setPlayTime(long playTime) {
		this.playTime = playTime;
	}

	public long getReciveDataTime() {
		return reciveDataTime;
	}

	public void setReciveDataTime(long reciveDataTime) {
		this.reciveDataTime = reciveDataTime;
	}

	public long getTeardownTime() {
		return teardownTime;
	}

	public void setTeardownTime(long teardownTime) {
		this.teardownTime = teardownTime;
	}

	public long getConnectTime_e() {
		return connectTime_e;
	}

	public void setConnectTime_e(long connectTime_e) {
		this.connectTime_e = connectTime_e;
	}

	public long getOptionsTime_e() {
		return optionsTime_e;
	}

	public void setOptionsTime_e(long optionsTime_e) {
		this.optionsTime_e = optionsTime_e;
	}

	public long getDescribeTime_e() {
		return describeTime_e;
	}

	public void setDescribeTime_e(long describeTime_e) {
		this.describeTime_e = describeTime_e;
	}

	public long getSetupTime_e() {
		return setupTime_e;
	}

	public void setSetupTime_e(long setupTime_e) {
		this.setupTime_e = setupTime_e;
	}

	public long getPlayTime_e() {
		return playTime_e;
	}

	public void setPlayTime_e(long playTime_e) {
		this.playTime_e = playTime_e;
	}

	public long getReciveDataTime_e() {
		return reciveDataTime_e;
	}

	public void setReciveDataTime_e(long reciveDataTime_e) {
		this.reciveDataTime_e = reciveDataTime_e;
	}

	public long getTeardownTime_e() {
		return teardownTime_e;
	}

	public void setTeardownTime_e(long teardownTime_e) {
		this.teardownTime_e = teardownTime_e;
	}
}
