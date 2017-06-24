/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.exception;

/**
 * 无原始服务提供者.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class NoServiceProviderException extends Exception {

	/**
	 * @param message
	 */
	public NoServiceProviderException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NoServiceProviderException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoServiceProviderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public NoServiceProviderException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
