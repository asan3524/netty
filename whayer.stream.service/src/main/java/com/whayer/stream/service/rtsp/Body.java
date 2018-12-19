package com.whayer.stream.service.rtsp;

public abstract class Body {
	protected String body;
	protected boolean serialize = false;
	protected boolean deserialize = false;

	protected abstract boolean serialize();

	protected abstract boolean deserialize();

	public Body parseBody(String body, boolean force) {
		if (null != body && !deserialize) {
			deserialize = deserialize();
		}
		this.body = body;
		return this;
	}

	public String generateSdp() {
		if (null == body && !serialize) {
			serialize = serialize();
		}
		return body;
	}
}
