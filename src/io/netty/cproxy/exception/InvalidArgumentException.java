/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.exception;

/**
 * 无效参数.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class InvalidArgumentException extends Exception {

	/**
	 * @param message
	 */
	public InvalidArgumentException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidArgumentException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InvalidArgumentException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
