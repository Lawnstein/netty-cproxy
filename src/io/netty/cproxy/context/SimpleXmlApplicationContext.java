/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;

/**
 * 简单Spring-ApplicationContext实现.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class SimpleXmlApplicationContext extends AbstractXmlApplicationContext {

	public SimpleXmlApplicationContext() {
	}

	public SimpleXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}

}
