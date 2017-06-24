/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * 后台服务。
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class Bootstrap {
	protected final static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	public static final Class DEFAULT_CONTEXT_CLASS = SimpleXmlApplicationContext.class;

	private Class contextClass = DEFAULT_CONTEXT_CLASS;

	private String contextConfigLocation;

	private ApplicationContext applicationContext;

	private boolean alived = false;

	public void setContextClass(Class contextClass) {
		this.contextClass = contextClass;
	}

	public void setContextConfigLocation(String contextConfigLocation) {
		this.contextConfigLocation = contextConfigLocation;
	}

	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	public final void init() throws BeansException {
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread(new Runnable() {
			public void run() {
				System.err.println("shutdown application ....");
				Bootstrap.this.close();
			}
		}, "detroy application context"));

		long startTime = System.currentTimeMillis();
		if (logger.isInfoEnabled()) {
			logger.info("tcp dispatcher init");
		}

		try {
			this.applicationContext = initApplicationContext();
		} catch (BeansException ex) {
			logger.error("Context initialization failed", ex);
			throw ex;
		}

		if (!(logger.isInfoEnabled()))
			return;

		long elapsedTime = System.currentTimeMillis() - startTime;
		logger.info("init completed in " + elapsedTime + " ms");
		alived = true;

		while (alived) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}

	protected ApplicationContext initApplicationContext() throws BeansException {
		if (logger.isInfoEnabled()) {
			logger.info("tcp dispatcher will try to create custom ApplicationContext context of class '"
					+ this.contextClass.getName() + "'");
		}

		SimpleXmlApplicationContext wac = (SimpleXmlApplicationContext) BeanUtils.instantiateClass(this.contextClass);
		wac.setParent(null);

		if (this.contextConfigLocation != null) {
			wac.setConfigLocations(StringUtils.tokenizeToStringArray(this.contextConfigLocation, " ,;", true, true));
		}
		wac.refresh();
		return wac;
	}

	public void close() {
		if (this.applicationContext instanceof ConfigurableApplicationContext)
			((ConfigurableApplicationContext) this.applicationContext).close();
	}

	public static void main(String[] args) {
		Bootstrap boot = new Bootstrap();
		boot.setContextConfigLocation("/tcp-config.xml");
		boot.init();

		System.err.println();
		System.exit(0);
	}
}