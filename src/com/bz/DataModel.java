package com.bz;

import java.io.Serializable;

/**
 *  消息传输模型
 *  ------------------------------------------------------------------------------------
 *  |                  HEADER          |               BODY            |    FOOTER     |
 *  ------------------------------------------------------------------------------------
 * 	| 消息总长 (4byte) | 消息类型(2byte) | 消息体长度(4byte) | 消息(byte)  | 时间戳(8byte)  |
 *  ------------------------------------------------------------------------------------
 *  注意: 消息长度不定
 *  	   消息总长 = 4+2+4+8+消息长度
 */
public class DataModel implements Serializable{
	/** 登录*/
	public final static short MSG_TYPE_LOGIN = 0x100;
	/** 登出*/
	public final static short MSG_TYPE_LOGIN_OUT = 0x101;
	/** 系统消息*/
	public final static short MSG_TYPE_SYS_MSG = 0x200;
	/** 用户消息*/
	public final static short MSG_TYPE_USER_MSG = 0x201;
	
	private static final long serialVersionUID = 1L;

	private int length;
	
	private short msgType;

	private int bodyLength;
	
	private String body;
	
	private long time;

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public short getMsgType() {
		return msgType;
	}

	public void setMsgType(short msgType) {
		this.msgType = msgType;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	public int getBodyLength() {
		return bodyLength;
	}

	public void setBodyLength(int bodyLength) {
		this.bodyLength = bodyLength;
	}

	@Override
	public String toString() {
		return getBody();
	}
}
