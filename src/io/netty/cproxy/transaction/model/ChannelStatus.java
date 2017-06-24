/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.model;

/**
 * TODO 请填写注释.
 * @author Lawnstein.Chan 
 * @version $Revision:$
 */
public enum ChannelStatus {
	UNKNOWN("未知", 0), 
	ONLINE("在线", 1), 
	OFFLINE("离线", 2);
	
	private String name;
	private int st;
	
	private ChannelStatus(String name, int st) {
		this.name = name;
		this.st = st;
	}
	
	public String toString() {
		return this.st + "_" + this.name;
	}
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public int getSt() {
		return st;
	}

	public void setSt(int st) {
		this.st = st;
	}
    
	public static String getName(int st) {
        for (ChannelStatus c : ChannelStatus.values()) {
            if (c.getSt() == st) {
                return c.name;
            }
        }
        return null;
    }

}
